package com.music.player.emo.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.music.player.emo.Activities.MainActivity;
import com.music.player.emo.Audio;
import com.music.player.emo.R;
import com.music.player.emo.Services.StorageUtil;

import java.util.ArrayList;
import java.util.Locale;

import static com.music.player.emo.AudioList.audioList;

/**
 * Created by Student on 23/7/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<Audio> mDataArray = new ArrayList<>();

    public SearchAdapter(Context context) {
        mContext = context;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.title.setText(mDataArray.get(position).getTitle());
        holder.album.setText(mDataArray.get(position).getAlbum());
        holder.album_id.setText(mDataArray.get(position).getAlbumId());
        Glide.with(mContext).load(mDataArray.get(position).getAlbum_art()).placeholder(R.drawable.image).into(holder.album_art);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageUtil storage = new StorageUtil(mContext.getApplicationContext());
                storage.storeAudio(mDataArray);
                ((MainActivity)mContext).playAudio(position, mDataArray);
                ((MainActivity)mContext).startAudioPlay(position, mDataArray);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataArray.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView album;
        ImageView play_pause;
        TextView album_id;
        ImageButton album_art;
        CardView cardView;

        MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.displayname);
            album = (TextView) itemView.findViewById(R.id.title);
            play_pause = (ImageView) itemView.findViewById(R.id.play_pause);
            album_id = (TextView) itemView.findViewById(R.id.album_id);
            album_art = (ImageButton) itemView.findViewById(R.id.play_pause);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
        }

    }

    public void filter(String charText) {

        String a = charText.toLowerCase(Locale.getDefault());
        mDataArray.clear();


        if (charText.length() == 0) {
            mDataArray.addAll(audioList);
        } else {
            for (int i = 0; i < audioList.size(); i++) {
                String s = audioList.get(i).getTitle().toLowerCase(Locale.getDefault());

                if (s.contains(a)) {
                    try {
                        mDataArray.add(audioList.get(i));
                    }catch (NullPointerException e){
                        Log.e("SearchAdapter", e.getMessage());
                    }
                }

            }
        }

    }

}
