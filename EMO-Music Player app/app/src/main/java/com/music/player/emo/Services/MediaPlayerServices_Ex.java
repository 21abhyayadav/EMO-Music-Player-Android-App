package com.music.player.emo.Services;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.music.player.emo.Activities.MainActivity;
import com.music.player.emo.Audio;
import com.music.player.emo.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import static com.music.player.emo.Activities.MainActivity.Broadcast_Update_FIRSTSEEK;
import static com.music.player.emo.Activities.MainActivity.Broadcast_Update_Notification;
import static com.music.player.emo.Activities.MainActivity.Broadcast_Update_SEEKPOSITION;
import static com.music.player.emo.Activities.MainActivity.artwork;
import static com.music.player.emo.Activities.MainActivity.handler;
import static com.music.player.emo.Activities.MainActivity.isStarted;
import static com.music.player.emo.Activities.MainActivity.playButton;
import static com.music.player.emo.Activities.MainActivity.playButton1;
import static com.music.player.emo.Activities.MainActivity.repeatOnOff;
import static com.music.player.emo.Activities.MainActivity.seekBar1;
import static com.music.player.emo.Activities.MainActivity.seekbar;
import static com.music.player.emo.Activities.MainActivity.shuffleOnOff;
import static com.music.player.emo.Activities.MainActivityLocalSong.Broadcast_PLAY_NEW_AUDIO;

public class MediaPlayerServices_Ex extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {
    private static final int UPDATE_FREQUENCY = 500;
   // public static final String Broadcast_UPDATE_UI
    public static final String Broadcast_UPDATE_UI = "com.example.android.hymn.Activities.PlayNewAudio1";
    public static final String Broadcast_UPDATE_SEEKBAR = "com.example.android.hymn.Activities.PlayNewAudio10";
    public static final String Broadcast_UPDATE_SEEK = "com.example.android.hymn.Activities.PlayNewAudio101";
    public static final String Broadcast_UPDATE_UI_ONCOMPLETION = "com.example.android.hymn.Activities.PlayNewAudio1001";
    public static final String Broadcast_UPDATE_ONDELETE_NOTIFICATOIN = "com.example.android.hymn.Activities.PlayNewAudio2001";

    public static final String ACTION_PLAY = "com.example.android.hymn.Services.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.android.hymn.Services.ACTION_PAUSE";
    public static final String ACTION_STOP = "com.example.android.hymn.Services.ACTION_STOP";
//    public static final String ACTION_PREVIOUS = "com.example.android.hymn.Services.ACTION_PREVIOUS";
//    public static final String ACTION_NEXT = "com.example.android.hymn.Services.ACTION_NEXT";
    public static MediaPlayer mediaPlayer;
    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;
    //AudioPlayer notification ID

