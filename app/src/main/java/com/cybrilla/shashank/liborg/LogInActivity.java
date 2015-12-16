package com.cybrilla.shashank.liborg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class LogInActivity extends AppCompatActivity {
    private EditText email, password;
    private TextView forgotPassword;
    private String mEmail, mPassword;
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferencesHandler sh = new SharedPreferencesHandler();
        String loggedIn = sh.getKeyAuth(this);
        String token = sh.getRegId(this);
        Log.e("Login Activity", "Logged In: "+loggedIn);
        Log.e("Login Activity", "Token value: " + token);
        if(loggedIn != null) {
            Intent intent = new Intent(this, LibraryActivity.class);
            startActivity(intent);
            finish();
        }else {
            setContentView(R.layout.activity_log_in);
            if(getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            TextView mailIcon = (TextView) findViewById(R.id.mailIcon);
            TextView keyIcon = (TextView) findViewById(R.id.keyIcon);

            Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
            mailIcon.setTypeface(font);
            keyIcon.setTypeface(font);
        }
    }


    public void forgotPassword(View v){
        Intent intent = new Intent(this, ForgotPassActivity.class);
        startActivity(intent);
    }

    public void login(View v){
        email = (EditText) findViewById(R.id.loginEmail);
        password = (EditText) findViewById(R.id.passwordLogin);

        mEmail = email.getText().toString().trim();
        mPassword = password.getText().toString();

        if(mEmail.equals("") || mPassword.equals("")){
            Toast.makeText(getApplicationContext(), "Email or Password empty",
                    Toast.LENGTH_LONG).show();
        } else{
            if (isNetworkAvailable())
                logIntoAccount();
            else {
                showDialog("Liborg", "Please connect to internet");
            }
        }
    }

    private void showDialog(String title, String message){
        new AlertDialog.Builder(LogInActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void logIntoAccount(){
        RequestQueue queue = Volley.newRequestQueue(this);

        String REGISTER_URL = "https://liborgs-1139.appspot.com/users/login?";

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("Sign Up", "Response is: "+response);
                            JSONObject jObj = new JSONObject(response);
                            Boolean status = jObj.getBoolean("status");
                            if(status){
                                String fname = jObj.getString("firstname");
                                String lname = jObj.getString("lastname");
                                String auth_token = jObj.getString("auth_token");

                                SharedPreferencesHandler s = new SharedPreferencesHandler();
                                s.setSharedPreference(getApplicationContext(), fname, lname
                                        , mEmail, auth_token);

                                RegisterGCMId app = (RegisterGCMId) getApplication();
                                app.sendToServer();

                                Intent intent = new Intent(getApplication(), LibraryActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String message = jObj.getString("message");

                                showDialog("Log In", message);
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
                params.put(KEY_EMAIL, mEmail);
                params.put(KEY_PASSWORD, mPassword);
                return params;
            }
        };

        queue.add(jsonObjectRequest);
    }

    public void signUp(View v){
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

}
