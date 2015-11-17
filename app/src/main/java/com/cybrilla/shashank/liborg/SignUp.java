package com.cybrilla.shashank.liborg;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SignUp extends AppCompatActivity {
    private TextView nameIcon, checkIcon, keyIconSignUp, mailIconSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mailIconSignUp = (TextView) findViewById(R.id.mailIconSignUp);
        keyIconSignUp = (TextView) findViewById(R.id.keyIconSignUp);
        checkIcon = (TextView) findViewById(R.id.checkIcon);
        nameIcon = (TextView) findViewById(R.id.nameIcon);

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        mailIconSignUp.setTypeface(font);
        keyIconSignUp.setTypeface(font);
        checkIcon.setTypeface(font);
        nameIcon.setTypeface(font);
    }
}
