package com.music.player.emo.Activities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Parcelab extends ArrayList<Parcelable> implements Parcelable{

    private String firstName;
    private ArrayList audiolist;

    public Parcelab(){
        super();
    }

    public Parcelab(Parcel parcel){
        this.firstName = parcel.readString();
       // this.audiolist = parcel.readArrayList(ClassLoader.getSystemClassLoader());
    }

    @Override
    public int describeContents() {
        return 1;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(this.firstName);

    }

    public void readToParcel(Parcel parcel, int i) {

        parcel.writeString(this.firstName);

    }

    public  static  final Creator<Parcelab> CREATOR = new Creator<Parcelab>() {
        @Override
        public Parcelab createFromParcel(Parcel parcel) {
            return new Parcelab(parcel);
        }

        @Override
        public Parcelab[] newArray(int size) {
            return new Parcelab[size];
        }
    };

    public void setFirstName(String firstName){this.firstName = firstName;}

    public String getFirstName(){return firstName;}


}
