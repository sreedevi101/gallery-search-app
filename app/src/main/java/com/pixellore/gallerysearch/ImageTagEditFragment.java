package com.pixellore.gallerysearch;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pixellore.gallerysearch.utils.Image;
import com.pixellore.gallerysearch.utils.ImageTag;
import com.pixellore.gallerysearch.utils.ImageTagEditAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ImageTagEditFragment extends Fragment {



    public static String TAG = "ImageTagEdit";

    private Uri mImage;
    private ImageTag mImageTag;
    private Context mContext;

    ImageTagFragmentListener listener;

    ArrayList<String> existingTagsList = new ArrayList<String>();
    ImageTagEditAdapter imageTagEditAdapter;

    TextView emptyView;
    RecyclerView existingTagsRecycler;
    RelativeLayout recyclerRelativeLayout;

    public ImageTagEditFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //mImage = getArguments().getParcelable("Image Object");
            mImage = (Uri) getArguments().getParcelable("Image Object");
            mImageTag = getArguments().getParcelable("ImageTag Object");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_tag_edit, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //This line of code will stay focus on selected edittext in list
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);

        // Delete a tag when clicking the "Delete" ImageButton corresponding to a Tag
        ImageTagEditAdapter.OnItemClickListener clickListenerObj = new ImageTagEditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String imageTagClicked) {
                Log.v(TAG, "Deleting... :" + imageTagClicked);
                existingTagsList.remove(imageTagClicked);
                displayExistingTags(existingTagsList);
                imageTagEditAdapter.notifyDataSetChanged();

                // If all tags are deleted, and no items to display in the recycler view,
                // change the visibility
                updateTagListVisibility();
            }
        };


        Button saveButton = (Button) view.findViewById(R.id.save_tags_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_tag_edit_button);
        Button newTagButton = (Button) view.findViewById(R.id.add_new_tag_button);
        EditText newTagText = (EditText) view.findViewById(R.id.new_tag_text);
        ImageView imagePreviewBox = (ImageView) view.findViewById(R.id.image);

        Log.v(TAG, mImage.toString());
        // Set the latest pic using Glide
        imagePreviewBox.setImageURI(mImage);
        imagePreviewBox.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        recyclerRelativeLayout = (RelativeLayout) view.findViewById(R.id.tags_recycler_layout);

        emptyView = (TextView) view.findViewById(R.id.empty_view);
        // RecyclerView for existing image tags
        existingTagsRecycler = view.findViewById(R.id.existing_tags_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        existingTagsRecycler.setLayoutManager(layoutManager);

        // define an adapter
        imageTagEditAdapter = new ImageTagEditAdapter(null, clickListenerObj);
        existingTagsRecycler.setAdapter(imageTagEditAdapter);

        if (mImageTag != null) {
            // Get the existing tags of this image
            String existingTags = mImageTag.getTag();
            Log.v(TAG, "Existing image tags - input received: " + existingTags);

            // convert from single string of tags separated by comma to an ArrayList of tags
            existingTagsList = stringToArrayList(existingTags);
            displayExistingTags(existingTagsList);

            // Get the adapter for recycler view by passing the data into it
            imageTagEditAdapter = new ImageTagEditAdapter(existingTagsList, clickListenerObj);

            // Set the adapter on the recycler view
            existingTagsRecycler.setAdapter(imageTagEditAdapter);


            if (existingTagsList.size() == 0) {
                Log.v(TAG, "No existing tags");
                existingTagsRecycler.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                existingTagsRecycler.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }

        } else {
            existingTagsRecycler.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }




        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get all tags as per displayed in the EditText views in the RecyclerView
                // This includes all the additions, deletions and edits to the tags
                existingTagsList = imageTagEditAdapter.getTagList();

                //
                if (existingTagsList!=null && existingTagsList.size() > 0) {
                    String updatedImageTag = arrayListToString(existingTagsList);
                    Log.v(TAG, "Updated Image Tag: " + updatedImageTag);
                    // call the listener callback and pass the String entered by user
                    if (listener != null) {
                        listener.onPositiveClickForTag(updatedImageTag);
                    }
                } else {

                    if (listener != null) {
                        listener.onPositiveClickForTag(null);
                    }

                }



                //requireActivity().getSupportFragmentManager().beginTransaction().remove(ImageTagEditFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        newTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTag = newTagText.getText().toString();
                Log.v(TAG, "New Tag: " + newTag);
                int beforeSize = existingTagsList.size();
                existingTagsList.add(newTag);
                displayExistingTags(existingTagsList);
                imageTagEditAdapter.notifyDataSetChanged();

                newTagText.setText("");

                // if tag list was previously empty (so recycler view was not visible)
                // and this is the first time a tag is added for this image,
                // update the visibility of recycler view and set the adapter with tag list (previously it was set with null)
                if (beforeSize == 0){
                    updateTagListVisibility();

                    // Get the adapter for recycler view by passing the data into it
                    imageTagEditAdapter = new ImageTagEditAdapter(existingTagsList, clickListenerObj);

                    // Set the adapter on the recycler view
                    existingTagsRecycler.setAdapter(imageTagEditAdapter);
                }

                //recyclerRelativeLayout.requestFocus();
            }
        });


    }

    private void displayExistingTags(ArrayList<String> tags){
        if (!tags.isEmpty()){
            for (int i = 0; i<tags.size(); i++){
                Log.v(TAG, "" + i + ": " + tags.get(i));
            }
        }
    }


    private void updateTagListVisibility() {

        if (existingTagsList.size() == 0) {
            Log.v(TAG, "No existing tags");
            existingTagsRecycler.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            existingTagsRecycler.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private String arrayListToString(ArrayList<String> stringArrayList){
        String fullString = "";

        for (int d=0; d<stringArrayList.size(); d++){
            fullString = fullString.concat(stringArrayList.get(d));
            fullString = fullString.concat(",");
        }

        return fullString;
    }

    private ArrayList<String>  stringToArrayList(String str){
        ArrayList<String> stringArrayList = new ArrayList<String>();
        // Split at comma ","
        String[] separated = str.split(",");
        if (separated.length == 0){
            Log.v("Get the tags", "No tags really present");
        } else {
            stringArrayList.addAll(Arrays.asList(separated));
        }
        return stringArrayList;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ImageTagFragmentListener)
        {
            listener = (ImageTagFragmentListener) context;
        }

    }

    /**
     * Interface defined to pass the data back to the activity opening the dialog
     *
     * listener is set when the positive button (OK button) is pressed
     * User entered image tags will be passed as a String to the caller activity
     * */
    public interface ImageTagFragmentListener {
        void onPositiveClickForTag(String imageTags);
    }

}