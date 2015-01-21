package com.example.ripzery.projectx01.ar.detail.weapon;

import android.content.Context;

import com.example.ripzery.projectx01.R;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Rawipol on 1/11/15 AD.
 */
public class Desert extends Gun {
    public static final String type = "Desert";
    private LatLng latLng;

    public Desert(Context mContext, int bullet) {
        super(mContext, "Desert Eagle", bullet, 7, 25f, 1200);
        gun_img = R.drawable.desert_eagle;
        gun_thumb = R.drawable.desert_eagle;
        setSound(R.raw.high_powered_pistol, R.raw.reload);
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
