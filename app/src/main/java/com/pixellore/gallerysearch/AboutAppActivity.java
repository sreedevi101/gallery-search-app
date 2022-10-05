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

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{developerEmailId});
                i.putExtra(Intent.EXTRA_SUBJECT, "GallerySearch");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AboutAppActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}