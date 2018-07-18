package com.music.player.emo;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.music.player.emo.Activities.MainActivity;
import com.music.player.emo.Adapter.MyAdapter;
import com.music.player.emo.Services.StorageUtil;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

import static com.music.player.emo.Activities.MainActivity.ar_recent_stored;
import static com.music.player.emo.Activities.MainActivity.gson;
import static com.music.player.emo.Activities.MainActivity.sharedpreferences;
import static com.music.player.emo.AudioList.audioList;

public class FragmentHome extends Fragment {
    View view;
    private static ViewPager mPager;
    private static final Integer[] XMEN = {R.drawable.lp, R.drawable.bm, R.drawable.cp, R.drawable.sg, R.drawable.es};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();
    private static int currentPage = 0;
    private ImageView recentImageView1;
    private ImageView recentImageView2;
    private ImageView recentImageView3;
    private TextView recentTextView1;
    private TextView recentTextView2;
    private TextView recentTextView3;
    private int sizeRecent = 0;
    private TextView tv1, tv2, tv3;
    private ImageView Iv1, Iv2, Iv3;
    private RelativeLayout RecentRel;
    private HorizontalScrollView RecentScroll;
    private CardView RelativeCard1;
    private CardView RelativeCard2;
    private CardView RelativeCard3;
    private RelativeLayout FavRelative;
    private HorizontalScrollView Favscr;
    private CardView FavCrd1;
    private CardView FavCrd2;
    private CardView FavCrd3;
    private TextView localTextView1;
    private TextView localTextView2;
    private TextView localTextView3;
    private ImageView localImageView1;
    private ImageView localImageView2;
    private ImageView localImageView3;
    private TextView localTextView4;
    private TextView localTextView5;
    private TextView localTextView6;
    private ImageView localImageView4;
    private ImageView localImageView5;
    private ImageView localImageView6;
    private RelativeLayout localSong1;
    private RelativeLayout localSong2;
    private RelativeLayout localSong3;
    private RelativeLayout localSong4;
    private RelativeLayout localSong5;
    private RelativeLayout localSong6;
    private RelativeLayout recentSong1;
    private RelativeLayout recentSong2;
    private RelativeLayout recentSong3;
    private RelativeLayout favourSong1;
    private RelativeLayout favourSong2;
    private RelativeLayout favourSong3;
    private int localIndex1 =0;
    private int localIndex2 =0;
    private int localIndex3 =0;
    private int localIndex4 =0;
    private int localIndex5 =0;
    private int localIndex6 =0;
    ArrayList<Audio> favou_list = new ArrayList<>();
    fdhandler fdh;
    int i = 0;
    private int index1,index2,index3,index4,index5,index0 = 0;
    private int Recentindex1,Recentindex2,Recentindex0 = 0;
    private int Favourindex1,Favourindex2,Favourindex0 = 0;

    Random random = new Random();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Emo");

