package com.liborgs.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
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
        this.allBooks = new ArrayList<>(allBooks);
        mContext = context;
        mActivity = activity;
    }


    @TargetApi(VERSION_CODES.M)
    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_layout, parent, false);

        final String transitionCircle = mActivity.getString(R.string.transition_name_circle);
        final String transitionArrow = mActivity.getString(R.string.transition_name_author);

        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imgr = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Intent intent = new Intent(mActivity, DetailActivity.class);
                // Define the view that the animation will start from
                View viewStart = itemView.findViewById(R.id.card_view_one);
                View authorStart = v.findViewById(R.id.authorName);

                viewStart.setTransitionName(transitionCircle);
                authorStart.setTransitionName(transitionArrow);

                int pos = (int) v.getTag();
                HomeView hv = allBooks.get(pos);
                intent.putExtra("allData", hv);
                intent.addFlags(Window.FEATURE_ACTIVITY_TRANSITIONS);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation
                        (mActivity,  new Pair<View, String>(viewStart, transitionCircle)
                                , new Pair<View, String>(authorStart, transitionArrow));

                mActivity.startActivity(intent, options.toBundle());
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
