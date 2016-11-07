package com.example.android.gymrat.ui;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.gymrat.GeoFenceTransitionIntentService;
import com.example.android.gymrat.R;
import com.example.android.gymrat.ads.ToastAdListener;
import com.example.android.gymrat.db.DBContract;
import com.example.android.gymrat.objects.Gym;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {
    public boolean mTwoPanes;
    private AdView mAdView;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private ArrayList<Geofence> geofenceList;
    private List<Gym> gymList;
    private List<Address> addressList;
    protected static final String TAG = "Location String";
    private Context context;
    private LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        geofenceList = new ArrayList<>();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        CategoryAdapter adapter = new CategoryAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        if (findViewById(R.id.context_container) != null) {
            mTwoPanes = true;
        }
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setAdListener(new ToastAdListener(this));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        buildGoogleApiClient();
        try {

        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        new getFencesTask().execute();
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.v("LOG",getResources().getString(R.string.conn_susp));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("LOG",getResources().getString(R.string.conn_fail));
    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void populateGeofenceList() {
        for (Gym gym : gymList) {
            try {
                geofenceList.add(new Geofence.Builder().setRequestId(gym.getName())
                        .setCircularRegion(gym.getLatitude(), gym.getLongitude(), 100)
                        .setExpirationDuration(12 * 60 * 60 * 1000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
            } catch (IllegalArgumentException e) {
                Log.e("Error", "Illegal argument" + e + gym.getName());
            }
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeoFenceTransitionIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    public void onResult(Status status) {
    }

    public List<Gym> getGyms() {
        List<Gym> gyms = new ArrayList<>();
        String gymName;
        String gymAddress;
        double gymLat;
        double gymLong;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(DBContract.GymEntry.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {

                gymName = cursor.getString(cursor.getColumnIndex(DBContract.GymEntry.COLUMN_GYM_NAME));
                gymAddress = cursor.getString(cursor.getColumnIndex(DBContract.GymEntry.COLUMN_GYM_ADDRESS));
                gymLat = cursor.getDouble(cursor.getColumnIndex(DBContract.GymEntry.COLUMN_GYM_LATT));
                gymLong = cursor.getDouble(cursor.getColumnIndex(DBContract.GymEntry.COLUMN_GYM_LONG));
                gyms.add(new Gym(gymName, gymAddress, gymLat, gymLong));
            } while (cursor.moveToNext());
        }

        return gyms;
    }


    public class getFencesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            int permissionCheck = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                gymList = getGyms();
                if (!gymList.isEmpty()) {
                    populateGeofenceList();
                    LocationServices.GeofencingApi.removeGeofences(googleApiClient, getGeofencePendingIntent());
                    LocationServices.GeofencingApi.addGeofences(
                            googleApiClient,
                            // The GeofenceRequest object.
                            getGeofencingRequest(),
                            // A pending intent that that is reused when calling removeGeofences(). This
                            // pending intent is used to generate an intent when a matched geofence
                            // transition is observed.
                            getGeofencePendingIntent()
                    ).setResultCallback(MainActivity.this); // Result processed in onResult().
                }
            }
            return null;
        }
    }
}

