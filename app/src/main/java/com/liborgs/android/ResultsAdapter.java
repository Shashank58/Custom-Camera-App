package com.liborgs.android;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.liborgs.android.util.AppUtils;
import com.liborgs.android.util.Constants;
import com.liborgs.android.util.SharedPreferencesHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shashankm on 15/12/15.
 */
public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {
    private List<HomeView> allResultBooks;
    private Activity mActivity;

    public ResultsAdapter(List<HomeView> allBooks, Activity activity) {
        allResultBooks = new ArrayList<>(allBooks);
        mActivity = activity;
    }

    @Override
    public ResultsAdapter.ResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.result_card_layout, parent, false);

        return new ResultsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ResultsAdapter.ResultsViewHolder holder, int position) {
        HomeView hv = allResultBooks.get(position);
        final String bookTitle = hv.getBookName();
        holder.bookName.setText(bookTitle);
        holder.authorName.setText(hv.getAuthorName());
        Glide.with(mActivity).load(hv.getThumbnail())
                .asBitmap().into(holder.bookImage);
        holder.getIt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.getInstance().showProgressDialog(mActivity, "Issuing");
                StringRequest jObject = new StringRequest(Request.Method.POST
                        , Constants.USERS_ISSUE_BOOK,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                AppUtils.getInstance().dismissProgressDialog();
                                try {
                                    Log.e("Library activity", "Response: " + response);
                                    JSONObject res = new JSONObject(response);
                                    String message = res.getString("message");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                    builder.setTitle("Book Issue")
                                            .setMessage(message)
                                            .setPositiveButton(android.R.string.yes,
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            mActivity.finish();
                                                        }
                                                    })
                                            .show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppUtils.getInstance().dismissProgressDialog();
                    }
                })
                {
                    @Override
                    public Map<String, String> getHeaders(){
                        Map<String, String> params = new HashMap<>();
                        SharedPreferencesHandler sh = new SharedPreferencesHandler();
                        String authToken = sh.getKeyAuth(mActivity);
                        params.put("auth-token", authToken);
                        return params;
                    }

                    @Override
                    public Map<String, String> getParams(){
                        Map<String, String> params = new HashMap<>();
                        params.put("isbn", "");
                        params.put("title", bookTitle);
                        return params;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(mActivity);
                queue.add(jObject);
            }
        });
    }


    @Override
    public int getItemCount() {
        return allResultBooks.size();
    }

    public static class ResultsViewHolder extends RecyclerView.ViewHolder {
        protected TextView bookName;
        protected TextView authorName;
        public CardView cardView;
        protected ImageView bookImage;
        protected Button getIt;

        public ResultsViewHolder(View v) {
            super(v);
            bookName = (TextView) v.findViewById(R.id.bookNameResult);
            authorName = (TextView) v.findViewById(R.id.authorNameResult);
            cardView = (CardView) v.findViewById(R.id.card_view_result);
            bookImage = (ImageView) v.findViewById(R.id.bookImageResult);
            getIt = (Button) v.findViewById(R.id.getIt);
        }
    }
}
