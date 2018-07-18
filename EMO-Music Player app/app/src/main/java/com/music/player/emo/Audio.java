package com.music.player.emo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Student on 6/21/2017.
 */

public class Audio implements Parcelable {

    private String data;
    private String title;
    private String album;
    private String artist;
    private String album_art;
    private String album_id;

    protected Audio(Parcel in) {
        data = in.readString();
        title = in.readString();
        album = in.readString();
        artist = in.readString();
        album_art = in.readParcelable(Bitmap.class.getClassLoader());
        album_id = in.readString();
    }

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public Audio(String data, String title, String album, String artist, String album_id,String album_art) {
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.album_art = album_art;
        this.album_id = album_id;

    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumId() {
        return album_id;
    }

    public void setAlbumId(String album_id) {
        this.album_id = album_id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeString(album_art);
        dest.writeString(album_id);
    }
}
