package com.example.android.gymrat.ui;

import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gymrat.R;
import com.example.android.gymrat.db.DBContract;
import com.example.android.gymrat.objects.PR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Artur on 22-Oct-16.
 */

public class PRAdapter extends
        RecyclerView.Adapter<PRAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView exerciseTV;
        private TextView totalTV;
        private ImageView playIV;

        public ViewHolder(View itemView) {
            super(itemView);
            exerciseTV = ButterKnife.findById(itemView, R.id.pr_exercise_TV);
            totalTV = ButterKnife.findById(itemView, R.id.pr_total);
            playIV = ButterKnife.findById(itemView, R.id.pr_play);
        }
    }

    private List<PR> prs;
    private Context context;

    public PRAdapter(Context context, List<PR> prs) {
        this.context = context;
        this.prs = prs;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public PRAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.pr_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final PRAdapter.ViewHolder viewHolder, final int position) {
        TextView exerciseTV = viewHolder.exerciseTV;
        TextView totalTV = viewHolder.totalTV;
        ImageView playIV = viewHolder.playIV;
        exerciseTV.setText(prs.get(viewHolder.getAdapterPosition()).getExercise());
        totalTV.setText(String.valueOf(prs.get(viewHolder.getAdapterPosition()).getWeight()) +" x " + String.valueOf(prs.get(viewHolder.getAdapterPosition()).getReps()));
        if (prs.get(viewHolder.getAdapterPosition()).isHasVid()) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(prs.get(viewHolder.getAdapterPosition()).getVidLink())));
                    } catch (ActivityNotFoundException ex) {
                        Toast.makeText(getContext(),getContext().getResources().getString(R.string.vid_err),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(),getContext().getResources().getString(R.string.no_vid),Toast.LENGTH_SHORT).show();
                }
            });
            playIV.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        int deletedRows = getContext().getContentResolver().delete(DBContract.PREntry.CONTENT_URI, DBContract.PREntry.COLUMN_PR_EXERCISE + "=?", new String[]{prs.get(position).getExercise()});
        prs.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        Collections.swap(prs, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        for (int i = 0; i < prs.size(); i++) {
            Builder builder = ContentProviderOperation.newUpdate(DBContract.PREntry.CONTENT_URI);
            builder.withValue(DBContract.PREntry.COLUMN_POSITION, i);
            builder.withYieldAllowed(true);
            builder.withSelection(DBContract.PREntry.COLUMN_PR_EXERCISE + "=?", new String[]{prs.get(i).getExercise()});
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
        if (prs == null) {
            return 0;
        } else {
            return prs.size();
        }
    }

}


