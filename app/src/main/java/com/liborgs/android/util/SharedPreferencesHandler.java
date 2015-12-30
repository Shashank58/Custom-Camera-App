package com.liborgs.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Responsible for setting shared preferences along with getting auth token.
 */

public class SharedPreferencesHandler {
    private SharedPreferences pref;
    private Editor editor;
    private String auth_token, fName, lName, email, reg_id;

    public void setSharedPreference(Context context, String fname, String lname
                                    , String mEmail, String authToken){
        this.auth_token = authToken;
        this.email = mEmail;
        this.fName = fname;
        this.lName = lname;

        pref = context.getSharedPreferences(Constants.LIB_KEY, Constants.PRIVATE_MODE);
        editor = pref.edit();

        editor.putString(Constants.KEY_AUTH, auth_token);
        editor.putString(Constants.KEY_FNAME, fName);
        editor.putString(Constants.KEY_EMAIL, email);
        editor.putString(Constants.KEY_LNAME, lName);

        editor.commit();
    }

    public void setSharedPreference(Context context, String regId){
        this.reg_id = regId;

        pref = context.getSharedPreferences(Constants.LIB_KEY, Constants.PRIVATE_MODE);
        editor = pref.edit();
        editor.putString(Constants.KEY_REG_ID, reg_id);

        editor.commit();
    }

    public String getKeyAuth(Context context){
        SharedPreferences pref = context.getSharedPreferences(Constants.LIB_KEY, Constants.PRIVATE_MODE);
        return pref.getString(Constants.KEY_AUTH, null);
    }

    public String getRegId(Context context){
        SharedPreferences pref = context.getSharedPreferences(Constants.LIB_KEY, Constants.PRIVATE_MODE);
        return pref.getString(Constants.KEY_REG_ID, null);
    }

    public void deleteSharedPreference(Context context){
        SharedPreferences pref = context.getSharedPreferences(Constants.LIB_KEY, Constants.PRIVATE_MODE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        Log.e("SHared pref handler", "Auth token: " + getKeyAuth(context));
        Log.e("SHared pref handler", "GCM token: "+getRegId(context));
    }
}
