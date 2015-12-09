package com.cybrilla.shashank.liborg;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Responsible for setting shared preferences along with getting auth token.
 */

public class SharedPreferencesHandler {
    private static final String KEY_AUTH = "auth_key" ;
    private static final String KEY_FNAME = "first name" ;
    private static final String KEY_LNAME = "last name" ;
    private static final String KEY_EMAIL = "email";
    private static final String KEY_REG_ID = "reg_id";
    private SharedPreferences pref;
    private Editor editor;
    private String auth_token, fName, lName, email, reg_id;

    private static final String LIB_KEY = "Liborg Auth";
    private static final int PRIVATE_MODE = 0;

    public void setSharedPreference(Context context, String fname, String lname
                                    , String mEmail, String authToken){
        this.auth_token = authToken;
        this.email = mEmail;
        this.fName = fname;
        this.lName = lname;

        pref = context.getSharedPreferences(LIB_KEY, PRIVATE_MODE);
        editor = pref.edit();

        editor.putString(KEY_AUTH, auth_token);
        editor.putString(KEY_FNAME, fName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_LNAME, lName);

        editor.commit();
    }

    public void setSharedPreference(Context context, String regId){
        this.reg_id = regId;

        pref = context.getSharedPreferences(LIB_KEY, PRIVATE_MODE);
        editor = pref.edit();
        editor.putString(KEY_REG_ID, reg_id);

        editor.commit();
    }

    public String getKeyAuth(Context context){
        SharedPreferences pref = context.getSharedPreferences(LIB_KEY, PRIVATE_MODE);
        return pref.getString(KEY_AUTH, null);
    }

    public String getRegId(Context context){
        SharedPreferences pref = context.getSharedPreferences(LIB_KEY, PRIVATE_MODE);
        return pref.getString(KEY_REG_ID, null);
    }
}
