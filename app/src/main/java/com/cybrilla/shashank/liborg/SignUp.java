package com.cybrilla.shashank.liborg;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private TextView fnameIcon, checkIcon, keyIconSignUp, mailIconSignUp,lnameIcon;
    private String mFname, mLname, mEmail, mPassword, mConfirm;
    private EditText fname, lname, email, password, confirm;
    public static final String KEY_FNAME = "firstname";
    public static final String KEY_LNAME = "lastname";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        findViews();

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        mailIconSignUp.setTypeface(font);
        keyIconSignUp.setTypeface(font);
        checkIcon.setTypeface(font);
        fnameIcon.setTypeface(font);
        lnameIcon.setTypeface(font);
    }

    private void findViews(){
        mailIconSignUp = (TextView) findViewById(R.id.mailIconSignUp);
        keyIconSignUp = (TextView) findViewById(R.id.keyIconSignUp);
        checkIcon = (TextView) findViewById(R.id.checkIcon);
        fnameIcon = (TextView) findViewById(R.id.fnameIcon);
        lnameIcon = (TextView) findViewById(R.id.lnameIcon);

        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirm = (EditText) findViewById(R.id.confirmPassword);
    }

    public void loginScreen(View v){
        mPassword = password.getText().toString();
        mConfirm = confirm.getText().toString();
        mFname = fname.getText().toString().trim();
        mLname = lname.getText().toString().trim();
        if(mConfirm.equals(mPassword)) {
            registerUser();
        } else {
            new AlertDialog.Builder(SignUp.this)
                    .setTitle("Sign up")
                    .setMessage("Passwords not matching")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }
    }

    public void registerUser(){
        mEmail = email.getText().toString().trim();
        RequestQueue queue = Volley.newRequestQueue(this);

        String REGISTER_URL = "https://liborgs-1139.appspot.com/users/register?";

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String message = jObj.getString("message");
                            Boolean status = jObj.getBoolean("status");
                            if(status){
                                new AlertDialog.Builder(SignUp.this)
                                        .setTitle("Sign up")
                                        .setMessage(message)
                                        .setPositiveButton(android.R.string.yes
                                                , new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).show();
                                finish();
                            } else {
                                new AlertDialog.Builder(SignUp.this)
                                        .setTitle("Sign up")
                                        .setMessage(message)
                                        .setPositiveButton(android.R.string.yes
                                                , new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put(KEY_FNAME, mFname);
                params.put(KEY_LNAME, mLname);
                params.put(KEY_EMAIL, mEmail);
                params.put(KEY_PASSWORD, mPassword);
                return params;
            }
        };

        queue.add(jsonObjectRequest);
    }
}