    int durationSeek = 0;
    int positionSeek = 0;
    int progress = 0;
    private static final int NOTIFICATION_ID = 101;
    //Used to pause/resume MediaPlayer
    private int resumePosition;
    //AudioFocus
    private AudioManager audioManager;
    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();
    //List of available Audio files
    public final Runnable updatePositionRunnable = new Runnable() {
        public void run() {
            updatePosition();
        }
    };
    int int_condition = 0;
    private ArrayList<Audio> audioList;
    private int audioIndex = -1;
    private Audio activeAudio; //an object on the currently playing audio
    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    public static PendingIntent play_pauseAction = null;
    public static int notificationAction ;
    private static Context mContext;
    String mpCurrentBitmap = null;
    public Context context;
    public boolean playerPlaying = false;
    public static int seekValue = 0;
    public static int seekDuration = 0;
    public String  mediaPlayerArrayListString = "";
    /**
     * Service lifecycle methods
     */
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }



   /* public MediaPlayerServices_Ex(Context context){
        this.context=context;
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        // Perform one-time setup procedures


        Log.e("on create","");
        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.
        mContext = this;
        callStateListener();
        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
        registerBecomingNoisyReceiver20();
        registerBecomingNoisyReceiver2000();
        registerBecomingNoisyReceiver2001();
        //Listen for new Audio to play -- BroadcastReceiver
        register_playNewAudio();
        register_playNewAudio1();

    }

    //The system calls this method when an activity, requests the service be started
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            //Load data from SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            audioList = storage.loadAudio();
            Log.e("audioList", audioList+"");
            audioIndex = storage.loadAudioIndex();

            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
        }

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaSession.release();
        removeNotification();
        return super.onUnbind(intent);
    }

    public void updatePosition() {
        handler.removeCallbacks(updatePositionRunnable);
        //MediaPlayerServices_Ex.updateSeekPosition();
        try{
        seekbar.setProgress(mediaPlayer.getCurrentPosition());
        seekBar1.setProgress(mediaPlayer.getCurrentPosition());}
        catch (Exception e){
            e.printStackTrace();
        }

        durationSeek = mediaPlayer.getDuration();
        positionSeek = mediaPlayer.getCurrentPosition();

        Intent broadcastUpdateSeek = new Intent(Broadcast_UPDATE_SEEK);
        broadcastUpdateSeek.putExtra("seek",positionSeek);
        broadcastUpdateSeek.putExtra("seekDuration",durationSeek);
        sendBroadcast(broadcastUpdateSeek);
        handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);

        if(((positionSeek >= durationSeek) || (positionSeek > (durationSeek - 1000)))&& positionSeek!=0 && durationSeek !=0){
            //completetion();
        }
    }

    public void completetion(){

        if(MainActivity.serviceBound) {
            if (audioIndex < audioList.size() - 1) {
                if (!shuffleOnOff && !repeatOnOff) {
                    if (audioIndex < (audioList.size() - 1))
                        audioIndex = audioIndex + 1;
                    else
                        audioIndex = 0;
                    Log.e("Image1564698" + "", "--Image");
                    Intent broadcastUpdateSeek1001 = new Intent(Broadcast_UPDATE_UI_ONCOMPLETION);
                    broadcastUpdateSeek1001.putExtra("Index",audioIndex);
                    sendBroadcast(broadcastUpdateSeek1001);
                } else if (!shuffleOnOff && repeatOnOff) {
                    Intent broadcastUpdateSeek1001 = new Intent(Broadcast_UPDATE_UI_ONCOMPLETION);
                    broadcastUpdateSeek1001.putExtra("Index",audioIndex);
                    sendBroadcast(broadcastUpdateSeek1001);

                } else if (shuffleOnOff && !repeatOnOff) {
                    Random random = new Random();
                    audioIndex = random.nextInt(audioList.size());
                    Intent broadcastUpdateSeek1001 = new Intent(Broadcast_UPDATE_UI_ONCOMPLETION);
                    broadcastUpdateSeek1001.putExtra("Index",audioIndex);
                    sendBroadcast(broadcastUpdateSeek1001);
                    //startPlay(audioIndex);
                    // openOnComplete(audioIndex);
                }
                activeAudio = audioList.get(audioIndex);
                initMediaPlayer();
                updateMetaData();
            } else {
                stopMedia();
                removeNotification();
                //stop the service
                stopSelf();
            }
        }else {}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);

        //clear cached playlist
        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
    }

    /**
     * Service Binder
     */
    public class LocalBinder extends Binder {
        public MediaPlayerServices_Ex getService() {
            // Return this instance of LocalService so clients can call public methods
            return MediaPlayerServices_Ex.this;
        }
    }
    /**
     * MediaPlayer callback methods
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCompletion(MediaPlayer mp) {
        //Invoked when playback of a media source has completed.
        if(MainActivity.serviceBound && positionSeek!=0 && durationSeek!=0) {
            if (audioIndex < audioList.size() - 1) {
                if (!shuffleOnOff && !repeatOnOff) {
                    if (audioIndex < (audioList.size() - 1))
                        audioIndex = audioIndex + 1;
                    else
                        audioIndex = 0;
                    Log.e("Image1564698" + "", "--Image");
                    Intent broadcastUpdateSeek1001 = new Intent(Broadcast_UPDATE_UI_ONCOMPLETION);
                    broadcastUpdateSeek1001.putExtra("Index",audioIndex);
                    sendBroadcast(broadcastUpdateSeek1001);
                    activeAudio = audioList.get(audioIndex);
                    buildSongNotification();
                } else if (!shuffleOnOff && repeatOnOff) {
                    Intent broadcastUpdateSeek1001 = new Intent(Broadcast_UPDATE_UI_ONCOMPLETION);
                    broadcastUpdateSeek1001.putExtra("Index",audioIndex);
                    sendBroadcast(broadcastUpdateSeek1001);
                    activeAudio = audioList.get(audioIndex);
                    buildSongNotification();
                } else if (shuffleOnOff && !repeatOnOff) {
                    Random random = new Random();
                    audioIndex = random.nextInt(audioList.size());
                    Intent broadcastUpdateSeek1001 = new Intent(Broadcast_UPDATE_UI_ONCOMPLETION);
                    broadcastUpdateSeek1001.putExtra("Index",audioIndex);
                    sendBroadcast(broadcastUpdateSeek1001);
                    //startPlay(audioIndex);
                    // openOnComplete(audioIndex);
                    activeAudio = audioList.get(audioIndex);
                    buildSongNotification();
                }
                activeAudio = audioList.get(audioIndex);
                initMediaPlayer();
                updateMetaData();
            } else {
                stopMedia();
                removeNotification();
                //stop the service
                stopSelf();
            }
        }else {}
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        //Invoked when the media source is ready for playback.
        playMedia();
    }
    private void skipToNext() {

        if (audioIndex == audioList.size() - 1) {
            //if last in playlist
            audioIndex = 0;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get next in playlist
            activeAudio = audioList.get(++audioIndex);
        }

        //Update stored index
        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }

    private void skipToPrevious() {

        if (audioIndex == 0) {
            //if first in playlist
            //set index to the last of audioList
            audioIndex = audioList.size() - 1;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get previous in playlist
            activeAudio = audioList.get(--audioIndex);
        }

        //Update stored index
        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }
    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.

    }

    @Override
    public void onAudioFocusChange(int focusState) {

        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    /**
     * AudioFocus
     */
    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    /**
     * MediaPlayer actions
     */
    private void initMediaPlayer() {
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();//new MediaPlayer instance

        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();
        Log.e("Media Player--","media");

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(activeAudio.getData());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        try {
            mediaPlayer.prepareAsync();
        }catch (Exception e){}

    }

    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    /**
     * ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs
     */
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(final Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia();
            buildNotification(PlaybackStatus.PAUSED);

        }
    };

    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    /**
     * Handle PhoneState changes
     */
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * MediaSession and Notification actions
     */
    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPlay() {
                super.onPlay();

                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPause() {
                super.onPause();

                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSkipToNext() {
                super.onSkipToNext();

                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();

                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }
            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    private void updateMetaData() {
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.image); //replace with medias albumArt
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getTitle())
                .build());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void openplay1(){
        if (mediaPlayer.isPlaying()) {
            Log.e("notify--","notify");
            notificationAction = android.R.drawable.ic_media_play;
            //create the pause action
//            mediaPlayer.pause();
//            playButton.setImageResource(android.R.drawable.ic_media_play);
//            playButton1.setImageResource(android.R.drawable.ic_media_play);
            play_pauseAction = playbackAction(1);
            handler.removeCallbacks(updatePositionRunnable);
            buildNotification(PlaybackStatus.PAUSED);
            Log.e("notify-;';';';';-","notify");
        } else if (isStarted) {
            Log.e("notify-else-","notify");
            //mediaPlayer.start();
            notificationAction = android.R.drawable.ic_media_pause;
//            updatePosition();
//            //create the play action
//            playButton.setImageResource(android.R.drawable.ic_media_pause);
//            playButton1.setImageResource(android.R.drawable.ic_media_pause);
            play_pauseAction = playbackAction(0);
            handler.removeCallbacks(updatePositionRunnable);
            //MediaPlayerServices_Ex.updateSeekPosition();
            try {
                seekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekBar1.setProgress(mediaPlayer.getCurrentPosition());
            }catch (Exception e){
                e.printStackTrace();
            }

            positionSeek = mediaPlayer.getCurrentPosition();
            Intent broadcastUpdateSeek = new Intent(Broadcast_UPDATE_SEEKBAR);
            broadcastUpdateSeek.putExtra("seek",positionSeek);
            broadcastUpdateSeek.putExtra("seekDuration",durationSeek);

            handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
            buildNotification(PlaybackStatus.PLAYING);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void buildNotification(PlaybackStatus playbackStatus) {

        /**
         * Notification actions -> playbackAction()
         *  0 -> Play
         *  1 -> Pause
         *  2 -> Next track
         *  3 -> Previous track
         */
        //updateSeekbarPosition();
        notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized

        Log.e("in noti","npyi");

        //Build a new notification according to the current state of the MediaPlayer
        if (mediaPlayer.isPlaying()) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the pause action
            mediaPlayer.pause();
            try {
                playButton.setImageResource(android.R.drawable.ic_media_play);
                playButton1.setImageResource(android.R.drawable.ic_media_play);
            }catch (Exception e){
                e.printStackTrace();
            }

            play_pauseAction = playbackAction(1);
        } else if (isStarted) {
            mediaPlayer.start();
            notificationAction = android.R.drawable.ic_media_pause;
            try {
                seekbar.setProgress(mediaPlayer.getCurrentPosition());
            }catch (Exception e){}
            try {
                seekBar1.setProgress(mediaPlayer.getCurrentPosition());
            }catch (Exception e){}
            positionSeek = mediaPlayer.getCurrentPosition();
            Intent broadcastUpdateSeek = new Intent(Broadcast_UPDATE_SEEK);
            broadcastUpdateSeek.putExtra("seek",positionSeek);
            broadcastUpdateSeek.putExtra("seekDuration",durationSeek);
            sendBroadcast(broadcastUpdateSeek);
            //create the play action
            try {
                playButton.setImageResource(android.R.drawable.ic_media_pause);
                playButton1.setImageResource(android.R.drawable.ic_media_pause);
            }catch (Exception e){
                e.printStackTrace();
            }

            play_pauseAction = playbackAction(0);
        }

        if(MediaStore.Audio.AlbumColumns.ALBUM_ID != null){
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            getAlbumArtNotification();
            try{
            Uri uri = ContentUris.withAppendedId(sArtworkUri, Integer.valueOf(mpCurrentBitmap));

            ContentResolver res = getContentResolver();
            InputStream in;
            try {
                in = res.openInputStream(uri);
                artwork = BitmapFactory.decodeStream(in);

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
                Log.e("catch",mpCurrentBitmap);
                artwork = BitmapFactory.decodeResource(getResources(),
                        R.drawable.image);
            }

            }catch (Exception e){e.printStackTrace();}}

            else{
            artwork = BitmapFactory.decodeResource(getResources(),
                    R.drawable.image);
        }

        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("visibilityGone","visibilityVisible");
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                myIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //Intent intentDelete = new Intent();
        Intent broadcastUpdateDeleteNotification = new Intent(Broadcast_UPDATE_ONDELETE_NOTIFICATOIN);
        //sendBroadcast(broadcastUpdateDeleteNotification);
        PendingIntent pendingIntentDelete = PendingIntent.getBroadcast(this, 0, broadcastUpdateDeleteNotification, 0);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                // Hide the timestamp
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compat view
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                .setColor(getResources().getColor(R.color.colorAccent))
                // Set the large and small icons
                .setLargeIcon(artwork)
                .setSmallIcon(R.mipmap.ic_launcher)
                // Set Notification content information
                .setContentText(activeAudio.getArtist())
                .setContentTitle(activeAudio.getTitle())
                //.setContentInfo(activeAudio.getTitle())
                // Add playback actions
                .setDeleteIntent(pendingIntentDelete)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.dark_header, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.dark_header, "next", playbackAction(2));
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());

