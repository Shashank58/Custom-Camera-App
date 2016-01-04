package com.liborgs.android.datahelpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.liborgs.android.R;
import com.liborgs.android.android.LibraryActivity;
import com.liborgs.android.util.Constants;

/**
 * Created by shashankm on 09/12/15.
 */
public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.e(TAG, "Bundle: "+data.toString());
        String message = data.getString("message");
        String type = data.getString("type");
        if (Constants.NOTIFICATION_TYPE_NEW_BOOK
                    .equals(type)) {
            sendNotification(message, 0);
        } else if (Constants.NOTIFICATION_TYPE_APPROVED.equals(type)){
            sendNotification(message, 1);
        }
    }

    private void sendNotification(String message, int selectedTab) {
        Intent intent = new Intent(this, LibraryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("SelectedTab", selectedTab);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Log.e(TAG, "Message: "+message);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }
        notificationBuilder.setContentTitle("Liborg")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
