package com.music.player.emo.Activities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.music.player.emo.Audio;
import com.music.player.emo.R;

import java.io.InputStream;
import java.util.ArrayList;

import static com.music.player.emo.AudioList.audioList;

public class SplashScreen extends AppCompatActivity {
    public static int SPLASH_TIME_OUT = 3500;
    public static Bitmap artwork = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//        if(audioList==null){
//            new FeedbackAsync().execute();}
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent intent = new Intent(SplashScreen.this, Skipnow.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

//    public class FeedbackAsync extends AsyncTask<String,String,String> {
//        @Override
//        protected String doInBackground(String... params) {
//            loadAudio();
//            return null;
//        }



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
                    ContentResolver res =getContentResolver();
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

}

