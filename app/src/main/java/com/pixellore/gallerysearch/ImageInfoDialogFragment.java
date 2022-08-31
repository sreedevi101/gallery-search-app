package com.pixellore.gallerysearch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pixellore.gallerysearch.utils.Image;


public class ImageInfoDialogFragment extends DialogFragment {

    public static String TAG = "ImageInfoDialog";

    private Image mImage;
    private TextView imageName, imagePath, imageSize, imageCreatedDate, imageModifiedDate;

    public ImageInfoDialogFragment(Image image, Context context) {
        mImage = image;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for dialog construction
        AlertDialog.Builder imageInfoDlg = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);

        imageInfoDlg.setMessage("Image Info");

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_image_info_dialog, null);
        imageInfoDlg.setView(view);

        imageName = view.findViewById(R.id.imageName);
        imageName.setText(mImage.getImageName());

        imagePath = view.findViewById(R.id.imagePath);
        imagePath.setText(mImage.getImagePath());

        imageSize = view.findViewById(R.id.imageSize);
        imageSize.setText(mImage.getImageSize());

        imageCreatedDate = view.findViewById(R.id.imageCreated);
        imageCreatedDate.setText(mImage.getImageCreatedDate());

        imageModifiedDate = view.findViewById(R.id.imageModified);
        imageModifiedDate.setText(mImage.getImageModifiedDate());


        // Set up the buttons
        imageInfoDlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        return imageInfoDlg.create();
    }
}