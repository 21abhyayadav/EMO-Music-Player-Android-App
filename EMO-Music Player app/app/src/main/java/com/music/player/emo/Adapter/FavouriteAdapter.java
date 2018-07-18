package com.music.player.emo.Adapter;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.music.player.emo.Audio;
import com.music.player.emo.NotificationActivity;
import com.music.player.emo.R;
import com.music.player.emo.fdhandler;

import java.util.ArrayList;

/**
 * Created by Student on 7/7/2017.
 */


public class FavouriteAdapter extends ArrayAdapter<Audio> {
    private final Context context;
    int index;
    Fragment fragment;
    ArrayList<Audio> fav_list=new ArrayList<>();
    public FavouriteAdapter(Context context,  ArrayList<Audio> fav_list,Fragment fragment) {
        super(context, -1, fav_list);
        this.context = context;
        this.fav_list = fav_list;
        this.fragment=fragment;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.favouritelist, parent, false);
        TextView textViewTitle = (TextView) rowView.findViewById(R.id.title);
        TextView textViewArtist = (TextView) rowView.findViewById(R.id.artist);
        Button delete=(Button) rowView.findViewById(R.id.delete);

        ImageButton art = (ImageButton) rowView.findViewById(R.id.play_pause);
        delete.setId(position);
//        TextView textviewplace = (TextView) rowView.findViewById(R.id.place);
//        TextView textviewdate = (TextView) rowView.findViewById(R.id.date);
        Glide.with(context).load(fav_list.get(position).getAlbum_art()).placeholder(R.drawable.image).into(art);
        textViewTitle.setText(fav_list.get(position).getTitle());
        textViewArtist.setText(fav_list.get(position).getArtist());
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Id is ",view.getId()+"");
                //((NotificationActivity)fragment).delete(view.getId());
                index=view.getId();
//                Log.e(" index 63",index+"");
                fdhandler fdh = new fdhandler(getContext().getApplicationContext());
                Audio fd = new Audio(fav_list.get(index).getData(),fav_list.get(index).getTitle(),fav_list.get(index).getAlbum(),fav_list.get(index).getArtist(),fav_list.get(index).getAlbum_art(),fav_list.get(index).getAlbum_id());
                fdh.deleteEvent(fd);
                ((NotificationActivity)fragment).delete(view.getId());

            }
        });

        return rowView;
    }
}