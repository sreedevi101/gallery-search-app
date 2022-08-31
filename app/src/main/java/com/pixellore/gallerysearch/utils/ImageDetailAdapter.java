package com.pixellore.gallerysearch.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pixellore.gallerysearch.R;

import java.util.ArrayList;

/**
 * This is the adapter for ViewPager2 in {@link com.pixellore.gallerysearch.ImageDetailActivity}
 * */
public class ImageDetailAdapter extends RecyclerView.Adapter<ImageDetailAdapter.ImageDetailViewHolder> {

    private ArrayList<Image> mImageList;
    private Context mContext;
    private OnItemClickListener mClickListener;

    public ImageDetailAdapter(ArrayList<Image> imageList, Context context, OnItemClickListener listener){
        mImageList = imageList;
        mContext = context;
        mClickListener = listener;
    }

    @NonNull
    @Override
    public ImageDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.image_detail_item, parent, false);
        return new ImageDetailViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ImageDetailViewHolder holder, int position) {

        // Get current position from adapter
        int currentPosition = holder.getAdapterPosition();

        // Get the current item in recycler view based on position argument
        Image currentImage = mImageList.get(currentPosition);

        try {
            Glide.with(mContext)
                    .load(currentImage.getImagePath())
                    .apply(new RequestOptions().centerInside())
                    .into(holder.imageView);

            holder.imageNameView.setText(currentImage.getImageName());
        } catch (Exception e) {
            e.printStackTrace();
        }



        // set click listener for the view pager
        holder.bind(currentImage, mClickListener);
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public class ImageDetailViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView imageNameView;

        public ImageDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItem);
            imageNameView = itemView.findViewById(R.id.imageNameDetail);
        }

        public void bind(Image image, OnItemClickListener listener) {
            // set the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(image);
                }
            });
        }
    }


    public interface OnItemClickListener {
        void onItemClick(Image imageItem);
    }
}
