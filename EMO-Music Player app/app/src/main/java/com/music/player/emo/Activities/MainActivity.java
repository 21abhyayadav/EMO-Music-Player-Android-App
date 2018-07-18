package com.music.player.emo.Activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.music.player.emo.Audio;
import com.music.player.emo.FragmentHome;
import com.music.player.emo.NotificationActivity;
import com.music.player.emo.R;
import com.music.player.emo.SearchFragment;
import com.music.player.emo.Services.MediaPlayerService;
import com.music.player.emo.Services.StorageUtil;
import com.music.player.emo.fdhandler;
import com.wunderlist.slidinglayer.SlidingLayer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.music.player.emo.Activities.Skipnow.mGoogleApiClient;
import static com.music.player.emo.AudioList.audioList;
import static com.music.player.emo.R.id.currentSongArtist;
import static com.music.player.emo.R.id.dismissOnClick;
import static com.music.player.emo.R.id.mpCurrentSongAlbum;
import static com.music.player.emo.R.id.mpCurrentSongArtist;
import static com.music.player.emo.R.id.mpCurrentSongImage;
import static com.music.player.emo.R.id.mpCurrentSongTitle;
import static com.music.player.emo.R.id.openMediaPlayer;
import static com.music.player.emo.R.id.selectedfile;
import static com.music.player.emo.R.id.slideClose;
import static com.music.player.emo.Services.MediaPlayerService.ACTION_NEXT;
import static com.music.player.emo.Services.MediaPlayerService.ACTION_NEXT_COMPLETE;
import static com.music.player.emo.Services.MediaPlayerService.ACTION_PAUSE;
import static com.music.player.emo.Services.MediaPlayerService.ACTION_PLAY;
import static com.music.player.emo.Services.MediaPlayerService.ACTION_PREVIOUS;
import static com.music.player.emo.Services.MediaPlayerService.ACTION_STOP;
import static com.music.player.emo.Services.MediaPlayerService.Broadcast_NOTIFY;
import static com.music.player.emo.Services.MediaPlayerService.Broadcast_PLAY_NEW_AUDIO;
import static com.music.player.emo.Services.MediaPlayerService.NOTIFICATION_ID;
import static com.music.player.emo.Services.MediaPlayerService.PAUSE;
import static com.music.player.emo.Services.MediaPlayerService.RESUME;
import static com.music.player.emo.Services.MediaPlayerServices_Ex.Broadcast_UPDATE_ONDELETE_NOTIFICATOIN;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String Broadcast_Update_Notification = "com.example.android.hymn.Activities.PlayNewAudio2";
    public static final String Broadcast_Update_SEEKPOSITION = "com.example.android.hymn.Activities.PlayNewAudio20";
    public static final String Broadcast_Update_FIRSTSEEK = "com.example.android.hymn.Activities.PlayNewAudio2000";
    private FragmentManager fm;
    String Ftag = "";
    public SlidingLayer mSlidingLayer;
    private fdhandler mDbHelper = new fdhandler(MainActivity.this);
    public static Boolean shuffleOnOff = false;
    public static Boolean repeatOnOff = false, isPaused = false;
    ;
    private String songDuration = "";
    private String songDurationPosition = "";
    public static Boolean favOn = false;
    static Runnable runnable;
    public ImageButton favButton = null;
    public static Button shuffleButton = null;
    public static Button repeatButton = null;
    public Context context;
    public TextView currentSongPositionD;
    private boolean isBound = false;
    public TextView mpCurrentTitle = null;
    public TextView mpCurrentAlbum = null;
    public TextView mpCurrentArtist = null;
    public ImageView mpCurrentImage;
    private static final int UPDATE_FREQUENCY = 500;
    private static final int STEP_VALUE = 4000;
    public TextView selelctedFile = null;
    public TextView currentArtist = null;
    ArrayList<Audio> fav_list = new ArrayList<>();
    private MediaPlayerService musicPlayer;
    public static ImageButton playButton = null;
    public static ImageButton playButton1 = null;
    public int currentIndex;
    public ArrayList<Audio> currentList = new ArrayList<>();
    public ImageButton prevButton = null;
    BroadcastReceiver NotifyAction;
    StorageUtil storage;
    private Button parcelButtonClick = null;
    public ImageView slideDownButton = null;
    public LinearLayout slideUp = null;
    private boolean isMoveingSeekBar = false;
    private boolean isMoveingSeekBar1 = false;
    public LinearLayout dismissOnClickOnMediPlayer = null;
    public ImageView slideUpButton = null;
    public static Bitmap artwork = null;
    public ImageButton nextButton = null;
    public ImageButton fastForwardButton = null;
    public ImageButton fastBackwardButton = null;
    private int index;
    public ImageView currentSongImage;
    private String currentBitmap = null;
    private String mpCurrentBitmap = null;
    public static boolean isRegistered = false;
    private int songIndex = 0;
    public static boolean isStarted = true;
    private String currentFile = "";
    public String mpCurrentSongOfTitle;
    public String mpCurrentSongOfAlbum;
    public String mpCurrentSongOfArtist;
    Intent playerIntent;
    public ProgressDialog pdia;
    private int onCompleteIndex = 0;
    private String currentTitle = "";
    private String currentArtistName = "";
    private Uri imageUri;
    private Intent intent;
    private TextView currntSongDuration;
    public static final Handler handler = new Handler();

    private static final int PERMS_REQUEST_CODE = 123;
    public static boolean serviceBound = false;
    private TextView swipeText;
    //    ArrayList<Audio> audioList ;
    private Audio activeAudio;
    public int currentIndexOfTheSong = 0;
    public static SeekBar seekbar = null;
    public static SeekBar seekBar1 = null;
    private String currentAlbum_Id = "";
    private String currentAlbum = "";
    private int seekDurationReceived = 0;
    public static ArrayList<Audio> ar_recent = new ArrayList<>();
    public static ArrayList<Audio> ar_recent_stored = new ArrayList<>();
    private String markers = "";
    private String markers1 = "";
    public static int indexForRecent = 0;
    public int seekValueReceived = 0;
    public static Gson gson;
    public static SharedPreferences sharedpreferences;
    private static final int REQUEST_WRITE_STORAGE = 110;
    public final Runnable updatePositionRunnable = new Runnable() {
        public void run() {
            positionUpdate();
        }
    };

    int j;
    public boolean playingStatus = false;
    public static LinearLayout layoutVisibility = null;
    public String visibility = null;
    public String stringArray = "";
    public ArrayList<Audio> currentAudioListNew = null;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    int resumePosition = 0;
    int durationSeek = 0;
    int positionSeek = 0;

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.e("MA207", "onServiceConnected");
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            musicPlayer = binder.getService();
            register_notify();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (hasPermissions()) {
        } else {
            requestPerms();
            while (!hasPermissions()) {

            }
        }
//        loadAudio();
//        visibility = getIntent().getStringExtra("visibilityGone");
//        try{
//        if(visibility.equals("visibilityGone")){
//        layoutVisibility= (LinearLayout) findViewById(R.id.layoutVisibility);
//        layoutVisibility.setVisibility(View.GONE);}
//        else{
//            layoutVisibility.setVisibility(View.VISIBLE);
//        }}
//        catch (Exception e){
//            layoutVisibility.setVisibility(View.VISIBLE);
//        }
        //startAudioPlay(songIndex);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(MainActivity.this, Skipnow.class));
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(MainActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Connection Failed, Try Again!", Toast.LENGTH_SHORT).show();

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        registerBecomingNoisyReceiver2001();
        context = this;
//        registerBecomingNoisyReceiver1();
//        registerBecomingNoisyReceiver10();
//        registerBecomingNoisyReceiver101();
//        registerBecomingNoisyReceiver1001();
        initViews();
//        registerBecomingNoisyReceiver1();
        fm = getFragmentManager();
        FragmentHome fragmentHome = new FragmentHome();

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.container1, fragmentHome, "fragHome");
        transaction.commit();
        if (audioList == null) {
            new FeedbackAsync().execute();
        }


        initState();
        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        markers = sharedpreferences.getString("Recents", "");
        playButton.setOnClickListener(onButtonClick);
        playButton1.setOnClickListener(onButtonClick);
        nextButton.setOnClickListener(onButtonClick);
        prevButton.setOnClickListener(onButtonClick);
        favButton.setOnClickListener(onButtonClick);
        slideDownButton.setOnClickListener(onButtonClick);
        slideUp.setOnClickListener(onButtonClick);
        selelctedFile.setOnClickListener(onButtonClick);
        currentArtist.setOnClickListener(onButtonClick);
        dismissOnClickOnMediPlayer.setOnClickListener(onButtonClick);
        shuffleButton.setOnClickListener(onButtonClick);
        repeatButton.setOnClickListener(onButtonClick);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        storage = new StorageUtil(MainActivity.this);
        init();
