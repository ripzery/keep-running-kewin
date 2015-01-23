package com.example.ripzery.projectx01.interface_model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rawipol on 1/20/15 AD.
 */
public interface Item {

    public int getThumb();

    public LatLng getLatLng();

    public void setLatLng(LatLng latLng);

    public String getType();
}
