package com.cybrilla.shashank.liborg;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;

public class DetailActivity extends AppCompatActivity {
    private TextView bookName, authorName;
    private ImageView thumbnail;

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        bookName = (TextView) findViewById(R.id.detail_book_name);
        authorName = (TextView) findViewById(R.id.detail_author_name);
        thumbnail = (ImageView) findViewById(R.id.detail_thumbnail);

        String title = getIntent().getExtras().getString("bookName");
        bookName.setText(title);

        String author = getIntent().getExtras().getString("authorName");
        authorName.setText(author);

        Bitmap bmp = null;
        String filename = getIntent().getStringExtra("thumbnail");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        thumbnail.setImageBitmap(bmp);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }
}