//        loadAudio();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            onResume();
//            finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (Ftag.equals("fragLocal")) {
                new FeedbackAsync1().execute();
            } else {
                new FeedbackAsync().execute();
            }
//            mAuth.signOut();

        }
        if (id == R.id.action_save) {
            //Toast.makeText(this, "!!..JAI MATA DII..!!", Toast.LENGTH_SHORT).show();

            mSlidingLayer.closeLayer(true);
            SearchFragment fragmentSearch = new SearchFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Ftag = "fragSearch";
            transaction.replace(R.id.container1, fragmentSearch, "fragHome");
            transaction.commit();

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the home action

            fm = getFragmentManager();
            mSlidingLayer.closeLayer(true);
            FragmentHome fragmentHome = new FragmentHome();
            FragmentTransaction transaction = fm.beginTransaction();
            Ftag = "fragHome";
            transaction.replace(R.id.container1, fragmentHome, "fragHome");
            transaction.commit();

        } else if (id == R.id.local_mp3) {

            mSlidingLayer.closeLayer(true);
            fm = getFragmentManager();
            MainActivityLocalSong fragmentlocal = new MainActivityLocalSong();
            FragmentTransaction transaction = fm.beginTransaction();
            Ftag = "fragLocal";
            transaction.replace(R.id.container1, fragmentlocal, "fragLocal");
            transaction.commit();

            // Handle the home action
        } else if (id == R.id.nav_downloadsongs) {
            mSlidingLayer.closeLayer(true);
            fm = getFragmentManager();
            Links favfragment = new Links();
            FragmentTransaction transaction = fm.beginTransaction();
            Ftag = "fragRecent";
            transaction.replace(R.id.container1, favfragment, "fragRecent");
            transaction.commit();
        } else if (id == R.id.nav_recentlyplayed) {
            mSlidingLayer.closeLayer(true);

            fm = getFragmentManager();
            Recents favfragment = new Recents();
            FragmentTransaction transaction = fm.beginTransaction();
            Ftag = "fragRecent";
            transaction.replace(R.id.container1, favfragment, "fragRecent");
            transaction.commit();
        } else if (id == R.id.nav_favourites) {
            mSlidingLayer.closeLayer(true);
            fm = getFragmentManager();
            NotificationActivity favfragment = new NotificationActivity();
            FragmentTransaction transaction = fm.beginTransaction();
            Ftag = "fragFavorite";
            transaction.replace(R.id.container1, favfragment, "fragFavorite");
            transaction.commit();

        }
        //else if (id == R.id.nav_helpandsupport) {
          //  mSlidingLayer.closeLayer(true);
            //fm = getFragmentManager();
           // AboutUs favfragment = new AboutUs();
           // FragmentTransaction transaction = fm.beginTransaction();
            //Ftag = "fragAboutUs";
            //transaction.replace(R.id.container1, favfragment, "fragAboutUs");
            //transaction.commit();
        //}
        else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Get Ready For the World Class Music Player 'EMO' ----- PLAYSTORE LINK COMING SOON");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

