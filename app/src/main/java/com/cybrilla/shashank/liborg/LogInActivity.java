package com.cybrilla.shashank.liborg;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        TextView mailIcon = (TextView) findViewById(R.id.mailIcon);
        TextView keyIcon = (TextView) findViewById(R.id.keyIcon);

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        mailIcon.setTypeface(font);
        keyIcon.setTypeface(font);
    }
}
