package com.cybrilla.shashank.liborg;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashankm on 15/12/15.
 */
public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {
    private List<HomeView> allResultBooks;
    private Context mContext;

    public ResultsAdapter(List<HomeView> allBooks, Context context) {
        allResultBooks = new ArrayList<>(allBooks);
        mContext = context;
    }

    @Override
    public ResultsAdapter.ResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_layout, parent, false);

        return new ResultsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ResultsAdapter.ResultsViewHolder holder, int position) {
        HomeView hv = allResultBooks.get(position);
        holder.bookName.setText(hv.getBookName());
        holder.authorName.setText(hv.getAuthorName());
        Glide.with(mContext).load(hv.getThumbnail())
                .asBitmap().into(holder.bookImage);
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

        public ResultsViewHolder(View v) {
            super(v);
            bookName = (TextView) v.findViewById(R.id.bookName);
            authorName = (TextView) v.findViewById(R.id.authorName);
            cardView = (CardView) v.findViewById(R.id.card_view_one);
            bookImage = (ImageView) v.findViewById(R.id.bookImage);
        }
    }
}