//            String text = "Get Ready For the World Class Music Player 'HYMN' ----- PLAYSTORE LINK COMMING SOON";
//            Uri pictureUri = Uri.parse(String.valueOf(R.drawable.banner));
//            Intent shareIntent = new Intent();
//            shareIntent.setAction(Intent.ACTION_SEND);
//            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
//            shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
//            shareIntent.setType("image/*");
//            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivity(Intent.createChooser(shareIntent, "Share images..."));
//            Bitmap imgBitmap=BitmapFactory.decodeResources(getResources(),R.drawable.banner);
//            String imgBitmapPath= MediaStore.Images.Media.insertImage(getContentResolver(),imgBitmap,"title",null);
//            Uri imgBitmapUri=Uri.parse(imgBitmapPath);
//            Uri uri = Uri.parse("android.resource://your.package.here/drawable/banner");
//            try {
//                InputStream stream = getContentResolver().openInputStream(uri);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            //imageUri = Uri.parse("android.resource://" + getPackageName()+ "/drawable/" + "banner");
//            intent = new Intent();
//            intent.setAction(Intent.ACTION_SEND);
//            intent.setType("image/*");
//            intent.putExtra(Intent.EXTRA_TEXT, "Get Ready For the World Class Music Player 'HYMN' ----- PLAYSTORE LINK COMMING SOON");
//            intent.putExtra(Intent.EXTRA_STREAM, uri);
////            intent.setType("image/*");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Intent broadcastIntent = new Intent(Broadcast_Update_Notification);
            sendBroadcast(broadcastIntent);
            signOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void gotoskipnow() {
        Intent intent = new Intent(this, Skipnow.class);
        startActivity(intent);
    }

    private void initState() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setupSlidingLayerPosition(prefs.getString("layer_location", "bottom"));

    }

    private void setupSlidingLayerPosition(String layerPosition) {

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mSlidingLayer.getLayoutParams();
        switch (layerPosition) {
            default:
                mSlidingLayer.setStickTo(SlidingLayer.STICK_TO_BOTTOM);
        }
        mSlidingLayer.setLayoutParams(rlp);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSlidingLayer.isOpened()) {
                mSlidingLayer.closeLayer(true);
                Log.e("log ", "if");
            } else {
                Log.e("log ", "else");
                if (Ftag.equalsIgnoreCase("fragHome")) {

//                         Log.e("open close app dialog","hggdgsdhhdshddhsd");
//                    finish();
                    moveTaskToBack(true);
                    Log.e("log ", "else if");
                } else {
                    fm = getFragmentManager();
                    mSlidingLayer.closeLayer(true);
                    FragmentHome fragmentHome = new FragmentHome();
                    FragmentTransaction transaction = fm.beginTransaction();
                    Ftag = "fragHome";
                    transaction.replace(R.id.container1, fragmentHome, "fragHome");
                    transaction.commit();
                    Log.e("log ", "else else");
                }
            }
        } else {
//            moveTaskToBack(true);

            return super.onKeyDown(keyCode, event);
        }
        return true;
    }


    private boolean hasPermissions() {
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};

        for (String perms : permissions) {
            res = this.checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestPerms() {
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;
        switch (requestCode) {
            case PERMS_REQUEST_CODE:

                for (int res : grantResults) {
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed) {
            //user granted all permissions we can perform our task.
            //makeFolder();
        } else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();
                }
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                    Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private MediaPlayer.OnCompletionListener onCompletion = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {

            if (!shuffleOnOff && !repeatOnOff) {


                if (songIndex < (audioList.size() - 1))
                    songIndex = songIndex + 1;
                else
                    songIndex = 0;

                Log.e("Image1564698" +
                        "", "--Image");
//                startPlay(songIndex);
            } else if (!shuffleOnOff && repeatOnOff) {

//                startPlay(songIndex);

            } else if (shuffleOnOff && !repeatOnOff) {

                Random random = new Random();
                songIndex = random.nextInt(audioList.size());

//                startPlay(songIndex);
            }
        }
    };

    private MediaPlayer.OnErrorListener onError = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            return false;
        }
    };


    private View.OnClickListener onButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play: {
//                    if (playingStatus) {
//                        //handler.removeCallbacks(updatePositionRunnable);
//                        playButton.setImageResource(android.R.drawable.ic_media_play);
////                        playButton1.setImageResource(android.R.drawable.ic_media_play);
////                        Intent broadcastIntent = new Intent(Broadcast_Update_Notification);
////                        sendBroadcast(broadcastIntent);
//                    } else {
//                        if (isStarted) {
//                            //updatePosition();
////                            playButton.setImageResource(android.R.drawable.ic_media_pause);
////                            playButton1.setImageResource(android.R.drawable.ic_media_pause);
////                            Intent broadcastIntent = new Intent(Broadcast_Update_Notification);
////                            sendBroadcast(broadcastIntent);
//                        }
//                    }
                    Log.e("play560----", "okay");
                    fn_play();
                    break;
                }
                case R.id.play1: {
//                    if (playingStatus) {
//                        //handler.removeCallbacks(updatePositionRunnable);
//                        playButton.setImageResource(android.R.drawable.ic_media_play);
//                        playButton1.setImageResource(android.R.drawable.ic_media_play);
//                        Intent broadcastIntent = new Intent(Broadcast_Update_Notification);
//                        sendBroadcast(broadcastIntent);
//                    } else {
//                        if (isStarted) {
//                            //updatePosition();
//                            playButton.setImageResource(android.R.drawable.ic_media_pause);
//                            playButton1.setImageResource(android.R.drawable.ic_media_pause);
//                            Intent broadcastIntent = new Intent(Broadcast_Update_Notification);
//                            sendBroadcast(broadcastIntent);
//                        } else {
////                            startPlay(songIndex);
//                        }
//                    }

                    Log.e("play584----", "okay");
                    fn_play();
                    break;
                }

                case R.id.favicon: {
                    if (!favOn) {
                        favButton.setImageResource(R.drawable.favt_icon);
                        favOn = true;
                        fdhandler fdh = new fdhandler(getApplicationContext());
                        Audio fd = new Audio(currentList.get(currentIndex).getData(), currentList.get(currentIndex).getTitle(), currentList.get(currentIndex).getAlbum(), currentList.get(currentIndex).getArtist(), currentList.get(currentIndex).getAlbum_art(), currentList.get(currentIndex).getAlbum_id());
                        fdh.addfavouriteevent(fd);
                        ArrayList<Audio> favolist = fdh.favdata();
                        index = favolist.size() - 1;
                        Toast.makeText(getApplicationContext(), "Song added to your Favourites", Toast.LENGTH_SHORT).show();
                        break;
                    } else {

                        Toast.makeText(getApplicationContext(), "Song removed from Favourites", Toast.LENGTH_SHORT).show();
                        favButton.setImageResource(R.drawable.unfav);
                        favOn = false;
                        fdhandler fdh = new fdhandler(getApplicationContext());
                        fav_list = fdh.favdata();
                        Audio fd = new Audio(fav_list.get(index).getData(), fav_list.get(index).getTitle(), fav_list.get(index).getAlbum(), fav_list.get(index).getArtist(), fav_list.get(index).getAlbum_art(), fav_list.get(index).getAlbum_id());
                        fdh.deleteEvent(fd);

                        //  NotificationActivity.delete(fav_list.get(index).get_id());
                        // index=-1;

                    }
                    break;
                }
                case R.id.next: {
//
//                    if (!shuffleOnOff && !repeatOnOff) {
//                        if (currentIndexOfTheSong < (audioList.size() - 1)) {
//                            currentIndexOfTheSong = currentIndexOfTheSong + 1;
//                            Log.e("SongIndex next", String.valueOf(currentIndexOfTheSong));
//                        } else
//                            currentIndexOfTheSong = 0;
////                        startPlay(currentIndexOfTheSong);
//
//                    } else if (!shuffleOnOff && repeatOnOff) {
////                        startPlay(currentIndexOfTheSong);
//                    } else if (shuffleOnOff && !repeatOnOff) {
//
//                        Random random = new Random();
//                        currentIndexOfTheSong = random.nextInt(audioList.size());
//                        Log.e("Image", "--Image");
////                        startPlay(currentIndexOfTheSong);
//                    }
                    Log.e("Image631", "--Image");
                    fn_playNext();

                    break;
                }
                case R.id.prev: {

//                    if (!shuffleOnOff && !repeatOnOff) {
//                        if (currentIndexOfTheSong > 0)
//                            currentIndexOfTheSong = currentIndexOfTheSong - 1;
//                        else
//                            currentIndexOfTheSong = audioList.size() - 1;
////                        startPlay(currentIndexOfTheSong);
//                    } else if (!shuffleOnOff && repeatOnOff) {
////                        startPlay(currentIndexOfTheSong);
//                    } else if (shuffleOnOff && !repeatOnOff) {
//
//                        Random random = new Random();
//                        currentIndexOfTheSong = random.nextInt(audioList.size());
////                        startPlay(currentIndexOfTheSong);
//                    }
                    Log.e("Image652", "--Image");
                    fn_playPrevious();
                    break;
                }

                case slideClose: {
                    mSlidingLayer.closeLayer(true);
                    break;
                }

                case selectedfile: {
                    mSlidingLayer.openLayer(true);
                    String[] projection = {
                            fdhandler.COLUMN_title,
                    };
                    SQLiteDatabase db = mDbHelper.getReadableDatabase();
                    Cursor cursor = db.query(
                            fdhandler.TABLE,   // The table to query
                            projection,            // The columns to return
                            null,                  // The columns for the WHERE clause
                            null,                  // The values for the WHERE clause
                            null,                  // Don't group the rows
                            null,                  // Don't filter by row groups
                            null);
                    Log.e("currentName 678", songIndex + "");
                    try {
                        currentTitle = currentList.get(currentIndex).getTitle();
                        favButton.setImageResource(R.drawable.unfav);
                        favOn = false;
                        while (cursor.moveToNext()) {

                            int nameColumnIndex = cursor.getColumnIndex(fdhandler.COLUMN_title);
                            String currentName = cursor.getString(nameColumnIndex);
                            if (currentTitle.equals(currentName)) {

                                favButton.setImageResource(R.drawable.favt_icon);
                                favOn = true;
                                Log.e("currentName 678", currentName);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case currentSongArtist: {
                    mSlidingLayer.openLayer(true);
//                            String[] projection = {
//                                    fdhandler.COLUMN_title,
//                            };
//                            SQLiteDatabase db = mDbHelper.getReadableDatabase();
//                            Cursor cursor = db.query(
//                                    fdhandler.TABLE,   // The table to query
//                                    projection,            // The columns to return
//                                    null,                  // The columns for the WHERE clause
//                                    null,                  // The values for the WHERE clause
//                                    null,                  // Don't group the rows
//                                    null,                  // Don't filter by row groups
//                                    null);
//                            Log.e("currentName 678",songIndex+"");
//                            currentTitle = currentList.get(currentIndex).getTitle();
//                            favButton.setImageResource(R.drawable.unfav);
//                            favOn = false;
//                            while (cursor.moveToNext()) {
//
//                                int nameColumnIndex = cursor.getColumnIndex(fdhandler.COLUMN_title);
//                                String currentName = cursor.getString(nameColumnIndex);
//                                if (currentTitle.equals(currentName)) {
//
//                                    favButton.setImageResource(R.drawable.favt_icon);
//                                    favOn = true;
//                                    Log.e("currentName 678",currentName);
//                                    break;
//                                }
//                            }
                    break;
                }
                case openMediaPlayer: {


                    String[] projection = {
                            fdhandler.COLUMN_title,
                    };
                    SQLiteDatabase db = mDbHelper.getReadableDatabase();
                    Cursor cursor = db.query(
                            fdhandler.TABLE,   // The table to query
                            projection,            // The columns to return
                            null,                  // The columns for the WHERE clause
                            null,                  // The values for the WHERE clause
                            null,                  // Don't group the rows
                            null,                  // Don't filter by row groups
                            null);
                    Log.e("currentName 678", songIndex + "");
                    currentTitle = currentList.get(currentIndex).getTitle();
                    favButton.setImageResource(R.drawable.unfav);
                    favOn = false;
                    while (cursor.moveToNext()) {

                        int nameColumnIndex = cursor.getColumnIndex(fdhandler.COLUMN_title);
                        String currentName = cursor.getString(nameColumnIndex);
                        if (currentTitle.equals(currentName)) {

                            favButton.setImageResource(R.drawable.favt_icon);
                            favOn = true;
                            Log.e("currentName 678", currentName);
                            break;
                        }
                    }
                    mSlidingLayer.openLayer(true);
                    break;
                }

                case dismissOnClick: {
                    break;
                }

                case R.id.repeat: {
                    if (!repeatOnOff) {
                        shuffleButton.setTextColor(Color.WHITE);
                        shuffleOnOff = false;
                        repeatButton.setTextColor(Color.CYAN);
                        repeatOnOff = true;

                    } else {
                        repeatButton.setTextColor(Color.WHITE);
                        repeatOnOff = false;
                    }
                    break;
                }

                case R.id.shuffle: {
                    if (!shuffleOnOff) {
                        Log.e("435", "Shuffle is on");
                        shuffleButton.setTextColor(Color.CYAN);
                        shuffleOnOff = true;
                        repeatButton.setTextColor(Color.WHITE);
                        repeatOnOff = false;
                    } else {
                        Log.e("445", "Shuffle is off");
                        shuffleButton.setTextColor(Color.WHITE);
                        shuffleOnOff = false;
                    }

                    break;
                }

            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        //unbindManualService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (serviceBound) {
//            unbindService(serviceConnection);
//            //service is active
//            player.stopSelf();
//            //  unregisterReceiver(NotifyAction);
//        }
        Log.e("onDestroy202", "Okay 11");
        if (isMyServiceRunning(MediaPlayerService.class) && playerIntent != null) {
            Log.e("onDestroy203", "Okay");
            stopService(playerIntent);
            MediaPlayerService.mediaPlayer.stop();
            try {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(NOTIFICATION_ID);
            }catch (Exception e)
            {

            }
        }

    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //updateUI(null);
                        startActivity(new Intent(MainActivity.this, Skipnow.class));

                        Log.e("onDestroy203", "Okay");
                        stopService(playerIntent);
                        try {
                            MediaPlayerService.mediaPlayer.stop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(MainActivity.this, "LOGGED OUT", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
//        if (mediaPlayer != null && !mediaPlayer.isPlaying())
//            mediaPlayer.start();
        super.onResume();
        if (isMyServiceRunning(MediaPlayerService.class)) {
            StorageUtil St = new StorageUtil(MainActivity.this);
            layoutVisibility.setVisibility(View.VISIBLE);
            startAudioPlay(St.loadAudioIndex(), St.loadAudio());
            currentIndex = St.loadAudioIndex();
            currentList = St.loadAudio();
            if (musicPlayer != null) {
                playButton.setImageResource(android.R.drawable.ic_media_play);
                playButton1.setImageResource(android.R.drawable.ic_media_play);
            }
            if (musicPlayer == null) {
                playButton.setImageResource(android.R.drawable.ic_media_pause);
                playButton1.setImageResource(android.R.drawable.ic_media_pause);
            }
        }
        // playButton.setImageResource(android.R.drawable.ic_media_play);
        // playButton1.setImageResource(android.R.drawable.ic_media_play);
        //mAuth.addAuthStateListener(mAuthListener);
    }

//    @Override
//    public void onBackPressed() {
//        onResume();
//        super.onBackPressed();
//    }

    public void startAudioPlay(int currentSongIndex, ArrayList<Audio> currentAudioList) {

        songIndex = currentSongIndex;
        currentFile = currentAudioList.get(songIndex).getData();
        currentTitle = currentAudioList.get(songIndex).getTitle();
        currentBitmap = currentAudioList.get(songIndex).getAlbum_art();
        mpCurrentBitmap = currentAudioList.get(songIndex).getAlbum_art();
        mpCurrentSongOfAlbum = currentAudioList.get(songIndex).getAlbum();
        mpCurrentSongOfArtist = currentAudioList.get(songIndex).getArtist();
        mpCurrentSongOfTitle = currentAudioList.get(songIndex).getTitle();
        currentArtistName = currentAudioList.get(songIndex).getArtist();
        currentArtist.setText(currentArtistName);
        selelctedFile.setText(currentTitle);
        Glide.with(this).load(currentBitmap).placeholder(R.drawable.image).into(currentSongImage);

        Glide.with(this).load(mpCurrentBitmap).placeholder(R.drawable.image).into(mpCurrentImage);
        mpCurrentAlbum.setText(mpCurrentSongOfAlbum);
        mpCurrentArtist.setText(mpCurrentSongOfArtist);
        mpCurrentTitle.setText(mpCurrentSongOfTitle);

        /*mediaPlayer.stop();
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(currentFile);
            mediaPlayer.prepare();
            Log.e("SongIndex start audio", String.valueOf(songIndex));
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
fsettt            e.printStackTrace();
        }*/

        playButton.setImageResource(android.R.drawable.ic_media_pause);
        playButton1.setImageResource(android.R.drawable.ic_media_pause);
        //updatePosition();
        isStarted = true;

        openRecentPlay(songIndex, currentAudioList.get(songIndex));
    }

    public void openRecentPlay(int recentSongIndex, Audio tmpAudio) {

        indexForRecent = songIndex;
        songIndex = recentSongIndex;
//        Audio mAudio = new Audio(String.valueOf(songIndex), currentTitle, currentAlbum, currentArtistName, currentAlbum_Id, currentBitmap);
        ar_recent.clear();
        ar_recent.add(tmpAudio);


        String markers = sharedpreferences.getString("Recents", null);
        if (markers == null) {
            try {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                gson = new Gson();
                String json = gson.toJson(ar_recent);
                editor.putString("Recents", json);
                editor.commit();
                Log.e("IN IF", String.valueOf(ar_recent.size()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String markers1 = sharedpreferences.getString("Recents", null);
            gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<ArrayList<Audio>>() {
            }.getType();
            ar_recent_stored = gson.fromJson(markers1, type);
            if (ar_recent_stored.size() >= 10) {
                ar_recent_stored.remove(ar_recent_stored.size() - 1);
            }
        }
        boolean isMatched = false;
        for (j = 0; j < ar_recent_stored.size(); j++) {
            //Log.e("recent Stored--", ar_recent_stored.get(j).getTitle());
            //Log.e("recent--", ar_recent.get(0).getTitle());
            if (ar_recent.get(0).getTitle().equalsIgnoreCase(ar_recent_stored.get(j).getTitle())) {
                isMatched = true;
                try {
                    ar_recent_stored.remove(j);
                    ar_recent_stored.addAll(0, ar_recent);
                    //Log.e("Line 932--", "932");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //save new list in preferences again
                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                Gson gson2 = new Gson();
                String json1 = gson2.toJson(ar_recent_stored);
                editor1.putString("Recents", json1);
                editor1.commit();

            }
        }

        if (!isMatched) {
            ar_recent_stored.addAll(0, ar_recent);
//                //save new list in preferences again
            SharedPreferences.Editor editor = sharedpreferences.edit();
            Gson gson1 = new Gson();
            String json = gson1.toJson(ar_recent_stored);
            editor.putString("Recents", json);
            editor.commit();
        }


        String markers1 = sharedpreferences.getString("Recents", null);
        java.lang.reflect.Type type = new TypeToken<ArrayList<Audio>>() {
        }.getType();
        ar_recent_stored = gson.fromJson(markers1, type);
        Log.e("Size--al_recent_stored", ar_recent_stored.size() + "");
        Log.e("stored list", ar_recent_stored + "");
    }


    public void getCurrentTitle(int songCurrentIndex) {
        currentTitle = audioList.get(songCurrentIndex).getTitle();
        Log.e("Tile", currentTitle);
    }

//    public void startRecentPlay(int recentSongIndex, ArrayList<Audio> currentAudioList){
//        if (!serviceBound) {
//            //Store Serializable audioList to SharedPreferences
//            StorageUtil storage = new StorageUtil(getApplicationContext());
//            storage.storeAudio(currentAudioList);
//            storage.storeAudioIndex(recentSongIndex);
//            Intent playerIntent = new Intent(this, MediaPlayerServices_Ex.class);
//            startService(playerIntent);
//            /*Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
//            broadcastIntent.putExtra("currentIndex",currentSongIndex);
//            sendBroadcast(broadcastIntent);*/
//            Log.e("not service bound","");
//            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//            //getCurrentTitle(recentSongIndex);
//            Intent broadcastUpdateSeek2 = new Intent(Broadcast_Update_FIRSTSEEK);
//            sendBroadcast(broadcastUpdateSeek2);
//            startAudioPlay(recentSongIndex, currentAudioList);
//            currentIndexOfTheSong = recentSongIndex;
//        } else {
//            //Store the new audioIndex to SharedPreferences
//            StorageUtil storage = new StorageUtil(getApplicationContext());
//            storage.storeAudio(currentAudioList);
//            storage.storeAudioIndex(recentSongIndex);
//            Intent playerIntent = new Intent(this, MediaPlayerServices_Ex.class);
//            startService(playerIntent);
//            /*Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
//            broadcastIntent.putExtra("currentIndex",currentSongIndex);
//            sendBroadcast(broadcastIntent);*/
//            Log.e("not service bound","");
//            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//            //Service is active
//            //Send a broadcast to the service -> PLAY_NEW_AUDIO
//            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
//            broadcastIntent.putExtra("currentIndex",recentSongIndex);
//            broadcastIntent.putExtra("arraylist","R");
//            sendBroadcast(broadcastIntent);
//            //getCurrentTitle(recentSongIndex);
//            //startAudioPlay(currentSongIndex);
//            currentAudioListNew = currentAudioList;
//        }
//
//    }

//    public void startPlay(int currentSongIndex) {
//
//        if (!serviceBound) {
//            //Store Serializable audioList to SharedPreferences
//            StorageUtil storage = new StorageUtil(getApplicationContext());
//            storage.storeAudio(audioList);
//            storage.storeAudioIndex(currentSongIndex);
//            Intent playerIntent = new Intent(this, MediaPlayerServices_Ex.class);
//            startService(playerIntent);
//            /*Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
//            broadcastIntent.putExtra("currentIndex",currentSongIndex);
//            sendBroadcast(broadcastIntent);*/
//            Log.e("not service bound","");
//            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//            getCurrentTitle(currentSongIndex);
//            Intent broadcastUpdateSeek2 = new Intent(Broadcast_Update_FIRSTSEEK);
//            sendBroadcast(broadcastUpdateSeek2);
//            startAudioPlay(currentSongIndex,audioList);
//            currentIndexOfTheSong = currentSongIndex;
//        } else {
//            //Store the new audioIndex to SharedPreferences
//            StorageUtil storage = new StorageUtil(getApplicationContext());
//            storage.storeAudioIndex(currentSongIndex);
//
//            //Service is active
//            //Send a broadcast to the service -> PLAY_NEW_AUDIO
//            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
//            broadcastIntent.putExtra("currentIndex",currentSongIndex);
//            sendBroadcast(broadcastIntent);
//            getCurrentTitle(currentSongIndex);
//            //startAudioPlay(currentSongIndex);
//        }
//        String[] projection = {
//                fdhandler.COLUMN_title,
//        };
//        SQLiteDatabase db= mDbHelper.getReadableDatabase();
//        Cursor cursor = db.query(
//                fdhandler.TABLE,   // The table to query
//                projection,            // The columns to return
//                null,                  // The columns for the WHERE clause
//                null,                  // The values for the WHERE clause
//                null,                  // Don't group the rows
//                null,                  // Don't filter by row groups
//                null);
//
//        currentTitle = audioList.get(currentSongIndex).getTitle();
//        favButton.setImageResource(R.drawable.unfav);
//        favOn=false;
//        while (cursor.moveToNext()){
//            int nameColumnIndex = cursor.getColumnIndex(fdhandler.COLUMN_title);
//            String currentName = cursor.getString(nameColumnIndex);
//            if(currentTitle.equals(currentName)){
//                favButton.setImageResource(R.drawable.favt_icon);
//                favOn=true;
//                break;
//            }
////            else{
////                favButton.setImageResource(R.drawable.unfav);
////                favOn=false;
////            }
//
//        }
//    }

    public void initViews() {
        Log.e("I am on Line no. 725 ", "725");
//        parcelButtonClick = (Button) findViewById(R.id.parcelButton);
        Log.e("I am on Line no. 727 ", "727");
        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
        mpCurrentImage = (ImageView) findViewById(mpCurrentSongImage);
        mpCurrentTitle = (TextView) findViewById(mpCurrentSongTitle);
        mpCurrentAlbum = (TextView) findViewById(mpCurrentSongAlbum);
        mpCurrentArtist = (TextView) findViewById(mpCurrentSongArtist);
        currntSongDuration = (TextView) findViewById(R.id.durationFetch);
        currentSongPositionD = (TextView) findViewById(R.id.durationFetch1);
        Log.e("I am on Line no. 732 ", "732");
        shuffleButton = (Button) findViewById(R.id.shuffle);
        repeatButton = (Button) findViewById(R.id.repeat);
        selelctedFile = (TextView) findViewById(R.id.selectedfile);
        favButton = (ImageButton) findViewById(R.id.favicon);
        currentSongImage = (ImageView) findViewById(R.id.currentSongImage);
        currentArtist = (TextView) findViewById(currentSongArtist);
        //currentSongImage = BitmapFactory.decodeResource(getResources(), R.id.currentSongImage);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        seekBar1 = (SeekBar) findViewById(R.id.seekbar1);
        playButton = (ImageButton) findViewById(R.id.play);
        playButton1 = (ImageButton) findViewById(R.id.play1);
        prevButton = (ImageButton) findViewById(R.id.prev);
        nextButton = (ImageButton) findViewById(R.id.next);
        layoutVisibility = (LinearLayout) findViewById(R.id.layoutVisibility);

        // mediaPlayer = MediaPlayerServices_Ex.mediaPlayer;
        // mediaPlayer.setOnCompletionListener(onCompletion);
//        mediaPlayer.setOnErrorListener(onError);
        seekbar.setOnSeekBarChangeListener(seekBarChanged);
        seekBar1.setOnSeekBarChangeListener(seekBarChanged);
        slideDownButton = (ImageView) findViewById(slideClose);
        slideUp = (LinearLayout) findViewById(openMediaPlayer);
        dismissOnClickOnMediPlayer = (LinearLayout) findViewById(dismissOnClick);
    }

    public void loadAudio() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ID));
                // Save to audioList

                try {
                    Log.e("album--", album);
                    Log.e("album-id--", album_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (album_id.equalsIgnoreCase("") || album_id.equalsIgnoreCase(null)) {

                } else {
                    // ImageButton album_image = (ImageButton) findViewById(R.id.play_pause);

                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

                    uri = ContentUris.withAppendedId(sArtworkUri, Integer.valueOf(album_id));
                    ContentResolver res = getContentResolver();
                    Log.e("URI:-", uri.toString());
                    InputStream in;
                    try {
                        in = res.openInputStream(uri);
                        artwork = BitmapFactory.decodeStream(in);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.e("catch", album_id);
                        artwork = BitmapFactory.decodeResource(getResources(),
                                R.drawable.image);
                        //Picasso.with(this).load(R.drawable.image).into(R.id.play_pause);
                    }
                }

                audioList.add(new Audio(data, title, album, artist, album_id, uri.toString()));
            }
        }
        cursor.close();
    }


//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            MediaPlayerServices_Ex.LocalBinder binder = (MediaPlayerServices_Ex.LocalBinder) service;
//            musicPlayer = binder.getService();
//            serviceBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            serviceBound = false;
//        }
//    };

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("serviceStatus");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("serviceStatus", serviceBound);
    }

//    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//        }
//    };

//    private BroadcastReceiver becomingNoisyReceiver1 = new BroadcastReceiver() {
//        @Override
//        public void onReceive(final Context context, Intent intent) {
//
//            //songIndex = new StorageUtil(getApplicationContext()).loadAudioIndex();
//            songIndex=intent.getExtras().getInt("Index",0);
//            playingStatus=intent.getBooleanExtra("Status",false);
//            playingStatus = intent.getExtras().getBoolean("Status");
//            stringArray = intent.getStringExtra("arraySongString");
//
//            try{
//            if(stringArray.equals("R")){
//
//                if (songIndex != -1 && songIndex < currentAudioListNew.size()) {
//                    //index is in a valid range
//                    activeAudio = currentAudioListNew.get(songIndex);
//                    currentIndexOfTheSong = songIndex;
//                    Log.e("currentIndexOfTheSong", String.valueOf(songIndex));
//                    startAudioPlay(songIndex,currentAudioListNew);
//                } else {
//                    // mediaPlayer.stop();
//                }
//            }else{
//                if (songIndex != -1 && songIndex < audioList.size()) {
//                    //index is in a valid range
//                    activeAudio = audioList.get(songIndex);
//                    currentIndexOfTheSong = songIndex;
//                    Log.e("currentIndexOfTheSong", String.valueOf(songIndex));
//                    startAudioPlay(songIndex,audioList);
//                } else {
//                    // mediaPlayer.stop();
//                }
//            }
//
//            }
//            catch(Exception e){
//                if (songIndex != -1 && songIndex < audioList.size()) {
//                    //index is in a valid range
//                    activeAudio = audioList.get(songIndex);
//                    currentIndexOfTheSong = songIndex;
//                    Log.e("currentIndexOfTheSong", String.valueOf(songIndex));
//                    startAudioPlay(songIndex,audioList);
//                } else {
//                    // mediaPlayer.stop();
//                }
//            }
//
//            //pause audio on ACTION_AUDIO_BECOMING_NOISY
////            pauseMedia();
////            buildNotification(PlaybackStatus.PAUSED);
//        }
//    };
//    private void registerBecomingNoisyReceiver1() {
//        //register after getting audio focus
//        IntentFilter intentFilter = new IntentFilter(Broadcast_UPDATE_UI);
//        registerReceiver(becomingNoisyReceiver1, intentFilter);
//    }

//    private void registerBecomingNoisyReceiver10() {
//        //register after getting audio focus
//        IntentFilter intentFilter = new IntentFilter(Broadcast_UPDATE_SEEKBAR);
//        registerReceiver(becomingNoisyReceiver10, intentFilter);
//    }

//    private void registerBecomingNoisyReceiver101() {
//        //register after getting audio focus
//        IntentFilter intentFilter = new IntentFilter(Broadcast_UPDATE_SEEK);
//        registerReceiver(becomingNoisyReceiver101, intentFilter);
//    }

//    private BroadcastReceiver becomingNoisyReceiver10 = new BroadcastReceiver() {
//        @Override
//        public void onReceive(final Context context, Intent intent) {
//
//            seekValueReceived = intent.getIntExtra("seek",0);
//            seekValueReceived = intent.getExtras().getInt("seek");
//            seekDurationReceived = intent.getIntExtra("seekDuration",0);
//            seekDurationReceived = intent.getExtras().getInt("seekDuration");
//
//        }
//    };

    public class FeedbackAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            loadAudio();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(context);
            pdia.setMessage("Loading...");
            pdia.setCancelable(true);
            pdia.setCanceledOnTouchOutside(false);
            pdia.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pdia.dismiss();
            FragmentHome fragmentHome = new FragmentHome();

            FragmentTransaction transaction = fm.beginTransaction();
            Ftag = "fragHome";
            transaction.add(R.id.container1, fragmentHome, "fragHome");
            transaction.commit();
        }
    }

    public class FeedbackAsync1 extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            loadAudio();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(context);
            pdia.setMessage("Loading...");
            pdia.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            fm = getFragmentManager();
            MainActivityLocalSong fragmentlocal = new MainActivityLocalSong();
            FragmentTransaction transaction = fm.beginTransaction();
            Ftag = "fragLocal";
            transaction.replace(R.id.container1, fragmentlocal, "fragLocal");
            transaction.commit();
            pdia.dismiss();

        }
    }

//    private BroadcastReceiver becomingNoisyReceiver101 = new BroadcastReceiver() {
//        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//        @Override
//        public void onReceive(final Context context, Intent intent) {
//            //pause audio on ACTION_AUDIO_BECOMING_NOISY
//            seekValueReceived = intent.getIntExtra("seek",0);
//            seekValueReceived = intent.getExtras().getInt("seek");
//            seekDurationReceived = intent.getIntExtra("seekDuration",0);
//            seekDurationReceived = intent.getExtras().getInt("seekDuration");
//
//            seekBar1.setMax(seekDurationReceived);
//            seekbar.setMax(seekDurationReceived);
//
//            seekBar1.setProgress(seekValueReceived);
//            seekbar.setProgress(seekValueReceived);
//
//
//            songDuration = String.format("%02d:%02d",
//                    TimeUnit.MILLISECONDS.toMinutes(seekDurationReceived),
//                    TimeUnit.MILLISECONDS.toSeconds(seekDurationReceived) -
//                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seekDurationReceived))
//            );
//            songDurationPosition = String.format("%02d:%02d",
//                    TimeUnit.MILLISECONDS.toMinutes(seekValueReceived),
//                    TimeUnit.MILLISECONDS.toSeconds(seekValueReceived) -
//                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seekValueReceived))
//            );
//
//
//            try{
//                currntSongDuration.setText(songDuration);}
//            catch (Exception e){e.printStackTrace();}
//
//            try{
//                currentSongPositionD.setText(songDurationPosition);}
//            catch (Exception e){e.printStackTrace();}
//        }
//    };

//    @Override
//    protected void onStart(){
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }


    private SeekBar.OnSeekBarChangeListener seekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isMoveingSeekBar = false;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isMoveingSeekBar = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (isMoveingSeekBar) {

//                    MediaPlayerService.mediaPlayer.pause();
                MediaPlayerService.mediaPlayer.seekTo(progress);
                //MediaPlayerService.mediaPlayer.start();
                //startProgress();
                //mediaPlayer.seekTo(progress);
//                Intent broadcastUpdateSeek = new Intent(Broadcast_Update_SEEKPOSITION);
//                broadcastUpdateSeek.putExtra("progress", progress);
//                sendBroadcast(broadcastUpdateSeek);
//                Log.i("OnSeekBarChangeListener", "onProgressChanged");
            }
        }
    };
