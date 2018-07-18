package com.music.player.emo.Activities;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.music.player.emo.Adapter.RecentAdapter;
import com.music.player.emo.Audio;
import com.music.player.emo.Listner.CustomTouchListener;
import com.music.player.emo.Listner.onItemClickListener;
import com.music.player.emo.R;
import com.music.player.emo.Services.StorageUtil;

import java.util.ArrayList;

import static com.music.player.emo.Activities.MainActivity.sharedpreferences;

public class Recents extends Fragment implements View.OnClickListener{

    View view;
    private RecyclerView recyclerViewRecent;
    private RecyclerView.Adapter adapterRecent;
    private SharedPreferences sharedPreferences;
    private ArrayList<Audio> ar_recent_stored_new = new ArrayList();
    Gson gson;
//Other Stuff

    public void onCreate(Bundle savedInstanceState) {
        //Other Stuff and initialize planetList with all the planets name before passing it to adapter

        super.onCreate(savedInstanceState);

    }
    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Recently Played");

        view = inflater.inflate(R.layout.activity_recents, container, false);
        sharedpreferences = getActivity().getSharedPreferences("MyPREFERENCES", getActivity().MODE_PRIVATE);
        gson = new Gson();

        String markers = sharedpreferences.getString("Recents", null);
        if (markers == null) {
        } else {
            java.lang.reflect.Type type = new TypeToken<ArrayList<Audio>>() {
            }.getType();
//            ar_recent_stored.clear();
            ar_recent_stored_new = gson.fromJson(markers, type);
        }

        recyclerViewRecent = (RecyclerView) view.findViewById(R.id.recycler_viewm);
        recyclerViewRecent.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterRecent = new RecentAdapter(ar_recent_stored_new, getActivity().getApplication().getApplicationContext());
        recyclerViewRecent.setAdapter(adapterRecent);

        recyclerViewRecent.addOnItemTouchListener(new CustomTouchListener(getActivity(), new onItemClickListener() {
            @Override
            public void onClick(View view, int index) {
                StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                storage.storeAudio(ar_recent_stored_new);
                ((MainActivity)getActivity()).playAudio(index, ar_recent_stored_new);
                ((MainActivity)getActivity()).startAudioPlay(index, ar_recent_stored_new);
               }
        }));
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}