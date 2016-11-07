package com.example.android.gymrat.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.gymrat.R;
import com.example.android.gymrat.ads.ToastAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AddPRActivity extends AppCompatActivity {
    private AdView mAdView;
    private String mWorkoutName;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pr);
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setAdListener(new ToastAdListener(this));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
//        mWorkoutName = getIntent().getStringExtra("PR_NAME");
//        AddWorkoutFragment fragment = AddWorkoutFragment.newInstance(mWorkoutName);
//
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_addpr, new AddPRsFragment())
                .commit();
    }
}
