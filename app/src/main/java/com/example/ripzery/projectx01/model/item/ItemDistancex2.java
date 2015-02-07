package com.example.ripzery.projectx01.model.item;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.interface_model.Item;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by visit on 1/20/15 AD.
 */
public class ItemDistancex2 implements Item {
    public static final String type = "Distancex2";
    public static final int id_thumb = R.drawable.speed_x2;
    public static final int effect_time = 15; // กำหนดระยะเวลาที่ไอเทมนี้ทำงาน
    LatLng latLng;
    private BitmapDescriptor icon;
    private String id;


    public ItemDistancex2(Activity mapsActivity) {
        Bitmap resize = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mapsActivity.getResources(), R.drawable.pin_speedx2),
                240,
                240,
                false);

        icon = BitmapDescriptorFactory.fromBitmap(resize);
    }

    @Override
    public String getName() {
        return type;
    }

    @Override
    public int getThumb() {
        return id_thumb;
    }

    @Override
    public String getDescription() {
        return "";
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
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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
