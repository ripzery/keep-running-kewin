package com.example.ripzery.projectx01.model;

import com.google.android.gms.maps.model.BitmapDescriptor;

/**
 * Created by visit on 10/26/14 AD.
 */
public class Ghost {
    private String name;
    private double speed;
    private BitmapDescriptor icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }
}
