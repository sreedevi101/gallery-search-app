package com.pixellore.gallerysearch.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pixellore.gallerysearch.R;

import java.util.ArrayList;

/**
 * RecyclerView adapter for the recycler view at the bottom of the {@link com.pixellore.gallerysearch.ImageDetailActivity}
 * that displays the current image displayed as well as the previous and next images horizontally
 * */
public class ImageIndicatorAdapter extends RecyclerView.Adapter<ImageIndicatorAdapter.ImageIndicatorViewHolder> {

    private ArrayList<Image> mImages;
    private Context mContext;

    public ImageIndicatorAdapter(Context context, ArrayList<Image> images){
        this.mImages = images;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ImageIndicatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.activity_image_detail, parent, false);
        return new ImageIndicatorViewHolder(cell);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageIndicatorViewHolder holder, int position) {
        Image image = mImages.get(position);

        Log.v("Recycler", "position " + position);

        Glide.with(mContext)
                .load(image.getImagePath())
                .apply(new RequestOptions().centerCrop())
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public static class ImageIndicatorViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;

        public ImageIndicatorViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.indicatorImage);
        }
    }
}
