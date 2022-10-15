package com.pixellore.gallerysearch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AboutAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        TextView versionText = findViewById(R.id.about_page_version);
        versionText.setText("Version: " + BuildConfig.VERSION_NAME);

        /**
         * Setup action bar/ tool bar
         *
         * reference: https://developer.android.com/training/appbar/setting-up
         * */
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarAboutActivity);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // Change title
        String actionBarTitle = ab.getTitle().toString();
        ab.setTitle("About " + actionBarTitle);

        // Introduction
        TextView introductionTextView = (TextView) this.findViewById(R.id.about_page_introducton);

        introductionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // open tutorial slides
                //When the button is clicked open the activity for the tutorial
                Intent move = new Intent(AboutAppActivity.this, TutorialActivity.class);
                startActivity(move);
            }

        });

        // User Notes
        TextView userNotesTextView = (TextView) this.findViewById(R.id.about_user_manual);

        userNotesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // open tutorial slides
                String userManualLink = "https://github.com/sreedevi101/gallery-search-app/blob/main/User%20Guide.md";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(userManualLink));
                startActivity(browserIntent);
            }

        });

        // Privacy policy
        TextView privacyPolicyTextView = (TextView) this.findViewById(R.id.about_page_privacy_policy);

        privacyPolicyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // open link
                String privacyPolicyLink = "https://github.com/sreedevi101/gallery-search-app/blob/main/Privacy%20Policy.md";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyLink));
                startActivity(browserIntent);
            }

        });


        TextView termsTextView = (TextView) this.findViewById(R.id.about_page_terms_conditions);

        termsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // open link
                String termsConditionsLink = "https://github.com/sreedevi101/gallery-search-app/blob/main/Terms%20%26%20Conditions.md";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(termsConditionsLink));
                startActivity(browserIntent);
            }

        });


        TextView thirdPartyTextView = (TextView) this.findViewById(R.id.about_page_third_parties);

        thirdPartyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // open link
                String thirdPartiesLink = "https://github.com/sreedevi101/gallery-search-app/blob/main/Third%20Parties.md";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(thirdPartiesLink));
                startActivity(browserIntent);
            }

        });


        TextView contactTextView = (TextView) this.findViewById(R.id.about_page_contact);

        contactTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // send email
                String developerEmailId = "sreedevi.appdev@gmail.com";
                String subject = "GallerySearch";

                Intent i = new Intent(Intent.ACTION_SENDTO);
                //i.setType("text/plain"); // or:
                i.setData(Uri.parse("mailto:")); // only email apps should handle this
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{developerEmailId});
                i.putExtra(Intent.EXTRA_SUBJECT, subject);
                try{
                    startActivity(i);
                } catch(android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AboutAppActivity.this,
                            "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
}