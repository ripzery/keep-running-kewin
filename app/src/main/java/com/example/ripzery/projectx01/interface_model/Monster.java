package com.example.ripzery.projectx01.interface_model;

import android.graphics.Point;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by visit on 10/26/14 AD.
 */
public interface Monster {

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

    int getHp();

    int getAttackPower();

    Point getPoint();

    void setPoint(Point xy);
}
