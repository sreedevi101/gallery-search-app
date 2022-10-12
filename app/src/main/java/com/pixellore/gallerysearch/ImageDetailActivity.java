package com.pixellore.gallerysearch;

import static android.util.Log.v;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pixellore.gallerysearch.utils.Image;
import com.pixellore.gallerysearch.utils.ImageActions;
import com.pixellore.gallerysearch.utils.ImageDetailAdapter;
import com.pixellore.gallerysearch.utils.ImageTag;
import com.pixellore.gallerysearch.utils.ImageTagViewModel;
import com.pixellore.gallerysearch.utils.SearchFilter;
import com.pixellore.gallerysearch.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * This activity displays the single image clicked by the user in {@link ImageDisplay} in a view pager
 * */
public class ImageDetailActivity extends AppCompatActivity implements RenameImageDialogFragment.RenameImageDialogListener {

    // Request codes for 'startActivityForResult'; applicable only Android 10
    private final int RENAME_INTENT_LAUNCH_CODE = 123;
    private final int TAG_EDIT_INTENT_LAUNCH_CODE = 456;


    ArrayList<Image> allImages;

    String selectedFolderPath;
    // Initialize an empty search filter object
    SearchFilter searchFilter = new SearchFilter();
    boolean isSearchResults;

    ViewPager2 viewPager2;
    ImageDetailAdapter imageDetailAdapter;
    int clickedImagePosition = 0;

    // The current index (in the ArrayList) of the image displayed in the view pager
    int viewPagerImagePosition = 0;

    Uri allImagesUri;
    Uri imageUri;
    String currentImageId;
    String originalImageName;
    String newName;
    int favStatus;

    ImageTag mImageTag;

    ImageActions imageActions = new ImageActions(this);


    ImageTagViewModel viewModel;

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    List<ImageTag> allData;


    ImageDetailAdapter.OnItemClickListener clickListenerObj;

    // https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<IntentSenderRequest> requestRenamePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // ActivityResult{resultCode=RESULT_OK, data=null}
                    // -1
                    // ActivityResult{resultCode=RESULT_CANCELED, data=null}
                    // 0

                    //v("Rename", "Result: " + result);
                    //v("Rename", "Result: " + result.getResultCode());

                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            // Perform rename

                            v("Rename", "UserPermission GRANTED");

                            performRenameAction();
                            break;
                        case Activity.RESULT_CANCELED:
                            // Inform user action cannot be performed

                            v("Rename", "UserPermission DENIED");

                            Toast.makeText(ImageDetailActivity.this, "Name cannot be updated without permission", Toast.LENGTH_SHORT).show();

                            break;
                        default:
                            // This case is unlikely
                            // inform user something went wrong, action cannot be performed

                            v("Rename", "UserPermission INVALID INPUT");

