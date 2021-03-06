package com.music.player.emo.Services;


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
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
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
import java.io.InputStream;
import java.util.ArrayList;


public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,

        AudioManager.OnAudioFocusChangeListener{

    public static final String ACTION_PLAY = "com.audioplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.audioplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.audioplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.audioplayer.ACTION_NEXT";
    public static final String ACTION_NEXT_COMPLETE = "com.audioplayer.ACTION_NEXT_COMPLETE";
    public static final String ACTION_STOP = "com.audioplayer.ACTION_STOP";
    public static final String Broadcast_NOTIFY = "com.audioplayer.notify";
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.audioplayer.PlayNewAudio";
    public static final String Broadcast_ACTION = "com.audioplayer.actions";
    public final String Broadcast_UPDATE_ONDELETE_NOTIFICATOIN = "com.example.android.hymn.Activities.PlayNewAudio2001";
    //AudioPlayer notification ID
    public static String PLAY = "play";
    public static Bitmap artwork = null;
    public static String RESUME = "resume";
    public static String PAUSE = "pause";
    public static String STOP = "stop";
    public static String PlayAction=null;
    public static String NotiFyAction=null;
    public static final int NOTIFICATION_ID = 101;
    public static MediaPlayer mediaPlayer;
    public static long maxdur, curdur;
    //Used to pause/resume MediaPlayer
    public static int resumePosition;
    //AudioFocus
    public static AudioManager audioManager;
    //List of available Audio files
    public static ArrayList<Audio> audioList;
    public static int audioIndex = -1;
    public static Audio activeAudio; //an object on the currently playing audio
    //Handle incoming phone calls
    public static boolean ongoingCall = false;
    public static PhoneStateListener phoneStateListener;
    public static TelephonyManager telephonyManager;
    public static MediaSessionCompat mediaSession;
    public static MediaControllerCompat.TransportControls transportControls;
    public static NotificationCompat.Builder notificationBuilder;
    // Binder given to clients
    public final IBinder iBinder = new LocalBinder();
    Handler handler;
    Runnable moveSeekBarThread;
    //MediaSession
    private MediaSessionManager mediaSessionManager;
    /**
     * ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs
     */
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia();
            buildNotification(PlaybackStatus.PAUSED);
        }
    };
    /**
     * Play new Audio
     */
    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("98", "playNewAudio");
            //Get the new media index form SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();
            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }

            //A PLAY_NEW_AUDIO action received
            //reset mediaPlayer to play the new Audio
            stopMedia();
            mediaPlayer.reset();
            initMediaPlayer();

            updateMetaData();

            buildNotification(PlaybackStatus.PLAYING);
        }
    };
    private BroadcastReceiver ActionPlay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (PlayAction != null && PlayAction.equalsIgnoreCase(PAUSE)) {
                PlayAction = null;
                buildNotification(PlaybackStatus.PAUSED);
            } else if (PlayAction != null && PlayAction.equalsIgnoreCase(PLAY)) {
                PlayAction = null;
                buildNotification(PlaybackStatus.PLAYING);
            } else if (PlayAction != null && PlayAction.equalsIgnoreCase(RESUME)) {
                PlayAction = null;
                buildNotification(PlaybackStatus.PLAYING);
            }
        }
    };

    public static void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    public static void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    /**
     * Service lifecycle methods
     */
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();
        // Perform one-time setup procedures

        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.
        callStateListener();
        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
        //Listen for new Audio to play -- BroadcastReceiver
        register_playNewAudio();
        register_actionreciver();
        handler = new Handler();
    }

    //The system calls this method when an activity, requests the service be started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            //Load data from SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();

            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
                Log.e("audioIndex 123", audioIndex + "");
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
            Log.e("e", e + "");
            e.printStackTrace();
            Log.e("e", "Exception=====================Exception========================Exception");
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

        unregisterReceiver(ActionPlay);
        //clear cached playlist
        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
    }

    /**
     * MediaPlayer callback methods
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Invoked when playback of a media source has completed.
//        if (MainActivity.playAll) {
            Log.e("onSeekComplete: 304", "Ok");
            transportControls.skipToNext();
            NotiFyAction = ACTION_NEXT_COMPLETE;
            Intent broadcastIntent = new Intent(Broadcast_NOTIFY);
            sendBroadcast(broadcastIntent);
//        }
//        else {
//            stopMedia();
//
//            removeNotification();
//            //stop the service
//            stopSelf();
//        }
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


        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Log.e("audio Name324", activeAudio.getTitle() + "");
//            Uri mp3 = Uri.parse("android.resource://" + getPackageName() + "/raw/" + activeAudio.getTitle());
//            mediaPlayer.setDataSource(getApplicationContext(), mp3);
            mediaPlayer.setDataSource(activeAudio.getData());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("336", "Exception============================Exception==============================Exception");
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            maxdur = mediaPlayer.getDuration();
            Log.e("342 ", mediaPlayer.getDuration() + "");
            // getCurrentProgress();
            if (mediaPlayer.isPlaying()) {
                getCurrentProgress();
            }
        }

    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
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
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();

                updateMetaData();

                buildNotification(PlaybackStatus.PLAYING);
            }

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
                Log.e("531", position + "");
                curdur = position;
            }
        });
    }

    private void updateMetaData() {


        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher); //replace with medias albumArt
        // Update the current metadata
        if(activeAudio != null)
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getTitle())
                .build());
    }

    private void buildNotification(PlaybackStatus playbackStatus) {

        /**
         * Notification actions -> playbackAction()
         *  0 -> Play
         *  1 -> Pause
         *  2 -> Next track
         *  3 -> Previous track
         */

        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher); //replace with your own image

        if (MediaStore.Audio.AlbumColumns.ALBUM_ID != null) {
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            //mpCurrentBitmap = audioList.get(audioIndex).getAlbum_id();
            try {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, Integer.valueOf(activeAudio.getAlbum_id()));

                ContentResolver res = getContentResolver();
                InputStream in;
                try {
                    in = res.openInputStream(uri);
                    artwork = BitmapFactory.decodeStream(in);

                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                    //Log.e("catch", mpCurrentBitmap);
                    artwork = BitmapFactory.decodeResource(getResources(),
                            R.drawable.image);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            artwork = BitmapFactory.decodeResource(getResources(),
                    R.drawable.image);
        }

        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("visibilityGone", "visibilityVisible");
//        Intent broadcastUpdateNotiUi= new Intent(Broadcast_UPDATE_NOTIFICATION_UI);
//        broadcastUpdateNotiUi.putExtra("index",audioIndex);
//        sendBroadcast(broadcastUpdateNotiUi);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                myIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //Intent intentDelete = new Intent();
        Intent broadcastUpdateDeleteNotification = new Intent(Broadcast_UPDATE_ONDELETE_NOTIFICATOIN);
        //sendBroadcast(broadcastUpdateDeleteNotification);
        PendingIntent pendingIntentDelete = PendingIntent.getBroadcast(this, 0, broadcastUpdateDeleteNotification, 0);

        // Create a new Notification
        PendingIntent notificationIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
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
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentIntent(notificationIntent)
                .setDeleteIntent(pendingIntentDelete)
//                .setOngoing(true)
                .setAutoCancel(false)
                .setContentText(activeAudio.getArtist())
                .setContentTitle(activeAudio.getTitle())
                .setContentInfo(activeAudio.getTitle())
                .setOngoing(false)
                // Add playback actions
                .addAction(android.R.drawable.dark_header, "previous", playbackAction(4))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.dark_header, "next", playbackAction(4));

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MediaPlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 4:
            default:
                break;
        }
        return null;
    }

    public  void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
            NotiFyAction = ACTION_PLAY;
            Intent broadcastIntent = new Intent(Broadcast_NOTIFY);
            sendBroadcast(broadcastIntent);
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
            NotiFyAction = ACTION_PAUSE;
            Intent broadcastIntent = new Intent(Broadcast_NOTIFY);
            sendBroadcast(broadcastIntent);
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
            NotiFyAction = ACTION_NEXT;
            Intent broadcastIntent = new Intent(Broadcast_NOTIFY);
            sendBroadcast(broadcastIntent);

        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
            NotiFyAction = ACTION_PREVIOUS;
            Intent broadcastIntent = new Intent(Broadcast_NOTIFY);
            sendBroadcast(broadcastIntent);

        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
            NotiFyAction = ACTION_STOP;
            Intent broadcastIntent = new Intent(Broadcast_NOTIFY);
            sendBroadcast(broadcastIntent);
        }
    }

    private void register_playNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    private void register_actionreciver() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(Broadcast_ACTION);
        registerReceiver(ActionPlay, filter);
    }

    //To get current progress of playing audio
    public void getCurrentProgress() {

        int mediaMax_new = mediaPlayer.getDuration();
        maxdur = mediaMax_new;


    }

    /**
     * Service Binder
     */
    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MediaPlayerService.this;
        }
    }

}
