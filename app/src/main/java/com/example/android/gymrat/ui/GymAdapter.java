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

public class GymAdapter extends
        RecyclerView.Adapter<GymAdapter.ViewHolder> implements ItemTouchHelperAdapter{

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTv;
        private TextView addressTv;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTv = ButterKnife.findById(itemView, R.id.gym_name_TV);
            addressTv = ButterKnife.findById(itemView, R.id.gym_address_TV);
        }
    }

    private List<Gym> gyms;
    // Store the context for easy access
    private Context context;

    // Pass in the contact array into the constructor
    public GymAdapter(Context context, List<Gym> gyms) {
        this.context = context;
        this.gyms = gyms;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    @Override
    public GymAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(final GymAdapter.ViewHolder viewHolder, final int position) {
        TextView nameTv = viewHolder.nameTv;
        TextView addressTv = viewHolder.addressTv;
        nameTv.setText(gyms.get(viewHolder.getAdapterPosition()).getName());
        addressTv.setText(gyms.get(viewHolder.getAdapterPosition()).getAddress());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getContext(),WorkoutDetailsActivity.class);
//                intent.putExtra("WORKOUT_NAME",prs.get(position).getName());
//                getContext().startActivity(intent);
//
//                Toast.makeText(getContext(),prs.get(position).getName(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onItemDismiss(int position) {
        int deletedRows = getContext().getContentResolver().delete(DBContract.GymEntry.CONTENT_URI, DBContract.GymEntry.COLUMN_GYM_NAME + "=?", new String[]{gyms.get(position).getName()});
        notifyItemRemoved(position);
        gyms.remove(position);

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        Collections.swap(gyms, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        for (int i = 0; i<gyms.size();i++) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(DBContract.GymEntry.CONTENT_URI);
            builder.withValue(DBContract.GymEntry.COLUMN_POSITION,i);
            builder.withYieldAllowed(true);
            builder.withSelection(DBContract.GymEntry.COLUMN_GYM_NAME + "=?",new String[]{gyms.get(i).getName()});
            contentProviderOperations.add(builder.build());
        }

        try {
            ContentProviderResult[] applyBatch = getContext().getContentResolver().applyBatch(DBContract.CONTENT_AUTHORITY, contentProviderOperations);
        } catch (RemoteException e) {
            Log.e("LOG",""+e);
        } catch (OperationApplicationException e) {
            Log.e("LOG","" + e);
        }
        return true;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        if (gyms == null) {
            return 0;
        } else {
            return gyms.size();
        }
    }


}


