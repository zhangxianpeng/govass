package com.xianpeng.govass.fragment.dyment;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class DymentInfo implements Parcelable {
    public String content;
    public ArrayList<String> photos;
    public String name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeStringList(this.photos);
        dest.writeString(this.name);
    }

    private DymentInfo() {
    }

    public DymentInfo(String content, String address, ArrayList<String> photos, boolean done) {
        this(content, address, photos, done, "李红梅");
    }

    public DymentInfo(String content, String address, ArrayList<String> photos, boolean done, String name) {
        this.content = content;
        this.photos = photos;
        this.name = name;
    }

    protected DymentInfo(Parcel in) {
        this.content = in.readString();
        this.photos = in.createStringArrayList();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<DymentInfo> CREATOR = new Parcelable.Creator<DymentInfo>() {
        @Override
        public DymentInfo createFromParcel(Parcel source) {
            return new DymentInfo(source);
        }

        @Override
        public DymentInfo[] newArray(int size) {
            return new DymentInfo[size];
        }
    };
}