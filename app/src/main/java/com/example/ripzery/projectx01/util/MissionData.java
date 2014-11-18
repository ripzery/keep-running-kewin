package com.example.ripzery.projectx01.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by oakraw90 on 11/4/2014.
 */
public class MissionData implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MissionData createFromParcel(Parcel in) {
            return new MissionData(in);
        }

        public MissionData[] newArray(int size) {
            return new MissionData[size];
        }
    };
    private String name;
    private String description;
    private int thumbnail;
    private int distance;

    public MissionData(String name, String description, int thumbnail) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public MissionData(String name, String description, int thumbnail, int distance) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.distance = distance;
    }

    // Parcelling part
    public MissionData(Parcel in) {
        String[] data = new String[2];
        in.readStringArray(data);
        int[] intData = new int[2];
        in.readIntArray(intData);

        this.name = data[0];
        this.description = data[1];
        this.thumbnail = intData[0];
        this.distance = intData[1];
    }

    public String getName() {
        return name;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public int getDistance() {
        return distance;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.name, this.description});
        dest.writeIntArray(new int[]{this.thumbnail, this.distance});

    }

}
