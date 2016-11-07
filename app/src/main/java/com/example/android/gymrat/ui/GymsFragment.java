package com.example.android.gymrat.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.gymrat.R;
import com.example.android.gymrat.db.DBContract;
import com.example.android.gymrat.objects.Gym;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class GymsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.empty_gym_TV)
    TextView empty;

    @OnClick(R.id.add_fab)
    public void addGym() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getResources().getString(R.string.gym_name));
        final EditText input = new EditText(getActivity());
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!TextUtils.isEmpty(input.getText().toString())) {
                    Intent addGymIntent = new Intent(getActivity(), AddGymActivity.class);
                    addGymIntent.putExtra("GYM_NAME", input.getText().toString());
                    startActivity(addGymIntent);
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private List<Gym> myGyms;
    private RecyclerView recyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private GymAdapter gymAdapter;

    public GymsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gyms, container, false);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this, rootView);
        myGyms = new ArrayList<>();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = ButterKnife.findById(view, R.id.gym_recycler_view);
        gymAdapter = new GymAdapter(getContext(), myGyms);
        recyclerView.setAdapter(gymAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(gymAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        empty.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().initLoader(2, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                DBContract.GymEntry.CONTENT_URI,
                null,
                null,
                null,
                DBContract.GymEntry.COLUMN_POSITION + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String gymName;
        String gymAddress;
        double gymLat;
        double gymLong;
        if (data.getCount() > 0 && data.moveToFirst()) {
            data.moveToFirst();
            if (!(myGyms == null)) {
                myGyms.clear();
            }
            do {
                gymName = data.getString(data.getColumnIndex(DBContract.GymEntry.COLUMN_GYM_NAME));
                gymAddress = data.getString(data.getColumnIndex(DBContract.GymEntry.COLUMN_GYM_ADDRESS));
                gymLat = data.getDouble(data.getColumnIndex(DBContract.GymEntry.COLUMN_GYM_LATT));
                gymLong = data.getDouble(data.getColumnIndex(DBContract.GymEntry.COLUMN_GYM_LONG));
                myGyms.add(new Gym(gymName, gymAddress, gymLat, gymLong));
            } while (data.moveToNext());
            data.close();
        }
        else {
            empty.setVisibility(View.VISIBLE);
        }
        getActivity().getSupportLoaderManager().destroyLoader(2);
        gymAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        gymAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
