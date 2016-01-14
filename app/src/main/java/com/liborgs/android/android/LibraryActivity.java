package com.liborgs.android.android;

import android.Manifest.permission;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.liborgs.android.R;
import com.liborgs.android.datahelpers.RegisterGCMId;
import com.liborgs.android.datahelpers.SendImage;
import com.liborgs.android.util.AppUtils;
import com.liborgs.android.util.Constants;
import com.liborgs.android.util.SharedPreferencesHandler;

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
    private int x1, y1, x2, y2;
    private static int selectedOption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferencesHandler sh = new SharedPreferencesHandler();
        String loggedIn = sh.getKeyAuth(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_library);

        ((RegisterGCMId) getApplication()).checkVersion(loggedIn, this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        myEditText = (EditText) findViewById(R.id.myEditText);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        if (getIntent().hasExtra("SelectedTab")){
            int tabSelected = getIntent().getIntExtra("SelectedTab", 0);
            TabLayout.Tab tab = tabLayout.getTabAt(tabSelected);
            if (tab != null) {
                tab.select();
            }
            viewPager.setCurrentItem(tabSelected);
        }

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

    //Fab button clicked
    public void scanBarCode(View v){
        if(AppUtils.getInstance().isNetworkAvailable(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.pick_issue_type)
                    .setItems(R.array.issue_method, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            selectedOption = which;
                            if (VERSION.SDK_INT < VERSION_CODES.M) {
                                Log.e("Library activity", "Which: "+selectedOption);
                                selectBookIssueOptions();
                            } else {
                                checkForPermission();
                            }
                        }
                    }).show();
        }
    }

    private void selectBookIssueOptions(){
        if (selectedOption == 0) {
            IntentIntegrator integrator = new IntentIntegrator(LibraryActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            integrator.initiateScan();
        } else {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Constants.PIC_CAPTURED);
        }
    }

    private void checkForPermission(){
        int storagePermission = ContextCompat.checkSelfPermission(this,
                permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(this,
                permission.CAMERA);
        if (storagePermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{permission.WRITE_EXTERNAL_STORAGE},
                    Constants.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        } else if (cameraPermission != PackageManager.PERMISSION_GRANTED){
            Log.e("Library activity", "Coming for another check");
            ActivityCompat.requestPermissions(this,
                    new String[]{permission.CAMERA},
                    Constants.MY_PERMISSIONS_ACCESS_CAMERA);
        } else {
            selectBookIssueOptions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case Constants.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    checkForPermission();
                } else {
                    String message = "Please enable storage permission to issue book";
                    AppUtils.getInstance().alertMessage(this,
                            Constants.LIBORGS, message);
                }
            break;

            case Constants.MY_PERMISSIONS_ACCESS_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    selectBookIssueOptions();
                } else {
                    String message = "Please enable camera permission to issue book";
                    AppUtils.getInstance().alertMessage(this,
                            Constants.LIBORGS, message);
                }
            break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == Constants.PIC_CAPTURED){
                performCrop(data.getData());
            } else if (requestCode == Constants.CROPPED_PIC){
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
                if (AppUtils.getInstance().isNetworkAvailable(this))
                    issueBook(_code, "");
                else {
                    AppUtils.getInstance().alertMessage(this, Constants.LIBORGS
                            , Constants.INTERNET_CONN);
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
            startActivityForResult(cropIntent, Constants.CROPPED_PIC);
        } catch (ActivityNotFoundException e){
            e.printStackTrace();
            Toast toast = Toast.makeText(getApplicationContext(), "Your device doesnot support cropping an image", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void issueBook(final String isbn, final String bookTitle){
        AppUtils.getInstance().showProgressDialog(this, "Issuing");

        StringRequest jObject = new StringRequest(Request.Method.POST, Constants.USERS_ISSUE_BOOK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            AppUtils.getInstance().dismissProgressDialog();
                            JSONObject res = new JSONObject(response);
                            String message = res.getString("message");
                            boolean status = res.getBoolean("status");
                            if(status){
                                twoFragment.getData(); //Update my shelf with the book
                            }
                            AppUtils.getInstance().alertMessage(LibraryActivity.this, "Book Issue", message);
                            //Goto My shelf
                            TabLayout.Tab tab = tabLayout.getTabAt(1);
                            if(tab != null)
                                tab.select();
                            viewPager.setCurrentItem(1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppUtils.getInstance().dismissProgressDialog();
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
