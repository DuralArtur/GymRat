package com.example.android.gymrat.ui;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gymrat.R;
import com.example.android.gymrat.db.DBContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Artur on 14-Oct-16.
 */

public class WorkoutAdapter extends
        RecyclerView.Adapter<WorkoutAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView woNameTv;
        private TextView excTv;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            woNameTv = ButterKnife.findById(itemView,R.id.gym_name_TV);
            excTv = ButterKnife.findById(itemView,R.id.gym_address_TV);
        }
    }

    private List<String> workouts;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public WorkoutAdapter(Context context, List<String> workouts) {
        this.workouts = workouts;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }
    @Override
    public WorkoutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.gym_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final WorkoutAdapter.ViewHolder viewHolder, final int position) {
        TextView woNameTv = viewHolder.woNameTv;
        TextView excTv = viewHolder.excTv;
        excTv.setVisibility(View.GONE);
        woNameTv.setText(workouts.get(viewHolder.getAdapterPosition()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),WorkoutDetailsActivity.class);
                intent.putExtra("WORKOUT_NAME",workouts.get(viewHolder.getAdapterPosition()));
                getContext().startActivity(intent);

                Toast.makeText(getContext(),workouts.get(viewHolder.getAdapterPosition()),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        int deletedRows = getContext().getContentResolver().delete(DBContract.WorkoutEntry.CONTENT_URI, DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME + "=?", new String[]{workouts.get(position)});
        workouts.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        Collections.swap(workouts, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        for (int i = 0; i<workouts.size();i++) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(DBContract.WorkoutEntry.CONTENT_URI);
            builder.withValue(DBContract.WorkoutEntry.COLUMN_WO_POSITION,i);
            builder.withYieldAllowed(true);
            builder.withSelection(DBContract.WorkoutEntry.COLUMN_WORKOUT_NAME + "=?",new String[]{workouts.get(i)});
            contentProviderOperations.add(builder.build());
        }

        try {
            ContentProviderResult[] applyBatch = getContext().getContentResolver().applyBatch(DBContract.CONTENT_AUTHORITY, contentProviderOperations);
        } catch (RemoteException e) {
            Log.e("Error","Remote Exception " + e);
        } catch (OperationApplicationException e) {
            Log.e("Error","Operation Application Error " + e);
        }
        return true;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        if (workouts == null) {
            return 0;
        } else {
            return workouts.size();
        }
    }
}


