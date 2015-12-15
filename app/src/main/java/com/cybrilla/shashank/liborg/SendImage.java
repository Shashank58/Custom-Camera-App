package com.cybrilla.shashank.liborg;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import cz.msebera.android.httpclient.Header;

/**
 * Created by shashankm on 15/12/15.
 */
public class SendImage {
    private Bitmap bookImage;
    private Context mContext;
    private String fileSrc;

    public SendImage(Bitmap bookImage, Context c) {
        this.bookImage = bookImage;
        mContext = c;
    }

    public void sendImage() {
        String url = "http://107.167.190.253/image_search/similar";

        Cursor cursor = mContext.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{Media.DATA, Media.DATE_ADDED, MediaStore.Images.ImageColumns.ORIENTATION}, Media.DATE_ADDED, null, "date_added ASC");
        if (cursor != null && cursor.moveToLast()) {
            Uri fileURI = Uri.parse(cursor.getString(cursor.getColumnIndex(Media.DATA)));
            fileSrc = fileURI.toString();
            cursor.close();
        }

        // Bitmap compressedImage = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        AsyncHttpClient client = new AsyncHttpClient();
        SharedPreferencesHandler s = new SharedPreferencesHandler();
        String auth_token = s.getKeyAuth(mContext);
        Log.e("Send Image", "Auth token: " + auth_token);
        client.addHeader("auth-token", auth_token);
        RequestParams params = new RequestParams();
        try {
            params.put("pic", storeImage());
        } catch (FileNotFoundException e) {
            Log.d("MyApp", "File not found!!!" + fileSrc);
        }

        client.post(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("Send Image", "Response: " + response.toString());
                Intent intent = new Intent(mContext, ResultsActivity.class);
                intent.putExtra("response", response.toString());
                mContext.startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.e("Send Image", "Under progress...");
            }
        });
    }

    private File storeImage() {
        String filename = "bookImage";
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(extStorageDirectory, filename + ".jpg");
        try {
            outStream = new FileOutputStream(file);
            bookImage.compress(CompressFormat.JPEG, 80, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
