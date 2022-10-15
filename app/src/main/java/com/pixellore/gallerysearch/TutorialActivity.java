package com.pixellore.gallerysearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.pixellore.gallerysearch.utils.TutorialPage;
import com.pixellore.gallerysearch.utils.TutorialRecyclerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

/**
 * Activity holding the ViewPager2 for the app introduction (Tutorial)
 *
 * The data to be displayed in each page of the ViewPager2 is defined in strings.xml and illustrations
 * in mipmap folder. The data of each page tied together by {@link com.pixellore.gallerysearch.utils.TutorialPage} class
 * The adapter feeds the data to the view pager.
 *
 * ViewPager2 also contains a TabLayout of dots. The layout of dots defined in drawable folder
 * */
public class TutorialActivity extends AppCompatActivity {


    // Define data to be displayed as ArrayList
    ArrayList<TutorialPage> tutorialPages = new ArrayList<TutorialPage>();

    ViewPager2 tutorialViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        defineData();

        // Get the view pager
        tutorialViewPager = findViewById(R.id.tutorial_viewpager);

        /**
         * Create an instance of {@link com.pixellore.gallerytutorial.TutorialRecyclerAdapter.OnItemClickListener}
         * interface and define the logic for what to occur when a click happens, in the onItemClick method
         * */
        TutorialRecyclerAdapter.OnItemClickListener clickListenerObj = new TutorialRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickNextDone(TutorialPage pageItem) {
                if (pageItem.isNextPageAvailable()){
                    // https://stackoverflow.com/a/13285950
                    tutorialViewPager.setCurrentItem(getItem(+1),true);
                } else {
                    // If click detected at last page, close TutorialActivity and go back to MainActivity
                    Intent move = new Intent(TutorialActivity.this, MainActivity.class);
                    startActivity(move);
                }

            }

            @Override
            public void onItemClickSkip(TutorialPage pageItem) {
                // If click detected, close TutorialActivity and go back to MainActivity
                Intent move = new Intent(TutorialActivity.this, MainActivity.class);
                startActivity(move);
            }
        };


        // Get the view pager adapter
        TutorialRecyclerAdapter adapter = new TutorialRecyclerAdapter(tutorialPages, TutorialActivity.this, clickListenerObj);

        // Attach adapter to view pager
        // (Do this before attaching tab layout to view pager)
        tutorialViewPager.setAdapter(adapter);


        // Get TabLayout
        TabLayout tabLayout = findViewById(R.id.page_dots_tab);

        // Connect TabLayout to ViewPager2
        // https://stackoverflow.com/questions/38459309/how-do-you-create-an-android-view-pager-with-a-dots-indicator
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, tutorialViewPager,
                true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        });
        tabLayoutMediator.attach();


    }


    private int getItem(int i) {
        return tutorialViewPager.getCurrentItem() + i;
    }

    public void defineData() {

        tutorialPages.add(new TutorialPage(R.string.page0_head, R.string.page0_content,
                R.mipmap.gallery_search_icon, true, R.color.darkblue, R.color.white));
        tutorialPages.add(new TutorialPage(R.string.page1_head, R.string.page1_content,
                R.mipmap.tutorial_slides_browse_folders, true, R.color.white, R.color.darkblue));
        tutorialPages.add(new TutorialPage(R.string.page2_head, R.string.page2_content,
                R.mipmap.tutorial_slides_image_actions, true, R.color.black, R.color.white));
        tutorialPages.add(new TutorialPage(R.string.page3_head, R.string.page3_content,
                R.mipmap.tutorial_slides_tags, true, R.color.darkblue, R.color.white));
        tutorialPages.add(new TutorialPage(R.string.page4_head, R.string.page4_content,
                R.mipmap.tutorial_slides_search, false, R.color.white, R.color.darkblue));

    }

}