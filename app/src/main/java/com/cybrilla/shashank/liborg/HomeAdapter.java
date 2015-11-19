package com.cybrilla.shashank.liborg;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shashankm on 17/11/15.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private List<HomeView> allBooks;
    private HomeView hv;
    private Context mContext;

    public HomeAdapter(List<HomeView> allBooks, Context context){
        super();
        this.allBooks = allBooks;
        mContext = context;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_layout, parent, false);

        return new HomeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HomeViewHolder holder, final int position) {
        hv = allBooks.get(position);
        holder.bookName.setText(hv.getBookName());
        holder.authorName.setText(hv.getAuthorName());
    }

    @Override
    public int getItemCount() {
        return allBooks.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        protected TextView bookName;
        protected TextView authorName;
        public CardView cardView;
        protected View bookImage;

        public HomeViewHolder(View v){
            super(v);
            bookName = (TextView) v.findViewById(R.id.bookName);
            authorName = (TextView) v.findViewById(R.id.authorName);
            cardView = (CardView) v.findViewById(R.id.card_view);
            bookImage = v.findViewById(R.id.bookImage);
        }
    }

}
