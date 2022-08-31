package com.pixellore.gallerysearch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pixellore.gallerysearch.utils.Image;
import com.pixellore.gallerysearch.utils.ImageTag;

import java.util.ArrayList;


public class ImageTagDialogFragment extends DialogFragment {

    public static String TAG = "ImageTagDialog";

    private Image mImage;
    private ImageTag mImageTag;
    private Context mContext;

    ImageTagDialogListener listener;


    public ImageTagDialogFragment(Image image, ImageTag imageTag, Context context) {
        mImage = image;
        mImageTag = imageTag;
        mContext = context;

        Log.v("existingTags", "" +imageTag);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for dialog construction
        AlertDialog.Builder imageTagDlg = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);

        imageTagDlg.setMessage("Add/Edit Image Tags");

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_image_tag_dialog, null);


        // Initialize ArrayLists to hold the EditText views
        ArrayList<EditText> allEditTextViews = new ArrayList<EditText>();
        ArrayList<EditText> allAddTextViews = new ArrayList<EditText>();
        // Theme Wrapper to provide custom style to EditText views
        ContextThemeWrapper tagLayout = new ContextThemeWrapper(mContext,
                R.style.TagEditTextStyle);

        RelativeLayout editTagsLayout = (RelativeLayout) view.findViewById(R.id.edit_tags_block);
        TextView editTagHeading = (TextView) view.findViewById(R.id.editTagHeading);

        // If this Image ID is not part of the Database table,
        // a "null" object would be received as input parameter
        if (mImageTag == null) {

            editTagsLayout.setVisibility(View.GONE);
            editTagHeading.setVisibility(View.GONE);

        } else {
            // Further processing only of the object is not null

            // Get the existing tags of this image
            String existingTags = mImageTag.getTag();
            Log.v("existingTags", existingTags);
            // convert from single string of tags separated by comma to an ArrayList of tags
            ArrayList<String> existingTagsList = stringToArrayList(existingTags);

            if (existingTagsList.size() == 0) {
                Log.v("Existing Tags", "No existing tags");

                editTagsLayout.setVisibility(View.GONE);
                editTagHeading.setVisibility(View.GONE);
            } else {
                editTagsLayout.setVisibility(View.VISIBLE);
                editTagHeading.setVisibility(View.VISIBLE);
                // Edit existing tags
                GridLayout parent = (GridLayout) view.findViewById(R.id.parentLayoutEditTags);

                // Loop through and create Edit Text box
                // Number of Edit text boxes to crete is according to the number of existing tags for this image
                for (int i = 0; i<existingTagsList.size(); i++) {
                    EditText tagText = new EditText(tagLayout);

                    // border for edit text
                    tagText.setBackgroundResource(R.drawable.tag_text_border);


                    GridLayout.LayoutParams params1 = new GridLayout.LayoutParams();

                    tagText.setLayoutParams(params1);

                    tagText.setId(i);

                    // set the text (existing tag) to be displayed
                    tagText.setText(existingTagsList.get(i));

                    allEditTextViews.add(i, tagText);

                    parent.addView(tagText);
                }

            }

        }




        // Add new tags
        LinearLayout parent2 = (LinearLayout) view.findViewById(R.id.parentLayoutAddTags);

        Button addTags = (Button) view.findViewById(R.id.addNewTagsBtn);
        addTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText tagAddText = new EditText(tagLayout);

                tagAddText.setBackgroundResource(R.drawable.tag_text_border);


                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                tagAddText.setLayoutParams(params1);

                allAddTextViews.add(tagAddText);
                parent2.addView(tagAddText);
            }
        });

        imageTagDlg.setView(view);







        // Set up the buttons
        imageTagDlg.setPositiveButton("SAVE TAGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Read tags from all edit texts - existing and new
                ArrayList<String> allTags = new ArrayList<String>();
                for (int k = 0; k<allEditTextViews.size(); k++){
                    allTags.add(allEditTextViews.get(k).getText().toString());
                }

                Log.v("Add tags", ""+ allAddTextViews.size());
                for (int j = 0; j<allAddTextViews.size(); j++){
                    allTags.add(allAddTextViews.get(j).getText().toString());
                }


                // Only for debugging
                for (int d=0; d<allTags.size(); d++){
                    Log.v("Tags", ""+ d + " : " + allTags.get(d));
                }

                // Convert all tags to a single string
                String allTagsSingleString = arrayListToString(allTags);
                Log.v("Tag String", allTagsSingleString);

                // call the listener callback and pass the String entered by user
                if(listener != null){
                    listener.onPositiveClickForTag(allTagsSingleString);
                }
            }
        });
        imageTagDlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        return imageTagDlg.create();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        if(context instanceof ImageTagDialogListener)
        {
            listener = (ImageTagDialogListener) context;
        }
        super.onAttach(context);
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
            Log.v("Get the tags", "No tags really prrsent");
        } else {
            for (int i = 0; i<separated.length; i++){
                stringArrayList.add(separated[i]);
            }
        }
        return stringArrayList;
    }

    /**
     * Interface defined to pass the data back to the activity opening the dialog
     *
     * listener is set when the positive button (OK button) is pressed
     * User entered image tags will be passed as a String to the caller activity
     * */
    public interface ImageTagDialogListener {
        void onPositiveClickForTag(String imageTags);
    }
}