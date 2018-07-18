package com.music.player.emo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.music.player.emo.Audio;
import com.music.player.emo.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Student on 6/21/2017.
 */

public class RecyclerView_Adapter extends RecyclerView.Adapter<ViewHolder> {

    List<Audio> list = Collections.emptyList();
    Context context;
    View v;
    public RecyclerView_Adapter(List<Audio> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
         v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.title.setText(list.get(position).getTitle());
        holder.album.setText(list.get(position).getAlbum());
        holder.album_id.setText(list.get(position).getAlbumId());
        Glide.with(context).load(list.get(position).getAlbum_art()).placeholder(R.drawable.image).into(holder.album_art);

        //holder.album_art.setImageResource(list.get(position).getAlbum_art());


    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

class ViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView album;
    ImageView play_pause;
    TextView album_id;
    ImageButton album_art;

    ViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.displayname);
        album = (TextView) itemView.findViewById(R.id.title);
        play_pause = (ImageView) itemView.findViewById(R.id.play_pause);
        album_id = (TextView) itemView.findViewById(R.id.album_id);
        album_art = (ImageButton) itemView.findViewById(R.id.play_pause);
    }}
