package com.example.ripzery.projectx01.model.item;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.interface_model.Item;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by visit on 1/22/15 AD.
 */

// TODO: rename drawable shield
public class Shield implements Item {
    public static final String type = "Shield";
    public static final int id = R.drawable.distancex2; // TODO : need to change
    LatLng latLng;

    @Override
    public int getThumb() {
        return id;
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
