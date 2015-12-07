package com.cybrilla.shashank.liborg;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ViewPager viewPager;
    PendingIntent pi;
    BroadcastReceiver br;
    AlarmManager am;
    private RecyclerView recList;
    private EditText myEditText;

    private static final String LIB_KEY = "Liborg Auth";
    private static final int PRIVATE_MODE = 0;
    private static final String KEY_AUTH = "auth_key" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_library);
        //getSupportActionBar().setElevation(0);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        myEditText = (EditText) findViewById(R.id.myEditText);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setCollapsible(true);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

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
        return;
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
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String _code = data.getStringExtra("SCAN_RESULT");
            issueBook(_code);
        }
    }

    private void issueBook(final String isbn){
        String url = "  https://liborgs-1139.appspot.com/users/issue";
        StringRequest jObject = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Library activity", "Message: "+response);

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
                params.put("title", "");
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jObject);
    }

}
