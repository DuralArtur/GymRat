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
import android.widget.Toast;

import com.example.android.gymrat.R;
import com.example.android.gymrat.db.DBContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.empty_wo_TV)
    TextView empty;

    @OnClick(R.id.add_fab)
    public void addWorkout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getResources().getString(R.string.workout_name));
        final EditText input = new EditText(getActivity());
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!TextUtils.isEmpty(input.getText().toString())) {
                    Cursor excCursor = getContext().getContentResolver().query(DBContract.WorkoutEntry.CONTENT_URI,
                            null,
                            DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME + "=?",
                            new String[]{input.getText().toString()}, null);
                    if (excCursor.getCount() == 0 || !(excCursor.moveToFirst())) {
                        Intent addWorkoutIntent = new Intent(getActivity(), AddWorkoutActivity.class);
                        addWorkoutIntent.putExtra("WORKOUT_NAME", input.getText().toString());
                        startActivity(addWorkoutIntent);
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.workout_already_exists), Toast.LENGTH_SHORT).show();
                        addWorkout();
                    }
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }

    private ItemTouchHelper itemTouchHelper;
    private List<String> myWorkouts;
    private WorkoutAdapter workoutAdapter;
    public WorkoutsFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_workouts, container, false);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this, rootView);
        myWorkouts = new ArrayList<>();
        empty.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = ButterKnife.findById(view, R.id.wo_recycler_view);
        workoutAdapter = new WorkoutAdapter(getContext(), myWorkouts);
        recyclerView.setAdapter(workoutAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(workoutAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getSupportLoaderManager().initLoader(3, null, this);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {"DISTINCT " + DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME};
        return new CursorLoader(getContext(),
                DBContract.WorkoutEntry.CONTENT_URI,
                projection,
                null,
                null,
                DBContract.WorkoutEntry.COLUMN_WO_POSITION + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.getCount() > 0 && data.moveToFirst()) {
            data.moveToFirst();
            if (!(myWorkouts == null)) {
                myWorkouts.clear();
            }
            do {
                myWorkouts.add(data.getString(data.getColumnIndex(DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME)));
            } while(data.moveToNext());

            workoutAdapter.notifyDataSetChanged();
        } else {
            empty.setVisibility(View.VISIBLE);
        }
        getActivity().getSupportLoaderManager().destroyLoader(3);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
            workoutAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

