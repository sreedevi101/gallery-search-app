package com.pixellore.gallerysearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pixellore.gallerysearch.utils.Image;
import com.pixellore.gallerysearch.utils.ImageDisplayAdapter;
import com.pixellore.gallerysearch.utils.ImageTagViewModel;
import com.pixellore.gallerysearch.utils.SearchFilter;
import com.pixellore.gallerysearch.utils.Utility;

import java.util.ArrayList;

public class ImageDisplay extends AppCompatActivity {

    TextView folderNameText;
    TextView folderPicNumber;

    ArrayList<Image> displayImages;

    RecyclerView imageRecycler;

    ImageDisplayAdapter pictureAdapter;

    // Initialize an empty search filter object
    SearchFilter searchFilter = new SearchFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        // Get the data passed through the intent from parent activity
        String folderName = getIntent().getStringExtra("folderName");
        String folderPath = getIntent().getStringExtra("folderPath");
        String numberOfImages = getIntent().getStringExtra("numberOfPics");
        // if the activity is opened from a search or not
        boolean isSearchResults = getIntent().getBooleanExtra("isSearchResults", false);

        // Get the search filter object passed from parent activity via the intent extra
        if (isSearchResults) {
            searchFilter = (SearchFilter) getIntent().getParcelableExtra("SearchFilter");
        }

        // Get handle to the TextViews for displaying title and number of images
        folderNameText = findViewById(R.id.folderName);
        folderPicNumber = findViewById(R.id.numberOfImages);

        if (isSearchResults) {
            // If the activity is opened to display search results

            // Display the title as "Search Results"
            folderNameText.setText("Search Results");

            ImageTagViewModel viewModel = new ViewModelProvider(this).get(ImageTagViewModel.class);
            Utility.viewModel = viewModel;

           // Image search
            if (folderName.equals("All")){
                /**
                 * If "All" folders selected for the search, iterate through all the folders and
                 * concatenate the image lists from each folder together
                 * */
                displayImages = Utility.searchAllFolders(searchFilter, this);
            }
            else {
                /**
                 * Search and find the list of images matching the search filter criteria
                 * */
                displayImages = Utility.imageSearch(searchFilter, this);
            }



            if (displayImages.isEmpty()) {
                // If NO results are found, display the message
                //Toast.makeText(getApplicationContext(), "No results found", Toast.LENGTH_SHORT).show();

                // If no results returned, make this text view invisible
                folderPicNumber.setVisibility(View.GONE);
            }
            else {

                // Set number of images in the action bar based on the results returned from the search
                folderPicNumber.setVisibility(View.VISIBLE);
                folderPicNumber.setText("(" + displayImages.size() + ")");

                /*for (int i = 0; i<displayImages.size(); i++) {
                    Log.v("Search Result", displayImages.get(i).getImageName());
                }*/
            }
        }
        else {
            // If the activity is opened when the user clicked a folder

            // Display the opened folder name in the Textview at the top
            folderNameText.setText(folderName);

            // Display the number of pictures in the folder in the TextView
            folderPicNumber.setVisibility(View.VISIBLE);
            folderPicNumber.setText("(" + numberOfImages + ")");

            // Get the list of images in this folder path
            // Keeping this method in Utility class and declaring as 'static' so that this can be called from
            // 'ImageDetailActivity' also
            displayImages =  Utility.getAllImagesByFolder(folderPath, this);

        }


        // Get the RecyclerView
        imageRecycler = findViewById(R.id.imageRecycler);

        // Auto fit GridLayout number of columns based on screen size
        // https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
        int mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext(), 140);

        imageRecycler.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        imageRecycler.hasFixedSize();

        /**
         * Create an instance of {@link ImageDisplayAdapter.OnItemClickListener}
         * interface and define the logic for what to occur when a click happens, in the onItemClick method
         * */
        ImageDisplayAdapter.OnItemClickListener clickListenerObj = new ImageDisplayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Image imageItem, int imageItemPosition) {
                //Toast.makeText(getApplicationContext(), "Opening Image...", Toast.LENGTH_SHORT).show();

                // Open a new activity called 'ImageDetailActivity' to display the image clicked
                Intent showImageDetail = new Intent(ImageDisplay.this, ImageDetailActivity.class);

                /**
                 * Pass the array list of objects of {@link Image} class to {@link ImageDetailActivity}
                 * Class {@link Image} should implement interface 'Parcelable'
                 *
                 * Reference:
                 * https://stackoverflow.com/questions/15790499/passing-arraylist-of-objects-between-activities
                 * https://medium.com/techmacademy/how-to-implement-and-use-a-parcelable-class-in-android-part-1-28cca73fc2d1
                 * */
                // Could not parcel folders with large number of images (like 3000), so not using ths method
                // instead passing folder path and calling 'getAllImagesByFolder' method from 'ImageDetailActivity' again
                //showImageDetail.putParcelableArrayListExtra("All images", (ArrayList<? extends Parcelable>) allImages);

                showImageDetail.putExtra("Image position", imageItemPosition);

                showImageDetail.putExtra("Folder path", folderPath);

                showImageDetail.putExtra("isSearchResults", isSearchResults);

                showImageDetail.putExtra("SearchFilter", searchFilter);

                startActivity(showImageDetail);

            }
        };

        RelativeLayout emptyView = findViewById(R.id.empty_view);

        if (displayImages.isEmpty()){
            // if data is empty, set "empty view"
            emptyView.setVisibility(View.VISIBLE);
            imageRecycler.setVisibility(View.GONE);
        }
        else {
            emptyView.setVisibility(View.GONE);
            imageRecycler.setVisibility(View.VISIBLE);

            // Get the adapter for the RecyclerView
            pictureAdapter = new ImageDisplayAdapter(displayImages, this, clickListenerObj);

            // Set the adapter to the RecyclerView
            imageRecycler.setAdapter(pictureAdapter);

        }

    }


}