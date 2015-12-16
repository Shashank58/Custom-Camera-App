package com.cybrilla.shashank.liborg;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Serializable;

/**
 * Class responsible for setting details of the given book.**/

public class DetailActivity extends AppCompatActivity implements Serializable {
    private TextView bookName, authorName, categories, pageCount, available, description, publication;
    private ImageView thumbnail;

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
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


        setData();
    }

    public void back(View v){
        super.onBackPressed();
    }

    private void setData(){
        HomeView hv = (HomeView) getIntent().getSerializableExtra("allData");

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
