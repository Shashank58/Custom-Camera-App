package com.cybrilla.shashank.liborg;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by shashankm on 17/11/15.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder>{
    private List<HomeView> allBooks;
    private HomeView hv;
    private Context mContext;
    private FragmentActivity mActivity;

    public HomeAdapter(List<HomeView> allBooks, Context context, FragmentActivity activity){
        super();
        this.allBooks = allBooks;
        mContext = context;
        mActivity = activity;
    }


    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_layout, parent, false);

        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                String transitionCircle = mContext.getString(R.string.transition_name_circle);
                // Define the view that the animation will start from
                View viewStart = v.findViewById(R.id.card_view_one);
                int pos = (int) v.getTag();
                HomeView hv = allBooks.get(pos);
                intent.putExtra("allData", hv);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation
                        (mActivity,  new Pair<View, String>(viewStart, transitionCircle));

                ActivityCompat.startActivity(mActivity, intent, options.toBundle());
            }
        });

        return new HomeViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final HomeViewHolder holder, final int position) {
        holder.cardView.setTag(position);
        hv = allBooks.get(position);
        holder.bookName.setText(hv.getBookName());
        holder.authorName.setText(hv.getAuthorName());
        Glide.with(mContext).load(hv.getThumbnail())
        .asBitmap().into(holder.bookImage);
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
            cardView = (CardView) v.findViewById(R.id.card_view_one);
            bookImage = (ImageView) v.findViewById(R.id.bookImage);
        }
    }
}
