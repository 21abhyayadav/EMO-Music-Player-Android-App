package com.music.player.emo;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.music.player.emo.Adapter.SearchAdapter;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    EditText edit_search;
    SearchAdapter adapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        edit_search = (EditText) v.findViewById(R.id.edit_search);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_search);

        adapter = new SearchAdapter(getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);

        mRecyclerView.addItemDecoration(itemDecoration);

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String searchText = edit_search.getText().toString().toLowerCase(Locale.getDefault());
              adapter.filter(searchText);
                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String searchText = edit_search.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(searchText);
                mRecyclerView.setAdapter(adapter);
            }
        });

        return v;
    }

}