                            Toast.makeText(ImageDetailActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();

                            break;
                    }

                }
            });


    private final ActivityResultLauncher<IntentSenderRequest> requestSetFavPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.R)
                @Override
                public void onActivityResult(ActivityResult result) {
                    // ActivityResult{resultCode=RESULT_OK, data=null}
                    // -1
                    // ActivityResult{resultCode=RESULT_CANCELED, data=null}
                    // 0

                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            // Perform Fav action
                            performFavAction();
                            break;
                        case Activity.RESULT_CANCELED:
                            // Inform user action cannot be performed
                            Toast.makeText(ImageDetailActivity.this, "Fav cannot be updated without permission", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            // This case is unlikely
                            // inform user something went wrong, action cannot be performed
                            Toast.makeText(ImageDetailActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });


    private final ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        // Handle the Intent
                        String imageTag = intent.getStringExtra("Tag");

                        if (TextUtils.isEmpty(imageTag)) {
                            // if tag is empty, pass null to the method 'saveImageTag'
                            saveImageTag(null);
                        } else{
                            saveImageTag(imageTag);
                        }

                        Log.v("TAGS", imageTag);
                    }
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);


        /**
         * Setup action bar/ tool bar
         * */
        Toolbar topToolbar = (Toolbar) findViewById(R.id.topToolbarImageDetailActivity);
        setSupportActionBar(topToolbar);
        // Add back button in action bar
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // remove title from action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);



        /**
         * Create an instance of the click listener {@link com.pixellore.gallerysearch.utils.ImageDetailAdapter.OnItemClickListener}
         * interface.
         * Define the logic of what happens when a click is detected inside the call back function
         */
        clickListenerObj = new ImageDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Image imageItem) {
                //Toast.makeText(ImageDetailActivity.this, imageItem.getImageName(), Toast.LENGTH_SHORT).show();

                // Toggle visibility of top toolbar on user click
                if (topToolbar.getVisibility() == View.GONE){
                    topToolbar.setVisibility(View.VISIBLE);
                }else if (topToolbar.getVisibility() == View.VISIBLE){
                    topToolbar.setVisibility(View.GONE);
                }
            }
        };


        /*
        // Not using bottom toolbar

        * Get the second toolbar (bottom toolbar), inflate its menu and
        * define how to handle user clicks on the menu items of the bottom toolbar
        * *//*
        Toolbar bottomToolbar = (Toolbar) findViewById(R.id.bottomToolbarImageDetailActivity);
        bottomToolbar.inflateMenu(R.menu.image_detail_bottom_menu);

        bottomToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_image_share:
                        // share action
                        break;
                    case R.id.action_image_rename:
                        // rename image

                        // Get the image to be renamed from the view pager
                        Image currentImage = allImages.get(viewPager2.getCurrentItem());

                        // call method for renaming
                        renameImage(currentImage);

                        break;
                }
                return true;
            }
        });*/


        Intent intent = getIntent();
        //final ArrayList<Image> allImages = intent.getParcelableArrayListExtra("All images");
        clickedImagePosition = intent.getIntExtra("Image position", 0);
        selectedFolderPath = intent.getStringExtra("Folder path");
        // if the activity is opened from a search or not
        isSearchResults = getIntent().getBooleanExtra("isSearchResults", false);

        // Get the search filter object passed from parent activity via the intent extra
        if (isSearchResults) {
            searchFilter = (SearchFilter) getIntent().getParcelableExtra("SearchFilter");
        }


        setupViewPager(false);

        getViewPagerCurrentPagePos();


        viewModel = new ViewModelProvider(this).get(ImageTagViewModel.class);


        /*
        * We will only observe getAll() method once in onCreate() and every time you will
        * add/delete/update in the database automatically you will get updated data to consume it.
        *
        * Reference:
        * https://medium.com/mindorks/room-implementation-with-rxjava-746f8ba39d19
        * https://github.com/android/architecture-components-samples/tree/main/BasicRxJavaSample
        * (check UserActivity.java)
         * */

        mDisposable.add(viewModel.getAll().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageTags -> {
                    saveDatabase(imageTags);
                }, throwable -> Log.e("getAll", "Unable to get image tags", throwable)));


        /**
         * Image Indicator Recycler View at the bottom
         * */
        /*RecyclerView imageIndicatorView = findViewById(R.id.indicatorRecycler);

        RecyclerView.LayoutManager lm = new GridLayoutManager(ImageDetailActivity.this, 1);
        imageIndicatorView.setLayoutManager(lm);

        imageIndicatorView.hasFixedSize();

        ImageIndicatorAdapter indicatorAdapter = new ImageIndicatorAdapter(getApplicationContext(), allImages);

        imageIndicatorView.setAdapter(indicatorAdapter);*/


    }


    private void saveDatabase(List<ImageTag> allImageTags) {

        allData = allImageTags;

    }



    // This function decides to show which item in toolbar for "Favourite"
    // https://stackoverflow.com/questions/39861202/how-can-i-change-the-icon-in-an-android-toolbar-programmatically
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (favStatus == 1) {
            menu.findItem(R.id.action_image_add_favourite).setVisible(false);
            menu.findItem(R.id.action_image_remove_favourite).setVisible(true);
        } else {
            menu.findItem(R.id.action_image_add_favourite).setVisible(true);
            menu.findItem(R.id.action_image_remove_favourite).setVisible(false);
        }
        return true;
    }

    private void changeStatusFavImage(Image image) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            // Android 11

            currentImageId = image.getImageId();
            imageActions.setImageId(currentImageId);

            allImagesUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            imageUri = Uri.withAppendedPath(allImagesUri, currentImageId);
            // content://media/external/images/media/1377

            imageActions.setImageUri(imageUri);

            favStatus = image.getImageFavourite();

            if (favStatus == 0){
                imageActions.setImageNewFavStatus(1);
            } else {
                imageActions.setImageNewFavStatus(0);
            }

            getPermissionFavourite();

        } else {
            Toast.makeText(ImageDetailActivity.this,
                    "This action is not supported in Android 10 and less :(", Toast.LENGTH_SHORT).show();
        }

    }


    private void renameImage(Image image) {

        currentImageId = image.getImageId();

        originalImageName = image.getImageName();

        /**
         * Pass the parameters necessary for renaming to the {@link ImageActions}
         * before checking/ asking for permission
         *
         * If permission is granted the callback function calls "rename" method
         * in {@link ImageActions}
         * */

        imageActions.setImageId(currentImageId);


        // Open the Dialog Fragment to get the new name from user as user input
        new RenameImageDialogFragment(image, ImageDetailActivity.this).show(getSupportFragmentManager(), RenameImageDialogFragment.TAG);


    }

    private void performRenameAction() {

        int renameResult = imageActions.rename();
        //Log.v("Rename", "No of rows updated: " + renameResult);

        if (renameResult == 1) {
            Toast.makeText(ImageDetailActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();

            // Reload the image with new name by setting up the view pager again
            setupViewPager(true);
        } else {
            Toast.makeText(ImageDetailActivity.this, "Could not complete action. Something went wrong!!!", Toast.LENGTH_SHORT).show();
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    private void performFavAction() {

        int favActionResult = imageActions.changeFavouriteStatus();

        if (favActionResult == 1) {
            Toast.makeText(ImageDetailActivity.this, "Fav status updated", Toast.LENGTH_SHORT).show();

            // Reload the image with new name by setting up the view pager again
            setupViewPager(true);

            invalidateOptionsMenu();
        } else {
            Toast.makeText(ImageDetailActivity.this, "Could not complete action. Something went wrong!!!", Toast.LENGTH_SHORT).show();
        }
    }


    private void getPermissionFavourite() {

        Log.v("Get Permission", "getting permission for setting/resetting favourite");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11

            int permissionStatus = checkCallingUriPermission(imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                // Perform fav status change
                Log.v("Favourite", "UserPermission Already Available");
                performFavAction();

            } else if (permissionStatus == PackageManager.PERMISSION_DENIED) {
                // Ask for permission
                List<Uri> urisToModify = new ArrayList<Uri>();
                urisToModify.add(imageUri);

                PendingIntent editPendingIntent = MediaStore.createWriteRequest(getContentResolver(),
                        urisToModify);

                IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(editPendingIntent).build();
                requestSetFavPermissionLauncher.launch(intentSenderRequest);

            }

        }
    }


    private void getPermissionRename () {

        Log.v("Get Permission", "getting permission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11

            allImagesUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            imageUri = Uri.withAppendedPath(allImagesUri, currentImageId);
            // content://media/external/images/media/1377
            v("Rename", "ImageUri: " + imageUri);
            imageActions.setImageUri(imageUri);

            int permissionStatus = checkCallingUriPermission(imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                // Perform rename
                Log.v("Rename", "UserPermission Already Available");

                performRenameAction();

            } else if (permissionStatus == PackageManager.PERMISSION_DENIED) {
                // Ask for permission
                List<Uri> urisToModify = new ArrayList<Uri>();
                urisToModify.add(imageUri);

                // Following method "createWriteRequest" works only from Android 11
                //https://developer.android.com/reference/android/provider/MediaStore#createWriteRequest(android.content.ContentResolver,%20java.util.Collection%3Candroid.net.Uri%3E)
                // https://developer.android.com/training/data-storage/shared/media#manage-groups-files
                PendingIntent editPendingIntent = MediaStore.createWriteRequest(getContentResolver(),
                        urisToModify);

                IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(editPendingIntent).build();
                requestRenamePermissionLauncher.launch(intentSenderRequest);

            }

        } else {
            // Android 10
            Log.v("Get Permission", "Android 10");

            //allImagesUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
            allImagesUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            imageUri = Uri.withAppendedPath(allImagesUri, currentImageId);

            // content://media/external/images/media/1377
            // content://media/external/downloads/52
            v("Rename", "ImageUri: " + imageUri);
            imageActions.setImageUri(imageUri);

            // Try to rename by updating
            try {

                performRenameAction();

                //getApplicationContext().getContentResolver().notifyChange(imageUri, null);
            } catch (SecurityException securityException) {
                // https://developer.android.com/training/data-storage/shared/media#update-native-code
                RecoverableSecurityException recoverableSecurityException;
                if (securityException instanceof RecoverableSecurityException) {
                    recoverableSecurityException =
                            (RecoverableSecurityException) securityException;
                } else {
                    throw new RuntimeException(
                            securityException.getMessage(), securityException);
                }
                IntentSender intentSender = recoverableSecurityException.getUserAction()
                        .getActionIntent().getIntentSender();

                try {
                    startIntentSenderForResult(intentSender, RENAME_INTENT_LAUNCH_CODE,
                            null, 0, 0, 0, null);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    private ArrayList<Image> getData (String folderPath, SearchFilter filter,
    boolean isSearchResults){

        ArrayList<Image> imagesData;

        // Get the images based on if this is the results of a search or opening a folder
        if (isSearchResults) {
            // Image search
            if (filter.getFolderName().equals("All")) {
                /**
                 * If "All" folders selected for the search, iterate through all the folders and
                 * concatenate the image lists from each folder together
                 * */
                imagesData = Utility.searchAllFolders(filter, this);
            } else {
                /**
                 * Search and find the list of images matching the search filter criteria
                 * */
                imagesData = Utility.imageSearch(filter, this);
            }
        } else {
            // Extract the images again since some folders with about 3000 images are too big to pass as parcel
            imagesData = Utility.getAllImagesByFolder(folderPath, this);

        }

        return imagesData;
    }

    /*
     * Get the data and initialize the viewpager adapter
     * set the adapter to the viewpager
     *
     * @param isItemChanged: if the method is called after a data item is changed
     *
     * if isItemChanged = true, set the current item to the 'viewPagerImagePosition'.
     * This is obtained from viewpager and updated when user scrolls.
     * Refer method: "getViewPagerCurrentPagePos"
     *
     * if isItemChanged = false, set the current item to 'clickedImagePosition'
     * This is passed through intent from 'ImageDisplay' activity
     *
     * */
    private void setupViewPager ( boolean isItemChanged){

        // display the image in the view pager
        viewPager2 = findViewById(R.id.imageViewPager);

        allImages = getData(selectedFolderPath, searchFilter, isSearchResults);
        imageDetailAdapter = new ImageDetailAdapter(allImages,
                ImageDetailActivity.this, clickListenerObj);

        if (isItemChanged) {
            imageDetailAdapter.notifyItemChanged(viewPagerImagePosition);
        }

        viewPager2.setAdapter(imageDetailAdapter);

        if (isItemChanged) {
            viewPager2.setCurrentItem(viewPagerImagePosition);
        } else {
            // item to display, clickedImagePosition is passed through intent from 'ImageDisplay' activity
            viewPager2.setCurrentItem(clickedImagePosition);
        }

    }


    /*
     * get the item/page currently shown by viewpager
     * Update the position when user scrolls
     * */
    private void getViewPagerCurrentPagePos () {

        // Get the position of the current item displayed.
        viewPagerImagePosition = viewPager2.getCurrentItem();

        // Get the position of the current item displayed if user scrolls through the viewpager pages
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                redrawToolbar();
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                viewPagerImagePosition = position;
                redrawToolbar();
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

    }

    private void redrawToolbar() {
        // Redraw the toolbar and change the icons according to the image on page scrolls/ image selected by user

        // Get the image from the view pager
        Image currentImage = allImages.get(viewPager2.getCurrentItem());

        // Get the favourite status
        favStatus = currentImage.getImageFavourite();

        // Get MediaStore image ID
        currentImageId = currentImage.getImageId();

        //  will cause the menu to be redrawn
        invalidateOptionsMenu();

    }


    /*
     * This method is to inflate the menu items for the top toolbar
     * */
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.image_detail_top_menu, menu);
        return true;
    }

    /*
     * This method is to handle the user clicks on the menu items (action icons) in the top toolbar
     * */
    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item){

        // Get the image from the view pager
        Image currentImage = allImages.get(viewPager2.getCurrentItem());

        switch (item.getItemId()) {
            case R.id.action_image_delete:
                // Delete action
                return true;
            case R.id.action_image_info:
                // display image info

                // Open the Dialog Fragment to display image info
                new ImageInfoDialogFragment(currentImage,
                        ImageDetailActivity.this).show(getSupportFragmentManager(),
                        ImageInfoDialogFragment.TAG);

                return true;
            case R.id.action_image_share:
                // share action

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // Android 11

                    allImagesUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    imageUri = Uri.withAppendedPath(allImagesUri, currentImageId);


                } else {
                    // Android 10

                    //allImagesUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                    allImagesUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                    imageUri = Uri.withAppendedPath(allImagesUri, currentImageId);
                }

                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpg");

                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(shareIntent, "Share image using"));

                return true;
            case R.id.action_image_rename:
                // rename image

                // call method for renaming
                renameImage(currentImage);

                return true;
            case R.id.action_image_add_favourite:

            case R.id.action_image_remove_favourite:
                // set or reset as favourite

                changeStatusFavImage(currentImage);

                return true;

            case R.id.action_image_tag:
                // Add or Edit image tags

                // Get the existing tags of the corresponding image from 'allData' extracted in 'onCreate'
                // Note: findRowByImageId not giving timely results since it works async
                ImageTag correctImageTag = null;
                if (allData.size() == 0){
                    Log.v("ImageTag", "DB table is empty");
                } else {
                    for (int i = 0; i<allData.size(); i++) {
                        if (Objects.equals(allData.get(i).getImageId(), currentImage.getImageId())){
                            correctImageTag = allData.get(i);
                        }
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // Android 11

                    allImagesUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    imageUri = Uri.withAppendedPath(allImagesUri, currentImageId);
                } else {
                    // Android 10

                    //allImagesUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                    allImagesUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                    imageUri = Uri.withAppendedPath(allImagesUri, currentImageId);
                }

                editImageTags(imageUri, correctImageTag);

                return true;

            case R.id.action_image_tag_delete:
                // Delete the row corresponding to an image from the table

                // get the row to be deleted - retrieve the row based on MediaStore image ID
                // Perform delete operation using ViewModel
                deleteRowByImageId(currentImage.getImageId());

                return true;
            case R.id.action_clear_tag_db:
                // DEBUGGING purpose only

                deleteAllFromDb();
                return true;

            case R.id.action_show_tag_db:
                // DEBUGGING purpose only

                if (allData.size() == 0){
                    Log.v("ImageTag", "DB table is empty");
                } else {
                    for (int i = 0; i<allData.size(); i++) {
                        Log.v("ImageTag",
                                "" + i + ". ImageID: " + allData.get(i).getImageId()
                                        + " : " + allData.get(i).getTag());
                    }
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editImageTags(Uri imageUri, ImageTag imageTags){

        // Open a new activity called 'ImageTagEditActivity' to Add/Edit/Delete the image tags associated with the clicked image
        Intent showImageTags = new Intent(this, ImageTagEditActivity.class);

        showImageTags.putExtra("Image Uri", imageUri.toString());

        showImageTags.putExtra("Image Tags", imageTags);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11
            mStartForResult.launch(showImageTags);
        } else {
            // Android 10
            startActivityForResult(showImageTags, TAG_EDIT_INTENT_LAUNCH_CODE);
        }

    }

    /**
     * For Android 10, for processing the results of :
     * 1. dialog requesting permission to rename image
     * 2. activity to edit image tags
     * */
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RENAME_INTENT_LAUNCH_CODE){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // rename
                        Log.v("Rename", "USER PERMISSION GRANTED: " + resultCode);

                        try {

                            performRenameAction();

                        } catch (Exception e) {
                            Log.v("Rename", "Renaming failed");
                            Toast.makeText(ImageDetailActivity.this, "Error: Renaming failed!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        break;
                    case Activity.RESULT_CANCELED:
                        // inform user action casson be performed
                        v("Rename", "USER PERMISSION DENIED: " + resultCode);

                        Toast.makeText(ImageDetailActivity.this, "Name cannot be updated without permission", Toast.LENGTH_SHORT).show();

                        break;
                    default:
                        // invalid state
                        Toast.makeText(ImageDetailActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        } else if (requestCode == TAG_EDIT_INTENT_LAUNCH_CODE) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {

                switch (resultCode) {
                    case Activity.RESULT_OK:
                        String imageTag = data.getStringExtra("Tag");

                        if (TextUtils.isEmpty(imageTag)) {
                            // if tag is empty, pass null to the method 'saveImageTag'
                            saveImageTag(null);
                        } else {
                            saveImageTag(imageTag);
                        }
                        Log.v("TAGS", imageTag);

                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(ImageDetailActivity.this, "Tags not updated", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        // invalid state
                        Toast.makeText(ImageDetailActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        } else {
            Log.v("onActivityResult", "Wrong request code");
        }

    }

    /**
     * Get the new name from {@link RenameImageDialogFragment} on clicking the "OK" button
     * */
    @Override
    public void onPositiveClick (String newNameForImage){
        boolean flagContinueRenaming = true;

        newName = newNameForImage;
        Log.v("NewName", "onPositiveClick " + newName);

        // Check if the new name is same as the original image name
        if (newName.equals(originalImageName)) {
            // User entered  name is same as original name
            // Alert user and abort rename
            flagContinueRenaming = false;
            Toast.makeText(ImageDetailActivity.this, "New name entered is same as the existing name", Toast.LENGTH_SHORT).show();
        }

        if (newName.isEmpty()) {
            // abort rename - inform user
            flagContinueRenaming = false;
            Toast.makeText(ImageDetailActivity.this, "New name entered cannot empty", Toast.LENGTH_SHORT).show();
        }

        // Continue the renaming operation by getting user permission
        if (flagContinueRenaming) {
            // set the new name in the object
            imageActions.setImageNewName(newName);

            /**
             * Get user permission to rename the picture and call the rename method in {@link ImageActions}
             **/
            getPermissionRename();
        }

    }


    /**
    * Method to be executed when the positive button of {@link ImageTagEditActivity} is pressed
    * */
    private void saveImageTag(String imageTagInput) {
        Log.v("Image Tag user input", ""+ imageTagInput);

        // image ID would be updated whenever user opens an image or scrolls in the ViewPager
        // refer "redrawToolbar" method

        // Check if this image ID already exists in the DB table
        boolean imageIdAlreadyInDb = false;
        ImageTag currentImageTagObj = null;
        if (allData.size() != 0) {
            // If DB table is not empty
            // Loop through all the rows in the table
            for (int i = 0; i<allData.size(); i++) {
                if (Objects.equals(allData.get(i).getImageId(), currentImageId)){
                    // Image ID already exist in the DB table
                    imageIdAlreadyInDb = true;
                    currentImageTagObj = allData.get(i);
                }
            }
        }

        // if tag input received is not null
        if (imageTagInput != null){
            // Create object of class "ImageTag"
            ImageTag newImageTagObj = new ImageTag(currentImageId, imageTagInput);

            // Based on if the image ID row already exists in the DB or not, determine if insert/update to be performed
            if (imageIdAlreadyInDb == false) {
                // insert new image and its tags to DB table
                modifyImageTagDatabase(newImageTagObj, "insert", true);
            } else if (imageIdAlreadyInDb == true) {
                // Instead of inserting as a new row in the DB table, update the existing row for this image ID

                // Skip updating if new image tag same as the one existing in the DB
                if (imageTagInput.equals(currentImageTagObj.getTag())){
                    return;
                }

                // Delete current image tag object from DB table
                modifyImageTagDatabase(currentImageTagObj, "delete", false);

                // insert the new ImageTag object to DB table
                modifyImageTagDatabase(newImageTagObj, "insert", true);
            }

        } else{
            // if tag input received is null
            if (imageIdAlreadyInDb == true) {
                // If currently there is a tag, delete it

                // Delete current image tag object from DB table
                modifyImageTagDatabase(currentImageTagObj, "delete", false);
            }
        }

    }

    public void modifyImageTagDatabase (ImageTag imageTag, String action, boolean verbose){

        switch (action) {
            case "insert":

                mDisposable.add(viewModel.insertImageTagRow(imageTag)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() ->
                                {if (verbose) {Toast.makeText(ImageDetailActivity.this,
                                        "Successfully added tags for this image",
                                        Toast.LENGTH_SHORT).show();}},
                                throwable ->
                                        Toast.makeText(ImageDetailActivity.this,
                                                "Error! Unable to add tags for this image",
                                                Toast.LENGTH_SHORT).show()));


                break;

            case "delete":

                mDisposable.add(viewModel.deleteImageTagRow(imageTag)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() ->
                                {if (verbose) {Toast.makeText(ImageDetailActivity.this,
                                        "Deleted the tags for this image",
                                        Toast.LENGTH_SHORT).show();}},
                                throwable ->
                                        Toast.makeText(ImageDetailActivity.this,
                                                "Error! Unable to delete the image tags",
                                                Toast.LENGTH_SHORT).show()));

                break;
            default:
                Toast.makeText(ImageDetailActivity.this,
                        "Invalid database operation!!!", Toast.LENGTH_SHORT).show();
        }

    }


    /*
    * With MediaStore Image ID as input, find the corresponding ImageTag object (row in the table)
    *
    * and delete this row from the table
    * */
    public void deleteRowByImageId(String selectedImageId){

        // Get the row to be deleted by its Image Id using 'findByImageId' method
        // Result will be saved to 'mImageTag'
        findRowByImageId(selectedImageId);

        if (mImageTag == null){
            Toast.makeText(ImageDetailActivity.this,
                    "No tags saved for this image!!",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Call method to delete this image tag object from the table
            modifyImageTagDatabase(mImageTag, "delete", true);
        }

    }


    public void findRowByImageId(String selectedImageId){

        // Get the row to be deleted by its Image Id using 'findByImageId' method
        mDisposable.add(viewModel.findByImageId(selectedImageId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((imageTag) ->
                                assignRow(imageTag),
                        throwable ->
                                Log.e("Get a row by Image ID", "Unable to retrieve the row")));


    }

    public void assignRow(ImageTag imTag) {
        mImageTag = imTag;
    }


    /*
    * Delete all the data (image IDs and image tags) in the ImageTag table in the database
    * */
    public void deleteAllFromDb(){

        mDisposable.add(viewModel.deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() ->
                                Toast.makeText(ImageDetailActivity.this,
                                        "Successfully cleared all image tags",
                                        Toast.LENGTH_SHORT).show(),
                        throwable ->
                                Toast.makeText(ImageDetailActivity.this,
                                        "Error! Unable to clear the data in the image tags table",
                                        Toast.LENGTH_SHORT).show()));


    }


}