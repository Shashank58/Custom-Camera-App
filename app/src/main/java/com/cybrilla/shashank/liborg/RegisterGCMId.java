package com.cybrilla.shashank.liborg;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.iid.InstanceID;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shashankm on 09/12/15.
 */
public class RegisterGCMId extends Application {
    private static final String TAG = "RegisterGCMId";
    private static final String GOOGLE_PROJECT_ID = "926498080108";
    private static RegisterGCMId singleton;
    SharedPreferencesHandler s;

    public RegisterGCMId getInstance(){
        return singleton;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        singleton = this;
        SharedPreferencesHandler sh = new SharedPreferencesHandler();
        if(sh.getRegId(this) == null) {
            Log.e("Register", "Is this called???");
            registerGCM();
        }
    }

    private void registerGCM(){
       new AsyncTask<Void, Void, String>() {
           @Override
           protected String doInBackground(Void... params) {
               String token = "";
               try {
                   InstanceID instanceID = InstanceID.getInstance(RegisterGCMId.this);
                   token = instanceID.getToken(GOOGLE_PROJECT_ID, null);
                   Log.e(TAG, "GCM Registration Token: " + token);
               } catch(Exception e) {
                   Log.e(TAG, "Failed to complete token refresh", e);
               }

               return token;
           }

           @Override
           protected void onPostExecute(String token) {
               if(!"".equals(token)) {
                   s = new SharedPreferencesHandler();
                   s.setSharedPreference(RegisterGCMId.this, token);
                   sendToServer();
               }
           }
       }.execute();
    }

    public void sendToServer(){
        SharedPreferencesHandler sh = new SharedPreferencesHandler();
        final String loggedIn = sh.getKeyAuth(this);
        final String registered = sh.getRegId(this);

        if((loggedIn != null) && (registered != null)){
            String url = "  https://liborgs-1139.appspot.com/device/register";
            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest req = new StringRequest(Method.POST, url
                    , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "Response from server: "+response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("auth-token", loggedIn);
                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("source", "android");
                    params.put("reg_id", registered);
                    return params;
                }
            };

            queue.add(req);
        }
    }
}
