package com.liborgs.android.register;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.liborgs.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassActivity extends AppCompatActivity {
    private EditText recoveryEmail;
    private static final String KEY_EMAIL = "email";
    private String mail;
    private Button recoveryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_forgot_pass);
        recoveryEmail = (EditText) findViewById(R.id.recoveryEmail);
        recoveryButton = (Button) findViewById(R.id.recoveryButton);
        if (isNetworkAvailable())
            resetPassword();
        else {
            new AlertDialog.Builder(ForgotPassActivity.this)
                    .setTitle("Liborg")
                    .setMessage("Please connect to internet")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void resetPassword(){
        recoveryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                recoveryButton.setEnabled(false);
                mail = recoveryEmail.getText().toString().trim();
                String url = "https://liborgs-1139.appspot.com/users/reset-password?";
                RequestQueue queue = Volley.newRequestQueue(ForgotPassActivity.this);

                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jObject = new JSONObject(response);
                                    Boolean status = jObject.getBoolean("status");
                                    if (status) {
                                        new AlertDialog.Builder(ForgotPassActivity.this)
                                                .setTitle("Account Recovery")
                                                .setMessage("Please check your email for further instructions")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                }).show();
                                    } else {
                                        new AlertDialog.Builder(ForgotPassActivity.this)
                                                .setTitle("Account Recovery")
                                                .setMessage("Please enter valid email")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                }).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KEY_EMAIL, mail);
                        return params;
                    }
                };

                queue.add(request);
            }
        });

    }
}
