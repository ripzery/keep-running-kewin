package com.example.ripzery.projectx01.model.weapon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.ripzery.projectx01.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Rawipol on 1/11/15 AD.
 */
public class Desert extends Gun {
    public static final String type = "Desert";
    public static final int id = R.drawable.pin_dessert;
    private final BitmapDescriptor icon;
    private LatLng latLng;

    public Desert(Context mContext, int bullet) {
        super(mContext, "Desert Eagle", bullet, 7, 25f, 1200);
        gun_img = R.drawable.desert_eagle;
        gun_thumb = R.drawable.desert_thumb;
        setSound(R.raw.high_powered_pistol, R.raw.reload);

        Bitmap resize = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pin_dessert),
                240,
                240,
                false);

        icon = BitmapDescriptorFactory.fromBitmap(resize);

    }


    @Override
    public int getThumb() {
        return gun_thumb;
    }

    @Override
    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public BitmapDescriptor getMarkerIcon() {
        return icon;
    }

    @Override
    public int getEffectTimeOut() {
        return 0;
    }

}
