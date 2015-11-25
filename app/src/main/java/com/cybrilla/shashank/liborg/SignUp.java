package com.cybrilla.shashank.liborg;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class SignUp extends AppCompatActivity {
    private TextView fnameIcon, checkIcon, keyIconSignUp, mailIconSignUp,lnameIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mailIconSignUp = (TextView) findViewById(R.id.mailIconSignUp);
        keyIconSignUp = (TextView) findViewById(R.id.keyIconSignUp);
        checkIcon = (TextView) findViewById(R.id.checkIcon);
        fnameIcon = (TextView) findViewById(R.id.fnameIcon);
        lnameIcon = (TextView) findViewById(R.id.lnameIcon);

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        mailIconSignUp.setTypeface(font);
        keyIconSignUp.setTypeface(font);
        checkIcon.setTypeface(font);
        fnameIcon.setTypeface(font);
        lnameIcon.setTypeface(font);
    }

    public void loginScreen(View v){
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
