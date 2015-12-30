package com.liborgs.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.liborgs.android.HomeView;
import com.liborgs.android.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        setContentView(R.layout.activity_detail);

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

        setData();
        requestBookListener();
    }

    public void back(View v){
        super.onBackPressed();
    }

    private void requestBookListener(){
        requestBook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
                final ProgressDialog dialog = new ProgressDialog(DetailActivity.this);
                dialog.setMessage("Sending request to admin");
                dialog.setInverseBackgroundForced(false);
                dialog.setCancelable(false);
                dialog.show();

                StringRequest jsonObjectRequest = new StringRequest(Method.POST
                        , Constants.USER_REQUEST_BOOK,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                dialog.hide();
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
                        dialog.hide();
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
