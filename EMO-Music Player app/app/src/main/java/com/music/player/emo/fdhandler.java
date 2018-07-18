package com.music.player.emo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Student on 7/5/2017.
 */

public class fdhandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "favourites.db";
    public static final String TABLE = "favoriteevent";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_data = "data";
    public static final String COLUMN_album = "album";
    public static final String COLUMN_title = "title";
    private static final String COLUMN_artist = "artist";
    private static final String COLUMN_album_id = "album_id";
    private static final String COLUMN_album_art = "album_art";
    public static ArrayList<Audio> favlist;

    public fdhandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "Create TABLE " + TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_data + " TEXT, " + COLUMN_title + " TEXT, "+  COLUMN_album + " TEXT, "+COLUMN_artist + " TEXT," + COLUMN_album_art + " TEXT," + COLUMN_album_id + " TEXT " +");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void addfavouriteevent(Audio database) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE + " WHERE " + COLUMN_title + "=\"" + database.getTitle() + "\";";

//        Log.e("query 47",query);
        Cursor c = db.rawQuery(query, null);
        Log.e("cursor",c+"");
        if (c.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_data, database.getData());
            values.put(COLUMN_title, database.getTitle());
            values.put(COLUMN_album, database.getAlbum());
            values.put(COLUMN_artist, database.getArtist());
            values.put(COLUMN_album_art, database.getAlbum_art());
            values.put(COLUMN_album_id, database.getAlbum_id());
            db.insert(TABLE, null, values);


            c.close();
            db.close();

        }
        else {

            String temparray[]=null;
            c.moveToFirst();
            int l = c.getCount();
            if (l != 0) {
                temparray = new String[l];
                int x = 0;
                while (x != l) {
                    if (c.getString(c.getColumnIndex("title")) != null) {
                        temparray[x] = c.getString(c.getColumnIndex("title"));
                        c.moveToNext();
                        x++;
                    }
                }
            }
            c.close();
            // String records[]= retrievetitle();
            Log.e("size 55",temparray.length+"");
        }



    }

//    public void addToFav(FavouriteDatabase database){
//        SQLiteDatabase db = getWritableDatabase();
//    }

    public void deleteEvent(Audio database) {

        Log.e("database 100",database.getTitle());
        SQLiteDatabase db = getWritableDatabase();
//        String query = "SELECT"+database.get_id()+"FROM " + TABLE + " WHERE " + COLUMN_title + "=\"" + database.get_title() + "\";";
        db.execSQL("DELETE FROM " + TABLE + " WHERE " + COLUMN_title + "=\"" + database.getTitle() + "\";");

    }

    String[] retrievetitle() {
        String dbString[] = null;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE + " WHERE 1;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int l = c.getCount();
        if (l != 0) {
            dbString = new String[l];
            int x = 0;
            while (x != l) {
                if (c.getString(c.getColumnIndex("title")) != null) {
                    dbString[x] = c.getString(c.getColumnIndex("title"));
                    c.moveToNext();
                    x++;
                }
            }
        }
        c.close();
        return dbString;
    }

    String[] retrievedata() {
        String dbString[] = null;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE + " WHERE 1;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int l = c.getCount();
        if (l != 0) {
            dbString = new String[l];
            int x = 0;
            while (x != l) {
                if (c.getString(c.getColumnIndex("data")) != null) {
                    dbString[x] = c.getString(c.getColumnIndex("data"));
                    c.moveToNext();
                    x++;
                }
            }
        }
        c.close();
        return dbString;
    }

    String[] retrievealbum() {
        String dbString[] = null;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE + " WHERE 1;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int l = c.getCount();
        if (l != 0) {
            dbString = new String[l];
            int x = 0;
            while (x != l) {
                if (c.getString(c.getColumnIndex("album")) != null) {
                    dbString[x] = c.getString(c.getColumnIndex("album"));
                    c.moveToNext();
                    x++;
                }
            }
        }
        c.close();
        return dbString;
    }


    public ArrayList<Audio> favdata() {
        favlist = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE + " WHERE 1;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int l = c.getCount();
        if (l != 0) {
            //dbString = new String[l];
            // favlist.add(new FavouriteDatabase( c.getString(c.getColumnIndex("data")), c.getString(c.getColumnIndex("title")), c.getString(c.getColumnIndex("artist")), c.getString(c.getColumnIndex("album_id"))));

            int x = 0;
            while (x != l) {
                if (c.getString(c.getColumnIndex("data")) != null) {

                    favlist.add(new Audio( c.getString(c.getColumnIndex("data")), c.getString(c.getColumnIndex("title")), c.getString(c.getColumnIndex("album")), c.getString(c.getColumnIndex("artist")), c.getString(c.getColumnIndex("album_art")),c.getString(c.getColumnIndex("album_id"))));

                    //  dbString[x] = c.getString(c.getColumnIndex("data"));
                    c.moveToNext();
                    x++;
                }
            }
        }
        c.close();
        return favlist;
    }

    String[] retrieveartist() {
        String dbString[] = null;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE + " WHERE 1;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int l = c.getCount();
        if (l != 0) {
            dbString = new String[l];
            int x = 0;
            while (x != l) {
                if (c.getString(c.getColumnIndex("artist")) != null) {
                    dbString[x] = c.getString(c.getColumnIndex("artist"));
                    c.moveToNext();
                    x++;
                }
            }
        }
        c.close();
        return dbString;
    }

    String[] retrievealbum_id() {
        String dbString[] = null;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE + " WHERE 1;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int l = c.getCount();
        if (l != 0) {
            dbString = new String[l];
            int x = 0;
            while (x != l) {
                if (c.getString(c.getColumnIndex("album_id")) != null) {
                    dbString[x] = c.getString(c.getColumnIndex("album_id"));
                    c.moveToNext();
                    x++;
                }
            }
        }
        c.close();
        return dbString;
    }

    String[] retrievealbum_art() {
        String dbString[] = null;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE + " WHERE 1;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int l = c.getCount();
        if (l != 0) {
            dbString = new String[l];
            int x = 0;
            while (x != l) {
                dbString[x] = c.getString(c.getColumnIndex("album_art"));
                c.moveToNext();
                x++;
            }

        }
        c.close();
        return dbString;
    }

}