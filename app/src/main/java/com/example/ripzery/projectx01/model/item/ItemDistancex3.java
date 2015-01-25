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
 * Created by visit on 1/20/15 AD.
 */
public class ItemDistancex3 implements Item {
    public static final String type = "Distancex3";
    public static final int id = R.drawable.pin_speedx3;
    public static final int id_thumb = R.drawable.speed_x3;
    LatLng latLng;
    private BitmapDescriptor icon;

    public ItemDistancex3(MapsActivity mapsActivity) {
        Bitmap resize = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mapsActivity.getResources(), id),
                240,
                240,
                false);

        icon = BitmapDescriptorFactory.fromBitmap(resize);
    }

    @Override
    public int getThumb() {
        return id_thumb;
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
}
