package com.music.player.emo.Activities;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.music.player.emo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Links extends Fragment {
    String[] links = new String[]{"IndiaMP3", "Mp3Skull", "SongsLover", "clickmaza", "Jamendo Music", "airmp3", "SongsMp3.co", "Djmaza.com"};
    View view;
    int[] flags = new int[]{
            R.drawable.a1,
            R.drawable.a2,
            R.drawable.a3,
            R.drawable.a4,
            R.drawable.a5,
            R.drawable.a6,
            R.drawable.a7,
            R.drawable.a8
    };

    public ListView ListView1 = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Download Songs");

        view = inflater.inflate(R.layout.activity_links, container, false);

        ListView1 = (ListView) view.findViewById(R.id.listviewlinks);

        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 8; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("name", "" + links[i]);
            hm.put("flag", Integer.toString(flags[i])

            );
            aList.add(hm);
        }

        String[] from = {"flag", "name"};

        int[] to = {R.id.flag, R.id.name};

        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.linklist, from, to);

        try {
            ListView1.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }

        ListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                switch (position) {
                    case 0:

                        intent.setData(Uri.parse("https://www.indiamp3.com/"));
                        startActivity(intent);
                        break;
                    case 1:
                        intent.setData(Uri.parse("https://www.mp3skulls.to/"));
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setData(Uri.parse("https://www.songslover.pk/"));
                        startActivity(intent);
                        break;
                    case 3:
                        intent.setData(Uri.parse("https://www.clickmaza.com/"));
                        startActivity(intent);
                        break;
                    case 4:
                        intent.setData(Uri.parse("https://www.jamendo.com/"));
                        startActivity(intent);
                        break;
                    case 5:
                        intent.setData(Uri.parse("https://www.airmp3.me/"));
                        startActivity(intent);
                        break;
                    case 6:
                        intent.setData(Uri.parse("https://www.songsmp3.co/"));
                        startActivity(intent);
                        break;
                    case 7:
                        intent.setData(Uri.parse("https://www.djmaza.life/"));
                        startActivity(intent);
                        break;
                }
            }
        });
        return view;
    }
}
