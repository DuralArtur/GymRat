package com.example.android.gymrat.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.gymrat.R;
import com.example.android.gymrat.db.DBContract;
import com.example.android.gymrat.objects.PR;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artur on 28-Oct-16.
 */

public class PRViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private List<PR> prs = new ArrayList<>();
    private Context context;
    private int appWidgetId;

    public PRViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        retrievePRs();
    }

    @Override
    public int getCount() {
        if (prs == null) {
            return 0;
        } else {
            return prs.size();
        }
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if ((prs == null)) {
            return null;
        } else {
            RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                    R.layout.list_item);
            remoteView.setTextViewText(R.id.text_view, prs.get(i).getExercise() + " - "+ prs.get(i).getWeight() + " x " + prs.get(i).getReps());
            return remoteView;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getViewTypeCount() {
        return 1;}
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public void onDataSetChanged() {
        retrievePRs();
    }

    private void retrievePRs() {
        prs.clear();
        String exercise;
        String vidLink;
        double reps;
        double weight;
        boolean hasVid;
        Cursor cursor = context.getContentResolver().query(DBContract.PREntry.CONTENT_URI, null,
                null,
                null,
                DBContract.PREntry.COLUMN_POSITION + " ASC");
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                exercise = cursor.getString(cursor.getColumnIndex(DBContract.PREntry.COLUMN_PR_EXERCISE));
                vidLink = cursor.getString(cursor.getColumnIndex(DBContract.PREntry.COLUMN_VID_ADDRESS));
                reps = cursor.getDouble(cursor.getColumnIndex(DBContract.PREntry.COLUMN_PR_REPS));
                weight = cursor.getDouble(cursor.getColumnIndex(DBContract.PREntry.COLUMN_PR_WEIGHT));
                if (TextUtils.isEmpty(vidLink)) {
                    hasVid = false;
                    prs.add(new PR(exercise, reps, weight, hasVid));
                } else {
                    hasVid = true;
                    prs.add(new PR(exercise, reps, weight, hasVid, vidLink));
                }

            } while (cursor.moveToNext());
        }
    }
}
