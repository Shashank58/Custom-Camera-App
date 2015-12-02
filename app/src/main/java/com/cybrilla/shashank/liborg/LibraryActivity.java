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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ViewPager viewPager;
    PendingIntent pi;
    BroadcastReceiver br;
    AlarmManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the status bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
//        // Never show the action bar if the
//        // Status bar is hidden, so hide that too if necessary.
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_library);
        getSupportActionBar().setElevation(0);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setup();
//        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
//                6*1000, pi);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "Home");
        adapter.addFragment(new TwoFragment(), "My Shelf");
        viewPager.setAdapter(adapter);
    }

    public void animateIntent(View v){
        Intent intent = new Intent(this, DetailActivity.class);
        String transitionCircle = getString(R.string.transition_name_circle);

        // Define the view that the animation will start from
        View viewStart = v.findViewById(R.id.card_view);

        TextView nameOfBook = (TextView) viewStart.findViewById(R.id.bookName);
        TextView nameOfAuthor = (TextView) viewStart.findViewById(R.id.authorName);
        ImageView bookImage = (ImageView) viewStart.findViewById(R.id.bookImage);

        String bookName = nameOfBook.getText().toString();
        String authorName = nameOfAuthor.getText().toString();
        Bitmap thumbnail = ((BitmapDrawable)bookImage.getDrawable()).getBitmap();

        intent.putExtra("bookName", bookName);
        intent.putExtra("authorName", authorName);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation
                (this,  new Pair<View, String>(viewStart, transitionCircle));

        try {
            //Write file
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Cleanup
            stream.close();
           // thumbnail.recycle();
            intent.putExtra("thumbnail", filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Start the Intent
        ActivityCompat.startActivity(this, intent, options.toBundle());
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

    public void barCode(View v){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            String _code = data.getStringExtra("SCAN_RESULT");
            Log.e("Library Activity", "Code: "+_code);
        }
    }
}
