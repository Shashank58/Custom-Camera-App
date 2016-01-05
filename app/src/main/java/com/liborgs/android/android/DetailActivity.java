package com.liborgs.android.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.liborgs.android.R;
import com.liborgs.android.datamodle.HomeView;
import com.liborgs.android.transition.RevealTransition;
import com.liborgs.android.util.AppUtils;
import com.liborgs.android.util.Constants;
import com.liborgs.android.util.SharedPreferencesHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for setting details of the given book.**/

public class DetailActivity extends AppCompatActivity implements Serializable {
    private TextView bookName, authorName, categories, pageCount, available, description, publication;
    private ImageView thumbnail, starOne, starTwo, starThree, starFour, startFive;
    private Button webReaderButton, requestBook;
    private HomeView hv;
    private ViewGroup allView;
    private View firstDetail, secondDetail, thirdDetail;
    public static final String EXTRA_EPICENTER = "EXTRA_EPICENTER";
    private static boolean isStartAnimation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        setContentView(R.layout.activity_detail);
        initTransitions();

        bookName = (TextView) findViewById(R.id.detail_book_name);
        authorName = (TextView) findViewById(R.id.detail_author_name);
        thumbnail = (ImageView) findViewById(R.id.detail_thumbnail);
        categories = (TextView) findViewById(R.id.detail_category);
        pageCount = (TextView) findViewById(R.id.detail_pages);
        available = (TextView) findViewById(R.id.detail_available);
        description = (TextView) findViewById(R.id.detail_description);
        publication = (TextView) findViewById(R.id.detail_publication);
        webReaderButton = (Button) findViewById(R.id.web_reader_button);
        requestBook = (Button) findViewById(R.id.request_book);
        starOne = (ImageView) findViewById(R.id.star_1);
        starTwo = (ImageView) findViewById(R.id.star_2);
        starThree = (ImageView) findViewById(R.id.star_3);
        starFour = (ImageView) findViewById(R.id.star_4);
        startFive = (ImageView) findViewById(R.id.star_5);
        allView = (ViewGroup) findViewById(R.id.allViewsDetail);
        firstDetail = findViewById(R.id.first_detail);
        secondDetail = findViewById(R.id.second_detail);
        thirdDetail = findViewById(R.id.third_detail);

        setData();
        requestBookListener();
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    private void initTransitions() {
        TransitionInflater inflater = TransitionInflater.from(this);
        Window window = getWindow();
        RevealTransition reveal = createRevealTransition();
        reveal.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {

            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        window.setEnterTransition(reveal);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isStartAnimation = true;
                beginAllViewTransition();
            }
        }, 500);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    private void beginAllViewTransition(){
        TransitionManager.beginDelayedTransition(allView, new Slide());
        if (isStartAnimation)
            toggleVisibilityOn(firstDetail, secondDetail, thirdDetail);
        else
            toggleVisibilityOff(firstDetail, secondDetail, thirdDetail);
    }

    private static void toggleVisibilityOff(View... views) {
        for (View view : views) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        isStartAnimation = false;
        beginAllViewTransition();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                superFinishAfterTransition();
            }
        }, 300);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    private void superFinishAfterTransition() {
        Log.e("Detail activity", "is it");
        super.onBackPressed();
    }

    private static void toggleVisibilityOn(View... views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public void back(View v){
        isStartAnimation = false;
        beginAllViewTransition();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                superFinishAfterTransition();
            }
        }, 300);
    }

    @TargetApi(VERSION_CODES.KITKAT)
    private RevealTransition createRevealTransition() {
        Point epicenter = getIntent().getParcelableExtra(EXTRA_EPICENTER);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int bigRadius = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
        RevealTransition reveal = new RevealTransition(epicenter, 0, bigRadius, 600);
        reveal.addTarget(R.id.detail_thumbnail);
        reveal.addTarget(android.R.id.statusBarBackground);
        return reveal;
    }

    private void requestBookListener(){
        requestBook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
                AppUtils.getInstance().showProgressDialog(DetailActivity.this,
                        "Sending request to admin");

                StringRequest jsonObjectRequest = new StringRequest(Method.POST
                        , Constants.USER_REQUEST_BOOK,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                AppUtils.getInstance().dismissProgressDialog();
                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    boolean status = jObj.getBoolean("status");
                                    if (status) {
                                        String message = jObj.getString("message");
                                        AppUtils.getInstance().alertMessage(DetailActivity.this,
                                                Constants.LIBORGS, message);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppUtils.getInstance().dismissProgressDialog();
                        error.printStackTrace();
                    }
                })
                {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Log.e("Detail Activity", "Authors: "+hv.getAuthorName());
                        HashMap<String, String> params = new HashMap<>();
                        params.put("title", hv.getBookName());
                        params.put("author", hv.getAuthorName());
                        params.put("publisher", hv.getPublisher());
                        params.put("thumbnail", hv.getThumbnail());
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        SharedPreferencesHandler s = new SharedPreferencesHandler();
                        String loggedIn = s.getKeyAuth(DetailActivity.this);
                        HashMap<String, String> param = new HashMap<>();
                        Log.e("Detail activity", "Auth token: " + loggedIn);
                        param.put("auth-token", loggedIn);
                        return param;
                    }
                };
                queue.add(jsonObjectRequest);
            }
        });
    }

    private void setData(){
        hv = (HomeView) getIntent().getSerializableExtra("allData");
        if (hv.getAverageRating().equals("NA")){
            webReaderButton.setVisibility(View.GONE);
            requestBook.setVisibility(View.GONE);
        } else {
            final String readerUrl = hv.getWebReaderLink();
            int stars = 0;
            if (!"".equals(hv.getAverageRating()))
                stars = (int) Double.parseDouble(hv.getAverageRating());
            switch (stars){
                case 1:
                    starOne.setImageResource(R.drawable.ic_star);
                    break;

                case 2:
                    starOne.setImageResource(R.drawable.ic_star);
                    starTwo.setImageResource(R.drawable.ic_star);
                    break;

                case 3:
                    starOne.setImageResource(R.drawable.ic_star);
                    starTwo.setImageResource(R.drawable.ic_star);
                    starThree.setImageResource(R.drawable.ic_star);
                    break;

                case 4:
                    starOne.setImageResource(R.drawable.ic_star);
                    starTwo.setImageResource(R.drawable.ic_star);
                    starThree.setImageResource(R.drawable.ic_star);
                    starFour.setImageResource(R.drawable.ic_star);
                    break;

                case 5:
                    starOne.setImageResource(R.drawable.ic_star);
                    starTwo.setImageResource(R.drawable.ic_star);
                    starThree.setImageResource(R.drawable.ic_star);
                    starFour.setImageResource(R.drawable.ic_star);
                    startFive.setImageResource(R.drawable.ic_star);
                    break;

                default:
                    break;
            }
            if (!"".equals(readerUrl)) {
                webReaderButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse(readerUrl));
                        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(viewIntent);
                    }
                });
            } else {
                webReaderButton.setEnabled(false);
            }
        }
        bookName.setText(hv.getBookName());
        authorName.setText(hv.getAuthorName());
        categories.setText(hv.getCategories());
        pageCount.setText(hv.getPageCount());
        description.setText(hv.getDescription());
        publication.setText(hv.getPublisher());
        Glide.with(this).load(hv.getThumbnail())
                .asBitmap().into(thumbnail);
    }
}
