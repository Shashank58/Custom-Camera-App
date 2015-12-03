package com.cybrilla.shashank.liborg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.FileOutputStream;
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
        this.allBooks = allBooks;
        mContext = context;
        mActivity = activity;
    }

    public HomeAdapter(){

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
                Log.e("Library activity", "Size of books after: "+allBooks.size());
                Log.e("Library activity", "Pos: " + pos);
                HomeView hv = allBooks.get(pos);
                intent.putExtra("allData", hv);
                ImageView bookImage = (ImageView) viewStart.findViewById(R.id.bookImage);
                Bitmap thumbnail = ((BitmapDrawable)bookImage.getDrawable()).getBitmap();


                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation
                        (mActivity,  new Pair<View, String>(viewStart, transitionCircle));

                try {
                    //Write file
                    String filename = "bitmap.png";
                    FileOutputStream stream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    //Cleanup
                    stream.close();
                    // thumbnail.recycle();
                    intent.putExtra("thumbnail", filename);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Start the Intent
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

    public void setModels(List<HomeView> models) {
        allBooks = new ArrayList<>(models);
    }

    public HomeView removeItem(int position) {
        final HomeView model = allBooks.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, HomeView model) {
        allBooks.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final HomeView model = allBooks.remove(fromPosition);
        allBooks.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<HomeView> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<HomeView> newModels) {
        for (int i = allBooks.size() - 1; i >= 0; i--) {
            final HomeView model = allBooks.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    public List<HomeView> sendBookList(){
        return allBooks;
    }

    private void applyAndAnimateAdditions(List<HomeView> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final HomeView model = newModels.get(i);
            if (!allBooks.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<HomeView> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final HomeView model = newModels.get(toPosition);
            final int fromPosition = allBooks.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
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
