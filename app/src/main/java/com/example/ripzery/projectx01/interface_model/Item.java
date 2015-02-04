package com.example.ripzery.projectx01.interface_model;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rawipol on 1/20/15 AD.
 */
public interface Item {

    public String getName();

    public int getThumb();

    public String getDescription();

    public LatLng getLatLng();

    public void setLatLng(LatLng latLng);

    public String getId();

    public void setId(String id);

    public String getType();

    public BitmapDescriptor getMarkerIcon();

    public int getEffectTimeOut(); // use with itemDistancex2,x3
}
