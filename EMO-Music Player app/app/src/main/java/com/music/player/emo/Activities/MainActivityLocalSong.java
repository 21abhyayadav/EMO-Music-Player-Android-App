package com.music.player.emo.Activities;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.music.player.emo.Adapter.RecyclerView_Adapter;
import com.music.player.emo.Listner.CustomTouchListener;
import com.music.player.emo.Listner.onItemClickListener;
import com.music.player.emo.R;
import com.music.player.emo.Services.StorageUtil;

import static com.music.player.emo.AudioList.audioList;

public class MainActivityLocalSong extends Fragment implements View.OnClickListener {

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.android.hymn.Activities.PlayNewAudio";
    public Button refreshButton;
    private RecyclerView recyclerView;
    RecyclerView_Adapter adapter;
    private int songIndex=0;
    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Local Songs");

        view = inflater.inflate(R.layout.activity_main_local_song, container, false);


        ((MainActivity)getActivity()).initViews();
        initRecyclerView();
        return view;
    }
    private void initRecyclerView() {
        if (audioList.size() > 0) {

            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
            adapter = new RecyclerView_Adapter(audioList, getActivity().getApplication());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

            recyclerView.addOnItemTouchListener(new CustomTouchListener(getActivity(), new onItemClickListener() {
                @Override
                public void onClick(View view, int index) {
                   // layoutVisibility.setVisibility(View.VISIBLE);
                    songIndex = index;

                    StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                    storage.storeAudio(audioList);
                    ((MainActivity)getActivity()).playAudio(songIndex, audioList);
                    ((MainActivity)getActivity()).startAudioPlay(songIndex, audioList);
//                    fdhandler fdh = new fdhandler(getActivity().getApplication().getApplicationContext(),null,null,1);
//                    FavouriteDatabase fd = new FavouriteDatabase(audioList.get(index).getData(),audioList.get(index).getTitle(),audioList.get(index).getArtist(),audioList.get(index).getAlbum_id(),audioList.get(index).getAlbum_art());
//                    fdh.addfavouriteevent(fd);
//                    Toast.makeText(getActivity().getApplication().getApplicationContext(),"Song added to your Favourites",Toast.LENGTH_LONG).show();
                }
            }));
        }
    }
    @Override
    public void onClick(View v) {
    }
    public class FeedbackAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            ((MainActivity)getActivity()).loadAudio();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((MainActivity)getActivity()).pdia = new ProgressDialog(getContext());
            ((MainActivity)getActivity()).pdia.setMessage("Loading...");
            ((MainActivity)getActivity()).pdia.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ((MainActivity)getActivity()).pdia.dismiss();
            ((MainActivity)getActivity()).openLocal(view);
        }
    }
}