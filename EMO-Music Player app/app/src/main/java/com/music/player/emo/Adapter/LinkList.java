package com.music.player.emo.Adapter;

/**
 * Created by Student on 7/20/2017.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.player.emo.R;

public class LinkList extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;
    public LinkList(Activity context,
                        String[] web, Integer[] imageId) {
        super(context, R.layout.activity_links, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.activity_links, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.name);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.flag);
        txtTitle.setText(web[position]);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}