//    private void registerBecomingNoisyReceiver1001() {
//        //register after getting audio focus
//        IntentFilter intentFilter = new IntentFilter(Broadcast_UPDATE_UI_ONCOMPLETION);
//        registerReceiver(becomingNoisyReceiver1001, intentFilter);
//    }

//    private BroadcastReceiver becomingNoisyReceiver1001 = new BroadcastReceiver() {
//        @Override
//        public void onReceive(final Context context, Intent intent) {
//            onCompleteIndex = intent.getIntExtra("Index",0);
//            onCompleteIndex = intent.getExtras().getInt("Index");
//            startAudioPlay(onCompleteIndex,audioList);
//
//        }
//    };


    private void init() {

        playerIntent = new Intent(this, MediaPlayerService.class);

        Log.e("init90", " " + isBound);
        if (!isBound) {
            bindServiceCustom();
            Log.e("init92", " " + isBound);
        }

        NotifyAction = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.e("MABroadcastReceiver1340", "" + MediaPlayerService.NotiFyAction);
                StorageUtil St = new StorageUtil(MainActivity.this);

                if (MediaPlayerService.NotiFyAction != null && MediaPlayerService.NotiFyAction.equalsIgnoreCase(ACTION_PLAY)) {
                    MediaPlayerService.NotiFyAction = null;
//                    fn_play();
                    playButton.setImageResource(android.R.drawable.ic_media_pause);
                    playButton1.setImageResource(android.R.drawable.ic_media_pause);
                    startProgress();
                    isPaused = false;
                    Log.e("ACTION_PLAY", "241");
                } else if (MediaPlayerService.NotiFyAction != null && MediaPlayerService.NotiFyAction.equalsIgnoreCase(ACTION_STOP)) {
                    MediaPlayerService.NotiFyAction = null;
//                    fn_pause();
                    playButton.setImageResource(android.R.drawable.ic_media_play);
                    playButton1.setImageResource(android.R.drawable.ic_media_play);
                    isPaused = true;
                    stopProgress();


                    Log.e("ACTION_STOP", "241");
                } else if (MediaPlayerService.NotiFyAction != null && MediaPlayerService.NotiFyAction.equalsIgnoreCase(ACTION_NEXT)) {
                    MediaPlayerService.NotiFyAction = null;
//                    fn_playNext();
                    startAudioPlay(St.loadAudioIndex(), St.loadAudio());
                    Log.e("ACTION_NEXT", "247");
                } else if (MediaPlayerService.NotiFyAction != null && MediaPlayerService.NotiFyAction.equalsIgnoreCase(ACTION_PREVIOUS)) {
                    MediaPlayerService.NotiFyAction = null;
//                    fn_playPrevious();
                    startAudioPlay(St.loadAudioIndex(), St.loadAudio());
                    Log.e("ACTION_PREVIOUS", "252");
                } else if (MediaPlayerService.NotiFyAction != null && MediaPlayerService.NotiFyAction.equalsIgnoreCase(ACTION_PAUSE)) {
                    MediaPlayerService.NotiFyAction = null;
                    playButton.setImageResource(android.R.drawable.ic_media_play);
                    playButton1.setImageResource(android.R.drawable.ic_media_play);
                    isPaused = true;
                    stopProgress();
//                    fn_pause();
                    Log.e("ACTION_PAUSE", "241");
                } else if (MediaPlayerService.NotiFyAction != null && MediaPlayerService.NotiFyAction.equalsIgnoreCase(ACTION_NEXT_COMPLETE)) {
//                    if(playAll){
                    MediaPlayerService.NotiFyAction = null;
                    fn_playNext();
//                    }
//                    startAudioPlay(St.loadAudioIndex(), St.loadAudio());
                    Log.e("ACTION_NEXT_COMPLETE", "292");
                }


            }
        };
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void register_notify() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(Broadcast_NOTIFY);
        registerReceiver(NotifyAction, filter);
        isRegistered = true;
    }

    public void bindServiceCustom() {

        if (isMyServiceRunning(MediaPlayerService.class)) {
            Log.e("onStart334", "Okay" + playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            isBound = true;
            Log.e("onStart334", "Okay" + isBound);
        }

    }

    //To play audio
    public void playAudio(int audioIndex, ArrayList<Audio> list) {
        layoutVisibility.setVisibility(View.VISIBLE);
        currentList = list;
        currentIndex = audioIndex;
        Log.e("audioIndex 169", audioIndex + " " + isBound + " " + isMyServiceRunning(MediaPlayerService.class));
        //Check is service is active
        if (!isMyServiceRunning(MediaPlayerService.class)) {
            //Store Serializable audioList to SharedPreferences
            Log.e("172", list.size() + "");
            storage.storeAudio(list);
            storage.storeAudioIndex(audioIndex);

            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
        playButton.setImageResource(android.R.drawable.ic_media_pause);
        playButton1.setImageResource(android.R.drawable.ic_media_pause);
        Handler handlerSub = new Handler();
        positionUpdate();
//        handlerSub.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //Do something after 100ms
////                                    Log.e("87", "" + (int) MediaPlayerService.maxdur / 1000);
//                //stopProgress();
//                Log.e("5", "handler ");
//                positionUpdate();
//                //startProgress();
//            }
//        }, 1000);

        String[] projection = {
                fdhandler.COLUMN_title,
        };
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int i = 0;
        Cursor cursor = db.query(
                fdhandler.TABLE,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);

        currentTitle = list.get(audioIndex).getTitle();
        favButton.setImageResource(R.drawable.unfav);
        favOn = false;
        while (cursor.moveToNext()) {

            int nameColumnIndex = cursor.getColumnIndex(fdhandler.COLUMN_title);
            String currentName = cursor.getString(nameColumnIndex);
            if (currentTitle.equals(currentName)) {

                favButton.setImageResource(R.drawable.favt_icon);
                favOn = true;
                index = i;

                Log.e("index 962", i + "");
                Log.e("currentName 963", currentName);
                break;
            }
//            else{
//                favButton.setImageResource(R.drawable.unfav);
//                favOn=false;
//            }
            i++;
        }
    }


    /**************************
     * To start progess of seekbar
     *****************/

    public void startProgress() {
        try {
            runnable = new Runnable() {
                @Override
                public void run() {
//                    scnds = scnds + 1;
                    try {
//                        if(MediaPlayerService.mediaPlayer != null){
//                        scnds = MediaPlayerService.mediaPlayer.getCurrentPosition() / 1000;

//                            scnds = scnds+1;
//                    Log.e("handler 145", "handler " + scnds);
                        if (MediaPlayerService.mediaPlayer != null) {
                            int progInd = (int) Math.floor((100 * MediaPlayerService.mediaPlayer.getCurrentPosition() / 1000) / ((int) MediaPlayerService.maxdur / 1000));
//                                    seekbar.setProgress(progInd);
//                                    seekBar1.setProgress(progInd);
                            songDuration = String.format("%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes(MediaPlayerService.maxdur),
                                    TimeUnit.MILLISECONDS.toSeconds(MediaPlayerService.maxdur) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(MediaPlayerService.maxdur))
                            );
                            songDurationPosition = String.format("%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes(MediaPlayerService.mediaPlayer.getCurrentPosition()),
                                    TimeUnit.MILLISECONDS.toSeconds(MediaPlayerService.mediaPlayer.getCurrentPosition()) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(MediaPlayerService.mediaPlayer.getCurrentPosition()))
                            );
                            currentSongPositionD.setText(songDurationPosition);
                            currntSongDuration.setText(songDuration);
                            Log.e("handler 145 : " + progInd, ((int) MediaPlayerService.maxdur / 1000) + "handler " + progInd);
                            if (handler != null)
                                handler.postDelayed(this, 1000);

                            if (MediaPlayerService.mediaPlayer.getCurrentPosition() / 1000 >= (int) MediaPlayerService.maxdur / 1000) {
                                stopProgress();
//                            MainActivity.clkHolder.imageView.setImageResource(R.drawable.btn_play);
                            }
                        }
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("startProgress420", "Exception===========Exception===============Exception");
                    }
                }
            };
            if (handler != null)
                handler.post(runnable);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("startProgress417", "Exception===========Exception===============Exception");
        }

    }


    /***************************
     * To stop Progress
     *******************************/
    public static void stopProgress() {

        if (runnable != null && handler != null) {
            Log.e("fn_pause 1485", "" + isPaused);
            handler.removeCallbacks(runnable);
        }

    }

    public void fn_pause() {

        try {
//            Log.e("MainActivity", MainActivity.selecteditem + "");
            if (musicPlayer != null) {
                Log.e("fn_pause 1495", "" + isPaused);
                playButton.setImageResource(android.R.drawable.ic_media_play);
                playButton1.setImageResource(android.R.drawable.ic_media_play);
                musicPlayer.pauseMedia();
                MediaPlayerService.PlayAction = PAUSE;
                Intent broadcastIntent = new Intent(MediaPlayerService.Broadcast_ACTION);
                context.sendBroadcast(broadcastIntent);
                isPaused = true;
                stopProgress();

//                Log.e("Stop seekbar 117 ", scnds + "");

//
//                HashMap tmpMap = new HashMap<String, Object>();
//                tmpMap.put(AUDIOPROGRESS, scnds);
//                tmpMap.put(AUDIOINDEX, MainActivity.selecteditem);
//                Settings.previous_song = tmpMap;
//                if (MainActivity.clkHolder != null) {
//                    MainActivity.clkHolder.imageView.setImageResource(R.drawable.btn_play);
//                    MainActivity.clkHolder.seekBar.setVisibility(View.VISIBLE);
//                    MainActivity.clkHolder.heading.setVisibility(View.VISIBLE);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("fn_pause417", "Exception===========Exception===============Exception");
        }
    }

    public void fn_play() {

        try {
            Log.e("fn_play 1528", "" + isPaused);
            if (isPaused) {
                Log.e("fn_play 1528", "" + isPaused);
                playButton.setImageResource(android.R.drawable.ic_media_pause);
                playButton1.setImageResource(android.R.drawable.ic_media_pause);
                MediaPlayerService.PlayAction = RESUME;
                Intent broadcastIntent = new Intent(MediaPlayerService.Broadcast_ACTION);
                context.sendBroadcast(broadcastIntent);
                musicPlayer.resumeMedia();
                startProgress();
                isPaused = false;
            } else {
                Log.e("MusicAdapter 1541", "");
                if (MediaPlayerService.mediaPlayer.isPlaying())
                    fn_pause();
            }

            Log.e("MusicAdapter 1546", "");
//        MainActivity.clkHolder.imageView.setImageResource(R.drawable.btn_pause);
//        MainActivity.clkHolder.seekBar.setVisibility(View.VISIBLE);
//        MainActivity.clkHolder.heading.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("fn_play478", "Exception===========Exception===============Exception");
        }
    }

    //    public void fn_playing() {
//        try{
//            if(MainActivity.selecteditem>-1) {
//                Log.e("fn_playing491", "selecteditem "+MainActivity.selecteditem);
//                ViewHolder nextHolder = (ViewHolder) Settings.holderList.get(MainActivity.selecteditem);
//                nextHolder.rl_holder.performClick();
//                Log.e("fn_playing494", "selecteditem "+MainActivity.selecteditem);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            Log.e("fn_playing487", "Exception===========Exception===============Exception");
//        }
//    }
//
    public void fn_playPrevious() {
        try {
            StorageUtil St = new StorageUtil(MainActivity.this);
            int pos = St.loadAudioIndex();
            Log.e("MainActivity", pos + " :pos");
            if (!shuffleOnOff && !repeatOnOff) {
                if (pos > 0) {
                    pos = pos - 1;
                    playAudio(pos, St.loadAudio());
                    startAudioPlay(pos, St.loadAudio());
                } else {
                    pos = St.loadAudio().size() - 1;
                    playAudio(pos, St.loadAudio());
                    startAudioPlay(pos, St.loadAudio());
                }

//                        startPlay(currentIndexOfTheSong);
            } else if (!shuffleOnOff && repeatOnOff) {
//                        startPlay(currentIndexOfTheSong);
                playAudio(pos, St.loadAudio());
                startAudioPlay(pos, St.loadAudio());
            } else if (shuffleOnOff && !repeatOnOff) {

                Random random = new Random();
                pos = random.nextInt(St.loadAudio().size());
//                        startPlay(currentIndexOfTheSong);
                playAudio(pos, St.loadAudio());
                startAudioPlay(pos, St.loadAudio());
            }
//            pos = pos + 1;
//            if (pos >= St.loadAudio().size()) {
//                pos = 0;
//            }
//            if (pos <= (St.loadAudio().size() - 1) && pos >= 0) {
//                playAudio(pos, St.loadAudio());
//                startAudioPlay(pos, St.loadAudio());
//            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("fn_playNext507", "Exception===========Exception===============Exception");
        }
    }

    //
    public void fn_playNext() {
        try {
            StorageUtil St = new StorageUtil(MainActivity.this);
            int pos = St.loadAudioIndex();
            Log.e("MainActivity", pos + " : pos");

            if (!shuffleOnOff && !repeatOnOff) {
                if (pos < (St.loadAudio().size() - 1)) {
                    pos = pos + 1;
                    Log.e("SongIndex next", String.valueOf(pos));
                    playAudio(pos, St.loadAudio());
                    startAudioPlay(pos, St.loadAudio());
                } else {
                    pos = 0;
//                        startPlay(currentIndexOfTheSong);
                    playAudio(pos, St.loadAudio());
                    startAudioPlay(pos, St.loadAudio());
                }

            } else if (!shuffleOnOff && repeatOnOff) {
//                        startPlay(currentIndexOfTheSong);
                playAudio(pos, St.loadAudio());
                startAudioPlay(pos, St.loadAudio());
            } else if (shuffleOnOff && !repeatOnOff) {

                Random random = new Random();
                pos = random.nextInt(St.loadAudio().size());
                Log.e("Image", "--Image");
//                        startPlay(currentIndexOfTheSong);
                playAudio(pos, St.loadAudio());
                startAudioPlay(pos, St.loadAudio());
            }


//        pos = St.loadAudioIndex() - 1;
//        if (pos < 0) {
//            pos = St.loadAudio().size() - 1;
//        }
//
////            Log.e("selecteditem289", MainActivity.selecteditem + "");
//        if (pos >= 0 && pos <= St.loadAudio().size() - 1) {
//            Log.e("selecteditem290",pos + " : pos");
//            playAudio(pos, St.loadAudio());
//            startAudioPlay(pos, St.loadAudio());
//        }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("fn_playPrevious527", "Exception===========Exception===============Exception");
        }
    }

    private void registerBecomingNoisyReceiver2001() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(Broadcast_UPDATE_ONDELETE_NOTIFICATOIN);
        registerReceiver(becomingNoisyReceiver2001, intentFilter);

    }

    private BroadcastReceiver becomingNoisyReceiver2001 = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(final Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            if (!isPaused) {
                //handler.removeCallbacks(updatePositionRunnable);
                playButton.setImageResource(android.R.drawable.ic_media_play);
                playButton1.setImageResource(android.R.drawable.ic_media_play);
                //fn_pause();
                playButton.setImageResource(android.R.drawable.ic_media_play);
                playButton1.setImageResource(android.R.drawable.ic_media_play);
                musicPlayer.pauseMedia();
                isPaused = true;
                stopProgress();
            } else {
                //fn_pause();
                playButton.setImageResource(android.R.drawable.ic_media_play);
                playButton1.setImageResource(android.R.drawable.ic_media_play);
                musicPlayer.pauseMedia();
                isPaused = true;
                stopProgress();
            }
            //mediaPlayer.stop();
        }
    };

    public void openRecentFragment(View v) {
        fm = getFragmentManager();
        Recents favfragment = new Recents();
        FragmentTransaction transaction = fm.beginTransaction();
        Ftag = "fragRecent";
        transaction.replace(R.id.container1, favfragment, "fragRecent");
        transaction.commit();
    }

    public void openFavoriteFragment(View v) {
        fm = getFragmentManager();
        NotificationActivity favfragment = new NotificationActivity();
        FragmentTransaction transaction = fm.beginTransaction();
        Ftag = "fragFavorite";
        transaction.replace(R.id.container1, favfragment, "fragFavorite");
        transaction.commit();
    }

    public void openLocal(View v) {
        fm = getFragmentManager();
        MainActivityLocalSong fragmentlocal = new MainActivityLocalSong();
        FragmentTransaction transaction = fm.beginTransaction();
        Ftag = "fragLocal";
        transaction.replace(R.id.container1, fragmentlocal, "fragLocal");
        transaction.commit();
    }

    public void positionUpdate() {

        handler.removeCallbacks(updatePositionRunnable);
        //MediaPlayerServices.updateSeekPosition();
//                try {
//                    seekbar.setProgress(MediaPlayerService.mediaPlayer.getCurrentPosition());
//                    seekBar1.setProgress(MediaPlayerService.mediaPlayer.getCurrentPosition());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
        try {

            durationSeek = MediaPlayerService.mediaPlayer.getDuration();
            positionSeek = MediaPlayerService.mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }

        seekBar1.setMax(durationSeek);
        seekbar.setMax(durationSeek);

        seekBar1.setProgress(positionSeek);
        seekbar.setProgress(positionSeek);


        songDuration = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(durationSeek),
                TimeUnit.MILLISECONDS.toSeconds(durationSeek) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationSeek))
        );
        songDurationPosition = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(positionSeek),
                TimeUnit.MILLISECONDS.toSeconds(positionSeek) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(positionSeek))
        );


        try {
            currntSongDuration.setText(songDuration);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            currentSongPositionD.setText(songDurationPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);

        if (((positionSeek >= durationSeek) || (positionSeek > (durationSeek - 1000))) && positionSeek != 0 && durationSeek != 0) {
            //completetion();

        }
    }

}