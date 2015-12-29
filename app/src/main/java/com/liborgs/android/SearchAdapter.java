package com.liborgs.android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashankm on 28/12/15.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> searchResults;
    private Activity mActivity;

    public SearchAdapter(List<Object> searchResults, Activity activity) {
        this.searchResults = new ArrayList<>(searchResults);
        this.mActivity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        if (searchResults.get(position) instanceof HomeView){
            return 0;
        } else if (searchResults.get(position) instanceof String) {
            return 1;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                final View itemView = inflater.inflate(R.layout.card_layout, parent, false);
                final String transitionCircle = mActivity.getString(R.string.transition_name_circle);

                itemView.setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                       startNewActivity(v, transitionCircle);
                                                }
                                            }
                );
                viewHolder = new SearchViewHolder(itemView);
            break;

            case 1:
                View item = inflater.inflate(R.layout.web_search_layout, parent, false);
                viewHolder = new WebViewHolder(item);
            break;

            default:
                viewHolder = null;
                break;
        }
        return viewHolder;
    }

    private void startNewActivity(View v, String transitionCircle){
        Intent intent = new Intent(mActivity, DetailActivity.class);
        View imageStart = v.findViewById(R.id.bookImage);

        int pos = (int) v.getTag();
        HomeView hv = (HomeView) searchResults.get(pos);
        intent.putExtra("allData", hv);

        mActivity.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                SearchViewHolder viewHolder = (SearchViewHolder) holder;
                viewHolder.cardView.setTag(position);
                HomeView hv = (HomeView) searchResults.get(position);
                viewHolder.bookName.setText(hv.getBookName());
                viewHolder.authorName.setText(hv.getAuthorName());
                Glide.with(mActivity).load(hv.getThumbnail())
                        .asBitmap().into(viewHolder.bookImage);
                break;

            case 1:
                mActivity.findViewById(R.id.searchResults);
                WebViewHolder webHolder = (WebViewHolder) holder;
                webHolder.sectionDivider.setText(searchResults.get(position).toString());
                break;
        }
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

    public static class WebViewHolder extends RecyclerView.ViewHolder {
        protected TextView sectionDivider;

        public WebViewHolder(View itemView) {
            super(itemView);
            sectionDivider = (TextView) itemView.findViewById(R.id.sectionDivider);
        }
    }
}
