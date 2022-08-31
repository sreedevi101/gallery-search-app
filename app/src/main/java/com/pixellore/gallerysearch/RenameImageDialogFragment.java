package com.pixellore.gallerysearch;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pixellore.gallerysearch.utils.Image;
import com.pixellore.gallerysearch.utils.SearchFilter;
import com.pixellore.gallerysearch.utils.Utility;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 */
public class RenameImageDialogFragment extends DialogFragment {

    public static String TAG = "RenameImageDialog";

    RenameImageDialogListener listener;

    private EditText ediText;
    private RelativeLayout warningView;
    private TextView warningText;

    private String userInputName;
    ArrayList<String> allImageNames;
    String originalImageName;

    public RenameImageDialogFragment(Image image, Context context) {

        String originalImageNameWithExt;
        String[] originalImageNameExtension;
        originalImageNameWithExt = image.getImageName();
        //Log.v("RenameImageDialogFragment", "originalImageName: " + originalImageName);

        // Remove extension (ex. ".jpg") from image name
        originalImageNameExtension = originalImageNameWithExt.split("\\.");
        originalImageName = originalImageNameExtension[0];

        // Get the list of all images in the folder (folder of the image) to alert the user if an existing name is entered
        ArrayList<Image> imagesData = Utility.getAllImagesByFolder(image.getImageFolderPath(), context);


        // Loop through and get all the names
        allImageNames = new ArrayList<String>();
        String eachImageNameWithExt, eachImageName;
        String[] nameExtension;

        //Log.v("RenameImageDialogFragment", "imagesData size: " + imagesData.size());

        for (int i = 0; i < imagesData.size(); i++) {
            eachImageNameWithExt = imagesData.get(i).getImageName();
            //Log.v("RenameImageDialogFragment", "imagesData " + i + ": " + eachImageNameWithExt);

            if (Objects.equals(image.getImageName(), eachImageNameWithExt)) {
                // This is the image to be renamed, its name should be spared as a different check is to be done for it
                //Log.v("RenameImageDialogFragment", "[excluded] imagesData " + i + ": " + eachImageNameWithExt);
            } else {
                // Add all image names in the folder except the original image name to be rename in an ArrayList
                nameExtension = eachImageNameWithExt.split("\\.");
                eachImageName = nameExtension[0];
                if (!(imagesData.get(i) == null)) {
                    Log.v("RenameImageDialogFragment", "" + i + " : " + eachImageName);
                    allImageNames.add(eachImageName);
                }
            }


        }


    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for dialog construction
        AlertDialog.Builder userInputDlg = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);

        userInputDlg.setMessage("Edit name");

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_rename_image_dialog, null);


        // Relative Layout with warning to user
        warningView = view.findViewById(R.id.userWarning);
        warningView.setVisibility(View.GONE);

        warningText = view.findViewById(R.id.warnUserText);

        // EditText in the dialog
        ediText = view.findViewById(R.id.newNameInputEditText);
        ediText.setText(originalImageName);
        ediText.setTextColor(R.color.bluegrey_100_dark);



        // Listener for any text changes in the edittext
        ediText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() != 0) {
                    charSequence = charSequence.toString();
                    Log.v("RenameImageDialogFragment", "" + charSequence);
                    Log.v("RenameImageDialogFragment", "newcar:   " + allImageNames.contains("newcar"));

                    for (int x =0; x<allImageNames.size(); x++) {

                        Log.v("RenameImageDialogFragment", "" + allImageNames.get(x));
                    }

                    // If entered name is same as the name of another image in the same folder
                    if (allImageNames.contains("" + charSequence)) {
                        Log.v("RenameImageDialogFragment", "image with same name already exists in the folder");
                        warningText.setText(R.string.name_exists_in_the_folder);
                        warningView.setVisibility(View.VISIBLE);
                    }
                    // If entered name is same as original name
                    else if (charSequence.equals(originalImageName)) {
                        Log.v("User Warning", "new name same as original name");
                        warningText.setText(R.string.name_same_as_original);
                        warningView.setVisibility(View.VISIBLE);
                    } else {
                        warningView.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ediText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    ediText.setTextColor(R.color.black);
                }
            }
        });


        ediText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ediText.setTextColor(R.color.black);
            }
        });


        // Set up the buttons
        userInputDlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userInputName = ediText.getText().toString();

                // call the listener callback and pass the String entered by user
                if(listener != null){
                    listener.onPositiveClick(userInputName);
                }
            }
        });
        userInputDlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        userInputDlg.setView(view);
        return userInputDlg.create();
    }

    // http://prodreadycode.com/passing-data-from-dialogfragment-to-calling-activity/
    @Override
    public void onAttach(@NonNull Context context) {
        if(context instanceof RenameImageDialogListener)
        {
            listener = (RenameImageDialogListener) context;
        }
        super.onAttach(context);
    }

    /**
     * Interface defined to pass the data back to the activity opening the dialog
     *
     * listener is set when the positive button (OK button) is pressed
     * User entered new name for image is passed as a String to the caller activity
     * */
    public interface RenameImageDialogListener {
        void onPositiveClick(String userInputName);
    }

}