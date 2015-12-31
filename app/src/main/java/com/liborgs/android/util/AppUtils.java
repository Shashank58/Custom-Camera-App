package com.liborgs.android.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

/**
 * Created by shashankm on 30/12/15.
 */
public class AppUtils {
    private static AppUtils instance;
    private ProgressDialog dialog;

    public static AppUtils getInstance(){
        if (instance == null){
            instance = new AppUtils();
        }
        return instance;
    }

    public boolean isNetworkAvailable(Activity activity){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void alertMessage(Activity activity, String title, String message){
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
    }

    public void showProgressDialog(Activity activity, String message){
        dialog = new ProgressDialog(activity);
        dialog.setMessage(message);
        dialog.setInverseBackgroundForced(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismissProgressDialog(){
        if (dialog != null){
            dialog.dismiss();
        }
    }
}
