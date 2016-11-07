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
import android.widget.Toast;

import com.example.android.gymrat.R;
import com.example.android.gymrat.db.DBContract;
import com.example.android.gymrat.objects.Gym;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkoutDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @OnClick(R.id.add_fab)
    public void addWorkout() {
                        Intent addWorkoutIntent = new Intent(getActivity(), AddWorkoutActivity.class);
                        addWorkoutIntent.putExtra("WORKOUT_NAME", woName);
                        startActivity(addWorkoutIntent);
                }
    public static String woName;
    private List<Gym> myExercises = new ArrayList<>();
    private RecyclerView recyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private WoDetailAdapter woDetailAdapter;


    public static WorkoutDetailsFragment newInstance(String workoutName) {
        Bundle args = new Bundle();
        args.putString("WORKOUT_NAME", workoutName);
        WorkoutDetailsFragment fragment = new WorkoutDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout_details, null, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            woName = bundle.getString("WORKOUT_NAME");

        }


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = ButterKnife.findById(view, R.id.wo_recycler_view);
        woDetailAdapter = new WoDetailAdapter(getContext(), myExercises);
        recyclerView.setAdapter(woDetailAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(woDetailAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

//        recyclerView.setHasFixedSize(true);


    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().initLoader(4, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                DBContract.WorkoutEntry.CONTENT_URI,
                null,
                DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME + "=?",
                new String[]{woName}, DBContract.WorkoutEntry.COLUMN_EXC_POSITION + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String workoutName;
        String excName;
        double sets;
        double reps;

        if (data.getCount() > 0 && data.moveToFirst()) {
            data.moveToFirst();
            if (!(myExercises == null)) {
                myExercises.clear();
            }
            do {
                workoutName = data.getString(data.getColumnIndex(DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME));
                excName = data.getString(data.getColumnIndex(DBContract.WorkoutEntry.COLUMN_EXERCISE));
                sets = data.getInt(data.getColumnIndex(DBContract.WorkoutEntry.COLUMN_SET));
                reps = data.getInt(data.getColumnIndex(DBContract.WorkoutEntry.COLUMN_REPS));
                myExercises.add(new Gym(workoutName, excName, sets, reps));
            } while (data.moveToNext());
        }
        data.close();
        getActivity().getSupportLoaderManager().destroyLoader(4);
        woDetailAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
