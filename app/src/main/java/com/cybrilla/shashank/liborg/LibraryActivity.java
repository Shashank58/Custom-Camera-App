package com.cybrilla.shashank.liborg;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

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
    PendingIntent pi;
    BroadcastReceiver br;
    AlarmManager am;
    private EditText myEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_library);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        myEditText = (EditText) findViewById(R.id.myEditText);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        toolbar.setCollapsible(true);

//        setup();
//        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
//                6*1000, pi);
    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "Home");
        adapter.addFragment(new TwoFragment(), "My Shelf");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        myEditText.setVisibility(View.INVISIBLE);
    }


    private void setup() {
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                sampleNotification();
            }
        };
        registerReceiver(br, new IntentFilter("com.cybrilla.shashank.liborg") );
        pi = PendingIntent.getBroadcast(this, 0, new Intent("com.cybrilla.shashank.liborg"),
                0 );
        am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    public void sampleNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                                                    Context.NOTIFICATION_SERVICE);
        Context context = getApplicationContext();
        String notificationTitle = "Exercise of Notification!";
        String notificationText = "http://android-er.blogspot.com/";
        Intent myIntent = new Intent(this, LibraryActivity.class);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(context,
                0, myIntent,
                0);
        Notification myNotification = new Notification.Builder(context)
                .setContentTitle(notificationTitle).setContentText(notificationText)
                .setSmallIcon(R.drawable.lifeofpi).setContentIntent(pendingIntent).build();
        myNotification.defaults |= Notification.DEFAULT_SOUND;
        myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1, myNotification);
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
                                AlertDialog.Builder build = new AlertDialog.Builder(LibraryActivity.this);
                                final LayoutInflater layoutInflater = LibraryActivity.this.getLayoutInflater();
                                build.setView(layoutInflater.inflate(R.layout.dialog_title, null))
                                        .setPositiveButton(android.R.string.yes,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Dialog dialogView = (Dialog) dialog;
                                                        EditText dialogBookTitle = (EditText) dialogView.findViewById(R.id.dialog_book_title);
                                                        String bookTitle = dialogBookTitle.getText().toString();
                                                        if (!bookTitle.equals("")) {
                                                            issueBook("", bookTitle);
                                                        }
                                                    }
                                                })
                                        .setNegativeButton(android.R.string.cancel,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                    }
                                                }).show();
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
            String _code = data.getStringExtra("SCAN_RESULT");
            issueBook(_code, "");
        }
    }

    public AlertDialog.Builder alertDialog(){
        return new AlertDialog.Builder(this);
    }

    private void issueBook(final String isbn, final String bookTitle){
        String url = "https://liborgs-1139.appspot.com/users/issue";
        StringRequest jObject = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            String message = res.getString("message");
                            AlertDialog.Builder builder = new AlertDialog.Builder(LibraryActivity.this);
                            builder.setTitle("Book Issue")
                                    .setMessage(message)
                                    .setPositiveButton(android.R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {

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
}
