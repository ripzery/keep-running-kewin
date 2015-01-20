package com.example.ripzery.projectx01.ar.detail.weapon;

import android.content.Context;

import com.example.ripzery.projectx01.R;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Rawipol on 1/11/15 AD.
 */
public class Pistol extends Gun {
    public static final String type = "Desert";
    private LatLng latLng;

    public Pistol(Context mContext, int bullet) {
        super(mContext, "9mm", bullet, 12, 5f, 1000);
        gun_img = R.drawable.pistol;
        gun_thumb = R.drawable.pistol;
        setSound(R.raw.gun, R.raw.reload);
    }

    @Override
    public int getThumb() {
        return gun_thumb;
    }

    @Override
    public void setThumb(int id) {
        gun_thumb = id;
    }

    @Override
    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public String getType() {
        return type;
    }
}
