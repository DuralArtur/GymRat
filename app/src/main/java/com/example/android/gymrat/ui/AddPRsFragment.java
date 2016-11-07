package com.example.android.gymrat.ui;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.gymrat.R;
import com.example.android.gymrat.db.DBContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPRsFragment extends Fragment {
    @BindView(R.id.exercise_spinner)
    Spinner spinner;
    @BindView(R.id.weight_et)
    EditText weightET;
    @BindView(R.id.reps_et)
    EditText reps_et;
    @BindView(R.id.vid_et)
    EditText vidET;

    @OnClick(R.id.add_exc_fab)
    public void addPRToDb() {
        if (!(spinner.getSelectedItem()==null ||
                reps_et.getText().toString().isEmpty() ||
                weightET.getText().toString().isEmpty())) {
            new addPRTask().execute(spinner.getSelectedItem().toString(), reps_et.getText().toString(), weightET.getText().toString(), vidET.getText().toString());
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
        }
        // Required empty public constructor


    }
private boolean updateSuccess;
    private ContentResolver contentResolver;

    public AddPRsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_prs, container, false);
        ButterKnife.bind(this, view);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentResolver = getContext().getContentResolver();
        getExercises();
    }

    public void getExercises() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        ContentResolver contentResolver = getContext().getContentResolver();

        String projection[] = {"DISTINCT " + DBContract.WorkoutEntry.COLUMN_EXERCISE};
        Cursor cursor = contentResolver.query(DBContract.WorkoutEntry.CONTENT_URI, projection, null, null, null);
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                spinnerAdapter.add(cursor.getString(cursor.getColumnIndex(DBContract.WorkoutEntry.COLUMN_EXERCISE)));
            } while (cursor.moveToNext());
        } else {
            spinnerAdapter.clear();
            Toast.makeText(getContext(),getResources().getString(R.string.add_workouts_first),Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        spinnerAdapter.notifyDataSetChanged();
    }

    private class addPRTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            int pos;
            int max;

            Cursor checkCursor = contentResolver.query(DBContract.PREntry.CONTENT_URI,
                    null,
                    DBContract.PREntry.COLUMN_PR_EXERCISE + "=?",
                    new String[]{params[0]},
                    null);
            if (checkCursor.getCount() > 0 && checkCursor.moveToFirst()) {
                updateSuccess = false;

            } else {
                ContentValues values = new ContentValues();

                values.put(DBContract.PREntry.COLUMN_PR_EXERCISE, params[0]);
                values.put(DBContract.PREntry.COLUMN_PR_REPS, params[1]);
                values.put(DBContract.PREntry.COLUMN_PR_WEIGHT, params[2]);
                if (!params[3].isEmpty()) {
                    values.put(DBContract.PREntry.COLUMN_VID_ADDRESS, params[3]);
                }
                Cursor cursor = contentResolver.query(DBContract.PREntry.CONTENT_URI, new String[]{"MAX(" + DBContract.PREntry.COLUMN_POSITION + ")"}, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    max = cursor.getInt(0);
                } else {
                    max = 0;
                }
                max++;
                values.put(DBContract.PREntry.COLUMN_POSITION, max);
                updateSuccess = true;
                Uri insertedUri = contentResolver.insert(
                        DBContract.PREntry.CONTENT_URI,
                        values);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (updateSuccess) {
                Toast.makeText(getContext(), getResources().getString(R.string.pr_added), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.pr_not_added), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
