package com.example.ripzery.projectx01.interface_model;

import android.graphics.Point;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by visit on 10/26/14 AD.
 */
public interface Monster {

    String getId();

    void setId(String id);

    String getType();

    void setType(String type);

    long getElapsed();

    void setElapsed(long elapsed);

    LatLng getToPosition();

    void setToPosition(LatLng toPosition);

    LatLng getStartLatLng();

    void setStartLatLng(LatLng startLatLng);

    double getSpeed();

    void setSpeed(double speed);

    BitmapDescriptor getIcon();

    LatLng getLatLng();

    void setLatLng(LatLng latlng);

    int getHp();

    int getAttackPower();

    Point getPoint();

    void setPoint(Point xy);

    public boolean isDie();

    public void setDie(boolean isDie);
}
