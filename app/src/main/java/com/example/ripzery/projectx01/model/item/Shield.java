package com.example.ripzery.projectx01.model.item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.app.MapsActivity;
import com.example.ripzery.projectx01.interface_model.Item;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by visit on 1/22/15 AD.
 */

// TODO: rename drawable shield
public class Shield implements Item {
    public static final String type = "Shield";
    public static final int id_thumb = R.drawable.distancex2; // TODO : need to change
    public static final int effect_time = 8; // กำหนดระยะเวลาที่ไอเทมนี้ทำงาน
    LatLng latLng;
    private BitmapDescriptor icon;

    public Shield(MapsActivity mapsActivity) {
        Bitmap resize = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mapsActivity.getResources(), R.drawable.pin_speedx2),
                120,
                120,
                false);

        icon = BitmapDescriptorFactory.fromBitmap(resize);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getThumb() {
        return id_thumb;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getType() {
        return type;
    }

    @Override
    public BitmapDescriptor getMarkerIcon() {
        return icon;
    }

    @Override
    public int getEffectTimeOut() {
        return effect_time;
    }
}
