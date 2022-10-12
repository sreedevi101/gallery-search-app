package com.pixellore.gallerysearch.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixellore.gallerysearch.ImageTagEditActivity;
import com.pixellore.gallerysearch.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ImageTagEditAdapter extends RecyclerView.Adapter<ImageTagEditAdapter.ImageTagEditViewHolder> {

    private Context mContext;
    private ArrayList<String> tagsList = new ArrayList<String>();

    private OnItemClickListener mListenToClick;


    public ImageTagEditAdapter(Context context, ArrayList<String> existingTagsList, OnItemClickListener itemClickListener){

        mContext = context;
        tagsList = existingTagsList;

        // allows clicks events to be caught
        mListenToClick = itemClickListener;
    }

    @NonNull
    @Override
    public ImageTagEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View folderCell = inflater.inflate(R.layout.image_tag_edit_item, parent, false);
        return new ImageTagEditViewHolder(folderCell);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageTagEditViewHolder holder, int position) {
        // position of the data item (here ArrayList item) to be displayed in RecyclerView
        //int currentPosition = holder.getAdapterPosition();

        String eachImageTag = tagsList.get(position);

        holder.eachImageTagText.setText(eachImageTag);

        /**
         * call the ViewHolder method 'bind' that has the click listener
         * */
        holder.bind(position, eachImageTag, mListenToClick);
    }



    @Override
    public int getItemCount() {
        if (tagsList == null) {
            return 0;
        }
        return tagsList.size();
    }


    public ArrayList<String> getTagList() {
        return tagsList;
    }

    public class ImageTagEditViewHolder extends RecyclerView.ViewHolder{

        EditText eachImageTagText;
        ImageButton deleteTagButton;
        LinearLayout backgroundTagLayout;

        String updatedTagAfterEdit;

        public ImageTagEditViewHolder(@NonNull View itemView) {
            super(itemView);
            eachImageTagText = itemView.findViewById(R.id.tag_edittext);
            deleteTagButton = itemView.findViewById(R.id.delete_imageButton);
            backgroundTagLayout = itemView.findViewById(R.id.tag_layout);
        }

        /**
         * This method is called from onBindViewHolder method
         *
         * setOnClickListener on the current item view in recyclerView
         *
         * Call the callback function 'onItemClick' in MainActivity when some items are clicked
         * */
        public void bind(final int position, final String tagItem, final OnItemClickListener listener){

            deleteTagButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListenToClick.onItemClick(tagItem);
                }
            });

            eachImageTagText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    updatedTagAfterEdit = charSequence.toString();

                    if (tagsList.contains(updatedTagAfterEdit)){
                        Toast.makeText(mContext,
                                "This tag already exists. \nDuplicates will be removed while saving",
                                Toast.LENGTH_SHORT).show();
                        backgroundTagLayout.setBackgroundColor(mContext.getResources().getColor(R.color.pink_900_light, mContext.getTheme()));
                    } else {
                        backgroundTagLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white, mContext.getTheme()));
                    }
                    tagsList.remove(position);
                    tagsList.add(position, updatedTagAfterEdit);
                }

                @Override
                public void afterTextChanged(Editable editable) {

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
        void onItemClick(String imageTagClicked);
    }

}
