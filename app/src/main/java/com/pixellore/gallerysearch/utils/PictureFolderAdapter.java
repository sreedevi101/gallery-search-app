package com.pixellore.gallerysearch.utils;


import android.content.Context;
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
 * Adapter for the RecyclerView in the MainActivity that displays the folders containing images
 * */
public class PictureFolderAdapter extends RecyclerView.Adapter<PictureFolderAdapter.FolderViewHolder> {

    // state variable for the data - array list of ImageFolder objects
    private ArrayList<ImageFolder> mFolderList;

    private Context mContext;

    private OnItemClickListener mListenToClick;

    /**
     * Constructor for the Recyclerview adapter
     *
     * @param folderList - get the data to be displayed in the RecyclerView as input to the adapter
     * */
    public PictureFolderAdapter(Context context, ArrayList<ImageFolder> folderList, OnItemClickListener itemClickListener) {
        mFolderList = folderList;
        mContext = context;

        // allows clicks events to be caught
        mListenToClick = itemClickListener;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View folderCell = inflater.inflate(R.layout.picture_folder_item, parent, false);
        return new FolderViewHolder(folderCell);
    }

    /**
     * @param holder - instance of {@link FolderViewHolder} class
     * */
    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {

        // position of the data item (here ArrayList item) to be displayed in RecyclerView
        int currentPosition = holder.getAdapterPosition();

        // Get the current ImageFolder object as 'folder' from the 'position' argument
        ImageFolder folder = mFolderList.get(currentPosition);

        // Extract the folder name and number of pictures in the folder from the ImageFolder object
        String folderName = folder.getFolderName();
        String folderSize = ""+folder.getNumberOfPics();
        String latestPicName = folder.getLatestPic();

        /**
         * Set the folder name and folder size to the TextViews in {@link FolderViewHolder} holder
         *
         * 'folderSizeText' and 'folderNameText' are state variables of {@link FolderViewHolder} class
         * */
        holder.folderNameText.setText(folderName);
        holder.folderSizeText.setText(folderSize);

        // Set the latest pic using Glide
        Glide.with(mContext)
                .load(latestPicName)
                .apply(new RequestOptions().centerCrop())
                .into(holder.folderImage);


        /**
         * call the ViewHolder method 'bind' that has the click listener
         * */
        holder.bind(mFolderList.get(currentPosition), mListenToClick);

    }



    @Override
    public int getItemCount() {
        return mFolderList.size();
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {

        ImageView folderImage;
        TextView folderNameText;
        TextView folderSizeText;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderImage = itemView.findViewById(R.id.latestPic);
            folderNameText = itemView.findViewById(R.id.folderName);
            folderSizeText = itemView.findViewById(R.id.folderSize);
        }

        /**
         * This method is called from onBindViewHolder method
         *
         * setOnClickListener on the current item view in recyclerView
         *
         * Call the callback function 'onItemClick' in MainActivity when some items are clicked
         * */
        public void bind(final ImageFolder folderItem, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListenToClick.onItemClick(folderItem);
                }
            });
        }

    }

    /**
     * This is an interface and need to override the methods inside  ("onItemClick")
     * whenever an instance of this interface is created
     *
     * https://stackoverflow.com/questions/49969278/recyclerview-item-click-listener-the-right-way
     * */
    public interface OnItemClickListener {
        void onItemClick(ImageFolder imageFolderItem);
    }
}
