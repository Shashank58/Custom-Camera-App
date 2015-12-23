package com.liborgs.android;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**  Main activity which contains home and myShelf fragments.
 *   Also contains issue of books through bar code or book title. **/


public class LibraryActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private EditText myEditText;
    private TwoFragment twoFragment;
    private OneFragment oneFragment;
    private Bitmap bm, scaledBitmap, bitmap;

    private static final int PIC_CAPTURED = 0;
    private static final int CROPPED_PIC = 2;
    private int x1, y1, x2, y2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferencesHandler sh = new SharedPreferencesHandler();
        String loggedIn = sh.getKeyAuth(this);
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_library);

        RegisterGCMId app = (RegisterGCMId) getApplication();
        app.checkVersion(loggedIn, this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        myEditText = (EditText) findViewById(R.id.myEditText);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        toolbar.setCollapsible(true);
    }



    private void setupViewPager(ViewPager viewPager) {
        twoFragment = new TwoFragment();
        oneFragment = new OneFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(oneFragment, "Home");
        adapter.addFragment(twoFragment, "My Shelf");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void scanBarCode(View v){
        if(isNetworkAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.pick_issue_type)
                    .setItems(R.array.issue_method, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                IntentIntegrator integrator = new IntentIntegrator(LibraryActivity.this);
                                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                                integrator.initiateScan();
                            } else {
                                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, PIC_CAPTURED);
                            }
                        }
                    }).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == PIC_CAPTURED){
                performCrop(data.getData());
            } else if (requestCode == CROPPED_PIC){
                bm = data.getExtras().getParcelable("data");
                int height = 0;
                if (bm != null) {
                    height = bm.getHeight();
                }
                //Resizing image
                int width = bm.getWidth();
                int finalWidth;
                int finalHeight ;
                if(height >= width){
                    finalHeight = 480;
                    finalWidth = (finalHeight * width) / height;
                }else {
                    finalWidth = 480;
                    finalHeight = (finalWidth * height) / width;
                }
                scaledBitmap = Bitmap.createScaledBitmap(bm, finalWidth, finalHeight, true);
                SendImage s = new SendImage(scaledBitmap, this);
                s.sendImage();
            } else {
                String _code = data.getStringExtra("SCAN_RESULT");
                if (isNetworkAvailable())
                    issueBook(_code, "");
                else {
                    new AlertDialog.Builder(this)
                                .setTitle("Liborg")
                                .setMessage("Please check your internet connection")
                                .setPositiveButton(android.R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                .show();
                }
            }
        }
    }

    private void performCrop(Uri bp){
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setData(bp);
            cropIntent.setType("image/*");

            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", x1);
            cropIntent.putExtra("aspectY", y1);
            cropIntent.putExtra("outputX", x2);
            cropIntent.putExtra("outputY", y2);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CROPPED_PIC);
        } catch (ActivityNotFoundException e){
            e.printStackTrace();
            Toast toast = Toast.makeText(getApplicationContext(), "Your device doesnot support cropping an image", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void issueBook(final String isbn, final String bookTitle){
        String url = "https://liborgs-1139.appspot.com/users/issue";
        final ProgressDialog dialog = new ProgressDialog(LibraryActivity.this);
        dialog.setMessage("Issuing");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        StringRequest jObject = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dialog.hide();
                            JSONObject res = new JSONObject(response);
                            String message = res.getString("message");
                            boolean status = res.getBoolean("status");
                            if(status){
                                twoFragment.getData(); //Update my shelf with the book
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(LibraryActivity.this);
                            builder.setTitle("Book Issue")
                                    .setMessage(message)
                                    .setPositiveButton(android.R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //Goto My shelf
                                                    TabLayout.Tab tab = tabLayout.getTabAt(1);
                                                    if(tab != null)
                                                        tab.select();
                                                    viewPager.setCurrentItem(1);
                                                }
                                            })
                                    .show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> params = new HashMap<>();
                SharedPreferencesHandler sh = new SharedPreferencesHandler();
                String authToken = sh.getKeyAuth(LibraryActivity.this);
                params.put("auth-token", authToken);
                return params;
            }

            @Override
            public Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("isbn", isbn);
                params.put("title", bookTitle);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jObject);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
