package com.pixellore.gallerysearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pixellore.gallerysearch.utils.ImageTag;
import com.pixellore.gallerysearch.utils.ImageTagEditAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ImageTagEditActivity extends AppCompatActivity {

    public static String TAG = "ImageTagEdit";

    TextView emptyView;
    RecyclerView existingTagsRecycler;
    ImageTagEditAdapter imageTagEditAdapter;

    ArrayList<String> existingTagsList = new ArrayList<String>();

    AlertDialog deleteDuplicatesDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_tag_edit);

        Intent intent = getIntent();

        Uri imageUri = Uri.parse(intent.getStringExtra("Image Uri"));
        ImageTag currentTags = (ImageTag) intent.getParcelableExtra("Image Tags");

        /**
         * Setup action bar/ tool bar
         *
         * reference: https://developer.android.com/training/appbar/setting-up
         * */
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarTagsEditActivity);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // Change title
        ab.setTitle("Edit Image Tags");


        ImageButton newTagButton = (ImageButton) findViewById(R.id.add_new_tag_button);
        EditText newTagText = (EditText) findViewById(R.id.new_tag_text);
        ImageView imagePreviewBox = (ImageView) findViewById(R.id.image);
        // Empty view to display when recycler view is empty (no existing tags)
        emptyView = (TextView) findViewById(R.id.empty_view);
        // RecyclerView for existing image tags
        existingTagsRecycler = findViewById(R.id.existing_tags_recyclerView);

        // Image preview
        Log.v(TAG, imageUri.toString());
        // Set the latest pic using Glide
        imagePreviewBox.setImageURI(imageUri);
        imagePreviewBox.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        // Delete a tag when clicking the "Delete" ImageButton corresponding to a Tag
        ImageTagEditAdapter.OnItemClickListener clickListenerObj = new ImageTagEditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String imageTagClicked) {
                int deleteItemAtIndex = existingTagsList.indexOf(imageTagClicked);

                Log.v(TAG, "Deleting... :" + imageTagClicked);
                existingTagsList.remove(imageTagClicked);

                displayExistingTags(existingTagsList);

                if (deleteItemAtIndex!=-1) {
                    imageTagEditAdapter.notifyItemRemoved(deleteItemAtIndex);
                } else {
                    imageTagEditAdapter.notifyDataSetChanged();
                }


                // If all tags are deleted, and no items to display in the recycler view,
                // change the visibility
                updateTagListVisibility();
            }
        };


        // Recycler View for existing tags
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        existingTagsRecycler.setLayoutManager(layoutManager);
        existingTagsRecycler.addItemDecoration(new DividerItemDecoration(existingTagsRecycler.getContext(),
                DividerItemDecoration.VERTICAL));
        // define an adapter with no tags to begin with
        imageTagEditAdapter = new ImageTagEditAdapter(ImageTagEditActivity.this,
                null, clickListenerObj);
        existingTagsRecycler.setAdapter(imageTagEditAdapter);

        // Get the existing Tags and set the adapter with these tags
        if (currentTags != null) {
            // Get the existing tags of this image
            String existingTags = currentTags.getTag();
            Log.v(TAG, "Existing image tags - input received: " + existingTags);

            // convert from single string of tags separated by comma to an ArrayList of tags
            existingTagsList = stringToArrayList(existingTags);
            displayExistingTags(existingTagsList);

            // Get the adapter for recycler view by passing the data into it
            imageTagEditAdapter = new ImageTagEditAdapter(ImageTagEditActivity.this,
                    existingTagsList, clickListenerObj);

            // Set the adapter on the recycler view
            existingTagsRecycler.setAdapter(imageTagEditAdapter);


            updateTagListVisibility();

        } else {
            existingTagsRecycler.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }


        newTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTag = newTagText.getText().toString();

                // Check if new string is empty
                if (TextUtils.isEmpty(newTag)) {
                    // If user tries to enter empty string
                    Toast.makeText(ImageTagEditActivity.this, "Please type a tag", Toast.LENGTH_SHORT).show();
                } else {
                    //if 'newTag' is not an empty string
                    Log.v(TAG, "New Tag: " + newTag);

                    displayExistingTags(existingTagsList);

                    if (existingTagsList.contains(newTag)) {
                        Log.v(TAG, "This tag already exists. Enter a new tag.");
                        Toast.makeText(ImageTagEditActivity.this, "This tag already exists. Enter a new tag.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.v(TAG, "Adding new Tag");
                        existingTagsList.add(newTag);


                        newTagText.setText("");

                        // if tag list was previously empty (so recycler view was not visible)
                        // and this is the first time a tag is added for this image,
                        // update the visibility of recycler view and set the adapter with tag list (previously it was set with null)
                        if (existingTagsList.size() == 1){
                            updateTagListVisibility();

                            // Get the adapter for recycler view by passing the data into it
                            imageTagEditAdapter = new ImageTagEditAdapter(ImageTagEditActivity.this,
                                    existingTagsList, clickListenerObj);

                            // Set the adapter on the recycler view
                            existingTagsRecycler.setAdapter(imageTagEditAdapter);
                        } else {
                            imageTagEditAdapter.notifyItemInserted(existingTagsList.size()-1);
                        }
                    }

                }
            }
        });


    }

    @Override
    public void onBackPressed() {

        if (isDuplicatesExists()){
            // if there are duplicate tags
            alertDeleteDuplicates();
        } else {
            // if no duplicates

            // save before going back
            saveAndExit();

        }

        // exit activity
        //super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.image_tag_edit_top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.delete_duplicate_tags:
                deleteDuplicates();
                return true;
            case R.id.delete_all_tags:
                alertDeleteAllTags();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void deleteAllTags(){
        int itemsBeforeStart = existingTagsList.size();
        existingTagsList.clear();
        imageTagEditAdapter.notifyItemRangeRemoved(0, itemsBeforeStart);
        updateTagListVisibility();
    }

    /*
    Alert user that the tags list currently contains duplicates
    Provide user to 'Continue' and delete duplicates OR 'Edit' the tags to remove duplicates

    This alert will be shown only if duplicates are present
    */
    private void alertDeleteDuplicates(){
        Log.v(TAG, "Alert dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setMessage(R.string.delete_duplicates_msg)
                .setTitle(R.string.delete_duplicates_heading);


        // Add the buttons
        builder.setPositiveButton(R.string.delete_duplicates_ok_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
                // delete duplicates
                deleteDuplicates();
                // save and exit
                saveAndExit();
            }
        });
        builder.setNegativeButton(R.string.delete_duplicates_cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        // Create the AlertDialog
        deleteDuplicatesDialog = builder.create();
        deleteDuplicatesDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( deleteDuplicatesDialog!=null && deleteDuplicatesDialog.isShowing() ){
            deleteDuplicatesDialog.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ( deleteDuplicatesDialog!=null && deleteDuplicatesDialog.isShowing() ){
            deleteDuplicatesDialog.cancel();
        }
    }

    private boolean isDuplicatesExists() {
        boolean duplicateExists = false;
        Set<String> set = new HashSet<>();
        ArrayList<String> duplicates = new ArrayList<String>();
        for (String s : existingTagsList) {
            if (!set.add(s)) {
                duplicates.add(s);
                duplicateExists = true;
            }
        }
        String duplicateTags = arrayListToString(duplicates);
        Log.v(TAG, "Duplicate Tags: " + duplicateTags);

        return duplicateExists;
    }


    private void deleteDuplicates(){

        String duplicateTags = arrayListToString(existingTagsList);
        Log.v(TAG, "Tags: " + duplicateTags);

        if (isDuplicatesExists()){
            // Delete duplicates
            ArrayList<String> newList = new ArrayList<String>();
            for (String s : existingTagsList){
                // If this element is not present in newList then add it
                if (!newList.contains(s)){
                    newList.add(s);
                }
            }
            // copy back to original ArrayList after clearing it
            existingTagsList.clear();
            existingTagsList.addAll(newList);

            String duplicateTagsRemoved = arrayListToString(existingTagsList);
            Log.v(TAG, "Tags: " + duplicateTagsRemoved);
        } else {
            Toast.makeText(ImageTagEditActivity.this,
                    "No duplicate tags exists",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void saveAndExit(){

        Log.v(TAG, "Saving the tags.");
        // get all tags as per displayed in the EditText views in the RecyclerView
        // This includes all the additions, deletions and edits to the tags
        existingTagsList = imageTagEditAdapter.getTagList();

        Intent resultInt = new Intent();

        if (existingTagsList!=null && existingTagsList.size() > 0) {
            String updatedImageTag = arrayListToString(existingTagsList);
            Log.v(TAG, "Updated Image Tag: " + updatedImageTag);

            resultInt.putExtra("Tag", updatedImageTag);

        } else {

            // No tags to save
            resultInt.putExtra("Tag", "");
        }
        setResult(Activity.RESULT_OK, resultInt);
        finish();
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


    /*
    * Alert when used presses 'Delete All Tags' menu button
    * Asks user for confirmation to delete all tags
    */
    private void alertDeleteAllTags(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setMessage(R.string.delete_all_tags_msg)
                .setTitle(R.string.delete_all_tags_heading);


        // Add the buttons
        builder.setPositiveButton(R.string.delete_all_tags_ok_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
                // delete all tags
                deleteAllTags();
            }
        });
        builder.setNegativeButton(R.string.delete_all_tags_cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        // Create the AlertDialog
        AlertDialog deleteAllTagsDialog = builder.create();
        deleteAllTagsDialog.show();
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


}