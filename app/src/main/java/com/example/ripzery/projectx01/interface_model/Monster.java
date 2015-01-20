package com.example.ripzery.projectx01.interface_model;

import android.graphics.Point;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by visit on 10/26/14 AD.
 */
public interface Monster {
//    private String type;
//    private double speed;
//    private BitmapDescriptor icon;

    int getId();

    void setId(int id);

    String getType();

    void setType(String type);

    double getSpeed();

    void setSpeed(double speed);

    BitmapDescriptor getIcon();

    void setIcon(BitmapDescriptor icon);

    LatLng getLatLng();

    void setLatLng(LatLng latlng);

    Point getPoint();

    void setPoint(Point xy);
}
