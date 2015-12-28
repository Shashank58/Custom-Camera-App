package com.liborgs.android;

import android.app.Activity;
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
 * Created by shashankm on 28/12/15.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private List<HomeView> searchResults;
    private Activity mActivity;

    public SearchAdapter(List<HomeView> searchResults, Activity activity) {
        this.searchResults = new ArrayList<>(searchResults);
        this.mActivity = activity;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_layout, parent, false);

        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchAdapter.SearchViewHolder holder, int position) {
        holder.cardView.setTag(position);
        HomeView hv = searchResults.get(position);
        holder.bookName.setText(hv.getBookName());
        holder.authorName.setText(hv.getAuthorName());
        Glide.with(mActivity).load(hv.getThumbnail())
                .asBitmap().into(holder.bookImage);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        protected TextView bookName;
        protected TextView authorName;
        public CardView cardView;
        protected ImageView bookImage;

        public SearchViewHolder(View v) {
            super(v);
            bookName = (TextView) v.findViewById(R.id.bookName);
            authorName = (TextView) v.findViewById(R.id.authorName);
            cardView = (CardView) v.findViewById(R.id.card_view_one);
            bookImage = (ImageView) v.findViewById(R.id.bookImage);
        }
    }
}
