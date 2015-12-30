package com.liborgs.android.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.liborgs.android.R;
import com.liborgs.android.RegisterGCMId;
import com.liborgs.android.activities.LibraryActivity;
import com.liborgs.android.util.AppUtils;
import com.liborgs.android.util.Constants;
import com.liborgs.android.util.SharedPreferencesHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {
    private EditText email, password;
    private String mEmail, mPassword;

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
        Button submit = (Button) findViewById(R.id.submit);

        if(mEmail.equals("") || mPassword.equals("")){
            Toast.makeText(getApplicationContext(), "Email or Password empty",
                    Toast.LENGTH_LONG).show();
        } else{
            if (AppUtils.getInstance().isNetworkAvailable(this)) {
                submit.setEnabled(false);
                logIntoAccount();
            }
            else {
                AppUtils.getInstance().alertMessage(this, Constants.LIBORGS,
                        Constants.INTERNET_CONN);
            }
        }
    }


    public void logIntoAccount(){
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST
                , Constants.USER_LOGIN_URL,
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

                                 ((RegisterGCMId) getApplication()).sendToServer();

                                Intent intent = new Intent(getApplication(), LibraryActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String message = jObj.getString("message");
                                AppUtils.getInstance().alertMessage(LogInActivity.this,
                                        "Log in", message);
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
                params.put(Constants.KEY_EMAIL, mEmail);
                params.put(Constants.KEY_PASSWORD, mPassword);
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
