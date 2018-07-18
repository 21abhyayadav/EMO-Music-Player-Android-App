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

import java.util.ArrayList;

/**
 * Created by Student on 7/6/2017.
 */

public class RecentAdapter extends RecyclerView.Adapter<ViewHolder1> {

    ArrayList<Audio> ar_recent_stored_new = new ArrayList<>();
    Context context;
    View v;
    public RecentAdapter(ArrayList<Audio> ar_recent_stored_new, Context context) {
        this.ar_recent_stored_new = ar_recent_stored_new;
        this.context = context;
    }

    @Override
    public ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_item_layout, parent, false);
        ViewHolder1 holder = new ViewHolder1(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder1 holder, int position) {
        holder.title.setText(ar_recent_stored_new.get(position).getTitle());
        holder.albumArtist.setText(ar_recent_stored_new.get(position).getArtist());
        holder.album_id.setText(ar_recent_stored_new.get(position).getAlbumId());
        Glide.with(context).load(ar_recent_stored_new.get(position).getAlbum_art()).placeholder(R.drawable.image).into(holder.album_art);
        //holder.album_art.setImageResource(list.get(position).getAlbum_art());
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return ar_recent_stored_new.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

class ViewHolder1 extends RecyclerView.ViewHolder {

    TextView title;
    TextView albumArtist;
    ImageView play_pause;
    TextView album_id;
    ImageButton album_art;

    ViewHolder1(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.displaynamem);
        albumArtist = (TextView) itemView.findViewById(R.id.titlem);
        play_pause = (ImageView) itemView.findViewById(R.id.play_pausem);
        album_id = (TextView) itemView.findViewById(R.id.album_idm);
        album_art = (ImageButton) itemView.findViewById(R.id.play_pausem);
    }}