        view = inflater.inflate(R.layout.activity_fragment_home, container, false);
        init();
        initRecentViews();
        displayRecentHome();
        displayFavourHome();
        displayLocalHome();
        localSong1.setOnClickListener(onButtonClick);
        localSong2.setOnClickListener(onButtonClick);
        localSong3.setOnClickListener(onButtonClick);
        localSong4.setOnClickListener(onButtonClick);
        localSong5.setOnClickListener(onButtonClick);
        localSong6.setOnClickListener(onButtonClick);
        recentSong1.setOnClickListener(onButtonClick);
        recentSong2.setOnClickListener(onButtonClick);
        recentSong3.setOnClickListener(onButtonClick);
        favourSong1.setOnClickListener(onButtonClick);
        favourSong2.setOnClickListener(onButtonClick);
        favourSong3.setOnClickListener(onButtonClick);
        return view;


    }

    private void init() {
        try {
            XMENArray.clear();
        }catch (Exception e)
        {
        }
        for (int i = 0; i < XMEN.length; i++)
            XMENArray.add(XMEN[i]);

        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(new MyAdapter(getActivity(), XMENArray));
        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == XMEN.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3500, 3500);
    }

    private View.OnClickListener onButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.localSong1: {
                    StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                    storage.storeAudio(audioList);
                    ((MainActivity)getActivity()).playAudio(localIndex1, audioList);
                    ((MainActivity)getActivity()).startAudioPlay(localIndex1, audioList);
                    displayRecentHome();
                    break;
                }
                case R.id.localSong2: {
                    StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                    storage.storeAudio(audioList);
                    ((MainActivity)getActivity()).playAudio(localIndex2, audioList);
                    ((MainActivity)getActivity()).startAudioPlay(localIndex2, audioList);
                    displayRecentHome();
                    break;
                }
                case R.id.localSong3: {
                    StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                    storage.storeAudio(audioList);
                    ((MainActivity)getActivity()).playAudio(localIndex3, audioList);
                    ((MainActivity)getActivity()).startAudioPlay(localIndex3, audioList);
                    displayRecentHome();
                    break;
                }
                case R.id.localSong4: {
                    StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                    storage.storeAudio(audioList);
                    ((MainActivity)getActivity()).playAudio(localIndex4, audioList);
                    ((MainActivity)getActivity()).startAudioPlay(localIndex4, audioList);
                    displayRecentHome();
                    break;
                }
                case R.id.localSong5: {
                    StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                    storage.storeAudio(audioList);
                    ((MainActivity)getActivity()).playAudio(localIndex5, audioList);
                    ((MainActivity)getActivity()).startAudioPlay(localIndex5, audioList);
                    displayRecentHome();
                    break;
                }
                case R.id.localSong6: {
                    StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                    storage.storeAudio(audioList);
                    ((MainActivity)getActivity()).playAudio(localIndex6, audioList);
                    ((MainActivity)getActivity()).startAudioPlay(localIndex6, audioList);
                    displayRecentHome();
                    break;
                }
                case R.id.recentSong1: {
                    StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                    storage.storeAudio(ar_recent_stored);
                    ((MainActivity)getActivity()).playAudio(0, ar_recent_stored);
                    ((MainActivity)getActivity()).startAudioPlay(0, ar_recent_stored);
                    displayRecentHome();
                    break;
                }
                case R.id.recentSong2: {
                    StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                    storage.storeAudio(ar_recent_stored);
                    ((MainActivity)getActivity()).playAudio(1, ar_recent_stored);
                    ((MainActivity)getActivity()).startAudioPlay(1, ar_recent_stored);
                    displayRecentHome();
                    break;
                }
                case R.id.recentSong3: {
                    StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                    storage.storeAudio(ar_recent_stored);
                    ((MainActivity)getActivity()).playAudio(2, ar_recent_stored);
                    ((MainActivity)getActivity()).startAudioPlay(2, ar_recent_stored);
                    displayRecentHome();
                    break;
                }
                case R.id.favourSong1: {
                    StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                    storage.storeAudio(favou_list);
                    ((MainActivity)getActivity()).playAudio(Favourindex0, favou_list);
                    ((MainActivity)getActivity()).startAudioPlay(Favourindex0, favou_list);
                    displayRecentHome();
                    break;
                }
                case R.id.favourSong2: {
                    StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                    storage.storeAudio(favou_list);
                    ((MainActivity)getActivity()).playAudio(Favourindex1, favou_list);
                    ((MainActivity)getActivity()).startAudioPlay(Favourindex1, favou_list);
                    displayRecentHome();
                    break;
                }
                case R.id.favourSong3: {
                    StorageUtil storage = new StorageUtil(getActivity().getApplication().getApplicationContext());
                    storage.storeAudio(favou_list);
                    ((MainActivity)getActivity()).playAudio(Favourindex2, favou_list);
                    ((MainActivity)getActivity()).startAudioPlay(Favourindex2, favou_list);
                    displayRecentHome();
                    break;
                }
            }

        }
};

    public void initRecentViews() {

        recentImageView1 = (ImageView) view.findViewById(R.id.recentIv1);
        recentImageView2 = (ImageView) view.findViewById(R.id.recentIv2);
        recentImageView3 = (ImageView) view.findViewById(R.id.recentIv3);
        recentTextView1 = (TextView) view.findViewById(R.id.recentTv1);
        recentTextView2 = (TextView) view.findViewById(R.id.recentTv2);
        recentTextView3 = (TextView) view.findViewById(R.id.recentTv3);

        tv1 = (TextView) view.findViewById(R.id.favoriteTv1);
        tv2 = (TextView) view.findViewById(R.id.favoriteTv2);
        tv3 = (TextView) view.findViewById(R.id.favoriteTv3);
        Iv1 = (ImageView) view.findViewById(R.id.favoriteIv1);
        Iv2 = (ImageView) view.findViewById(R.id.favoriteIv2);
        Iv3 = (ImageView) view.findViewById(R.id.favoriteIv3);


        localTextView1 = (TextView) view.findViewById(R.id.localTv1);
        localTextView2 = (TextView) view.findViewById(R.id.localTv2);
        localTextView3 = (TextView) view.findViewById(R.id.localTv3);
        localTextView4 = (TextView) view.findViewById(R.id.localTv4);
        localTextView5 = (TextView) view.findViewById(R.id.localTv5);
        localTextView6 = (TextView) view.findViewById(R.id.localTv6);
        localImageView1 = (ImageView) view.findViewById(R.id.localIv1);
        localImageView2 = (ImageView) view.findViewById(R.id.localIv2);
        localImageView3 = (ImageView) view.findViewById(R.id.localIv3);
        localImageView4 = (ImageView) view.findViewById(R.id.localIv4);
        localImageView5 = (ImageView) view.findViewById(R.id.localIv5);
        localImageView6 = (ImageView) view.findViewById(R.id.localIv6);
        RecentRel = (RelativeLayout) view.findViewById(R.id.recRel);
        RecentScroll = (HorizontalScrollView) view.findViewById(R.id.recScroll);
        RelativeCard1 = (CardView) view.findViewById(R.id.RecCard1);
        RelativeCard2 = (CardView) view.findViewById(R.id.RecCard2);
        RelativeCard3 = (CardView) view.findViewById(R.id.RecCard3);
        FavRelative = (RelativeLayout) view.findViewById(R.id.FavRel);
        Favscr = (HorizontalScrollView) view.findViewById(R.id.FavScroll);
        FavCrd1 = (CardView) view.findViewById(R.id.FavCard1);
        FavCrd2 = (CardView) view.findViewById(R.id.FavCard2);
        FavCrd3 = (CardView) view.findViewById(R.id.FavCard3);

        localSong1 = (RelativeLayout) view.findViewById(R.id.localSong1);
        localSong2 = (RelativeLayout) view.findViewById(R.id.localSong2);
        localSong3 = (RelativeLayout) view.findViewById(R.id.localSong3);
        localSong4 = (RelativeLayout) view.findViewById(R.id.localSong4);
        localSong5 = (RelativeLayout) view.findViewById(R.id.localSong5);
        localSong6 = (RelativeLayout) view.findViewById(R.id.localSong6);

        recentSong1 = (RelativeLayout) view.findViewById(R.id.recentSong1);
        recentSong2 = (RelativeLayout) view.findViewById(R.id.recentSong2);
        recentSong3 = (RelativeLayout) view.findViewById(R.id.recentSong3);

        favourSong1 = (RelativeLayout) view.findViewById(R.id.favourSong1);
        favourSong2 = (RelativeLayout) view.findViewById(R.id.favourSong2);
        favourSong3 = (RelativeLayout) view.findViewById(R.id.favourSong3);


    }

    public void displayRecentHome(){

        sharedpreferences = getActivity().getSharedPreferences("MyPREFERENCES", getActivity().MODE_PRIVATE);
        gson = new Gson();
        String markers = sharedpreferences.getString("Recents", null);
        if (markers == null) {} else {
            java.lang.reflect.Type type = new TypeToken<ArrayList<Audio>>() {
            }.getType();
//            ar_recent_stored.clear();
            ar_recent_stored = gson.fromJson(markers, type);
        }
        sizeRecent = ar_recent_stored.size();
        //Recentindex0 = random.nextInt(audioList.size());
        if(sizeRecent>0){
            Glide.with(this).load(ar_recent_stored.get(0).getAlbum_art()).placeholder(R.drawable.image).into(recentImageView1);
            recentTextView1.setText(ar_recent_stored.get(0).getTitle());
            RecentRel.setVisibility(View.VISIBLE);
            RecentScroll.setVisibility(View.VISIBLE);
            RelativeCard1.setVisibility(View.VISIBLE);
        }
        // Recentindex1 = random.nextInt(audioList.size());
        if(sizeRecent>1){
            Glide.with(this).load(ar_recent_stored.get(1).getAlbum_art()).placeholder(R.drawable.image).into(recentImageView2);
            recentTextView2.setText(ar_recent_stored.get(1).getTitle());
            RelativeCard2.setVisibility(View.VISIBLE);
        }
        //Recentindex2 = random.nextInt(audioList.size());
        if (sizeRecent>2){
            Glide.with(this).load(ar_recent_stored.get(2).getAlbum_art()).placeholder(R.drawable.image).into(recentImageView3);
            recentTextView3.setText(ar_recent_stored.get(2).getTitle());
            RelativeCard3.setVisibility(View.VISIBLE);
        }
    }

    public void displayFavourHome(){


        fdh = new fdhandler(getActivity());
        favou_list = fdh.favdata();
        i = favou_list.size() - 1;
        getActivity().setTitle("Emo");
        if (i >= 0) {
            Favourindex0 = random.nextInt(favou_list.size());

            tv1.setText(favou_list.get(Favourindex0).getTitle());
            Glide.with(this).load(favou_list.get(Favourindex0).getAlbum_art()).placeholder(R.drawable.image).into(Iv1);
            FavRelative.setVisibility(View.VISIBLE);
            Favscr.setVisibility(View.VISIBLE);
            FavCrd1.setVisibility(View.VISIBLE);
        }

        if(i>=1) {
            Favourindex1 = random.nextInt(favou_list.size());
            tv2.setText(favou_list.get(Favourindex1).getTitle());
            Glide.with(this).load(favou_list.get(Favourindex1).getAlbum_art()).placeholder(R.drawable.image).into(Iv2);
            FavCrd2.setVisibility(View.VISIBLE);
        }
        if(i>=2) {
            Favourindex2 = random.nextInt(favou_list.size());
            tv3.setText(favou_list.get(Favourindex2).getTitle());
            Glide.with(this).load(favou_list.get(Favourindex2).getAlbum_art()).placeholder(R.drawable.image).into(Iv3);
            FavCrd3.setVisibility(View.VISIBLE);
        }
    }

    public void displayLocalHome(){
        i = audioList.size() - 1;
        if (i >= 0) {
            index0 = random.nextInt(audioList.size());
            localIndex1 = index0;
            localTextView1.setText(audioList.get(index0).getTitle());
            Glide.with(this).load(audioList.get(index0).getAlbum_art()).placeholder(R.drawable.image).into(localImageView1);
        }
        if(i>=1) {
            index1 = random.nextInt(audioList.size());
            localIndex2 = index1;
            localTextView2.setText(audioList.get(index1).getTitle());
            Glide.with(this).load(audioList.get(index1).getAlbum_art()).placeholder(R.drawable.image).into(localImageView2);
        }
        if(i>=2) {
            index2 = random.nextInt(audioList.size());
            localIndex3 = index2;
            localTextView3.setText(audioList.get(index2).getTitle());
            Glide.with(this).load(audioList.get(index2).getAlbum_art()).placeholder(R.drawable.image).into(localImageView3);
        }
        if(i>=3) {
            index3 = random.nextInt(audioList.size());
            localIndex4 = index3;
            localTextView4.setText(audioList.get(index3).getTitle());
            Glide.with(this).load(audioList.get(index3).getAlbum_art()).placeholder(R.drawable.image).into(localImageView4);
        }
        if(i>=4) {
            index4 = random.nextInt(audioList.size());
            localIndex5 = index4;
            localTextView5.setText(audioList.get(index4).getTitle());
            Glide.with(this).load(audioList.get(index4).getAlbum_art()).placeholder(R.drawable.image).into(localImageView5);
        }
        if(i>=5) {
            index5 = random.nextInt(audioList.size());
            localIndex6 = index5;
            localTextView6.setText(audioList.get(index5).getTitle());
            Glide.with(this).load(audioList.get(index5).getAlbum_art()).placeholder(R.drawable.image).into(localImageView6);
        }
    }
}
