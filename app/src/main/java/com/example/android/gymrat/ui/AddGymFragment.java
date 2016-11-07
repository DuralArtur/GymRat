package com.example.android.gymrat.ui;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gymrat.R;
import com.example.android.gymrat.db.DBContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddGymFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener {
    @BindView(R.id.gymaddress_et)
    EditText addressET;
    @BindView(R.id.latt_et)
    EditText latET;
    @BindView(R.id.long_et)
    EditText longET;
    @BindView(R.id.gym_name_TV)
    TextView gymnameTV;

    @OnClick(R.id.add_exc_fab)
    public void addGymToDb() {
        if (!(addressET.getText().toString().isEmpty() ||
                latET.getText().toString().isEmpty() ||
                longET.getText().toString().isEmpty())) {
            new addGymTask().execute(gymName, addressET.getText().toString(), String.valueOf(latET.getText()), String.valueOf(longET.getText()));
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
        }

    }
    private boolean updateSuccess;
    private String gymName;
    private ContentResolver contentResolver;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addressList;
            if (!(lastLocation==null)) {
                try {
                    addressList = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                    addressET.setText(addressList.get(0).getAddressLine(0));
                    longET.setText("" + lastLocation.getLongitude());
                    latET.setText("" + lastLocation.getLatitude());
                } catch (IOException a) {
                    Log.d("LOG", getResources().getString(R.string.ioexception));
                }
            }
        }
        else {
            Toast.makeText(getContext(),getResources().getString(R.string.allow_loc),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("LOG",getResources().getString(R.string.conn_susp));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("LOG",getResources().getString(R.string.conn_fail));
    }

    public AddGymFragment() {
        // Required empty public constructor
    }

    public static AddGymFragment newInstance(String gymName) {
        Bundle args = new Bundle();
        args.putString("GYM_NAME", gymName);
        AddGymFragment fragment = new AddGymFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_gym, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            gymName = bundle.getString("GYM_NAME");
        }
        buildGoogleApiClient();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!TextUtils.isEmpty(gymName)) {
            gymnameTV.setText(gymName);
        }
        contentResolver = getContext().getContentResolver();
    }

    private class addGymTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {int pos;
            ContentValues values = new ContentValues();
            values.put(DBContract.GymEntry.COLUMN_GYM_NAME, params[0]);
            values.put(DBContract.GymEntry.COLUMN_GYM_ADDRESS, params[1]);
            values.put(DBContract.GymEntry.COLUMN_GYM_LATT, params[2]);
            values.put(DBContract.GymEntry.COLUMN_GYM_LONG, params[3]);
            Cursor cursor = contentResolver.query(DBContract.GymEntry.CONTENT_URI, new String [] {"MAX("+DBContract.GymEntry.COLUMN_POSITION + ")"}, null, null, null);
            if (cursor!=null){
                cursor.moveToFirst();
                pos = cursor.getInt(0);
            } else {
                pos = 0;
            }
            pos ++;
            values.put(DBContract.GymEntry.COLUMN_POSITION,pos);
            Uri insertedUri = contentResolver.insert(
                    DBContract.GymEntry.CONTENT_URI,
                    values);
            updateSuccess =true;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (updateSuccess) {
                Toast.makeText(getContext(),getResources().getString(R.string.gym_name),Toast.LENGTH_SHORT).show();
                getActivity().finish();
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.gym_not_added), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
