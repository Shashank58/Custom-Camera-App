package com.liborgs.android.android;

import android.os.Bundle;
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
import com.liborgs.android.util.AppUtils;
import com.liborgs.android.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassActivity extends AppCompatActivity {
    private EditText recoveryEmail;
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
        if (AppUtils.getInstance().isNetworkAvailable(this))
            resetPassword();
        else {
            AppUtils.getInstance().alertMessage(this, Constants.LIBORGS, Constants.INTERNET_CONN);
        }
    }

    public void resetPassword(){
        recoveryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                recoveryButton.setEnabled(false);
                mail = recoveryEmail.getText().toString().trim();
                RequestQueue queue = Volley.newRequestQueue(ForgotPassActivity.this);

                StringRequest request = new StringRequest(Request.Method.POST,
                        Constants.PASSWORD_RESET_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jObject = new JSONObject(response);
                                    Boolean status = jObject.getBoolean("status");
                                    if (status) {
                                        AppUtils.getInstance().alertMessage(ForgotPassActivity.this,
                                                "Account Recovery,", "Please check your email for further instructions");
                                    } else {
                                        AppUtils.getInstance().alertMessage(ForgotPassActivity.this,
                                                "Account Recovery", "Please enter correct email");
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
                        params.put(Constants.KEY_EMAIL, mail);
                        return params;
                    }
                };

                queue.add(request);
            }
        });

    }
}
