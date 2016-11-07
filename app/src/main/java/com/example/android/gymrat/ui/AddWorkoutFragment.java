package com.example.android.gymrat.ui;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.Voice;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddWorkoutFragment extends Fragment {
    @BindView(R.id.sets_et)
    EditText sets_et;
    @BindView(R.id.reps_et)
    EditText reps_et;
    @BindView(R.id.exc_et)
    EditText exc_et;
    @BindView(R.id.workout_name_TV)
    TextView workoutName;

    @OnClick(R.id.add_exc_fab)
    public void addExcToDb() {
        if (!(exc_et.getText().toString().isEmpty() ||
                reps_et.getText().toString().isEmpty() ||
                sets_et.getText().toString().isEmpty())) {
            exercise = exc_et.getText().toString();
            reps = reps_et.getText().toString();
            sets = sets_et.getText().toString();
            new addWorkoutTask().execute(exercise, reps, sets);
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean updateSuccess;
    private String exercise;
    private String reps;
    private String sets;
    private String mWorkoutName;
    private ContentResolver contentResolver;

    public AddWorkoutFragment() {
        // Required empty public constructor
    }

    public static AddWorkoutFragment newInstance(String workoutName) {
        Bundle args = new Bundle();
        args.putString("WORKOUT_NAME", workoutName);
        AddWorkoutFragment fragment = new AddWorkoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_workout, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mWorkoutName = bundle.getString("WORKOUT_NAME");
        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!TextUtils.isEmpty(mWorkoutName)) {
            workoutName.setText(mWorkoutName);
        }
        contentResolver = getContext().getContentResolver();
    }

    private class addWorkoutTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            int exc_pos;
            int wo_pos;
            ContentValues values = new ContentValues();
            values.put(DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME, mWorkoutName);
            values.put(DBContract.WorkoutEntry.COLUMN_EXERCISE, params[0]);
            values.put(DBContract.WorkoutEntry.COLUMN_REPS, params[1]);
            values.put(DBContract.WorkoutEntry.COLUMN_SET, params[2]);
            Cursor checkCursor = contentResolver.query(DBContract.WorkoutEntry.CONTENT_URI,
                    null,
                    DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME + "=? and "
                            + DBContract.WorkoutEntry.COLUMN_EXERCISE + "=?",
                    new String[]{mWorkoutName, params[0]},
                    null);
            if (checkCursor.getCount() > 0 && checkCursor.moveToFirst()) {
                updateSuccess = false;

            } else {
                Cursor woCursor = contentResolver.query(DBContract.WorkoutEntry.CONTENT_URI,
                        new String[]{"DISTINCT " + DBContract.WorkoutEntry.COLUMN_WO_POSITION},
                        DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME + "=?",
                        new String[]{mWorkoutName},
                        null);
                if (woCursor.getCount() > 0 && woCursor.moveToFirst()) {
                    woCursor.moveToFirst();
                    wo_pos = woCursor.getInt(0);
                } else {
                    woCursor = contentResolver.query(DBContract.WorkoutEntry.CONTENT_URI,
                            new String[]{"MAX(" + DBContract.WorkoutEntry.COLUMN_WO_POSITION + ")"},
                            null,
                            null,
                            null);
                    if (woCursor.getCount() > 0 && woCursor.moveToFirst()) {
                        woCursor.moveToFirst();
                        wo_pos = woCursor.getInt(0) + 1;
                    } else {
                        wo_pos = 0;
                    }
                }
                Cursor excCursor = contentResolver.query(DBContract.WorkoutEntry.CONTENT_URI,
                        new String[]{"MAX(" + DBContract.WorkoutEntry.COLUMN_EXC_POSITION + ")"},
                        DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME + "=?",
                        new String[]{mWorkoutName},
                        null);
                if (excCursor.getCount() > 0 && excCursor.moveToFirst()) {
                    excCursor.moveToFirst();
                    exc_pos = excCursor.getInt(0) + 1;
                } else {
                    exc_pos = 0;
                }
                woCursor.close();
                excCursor.close();
                values.put(DBContract.WorkoutEntry.COLUMN_EXC_POSITION, exc_pos);
                values.put(DBContract.WorkoutEntry.COLUMN_WO_POSITION, wo_pos);
                Uri insertedUri = contentResolver.insert(
                        DBContract.WorkoutEntry.CONTENT_URI,
                        values);
                updateSuccess = true;

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (updateSuccess) {
                Toast.makeText(getContext(),getResources().getString(R.string.exc_added),Toast.LENGTH_SHORT).show();
                exc_et.setText("");
                sets_et.setText("");
                reps_et.setText("");
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.already_exist), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
