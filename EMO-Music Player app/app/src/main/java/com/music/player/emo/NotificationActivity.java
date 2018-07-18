package com.music.player.emo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.music.player.emo.Activities.MainActivity;
import com.music.player.emo.Adapter.FavouriteAdapter;
import com.music.player.emo.Services.StorageUtil;

import java.util.ArrayList;


public class NotificationActivity extends Fragment {
    View view;
    Audio fd;
    String titlename;
    fdhandler fdh;
    private LinearLayout FavLayout;
    //    favouritelistadapter fla;
    public static FavouriteAdapter favouriteAdapter;
    ListView favouritelist;
    String[] title;
 //   LinearLayout favour = (LinearLayout) view.findViewById(R.id.favour);
    public static ArrayList<Audio> ff_list=new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Favourites");
        view = inflater.inflate(R.layout.activity_notification, container, false);
        favouritelist = (ListView) view.findViewById(R.id.listview);
        FavLayout = (LinearLayout) view.findViewById(R.id.FavLay);
        fdh = new fdhandler(getActivity());
        //fd = new favouritesdatabase("Test1","23rd February | 12:00 pm","x");
        //fdh.addfavouriteevent(fd);
        title = fdh.retrievetitle();
        String[] data = fdh.retrievedata();
        String[] album=fdh.retrievealbum();
        String[] artist = fdh.retrieveartist();
        String[] album_id = fdh.retrievealbum_id();
        String[] album_art=fdh.retrievealbum_art();



        ff_list=fdh.favdata();

        /*if (title != null) {

            fla = new favouritelistadapter(getActivity(),ff_list, NotificationActivity.this);
        } else {
            fla = new favouritelistadapter(getActivity());
        }*/

        Log.e("fflist 51",ff_list.size()+"");
//        Log.e("fflist 52 ", ff_list.get(0).get_title()+"");

        if(ff_list.size()==0){
            FavLayout.setVisibility(View.VISIBLE);
        }
        favouriteAdapter=new FavouriteAdapter(getActivity(),ff_list,NotificationActivity.this);
        favouritelist.setAdapter(favouriteAdapter);
//        favouritelist.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.favouritelist,ff_list));
        favouritelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Log.e("position is ", String.valueOf(position));
                Log.e("index is ", String.valueOf(view.getId()));
                StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                storage.storeAudio(ff_list);
                ((MainActivity)getActivity()).playAudio(position, ff_list);
                ((MainActivity)getActivity()).startAudioPlay(position, ff_list);

            }


        });
        Toast.makeText(getActivity().getApplication().getApplicationContext(),"Swipe left on song to delete",Toast.LENGTH_SHORT).show();
        return view;
    }
    public static void delete(int a) {

        ff_list.remove(a);

        favouriteAdapter.notifyDataSetChanged();
        //  ff_list.remove(i)
      /* // String name2 = fla.rowdetails(view);
//        TextView name = (TextView) view.findViewById(R.id.title);
//        titlename=name2.getText().toString();
        Log.e(titlename,"titlename is");
        Toast.makeText(getActivity(),"Event "+name2+"Deleted from your favourite list",Toast.LENGTH_LONG).show();
        fdh.deleteEvent(name2);
        String[] data = fdh.retrievedata();
        String[] title = fdh.retrievetitle();
        String[] artist = fdh.retrieveartist();
        String[] album_id = fdh.retrievealbum_id();
        if(data !=null)
        {
            fla = new favouritelistadapter(getActivity(),data,title,artist,album_id,NotificationActivity.this);
        }
        else
        {
            fla = new favouritelistadapter(getActivity());
        }
        favouritelist.setAdapter(fla);*/

//        fdh.deleteEvent(title[view]);
//        String[] data = fdh.retrievedata();
//        String[] title = fdh.retrievetitle();
//        String[] artist = fdh.retrieveartist();
//        String[] album_id = fdh.retrievealbum_id();
        //title[view].

    }
}