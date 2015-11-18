package com.cybrilla.shashank.liborg;

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

    public HomeAdapter(List<HomeView> allBooks){
        super();
        this.allBooks = allBooks;
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookName = allBooks.get(position).getBookName();
                String authorName = allBooks.get(position).getAuthorName();

            }
        });
    }

    @Override
    public int getItemCount() {
        return allBooks.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        protected TextView bookName;
        protected TextView authorName;
        protected CardView cardView;

        public HomeViewHolder(View v){
            super(v);
            bookName = (TextView) v.findViewById(R.id.bookName);
            authorName = (TextView) v.findViewById(R.id.authorName);
            cardView = (CardView) v.findViewById(R.id.card_view);
        }
    }
}
