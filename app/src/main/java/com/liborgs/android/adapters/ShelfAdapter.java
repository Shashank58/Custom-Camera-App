package com.liborgs.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.liborgs.android.datamodle.HomeView;
import com.liborgs.android.R;
import com.liborgs.android.util.AppUtils;
import com.liborgs.android.util.Constants;
import com.liborgs.android.util.SharedPreferencesHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for myShelf fragment. Sets data of issued books and archives.
 * Also handles return of book.
 */

public class ShelfAdapter extends RecyclerView.Adapter<ShelfAdapter.ShelfViewHolder> {
    private List<HomeView> allBooks;
    private Context mContext;
    private Activity mActivity;

    public ShelfAdapter(List<HomeView> allBooks, Context context, Activity activity){
        super();
        this.allBooks = allBooks;
        mContext = context;
        mActivity = activity;
    }

    @Override
    public ShelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_layout_shelf, parent, false);

        Button returnBook = (Button) itemView.findViewById(R.id.returnBook);
        returnBook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                final HomeView hv = allBooks.get(pos);
                RequestQueue queue = Volley.newRequestQueue(mContext);
                StringRequest sRequest = new StringRequest(Method.POST,
                        Constants.USER_BOOK_RETURN,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                JSONObject res;
                                String message = "";
                                try {
                                    res = new JSONObject(response);
                                    message = res.getString("message");
                                    itemView.findViewById(R.id.returnBook).setEnabled(false);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                AppUtils.getInstance().alertMessage(mActivity,
                                        "Book Return", message);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("isbn", hv.getIsbn());
                        params.put("title", hv.getBookName());
                        params.put("issue_date", String.valueOf(hv.getIssueDate()));
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<>();
                        SharedPreferencesHandler s = new SharedPreferencesHandler();
                        params.put("auth-token", s.getKeyAuth(mContext));
                        return params;
                    }
                };

                queue.add(sRequest);
            }
        });

        return new ShelfViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShelfViewHolder holder, int position) {
        holder.cardViewShelf.setTag(position);
        HomeView hv = allBooks.get(position);
        if(hv.getReturnDate() == null) {
            holder.returnBook.setTag(position);
            holder.dueDate.setText(hv.getDueDate());
            holder.borrowedDate.setText(hv.getBorrowedDate());
            holder.returnBook.setVisibility(View.VISIBLE);
        } else {
            int bookStatus = hv.getReturnStatus();
            String status;
            if(bookStatus == 0){
                status = "Pending";
                holder.returnBook.setTag(position);
                holder.returnBook.setEnabled(true);
            } else {
                status = "Returned";
                holder.returnBook.setVisibility(View.GONE);
            }
            holder.dueDateText.setText("Returned date");
            holder.dueDate.setText(hv.getReturnDate());
            holder.bookStatus.setText("Status:");
            holder.borrowedDate.setText(status);
        }
        holder.bookNameShelf.setText(hv.getBookName());
        holder.authorNameShelf.setText(hv.getAuthorName());
        Glide.with(mContext).load(hv.getThumbnail())
                .asBitmap().into(holder.bookImage);
    }

    @Override
    public int getItemCount() {
        return allBooks.size();
    }

    public static class ShelfViewHolder extends RecyclerView.ViewHolder {
        protected TextView bookNameShelf, authorNameShelf, dueDate, borrowedDate, dueDateText, bookStatus;
        protected ImageView bookImage;
        protected CardView cardViewShelf;
        protected Button returnBook;

        public ShelfViewHolder(View v){
            super(v);
            bookNameShelf = (TextView) v.findViewById(R.id.bookNameShelf);
            authorNameShelf = (TextView) v.findViewById(R.id.authorNameShelf);
            dueDate = (TextView) v.findViewById(R.id.dueDate);
            bookImage = (ImageView) v.findViewById(R.id.shelf_book_image);
            borrowedDate = (TextView) v.findViewById(R.id.borrowedDate);
            cardViewShelf = (CardView) v.findViewById(R.id.card_view_shelf);
            returnBook = (Button) v.findViewById(R.id.returnBook);
            dueDateText = (TextView) v.findViewById(R.id.dueDateText);
            bookStatus = (TextView) v.findViewById(R.id.bookStatusText);
        }
    }
}