package com.example.android.gymrat.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.gymrat.R;

/**
 * Created by Artur on 26-Oct-16.
 */

public class CategoryAdapter extends FragmentPagerAdapter {
    private Context mContext;

    /**
     * Create a new {@link CategoryAdapter} object.
     *
     * @param context is the context of the app
     * @param fm is the fragment manager that will keep each fragment's state in the adapter
     *           across swipes.
     */
    public CategoryAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    /**
     * Return the {@link Fragment} that should be displayed for the given page number.
     */
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new WorkoutsFragment();
        } else if (position == 1) {
            return new GymsFragment();
        } else if (position == 2) {
            return new PRsFragment();
        } else {
            return new InviteFragment();
        }
    }

    /**
     * Return the total number of pages.
     */
    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.workouts);
        } else if (position == 1) {
            return mContext.getString(R.string.gyms);
        } else if (position == 2) {
            return mContext.getString(R.string.myGyms);
        } else {
            return mContext.getString(R.string.invite);
        }
    }
}
