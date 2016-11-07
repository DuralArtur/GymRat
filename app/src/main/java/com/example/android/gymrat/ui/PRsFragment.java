package com.example.android.gymrat.ui;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.gymrat.R;
import com.example.android.gymrat.db.DBContract;
import com.example.android.gymrat.objects.PR;
import com.example.android.gymrat.widget.PRWidgetProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class PRsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView recyclerView;
    private ItemTouchHelper itemTouchHelper;
    private PRAdapter prAdapter;
    public static List<PR> prs;


    @BindView(R.id.empty_pr_TV)
    TextView empty;

    @OnClick(R.id.add_fab)
    public void addPR() {
        Intent addPRIntent = new Intent(getActivity(), AddPRActivity.class);
        startActivity(addPRIntent);
    }

    public PRsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_prs, container, false);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = ButterKnife.findById(view, R.id.pr_recycler_view);
        empty.setVisibility(View.GONE);
        prs = new ArrayList<>();
        prAdapter = new PRAdapter(getContext(), prs);
        recyclerView.setAdapter(prAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(prAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        updateMyWidgets(getContext());

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                DBContract.PREntry.CONTENT_URI,
                null,
                null,
                null,
                DBContract.PREntry.COLUMN_POSITION + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String exercise;
        String vidLink;
        double reps;
        double weight;
        boolean hasVid;
        if (data.getCount() > 0 && data.moveToFirst()) {
            data.moveToFirst();
            if (!(prs == null)) {
                prs.clear();
            }
            do {
                exercise = data.getString(data.getColumnIndex(DBContract.PREntry.COLUMN_PR_EXERCISE));
                vidLink = data.getString(data.getColumnIndex(DBContract.PREntry.COLUMN_VID_ADDRESS));

                reps = data.getDouble(data.getColumnIndex(DBContract.PREntry.COLUMN_PR_REPS));
                weight = data.getDouble(data.getColumnIndex(DBContract.PREntry.COLUMN_PR_WEIGHT));
                if (TextUtils.isEmpty(vidLink)) {
                    hasVid = false;
                    prs.add(new PR(exercise, reps, weight, hasVid));
                } else {
                    hasVid = true;
                    prs.add(new PR(exercise, reps, weight, hasVid, vidLink));
                }
            } while (data.moveToNext());
//            data.close();
        } else {
            empty.setVisibility(View.VISIBLE);
        }
        getActivity().getSupportLoaderManager().destroyLoader(1);
        updateMyWidgets(getContext());
        prAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        prAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void updateMyWidgets(Context context) {
        AppWidgetManager man = AppWidgetManager.getInstance(context);
        int[] ids = man.getAppWidgetIds(
                new ComponentName(context,PRWidgetProvider.class));
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(updateIntent);
    }
}
