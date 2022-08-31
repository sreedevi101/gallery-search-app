package com.pixellore.gallerysearch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.pixellore.gallerysearch.utils.ImageFolder;
import com.pixellore.gallerysearch.utils.SearchFilter;
import com.pixellore.gallerysearch.utils.Utility;

import java.util.ArrayList;

/**
 *
 * Class extending from DialogFragment to create a fragment for AlertDialog
 * AlertDialog builder is used to build the dialog
 *
 * An interface {@link SearchDialogListener} is implemented in this class so that the data from
 * user obtained from the dialog views (EditText, Spinner etc.) can be passed to the activity opening
 * the dialog box. (The search functionality is then implemented in the calling activities based on these inputs)
 *
 * This class implements 'OnItemSelectedListener' for the spinner in the dialog box
 *
 * The custom layout for content of the dialog box is defined in the XML file fragment_search_dialog.xml
 *
 * Reference:
 * https://developer.android.com/guide/fragments/dialogs
 * https://developer.android.com/guide/topics/ui/dialogs
 * http://prodreadycode.com/passing-data-from-dialogfragment-to-calling-activity/
 *
 * */
public class SearchDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    public static String TAG = "SearchDialog";

    SearchDialogListener listener;

    private EditText mSearchInput;
    private Spinner spinner;

    SearchFilter searchFilter = new SearchFilter();

    private ImageFolder mSelectedFolder;

    // set default
    private String mSearchCategory = "image_name_and_tags";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);

        builder.setMessage("Search");

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_search_dialog, null);
        builder.setView(view);

        // setup the spinner with folder names
        ArrayList<ImageFolder> folders = Utility.getPicturePaths(getContext());

        ImageFolder allFolders = new ImageFolder();
        allFolders.setFolderName("All");

        // Spinner in the dialog
        spinner = view.findViewById(R.id.folder_select_spinner);

        // adapter for spinner
        ArrayAdapter<ImageFolder> spinnerAdapter = new ArrayAdapter<ImageFolder>(getContext(),
                android.R.layout.simple_spinner_item, folders);
        spinnerAdapter.insert(allFolders, 0);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdapter);

        /**
         * Set the listener so that the listener callback method 'onItemSelected' works
         * when an item is selected from the spinner
         * */
        spinner.setOnItemSelectedListener(this);

        // Edit text in the dialog
        mSearchInput = view.findViewById(R.id.search_text);

        /*
        * Find the user selection of search content - search image name or tags or both
        * Using 'setOnCheckedChangeListener' on the RadioGroup to identify user selection
        * RadioGroup allows only selection of a single RadioButton at a time
        * "Image name and tags" is selected by default
        *
        * 'mSearchCategory' will be assigned a String based on RadioButton selection
        * which will be added to SearchFilter
        * */
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.search_category_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // checkedId is the RadioButton selected
                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.radio_name_only:
                        mSearchCategory = "image_name_only";
                        break;
                    case R.id.radio_tags_only:
                        mSearchCategory = "image_tags_only";
                        break;
                    case R.id.radio_name_and_tags:
                        mSearchCategory = "image_name_and_tags";
                        break;
                }
            }
        });

        /*
        * Action to be taken when user presses the positive button "Search"
        * */
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                /**
                 * Get the words to be searched from the EditText view
                 *
                 * The single string obtained should be split at the delimiter
                 * The words are then added to the SearchFilter object to pass to the activity
                 * that called the dialog
                 * */
                String searchText = mSearchInput.getText().toString();
                String[] searchWords = searchText.split(",");
                searchFilter.setSearchWords(searchWords);

                /**
                 * Get the folder name and folder path from the ImageFolder object obtained from
                 * spinner - from 'onItemSelected' callback of 'OnItemSelectedListener' attached to spinner
                 *
                 * Set folder name and folder path to SearchFilter object
                 * */
                String folderName = mSelectedFolder.getFolderName();
                searchFilter.setFolderName(folderName);

                String folderPath = mSelectedFolder.getPath();
                searchFilter.setFolderPath(folderPath);

                searchFilter.setSearchCategory(mSearchCategory);

                // call the listener callback and pass the SearchFilter object
                if(listener != null){
                    listener.onPositiveClick(searchFilter);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        return builder.create();
    }

    // http://prodreadycode.com/passing-data-from-dialogfragment-to-calling-activity/
    @Override
    public void onAttach(@NonNull Context context) {
        if(context instanceof SearchDialogListener)
        {
            listener = (SearchDialogListener) context;
        }
        super.onAttach(context);
    }

    /**
     * Following are the callback methods for 'OnItemSelectedListener' for the spinner
     * These methods contain actions to be taken when a spinner selection is made
     * */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        ImageFolder imageFolder = (ImageFolder) adapterView.getItemAtPosition(position);

        mSelectedFolder = imageFolder;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    /**
     * Interface defined to pass the data back to the activity opening the dialog
     *
     * listener is set when the positive button (Search button) is pressed
     * Data passed is a custom object of {@link SearchFilter}
     * */
    public interface SearchDialogListener {
        void onPositiveClick(SearchFilter searchFilter);
    }


}