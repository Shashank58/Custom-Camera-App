package com.cybrilla.shashank.liborg;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shashankm on 17/11/15.
 */
public class ShelfAdapter extends RecyclerView.Adapter<ShelfAdapter.ShelfViewHolder> {
    private List<HomeView> allBooks;

    public ShelfAdapter(List<HomeView> allBooks){
        super();
        this.allBooks = allBooks;
    }

    @Override
    public ShelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_layout_shelf, parent, false);

        return new ShelfViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShelfViewHolder holder, int position) {
        HomeView hv = allBooks.get(position);
        holder.bookNameShelf.setText(hv.getBookName());
        holder.authorNameShelf.setText(hv.getAuthorName());
        holder.dueDate.setText(hv.getDueDate());
    }

    @Override
    public int getItemCount() {
        return allBooks.size();
    }

    public static class ShelfViewHolder extends RecyclerView.ViewHolder {
        protected TextView bookNameShelf, authorNameShelf, dueDate;

        public ShelfViewHolder(View v){
            super(v);
            bookNameShelf = (TextView) v.findViewById(R.id.bookNameShelf);
            authorNameShelf = (TextView) v.findViewById(R.id.authorNameShelf);
            dueDate = (TextView) v.findViewById(R.id.dueDate);
        }
    }
}