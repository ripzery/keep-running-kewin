package com.example.ripzery.projectx01.model.monster;

import android.graphics.Point;

import com.example.ripzery.projectx01.interface_model.Monster;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by visit on 1/14/15 AD.
 */
public class Ant implements Monster {

    private int id;
    private String type;
    private double speed;
    private BitmapDescriptor icon;
    private double latitude;
    private double longitude;
    private int x, y;
    private int attackPower = 20;
    private int hp = 30;

    public Ant() {

    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public BitmapDescriptor getIcon() {
        return icon;
    }

    @Override
    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }

    @Override
    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public void setLatLng(LatLng latlng) {
        this.latitude = latlng.latitude;
        this.longitude = latlng.longitude;

    }

    @Override
    public int getHp() {
        return hp;
    }

    @Override
    public int getAttackPower() {
        return attackPower;
    }

    @Override
    public Point getPoint() {
        return new Point(x, y);
    }

    @Override
    public void setPoint(Point xy) {
        this.x = xy.x;
        this.y = xy.y;
    }
}
