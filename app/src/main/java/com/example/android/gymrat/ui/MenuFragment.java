package com.example.android.gymrat.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.gymrat.R;

import butterknife.BindArray;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {
    @BindArray(R.array.menu_items) String[] menu_items;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private Intent newActivityIntent;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.menu_fragment, container, false);
        ButterKnife.bind(this,rootView);
        menu_items = getResources().getStringArray(R.array.menu_items);
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                menu_items);
        listView = (ListView) rootView.findViewById(R.id.menu_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!((MainActivity) getActivity()).mTwoPanes) {
                    Object listItem = listView.getItemAtPosition(i);
                    if (listItem.toString().equals(getResources().getString(R.string.workouts))) {
                        newActivityIntent = new Intent(getActivity(), WorkoutsActivity.class);
                        startActivity(newActivityIntent);
                    } else if (listItem.toString().equals(getResources().getString(R.string.gyms))) {
                        newActivityIntent = new Intent(getActivity(), GymsActivity.class);
                        startActivity(newActivityIntent);
                    } else if (listItem.toString().equals(getResources().getString(R.string.myGyms))) {
                        newActivityIntent = new Intent(getActivity(), PRsActivity.class);
                        startActivity(newActivityIntent);
                    } else if (listItem.toString().equals(getResources().getString(R.string.invite))) {
                        newActivityIntent = new Intent(getActivity(), InviteActivity.class);
                        startActivity(newActivityIntent);
                    }
                }
            }
        });
        return rootView;
    }

}
