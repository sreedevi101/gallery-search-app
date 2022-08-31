package com.pixellore.gallerysearch.utils;

import android.content.Context;
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
 *
 * A RecyclerView Adapter class that's populates a RecyclerView with images from
 * a folder on the device external storage
 */
public class ImageDisplayAdapter extends RecyclerView.Adapter<ImageDisplayAdapter.PictureViewHolder> {

    private ArrayList<Image> mImageList;
    private Context mContext;
    private OnItemClickListener mClickListener;

    /**
     *
     * @param imageList ArrayList of pictureFacer objects
     * @param context The Activities Context
     *
     */
    public ImageDisplayAdapter(ArrayList<Image> imageList, Context context, OnItemClickListener listener){
        this.mImageList = imageList;
        this.mContext = context;
        this.mClickListener = listener;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View imageCell = inflater.inflate(R.layout.picture_item, parent, false);
        return new PictureViewHolder(imageCell);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {

        // Get current position from adapter
        int currentPosition = holder.getAdapterPosition();

        // Get the current image in recycler view from arraylist of images
        Image image = mImageList.get(currentPosition);

        // Get image path from 'image' attributes
        String imagePath = image.getImagePath();

        try {
            // Set the picture in the ImageView in holder using Glide
            Glide.with(mContext)
                    .load(imagePath)
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.imageView);


        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * Set the name of the View to identify views in transition. Use unique name
         * */
        //setTransitionName(String.valueOf(currentPosition) + "_image");


        /**
         * Call the 'bind' method {@link PictureViewHolder} to set the click listener to
         * the current image item in the recycler view
         * */
        holder.bind(image, currentPosition, mClickListener);
    }


    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
        }

        public void bind(Image image, int position, OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(image, position);
                }
            });
        }
    }


    public interface OnItemClickListener {
        void onItemClick(Image imageItem, int imagePosition);
    }


}
