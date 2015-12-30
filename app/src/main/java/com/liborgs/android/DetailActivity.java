package com.liborgs.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Serializable;

/**
 * Class responsible for setting details of the given book.**/

public class DetailActivity extends AppCompatActivity implements Serializable {
    private TextView bookName, authorName, categories, pageCount, available, description, publication;
    private ImageView thumbnail, starOne, starTwo, starThree, starFour, startFive;
    private LinearLayout startLayout;
    private Button webReaderButton, requestBook;

    @TargetApi(VERSION_CODES.LOLLIPOP)
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
        startLayout = (LinearLayout) findViewById(R.id.star_layout);
        webReaderButton = (Button) findViewById(R.id.web_reader_button);
        requestBook = (Button) findViewById(R.id.request_book);
        starOne = (ImageView) findViewById(R.id.star_1);
        starTwo = (ImageView) findViewById(R.id.star_2);
        starThree = (ImageView) findViewById(R.id.star_3);
        starFour = (ImageView) findViewById(R.id.star_4);
        startFive = (ImageView) findViewById(R.id.star_5);

        setData();
    }

    public void back(View v){
        super.onBackPressed();
    }

    private void setData(){
        HomeView hv = (HomeView) getIntent().getSerializableExtra("allData");
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
