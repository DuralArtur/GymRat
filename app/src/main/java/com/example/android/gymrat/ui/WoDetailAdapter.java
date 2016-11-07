package com.example.android.gymrat.ui;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.gymrat.R;
import com.example.android.gymrat.db.DBContract;
import com.example.android.gymrat.objects.Gym;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Artur on 22-Oct-16.
 */

public class WoDetailAdapter extends
        RecyclerView.Adapter<WoDetailAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView exerciseTV;
        private TextView setsTv;

        public ViewHolder(View itemView) {
            super(itemView);
            exerciseTV = ButterKnife.findById(itemView, R.id.exc_TV);
            setsTv = ButterKnife.findById(itemView,R.id.sets_reps_tv);
        }
    }

    private List<Gym> exercises;
    private Context context;

    public WoDetailAdapter(Context context, List<Gym> exercises) {
        this.context = context;
        this.exercises = exercises;

    }

    private Context getContext() {
        return context;
    }

    @Override
    public WoDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.checkbox_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final WoDetailAdapter.ViewHolder viewHolder, final int position) {
        TextView exerciseTV = viewHolder.exerciseTV;
        TextView setsrepsTV = viewHolder.setsTv;
        String sets = String.format(getContext().getResources().getString(R.string.setsreps),
                exercises.get(viewHolder.getAdapterPosition()).getLatitude(),exercises.get(viewHolder.getAdapterPosition()).getLongitude());
        exerciseTV.setText(exercises.get(viewHolder.getAdapterPosition()).getAddress());
        setsrepsTV.setText(sets);

    }

    @Override
    public void onItemDismiss(int position) {
        int deletedRows = getContext().getContentResolver().delete(DBContract.PREntry.CONTENT_URI, DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME + "=? AND "
                + DBContract.WorkoutEntry.COLUMN_EXERCISE + "=?", new String[]{exercises.get(position).getName(), exercises.get(position).getAddress()});
        exercises.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        Collections.swap(exercises, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        for (int i = 0; i < exercises.size(); i++) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(DBContract.WorkoutEntry.CONTENT_URI);
            builder.withValue(DBContract.WorkoutEntry.COLUMN_EXC_POSITION, i);
            builder.withYieldAllowed(true);
            builder.withSelection(DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME + "=? AND "
                    + DBContract.WorkoutEntry.COLUMN_EXERCISE + "=?", new String[]{exercises.get(i).getName(), exercises.get(i).getAddress()});
            contentProviderOperations.add(builder.build());
        }

        try {
            ContentProviderResult[] applyBatch = getContext().getContentResolver().applyBatch(DBContract.CONTENT_AUTHORITY, contentProviderOperations);
        } catch (RemoteException e) {
            Log.e("Error", "Remote Exception " + e);
        } catch (OperationApplicationException e) {
            Log.e("Error", "Operation Application Error " + e);
        }
        return true;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return exercises.size();
    }

}


