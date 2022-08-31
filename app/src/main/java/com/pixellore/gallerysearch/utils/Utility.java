package com.pixellore.gallerysearch.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.pixellore.gallerysearch.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class Utility {

    public static int SORT_ORDER_LAST_TO_FIRST = 1;

    private static List<ImageTag> allData;
    public static ImageTagViewModel viewModel;
    private static CompositeDisposable mDisposable = new CompositeDisposable();

    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }

    /**
     * This Method gets all the images in the folder path passed as a String to the method and returns
     * and ArrayList of {@link Image} a custom object that holds data of a given image
     *
     * @param folderPath a String corresponding to a folder path on the device external storage
     */
    public static ArrayList<Image> getAllImagesByFolder(String folderPath, Context context) {

        // ArrayList of images from the folder, extracted from cursor, to be returned
        ArrayList<Image> images = new ArrayList<>();

        // Object of Image class containing details of a single image from the folder
        Image imageItem;

        // Get the cursor from ContentResolver by passing external content Uri and
        // projection to get image names, image paths and image size
        Uri allImagesUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11
            projection = new String[]{MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.DATE_MODIFIED,
                    MediaStore.MediaColumns.IS_FAVORITE,
                    MediaStore.Images.Media.DESCRIPTION};
        } else {
            // Android 10
            projection = new String[]{MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.DATE_MODIFIED,
                    MediaStore.Images.Media.DESCRIPTION};
        }

        Cursor cursor = context.getContentResolver().query( allImagesUri,
                projection,
                MediaStore.Images.Media.DATA + " like ? ",
                new String[] {"%"+folderPath+"%"},
                null);

        try {
            //cursor.moveToFirst();

            // Get the column indices from the cursor
            int imageIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int imageNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int imagePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int imageSizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

            int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
            int dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
            int dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);

            int descriptionColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DESCRIPTION);



            while (cursor.moveToNext()) {

                imageItem = new Image();

                // Get the image details like name, path and size from the cursor
                String imageId = cursor.getString(imageIdColumn);
                String imageName = cursor.getString(imageNameColumn);
                String imagePath = cursor.getString(imagePathColumn);
                long imageSize = cursor.getLong(imageSizeColumn);

                long dateAdded = cursor.getLong(dateAddedColumn);
                long dateTaken = cursor.getLong(dateTakenColumn);
                long dateModified = cursor.getLong(dateModifiedColumn);

                String description = cursor.getString(descriptionColumn);

                int favourite = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    int isFavouriteColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.IS_FAVORITE);
                    favourite = cursor.getInt(isFavouriteColumn);
                }


                /**
                 * Set the image attributes like name, path and size to the {@link Image} object
                 * */
                imageItem.setImageId(imageId);
                imageItem.setImageName(imageName);
                imageItem.setImagePath(imagePath);
                imageItem.setImageSize(formatFileSize(imageSize));
                imageItem.setImageFolderPath(folderPath);

                if (dateTaken != 0L){
                    // if DATE_TAKEN is available, use it as the 'image created date'
                    // DATE_TAKEN is in milliseconds, not seconds
                    imageItem.setImageCreatedDate(epoch2DateString(dateTaken, false));
                } else {
                    // if DATE_TAKEN not available, use DATE_ADDED instead as the 'image created date'
                    // DATE_ADDED is in seconds
                    imageItem.setImageCreatedDate(epoch2DateString(dateAdded, true));
                }
                // DATE_MODIFIED is in seconds
                imageItem.setImageModifiedDate(epoch2DateString(dateModified, true));

                imageItem.setImageDescription(description);
                imageItem.setImageFavourite(favourite);


                /**
                 * Add the {@link Image} object to the ArrayList {@link images}
                 * */
                images.add(imageItem);
            }
            cursor.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: add Menu option to change order (Sort..)
        // Sort the pictures based on the option first to last OR last to first
        ArrayList<Image> sortedAllImages;
        if (SORT_ORDER_LAST_TO_FIRST == 1){
            // Sort order: last to first
            sortedAllImages = new ArrayList<Image>();

            for (int i = images.size()-1; i > -1; i--){
                sortedAllImages.add(images.get(i));
            }
        }
        else {
            // Keep the order: first to last
            sortedAllImages = images;
        }

        return sortedAllImages;
    }

    // Function to convert date in epoc to human readable format
    // https://www.epochconverter.com/
    public static String epoch2DateString(long epochSeconds, boolean isSeconds) {
        Date updateDate;
        if (isSeconds) {
            // convert to milliseconds by multiplying second values by 1000
            updateDate = new Date(epochSeconds * 1000L);
        } else {
            // If in milliseconds
            updateDate = new Date(epochSeconds);
        }

        // format: Tue Jun 7 2022 12:22:49 EDT
        // Refer: https://developer.android.com/reference/java/text/SimpleDateFormat
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss z");
        return format.format(updateDate);
    }

    // Convert file size in Bytes to KB, MB etc
    // https://stackoverflow.com/questions/18099710/format-string-into-kb-mb-and-gb
    public static String formatFileSize(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public static ArrayList<ImageFolder> getPicturePaths(Context context) {

        // ArrayList to return the ImageFolder objects
        ArrayList<ImageFolder> picFolders = new ArrayList<>();
        // String array list to store folder data paths
        // each folder path stored only once
        ArrayList<String> picPaths = new ArrayList<>();

        ImageFolder imageFolder;

        // Query the MediaStore for all images in the external storage
        // Get the image name, folder name and data path to the image
        Uri allImagesUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID};
        Cursor cursor = context.getContentResolver().query(allImagesUri, projection, null, null, null);

        try {
            /*if (cursor != null) {
                cursor.moveToFirst();
            }*/

            // Get the column indices from the cursor
            int imageNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int folderNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            while (cursor.moveToNext()) {

                // create an ImageFolder object
                imageFolder = new ImageFolder();

                // get the image name, the containing folder name and the data path to the image from the cursor
                // IMG-20201127-WA0001.jpg
                String imageName = cursor.getString(imageNameColumn);
                // WhatsApp Images
                String folderName = cursor.getString(folderNameColumn);
                // /storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images/IMG-20201127-WA0001.jpg
                String dataPath = cursor.getString(dataColumn);

                // dataPath has path to the specific image
                // get only the path till the containing folder
                // /storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images/
                String folderDataPaths = dataPath.substring(0, dataPath.lastIndexOf("/"));
                folderDataPaths = folderDataPaths + "/";

                // If this folder name is appearing for the first time, it may not be in
                // the ArrayList of folder paths
                // Check if the folder path already added to the array list of folder paths
                if (!picPaths.contains(folderDataPaths)) {

                    // add folder path to the ArrayList of folder paths
                    picPaths.add(folderDataPaths);

                    /*
                     * Update the ImageFolder object variables with the current data obtained from the cursor
                     * */
                    // Set folder data path in the ImageFolder object
                    imageFolder.setPath(folderDataPaths);

                    // Set the name of the folder
                    imageFolder.setFolderName(folderName);

                    // Set the current image (image path) as the latest image
                    // We will change this when we get more images from this folder via cursor
                    imageFolder.setLatestPic(dataPath);

                    // Increment the image count in the folder
                    imageFolder.countPics();

                    /* Add the ImageFolder object to the ArrayList of ImageFolder objects */
                    picFolders.add(imageFolder);

                } else {
                    /* If folder object already added to the array list of image folder objects,
                     * and another image from the same folder is extracted from the cursor,
                     * then just increment image count of the folder and add the current image as the latest pic */

                    // Loop through the array list and get the correct object matching the folder data path
                    for (int i = 0; i < picFolders.size(); i++) {

                        if (picFolders.get(i).getPath().equals(folderDataPaths)) {

                            picFolders.get(i).countPics();

                            picFolders.get(i).setLatestPic(dataPath);
                        }
                    }
                }

            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return picFolders;
    }



    public static ArrayList<Image> imageSearch(SearchFilter searchFilter, Context context) {

        // get the data in ImageTagDatabase
        // Contains table of Image ID and corresponding tags
        getImageTags(context);

        String folderPath = searchFilter.getFolderPath();
        String[] searchWords = searchFilter.getSearchWords();
        String searchBy = searchFilter.getSearchCategory();



        // ArrayList of images from the folder
        ArrayList<Image> allImages = new ArrayList<>();
        allImages =  getAllImagesByFolder(folderPath, context);


        // ArrayList of images matching the search criteria - the output
        ArrayList<Image> output = new ArrayList<>();

        switch(searchBy) {
            case "image_name_only":
                // SEARCH BY IMAGE NAME
                output = searchByName(allImages, searchWords);
                break;
            case "image_tags_only":
                // SEARCH BY IMAGE TAGS
                output = searchByTag(allImages, searchWords);
                break;
            case "image_name_and_tags":
                // SEARCH BY NAME AND TAGS
                output = searchByNameAndTag(allImages, searchWords);
                break;
        }

        return output;

    }


    public static ArrayList<Image> searchByName(ArrayList<Image> searchTheseImages, String[] searchWordsList){

        // SEARCH BY IMAGE NAME

        Image singleImage;
        String singleImageName;

        // ArrayList of images matching the search criteria
        ArrayList<Image> searchResultImagesByName = new ArrayList<Image>();

        for (int i = 0; i<searchTheseImages.size(); i++) {

            singleImage = searchTheseImages.get(i);
            singleImageName = singleImage.getImageName();

            // Loop through the string array of search words
            for (int j = 0; j<searchWordsList.length; j++) {
                if (singleImageName.contains(searchWordsList[j])) {
                    // Add to search result
                    searchResultImagesByName.add(singleImage);
                    break;
                }
            }

        }

        return searchResultImagesByName;
    }


    public static ArrayList<Image> searchByTag(ArrayList<Image> searchTheseImages, String[] searchWordsList){

        // SEARCH BY IMAGE TAGS

        ImageTag singleDbRow;
        Image singleImage;
        String singleImageTags;
        // ArrayList of images matching the search criteria
        ArrayList<ImageTag> searchResultImageTags = new ArrayList<>();

        // Loop through all rows in database ImageTag table
        for (int k = 0; k<allData.size(); k++){
            singleDbRow = allData.get(k);
            singleImageTags = singleDbRow.getTag();

            // Loop through the string array of search words
            for (int j = 0; j<searchWordsList.length; j++) {
                if (singleImageTags.contains(searchWordsList[j])) {
                    // Save only matching rows - with a search word as a tag
                    searchResultImageTags.add(singleDbRow);
                    break;
                }
            }

        }

        // ImageTag object only has image ID and Image tags,
        // but for displaying the results we need results as Image objects
        // Based on Image ID as the common factor, identify the Image object corresponding to the
        // database row

        // ArrayList of images matching the search criteria
        ArrayList<Image> searchResultImagesByTag = new ArrayList<>();

        // Loop through all images
        for (int i = 0; i<searchTheseImages.size(); i++){
            singleImage = searchTheseImages.get(i);
            // Loop through all rows matching the search words
            for (int m=0; m<searchResultImageTags.size(); m++){
                if (Objects.equals(searchResultImageTags.get(m).getImageId(),
                        singleImage.getImageId())){
                    searchResultImagesByTag.add(singleImage);
                }
            }
        }

        return searchResultImagesByTag;

    }


    public static ArrayList<Image> searchByNameAndTag(ArrayList<Image> searchTheseImages, String[] searchWordsList){

        // SEARCH BY IMAGE NAME
        ArrayList<Image> searchResultImagesByName = searchByName(searchTheseImages, searchWordsList);


        // SEARCH BY IMAGE TAGS
        ArrayList<Image> searchResultImagesByTag = searchByTag(searchTheseImages, searchWordsList);


        Log.v("Size", "searchResultImagesByName " + searchResultImagesByName.size());
        Log.v("Size", "searchResultImagesByTag " + searchResultImagesByTag.size());

        // ArrayList of images matching the search criteria
        ArrayList<Image> searchResultImagesByBoth = new ArrayList<>();

        if (searchResultImagesByName.size() > 0 && searchResultImagesByTag.size() > 0){
            // combine the results of both


            // Save search results by name to output
            searchResultImagesByBoth = searchResultImagesByName;

            // loop through search results by tag
            for (int q=0; q<searchResultImagesByTag.size(); q++){
                // loop through the output and see if this Image object already available in output
                for (int r=0; r<searchResultImagesByBoth.size(); r++){
                    if (Objects.equals(searchResultImagesByBoth.get(r).getImageId(),
                            searchResultImagesByTag.get(q).getImageId())){
                        // skip saving it again
                    } else {
                        //not saved before, so save this to output
                        searchResultImagesByBoth.add(searchResultImagesByTag.get(q));
                    }
                }

            }

        } else if (searchResultImagesByName.size() > 0 && searchResultImagesByTag.size() == 0) {
            searchResultImagesByBoth = searchResultImagesByName;
        } else if (searchResultImagesByName.size() == 0 && searchResultImagesByTag.size() > 0) {
            searchResultImagesByBoth = searchResultImagesByTag;
        }

        return searchResultImagesByBoth;
    }



        public static ArrayList<Image> searchAllFolders(SearchFilter filter, Context context){

        // Initialize the array list to hold the images from all folders by concatenating the results
        ArrayList<Image> allFolderImages = new ArrayList<Image>();
        // Initialize the array list to hold images from each folder
        ArrayList<Image> folderImages;

        // Get the search words from the search filter passed from parent activity
        // create a new search filter for each folder while iterating through the folders
        SearchFilter separateSearchFilter = new SearchFilter();
        separateSearchFilter.setSearchWords(filter.getSearchWords());
        separateSearchFilter.setSearchCategory(filter.getSearchCategory());

        // First get the list of all folders
        ArrayList<ImageFolder> folds = getPicturePaths(context);

        // Iterate through all folders
        for (int k = 0; k < folds.size(); k++) {
            // set the folder name and path for each folder in the search filter
            separateSearchFilter.setFolderName(folds.get(k).getFolderName());
            separateSearchFilter.setFolderPath(folds.get(k).getPath());

            // get matching images from each folder
            folderImages = Utility.imageSearch(separateSearchFilter, context);
            allFolderImages.addAll(folderImages);
        }
        return allFolderImages;

    }


    public static void getImageTags(Context context){

        mDisposable.add(viewModel.getAll().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageTags -> {
                    saveDatabase(imageTags);
                }, throwable -> Log.e("getAll", "Unable to get image tags", throwable)));


    }

    private static void saveDatabase(List<ImageTag> allImageTags) {

        allData = allImageTags;

        if (allData.size() == 0){
            Log.v("ImageTag Utility", "DB table is empty");
        } else {
            for (int i = 0; i<allData.size(); i++) {
                Log.v("ImageTag Utility",
                        "" + i + ". ImageID: " + allData.get(i).getImageId()
                                + " : " + allData.get(i).getTag());
            }
        }

    }


}
