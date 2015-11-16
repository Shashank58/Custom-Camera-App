package com.cybrilla.shashank.liborg;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView mailIconSignUp = (TextView) findViewById(R.id.mailIconSignUp);
        TextView keyIconSignUp = (TextView) findViewById(R.id.keyIconSignUp);
        TextView checkIcon = (TextView) findViewById(R.id.checkIcon);

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        mailIconSignUp.setTypeface(font);
        keyIconSignUp.setTypeface(font);
        checkIcon.setTypeface(font);
    }
}
