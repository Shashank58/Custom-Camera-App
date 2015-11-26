package com.cybrilla.shashank.liborg;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
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

public class ForgotPassActivity extends AppCompatActivity {
    private EditText recoveryEmail;
    private static final String KEY_EMAIL = "email";
    private String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        recoveryEmail = (EditText) findViewById(R.id.recoveryEmail);
    }

    public void resetPassword(View v){
        mail = recoveryEmail.getText().toString().trim();
        String url = "https://liborgs-1139.appspot.com/users/reset-password?";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObject = new JSONObject(response);
                            Boolean status = jObject.getBoolean("status");
                            if(status){
                                new AlertDialog.Builder(ForgotPassActivity.this)
                                        .setTitle("Account Recovery")
                                        .setMessage("Please check your email for further instructions")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        }).show();
                            } else{
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_EMAIL, mail);
                return params;
            }
        };

        queue.add(request);
    }
}
