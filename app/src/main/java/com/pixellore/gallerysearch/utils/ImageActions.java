package com.pixellore.gallerysearch.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

public class ImageActions {

    String imageId;
    Uri imageUri;
    String imageNewName;
    Context context;
    int imageNewFavStatus;

    public ImageActions() {

    }

    public ImageActions(Context context) {
        this.context =context;
    }

    public ImageActions(Context context, String imageId, Uri imageUri, String imageNewName, int imageNewFavStatus) {
        this.context = context;
        this.imageId = imageId;
        this.imageUri = imageUri;
        this.imageNewName = imageNewName;
        this.imageNewFavStatus = imageNewFavStatus;
    }



    public String getImageId() {
        return imageId;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getImageNewName() {
        return imageNewName;
    }

    public int getImageNewFavStatus() {
        return imageNewFavStatus;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public void setImageNewName(String imageNewName) {
        this.imageNewName = imageNewName;
    }

    public void setImageNewFavStatus(int imageNewFavStatus) {
        this.imageNewFavStatus = imageNewFavStatus;
    }

    /*
    * Rename the image
    * */
    public int rename(){

        // create an object with the value to set
        ContentValues test_values = new ContentValues();
        test_values.put(MediaStore.Images.Media.DISPLAY_NAME, imageNewName);

        // When performing a single item update, prefer using the ID
        String selection = MediaStore.Images.Media._ID + " = ?";
        String[] selectionArgs = new String[] { imageId };

        int result = context.getContentResolver().update(
                imageUri,
                test_values,
                null,
                null);

        return result;
    }


    /*
     * Mark an image as Favourite or not
     * */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public int changeFavouriteStatus(){

        // create an object with the value to set
        ContentValues test_values = new ContentValues();
        test_values.put(MediaStore.MediaColumns.IS_FAVORITE, imageNewFavStatus);

        // When performing a single item update, prefer using the ID
        String selection = MediaStore.Images.Media._ID + " = ?";
        String[] selectionArgs = new String[] { imageId };

        int result = context.getContentResolver().update(
                imageUri,
                test_values,
                null,
                null);

        return result;
    }


}
