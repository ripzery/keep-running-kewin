package com.example.ripzery.projectx01.model.monster;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.interface_model.Monster;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by visit on 1/14/15 AD.
 */
public class KingKong implements Monster {

    private String id;
    private boolean isDie = false;
    private String type;
    private double speed;
    private BitmapDescriptor icon;
    private double latitude;
    private double longitude;
    private int x, y;
    private int attackPower = 5;
    private int hp = 30;
    private Activity mapsActivity;
    private boolean isRaged = false;
    private LatLng toPosition;
    private long elapsed = 0;
    private LatLng startLatLng;

    public KingKong(Activity mapsActivity) {

        this.mapsActivity = mapsActivity;

        Bitmap resize = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mapsActivity.getResources(), R.drawable.monster_ic),
                120,
                120,
                false);

        icon = BitmapDescriptorFactory.fromBitmap(resize);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
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
    public long getElapsed() {
        return elapsed;
    }

    @Override
    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    @Override
    public LatLng getToPosition() {
        return toPosition;
    }

    @Override
    public void setToPosition(LatLng toPosition) {
        this.toPosition = toPosition;
    }

    @Override
    public LatLng getStartLatLng() {
        return startLatLng;
    }

    @Override
    public void setStartLatLng(LatLng startLatLng) {
        this.startLatLng = startLatLng;
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

    public void setIcon(int drawable) {
        if (drawable == R.drawable.monster_ic) {
            isRaged = false;
        } else {
            isRaged = true;
        }
        Bitmap resize = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mapsActivity.getResources(), drawable),
                120,
                120,
                false);

        icon = BitmapDescriptorFactory.fromBitmap(resize);
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

    public void setHp(int hp) {
        this.hp = hp;
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

    public boolean isRaged() {
        return isRaged;
    }

    public boolean isDie() {
        return isDie;
    }

    public void setDie(boolean isDie) {
        this.isDie = isDie;
    }
}
