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
        Glide.with(mContext).load("http://ia.media-imdb.com/images/M/MV5BNTg2OTY2ODg5OF5BMl5BanBnXkFtZTcwODM5MTYxOA@@._V1_SX214_AL_.jpg")
        .crossFade().into(holder.bookImage);
    }

    @Override
    public int getItemCount() {
        return allBooks.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        protected TextView bookName;
        protected TextView authorName;
        public CardView cardView;
        protected ImageView bookImage;

        public HomeViewHolder(View v){
            super(v);
            bookName = (TextView) v.findViewById(R.id.bookName);
            authorName = (TextView) v.findViewById(R.id.authorName);
            cardView = (CardView) v.findViewById(R.id.card_view);
            bookImage = (ImageView) v.findViewById(R.id.bookImage);
        }
    }

}
