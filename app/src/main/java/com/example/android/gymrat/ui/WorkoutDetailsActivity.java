package com.example.android.gymrat.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.gymrat.R;
import com.example.android.gymrat.ads.ToastAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class WorkoutDetailsActivity extends AppCompatActivity {
    private AdView mAdView;
    private String workoutName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setAdListener(new ToastAdListener(this));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        workoutName = getIntent().getStringExtra("WORKOUT_NAME");
        setTitle(workoutName);
        WorkoutDetailsFragment fragment = WorkoutDetailsFragment.newInstance(workoutName);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_workout_details, fragment)
                .commit();
    }

}
