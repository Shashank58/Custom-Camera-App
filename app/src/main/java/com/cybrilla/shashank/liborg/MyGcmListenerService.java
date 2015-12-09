package com.cybrilla.shashank.liborg;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by shashankm on 09/12/15.
 */
public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.e(TAG, "Bundle: "+data.toString());
        Log.e(TAG, "From: "+from);
        String message = data.getString("message");
        Log.e(TAG, "Message: "+message);
    }
}
