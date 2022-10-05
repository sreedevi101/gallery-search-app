package com.pixellore.gallerysearch;

import static android.os.Build.VERSION.SDK_INT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pixellore.gallerysearch.utils.ImageFolder;
import com.pixellore.gallerysearch.utils.ImageTag;
import com.pixellore.gallerysearch.utils.ImageTagDao;
import com.pixellore.gallerysearch.utils.ImageTagDatabase;
import com.pixellore.gallerysearch.utils.ImageTagViewModel;
import com.pixellore.gallerysearch.utils.PictureFolderAdapter;
import com.pixellore.gallerysearch.utils.SearchFilter;
import com.pixellore.gallerysearch.utils.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SearchDialogFragment.SearchDialogListener {

    // Application specific request code for requestPermissions() method
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_MANAGE_EXTERNAL_STORAGE = 3;

    // RecyclerView for displaying the folders containing images
    RecyclerView folderRecycler;
    PictureFolderAdapter picFolderAdapter;
    ArrayList<ImageFolder> folds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if this is the first run of the app
        // display tutorial if its first run
        checkFirstRun();

        /**
         * Setup action bar/ tool bar
         *
         * reference: https://developer.android.com/training/appbar/setting-up
         * */
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarMainActivity);
        setSupportActionBar(myToolbar);


        /**
         * Request the user for permission to access media files and read images on the device.
         * This will be useful as from api 21 and above, if this check is not done,
         * the Activity will crash
         * */

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }


        // Get the data to display
        // ArrayList<ImageFolder> folds
        folds = Utility.getPicturePaths(this);

        ImageTagViewModel viewModel = new ViewModelProvider(this).get(ImageTagViewModel.class);
        Utility.viewModel = viewModel;
        // get image tags from database
        Utility.getImageTags(this);


        // Get the RecyclerView
        folderRecycler = findViewById(R.id.folderRecycler);

        folderRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        folderRecycler.hasFixedSize();

        /**
         * Create an instance of {@link com.pixellore.gallery.utils.PictureFolderAdapter.OnItemClickListener}
         * to pass as a parameter to {@link PictureFolderAdapter} constructor
         *
         * implement the 'onItemClick' method in the instance and pass it to adapter.
         * When an item in recyclerView is clicked, the adapter will call this callback function and
         * the logic inside will be executed.
         *
         * https://stackoverflow.com/questions/49969278/recyclerview-item-click-listener-the-right-way
         * */
        PictureFolderAdapter.OnItemClickListener clickListenerObj = new PictureFolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ImageFolder imageFolderItem) {
                /**
                 * The actual logic when an item in recycler view (with adapter {@link PictureFolderAdapter})
                 * is clicked goes in here
                 * */

                // Get the folder name, folder data path and number of images in the folder
                String pictureFolderPath = imageFolderItem.getPath();
                String folderName = imageFolderItem.getFolderName();
                String numberOfImagesString = "" + imageFolderItem.getNumberOfPics();

                // Open a new activity called 'ImageDisplay' to display the images in the folder clicked
                Intent move = new Intent(MainActivity.this, ImageDisplay.class);

                // pass the folder name, folder data path and number of images in the folder
                // as extras in the intent to 'ImageDisplay'
                move.putExtra("folderPath", pictureFolderPath);
                move.putExtra("folderName", folderName);
                move.putExtra("numberOfPics", numberOfImagesString);
                move.putExtra("isSearchResults", false);

                startActivity(move);
            }
        };

        // Get the adapter for recycler view by passing the data into it
        picFolderAdapter = new PictureFolderAdapter(MainActivity.this, folds, clickListenerObj);

        // Set the adapter on the recycler view
        folderRecycler.setAdapter(picFolderAdapter);


        /**
         * Search Floating Button click action
         * */
        FloatingActionButton fab = findViewById(R.id.search_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SearchDialogFragment().show(getSupportFragmentManager(), SearchDialogFragment.TAG);
            }
        });


    }

    /*
     * This method is to inflate the menu items for the top toolbar
     * */
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainactivity_menu, menu);
        return true;
    }

    /*
     * This method is to handle the user clicks on the menu items (action icons) in the top toolbar
     * */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case R.id.about_menu:
                // Display details "About" the app

                // Open a new activity called 'AboutAppActivity' to display the information about the app
                Intent about = new Intent(MainActivity.this, AboutAppActivity.class);

                startActivity(about);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkPermission() {
        // Android R = Android 11
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        Log.v("Version", "" + SDK_INT);
        if (SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                //startActivityForResult(intent, 2296);
                Log.v("Version", "" + "Here");
                ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                // Add same code that you want to add in onActivityResult method

                                if (Environment.isExternalStorageManager()) {
                                    // perform action when allow permission success
                                } else {
                                    Toast.makeText(MainActivity.this,
                                            "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                                }
                            }


                        });
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                //startActivityForResult(intent, 2296);

                ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                // Add same code that you want to add in onActivityResult method
                            }
                        });
            }
        } else {
            //below android 11
            int PERMISSION_REQUEST_CODE = 1;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private boolean hasWriteStoragePermission() {
        if (SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
        return true;
    }


    /**
    * Method to be executed when positive button of {@link SearchDialogFragment} is clicked
    * */
    @Override
    public void onPositiveClick(SearchFilter searchFilter) {

        String[] searchWords = searchFilter.getSearchWords();
        String folderName = searchFilter.getFolderName();
        String folderPath = searchFilter.getFolderPath();

        // Join the String array to a single string with words separated by space
        String wordsJoined = TextUtils.join(" ", searchWords);

        // Open a new activity called 'ImageDisplay' to display the images from the search
        Intent move = new Intent(MainActivity.this, ImageDisplay.class);

        // pass the folder name, folder data path from search filter
        // as extras in the intent to 'ImageDisplay'
        // number of images is sent as "0" since this will be known only after performing the search
        move.putExtra("folderPath", folderPath);
        move.putExtra("folderName", folderName);
        move.putExtra("numberOfPics", "0");
        move.putExtra("isSearchResults", true);
        move.putExtra("SearchFilter", searchFilter);

        startActivity(move);


    }

    /**
     * Check if this is the first run of the app after installation or an update
     *
     * This is to determine if app intro pages need to be shown or not
     * */
    private void checkFirstRun() {

        /**
         * Get saved version code from SharedPreferences file to determine if the current version code
         * is same or an updated version. (Version code integer would br higher if its an update)
         * */
        // File name for SharedPreferences file
        final String PREF_FILE_NAME = "VersionPref";
        //Define the string name for version code key in SharedPreferences file
        final String PREF_FILE_VER_CODE_KEY = "VersionCode";
        // Define a default value if
        final int KEY_NOT_AVAILABLE = -1;

        // Get saved version code (saved in SharedPreferences file)
        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        int savedVerCode = preferences.getInt(PREF_FILE_VER_CODE_KEY, KEY_NOT_AVAILABLE);

        //Toast.makeText(MainActivity.this, "Existing version: " + savedVerCode, Toast.LENGTH_SHORT).show();


        /**
         * Check if this is
         * - a first run after new app install (or data cleared by user)
         *          - saved version code would be not available and will be equal to the set default value
         * - a first run after an update
         *          - saved version code would be less than the current version code of the app
         * - a normal run
         *          - saved version code would be equal to current version code of the app
         * */
        if (savedVerCode == BuildConfig.VERSION_CODE){
            // This is a normal run, thus no action required
            return;
        } else {

            // If not a normal run
            if (savedVerCode == KEY_NOT_AVAILABLE) {
                // This is the first run of the app after new installation (or the user cleared the
                // shared preferences)

            } else if (BuildConfig.VERSION_CODE > savedVerCode) {
                // The app been updated. This is the first run after update

            }

            // show intro message
            openAppIntroActivity();

            // Update the shared preferences version code with the current version code of the app
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(PREF_FILE_VER_CODE_KEY, BuildConfig.VERSION_CODE);
            editor.apply();
        }

        return;
    }

    public void openAppIntroActivity(){
        //When the button is clicked open the activity for the tutorial
        Intent move = new Intent(MainActivity.this, TutorialActivity.class);
        startActivity(move);
    }

}