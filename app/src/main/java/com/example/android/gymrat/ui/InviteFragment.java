package com.example.android.gymrat.ui;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.gymrat.R;
import com.example.android.gymrat.db.DBContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.android.gymrat.ui.WorkoutDetailsFragment.woName;

/**
 * A simple {@link Fragment} subclass.
 */
public class InviteFragment extends Fragment {
    @BindView(R.id.wo_CB)
    CheckBox woCB;
    @BindView(R.id.workouts_spinner)
    Spinner spinner;
    @BindView(R.id.time_Button)
    Button timeButton;
    @BindView(R.id.date_Button)
    Button dateButton;

    @OnClick(R.id.time_Button)
    public void openTimePicker() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getActivity().getFragmentManager(), "timePicker");
    }

    @OnClick(R.id.date_Button)
    public void openDatePicker() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getFragmentManager(), "datePicker");
    }

    @OnClick(R.id.share_fab)
    public void sendInvite() {
        if (timeButton.getText().toString().equals("") || dateButton.getText().toString().equals("")){
            Toast.makeText(getContext(), getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
        } else {
            woName = null;
            if (!(spinner.getSelectedItem() == null)) {
                woName = spinner.getSelectedItem().toString();
            }
            new getExercisesTask().execute(woCB.isChecked());
        }
    }

    private static Calendar c;
    ArrayAdapter<String> spinnerAdapter;
    List<String> workouts;
    List<String> exercises;
    private String woName;

    public InviteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invite, container, false);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this, view);
        c = Calendar.getInstance();
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        woCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                new getWorkoutsTask().execute(b);
            }
        });
        return view;
    }


    public void showDialog() {

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Button timeButton = ButterKnife.findById(getActivity(), R.id.time_Button);
            timeButton.setText(hourOfDay + ":" + minute);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Button dateButton = ButterKnife.findById(getActivity(), R.id.date_Button);
            dateButton.setText(month + "-" + day + "-" + year);
        }

    }


    private class getWorkoutsTask extends AsyncTask<Boolean, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Boolean... bools) {
            workouts = new ArrayList<>();
            if (bools[0]) {
                ContentResolver contentResolver = getContext().getContentResolver();

                String projection[] = {"DISTINCT " + DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME};
                Cursor cursor = contentResolver.query(DBContract.WorkoutEntry.CONTENT_URI, projection, null, null, null);
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    cursor.moveToFirst();
                    do {
                        workouts.add(cursor.getString(cursor.getColumnIndex(DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME)));
                    } while (cursor.moveToNext());
                }
            }
            return workouts;
        }

        @Override
        protected void onPostExecute(List<String> workouts) {
            spinnerAdapter.clear();
            if (workouts.size() > 0) {

                spinnerAdapter.addAll(workouts);

            }
            spinnerAdapter.notifyDataSetChanged();


        }
    }

    private class getExercisesTask extends AsyncTask<Boolean, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Boolean... bools) {
            exercises = new ArrayList<>();
            if (bools[0]) {
                ContentResolver contentResolver = getContext().getContentResolver();

                String projection[] = {"DISTINCT " + DBContract.WorkoutEntry.COLUMN_EXERCISE};
                Cursor cursor = contentResolver.query(DBContract.WorkoutEntry.CONTENT_URI, projection, DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME + "=?" ,
                        new String[]{woName}, DBContract.WorkoutEntry.COLUMN_EXC_POSITION + " ASC");
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    cursor.moveToFirst();
                    do {
                        exercises.add(cursor.getString(cursor.getColumnIndex(DBContract.WorkoutEntry.COLUMN_EXERCISE)));
                    } while (cursor.moveToNext());
                }
            }
            return exercises;

        }

        @Override
        protected void onPostExecute(List<String> exercises) {
            StringBuilder sb = new StringBuilder();
            sb.append(getResources().getString(R.string.invite_p1));
            sb.append(dateButton.getText().toString()+ " ");
            sb.append(timeButton.getText().toString() + ".");
            if (exercises.size()>0){
                sb.append(getResources().getString(R.string.invite_p2));
                for(int i=0;i<exercises.size();i++){
                    sb.append(exercises.get(i) + ", ");
                }
            }
            sb.append(getResources().getString(R.string.invite_p3));
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,sb.toString());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
    }
}

