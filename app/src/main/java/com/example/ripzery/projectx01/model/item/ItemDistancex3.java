package com.example.ripzery.projectx01.model.item;

import com.example.ripzery.projectx01.interface_model.Item;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by visit on 1/20/15 AD.
 */
public class ItemDistancex3 implements Item {
    public static final String type = "Distancex3";
    LatLng latLng;
    int id;

    @Override
    public int getThumb() {
        return id;
    }

    @Override
    public void setThumb(int id) {
        this.id = id;
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
}
