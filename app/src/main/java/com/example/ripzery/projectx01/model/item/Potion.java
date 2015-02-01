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
 * Created by visit on 1/30/15 AD.
 */
public class Potion implements Item {
    public static final String type = "Potion";
    public static final int id_thumb = R.drawable.potion;
    public static final int effect_time = 0; // กำหนดระยะเวลาที่ไอเทมนี้ทำงาน
    public static final int heal = 20;
    LatLng latLng;
    private BitmapDescriptor icon;
    private String id;

    public Potion(MapsActivity mapsActivity) {
        Bitmap resize = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mapsActivity.getResources(), R.drawable.pin_potion),
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

    public int getHeal() {
        return heal;
    }

}