//        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void getAlbumArtNotification(){
        mpCurrentBitmap = audioList.get(audioIndex).getAlbum_id();
    }

    @Override
    public void onTaskRemoved(Intent i){
        mediaPlayer.stop();
        stopSelf();
    }

    public static PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(mContext, MediaPlayerServices_Ex.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(mContext, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(mContext, actionNumber, playbackAction, 0);
//            case 2:
//                // Next track
//                playbackAction.setAction(ACTION_NEXT);
//                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
//            case 3:
//                // Previous track
//                playbackAction.setAction(ACTION_PREVIOUS);
//                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
     }
// else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
//            transportControls.skipToNext();
//        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
//            transportControls.skipToPrevious();
//        }
            else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }


    /**
     * Play new Audio
     */
    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {

            //Get the new media index form SharedPreferences
            audioIndex=intent.getIntExtra("currentIndex",0);
            StorageUtil storage = new StorageUtil(getApplicationContext());
            audioList = storage.loadAudio();
            mediaPlayerArrayListString = intent.getStringExtra("arraylist");
            //audioIndex = new StorageUtil(getApplicationContext()).loadAudioIndex();
            Log.e("play audio receive", String.valueOf(audioIndex));
            if (audioIndex != -1 && audioIndex < audioList.size()) {

                //index is in a valid range
                activeAudio = audioList.get(audioIndex);

            } else {
                stopSelf();
            }

            Log.e("Received","");
            //A PLAY_NEW_AUDIO action received
            //reset mediaPlayer to play the new Audio
            stopMedia();
            //updateSeekbarPosition();
           /* if(mediaPlayer.isPlaying())
            mediaPlayer.reset();*/
            initMediaPlayer();
            updateMetaData();
//            updateSeekbarPosition();
            Intent broadcastUpdateUi = new Intent(Broadcast_UPDATE_UI);
            broadcastUpdateUi.putExtra("Index",audioIndex);
            try {
                seekbar.setProgress(0);
                seekBar1.setProgress(0);
                seekbar.setMax(mediaPlayer.getDuration());
                seekBar1.setMax(mediaPlayer.getDuration());
            }catch (Exception e){
                e.printStackTrace();
            }

            durationSeek = mediaPlayer.getDuration();
            Intent broadcastUpdateSeek = new Intent(Broadcast_UPDATE_SEEK);
            broadcastUpdateSeek.putExtra("seek",positionSeek);
            broadcastUpdateSeek.putExtra("seekDuration",durationSeek);
            sendBroadcast(broadcastUpdateSeek);
            updatePosition();
            if(mediaPlayer.isPlaying()){
                playerPlaying = true;
            }else{
                playerPlaying = false;
            }
            broadcastUpdateUi.putExtra("Status",!playerPlaying);
            //broadcastUpdateUi.putExtra(audioIndex);
            broadcastUpdateUi.putExtra("arraySongString", mediaPlayerArrayListString);
            sendBroadcast(broadcastUpdateUi);

            buildNotification(PlaybackStatus.PLAYING);

        }
    };

    private void register_playNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }
    private void register_playNewAudio1() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(Broadcast_Update_Notification);
        registerReceiver(playNewAudio1, filter);
    }

    private BroadcastReceiver playNewAudio1 = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("received-","notify");
            //Get the new media index form SharedPreferences
            openplay1();
        }
    };

    public void updateSeekbarPosition(){
        seekDuration = mediaPlayer.getDuration();
        seekValue = mediaPlayer.getCurrentPosition();
        Intent broadcastUpdateSeek = new Intent(Broadcast_UPDATE_SEEKBAR);
        broadcastUpdateSeek.putExtra("seek",seekValue);
        broadcastUpdateSeek.putExtra("seekDuration",seekDuration);
        sendBroadcast(broadcastUpdateSeek);
        Log.e("in update seekbar--","");
    }

    public static void updateSeekPosition(){
        seekDuration = mediaPlayer.getDuration();
        seekValue = mediaPlayer.getCurrentPosition();
    }


    private void registerBecomingNoisyReceiver20() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(Broadcast_Update_SEEKPOSITION);
        registerReceiver(becomingNoisyReceiver20, intentFilter);
    }

    private BroadcastReceiver becomingNoisyReceiver20 = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(final Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY

            progress = intent.getIntExtra("progress", 0);
            progress = intent.getExtras().getInt("progress");
            mediaPlayer.seekTo(progress);

        }
    };

    private void registerBecomingNoisyReceiver2000() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(Broadcast_Update_FIRSTSEEK);

        registerReceiver(becomingNoisyReceiver2000, intentFilter);

    }

    private BroadcastReceiver becomingNoisyReceiver2000 = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(final Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            handler.removeCallbacks(updatePositionRunnable);
            //MediaPlayerServices_Ex.updateSeekPosition();
            try {
                seekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekBar1.setProgress(mediaPlayer.getCurrentPosition());
            }catch (Exception e){

            e.printStackTrace();
            }

            durationSeek = mediaPlayer.getDuration();
            positionSeek = mediaPlayer.getCurrentPosition();
            Intent broadcastUpdateSeek = new Intent(Broadcast_UPDATE_SEEK);
            broadcastUpdateSeek.putExtra("seek",positionSeek);
            broadcastUpdateSeek.putExtra("seekDuration",durationSeek);
            sendBroadcast(broadcastUpdateSeek);
        }
    };

    public void buildSongNotification(){
        stopMedia();
        //updateSeekbarPosition();
           /* if(mediaPlayer.isPlaying())
            mediaPlayer.reset();*/
        initMediaPlayer();
        updateMetaData();
//            updateSeekbarPosition();
        Intent broadcastUpdateUi = new Intent(Broadcast_UPDATE_UI);
        broadcastUpdateUi.putExtra("Index",audioIndex);
        try {
            seekbar.setProgress(0);
            seekBar1.setProgress(0);
            seekbar.setMax(mediaPlayer.getDuration());
            seekBar1.setMax(mediaPlayer.getDuration());
        }catch (Exception e){
            e.printStackTrace();
        }

        durationSeek = mediaPlayer.getDuration();
        Intent broadcastUpdateSeek = new Intent(Broadcast_UPDATE_SEEK);
        broadcastUpdateSeek.putExtra("seek",positionSeek);
        broadcastUpdateSeek.putExtra("seekDuration",durationSeek);
        sendBroadcast(broadcastUpdateSeek);
        updatePosition();
        if(mediaPlayer.isPlaying()){
            playerPlaying = true;
        }else{
            playerPlaying = false;
        }
        broadcastUpdateUi.putExtra("Status",!playerPlaying);
        //broadcastUpdateUi.putExtra(audioIndex);
        sendBroadcast(broadcastUpdateUi);

        buildNotification(PlaybackStatus.PLAYING);

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
            if (mediaPlayer.isPlaying()) {
                //handler.removeCallbacks(updatePositionRunnable);
                playButton.setImageResource(android.R.drawable.ic_media_play);
                playButton1.setImageResource(android.R.drawable.ic_media_play);
                mediaPlayer.pause();
            } else {
                    mediaPlayer.pause();
            }
           //mediaPlayer.stop();
        }
    };
}
