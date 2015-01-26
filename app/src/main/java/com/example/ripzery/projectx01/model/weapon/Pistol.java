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
public class Pistol extends Gun {
    public static final String type = "Pistol";
    public static final int id = R.drawable.pin_pistol;
    private BitmapDescriptor icon;
    private LatLng latLng;

    public Pistol(Context mContext, int bullet) {
        super(mContext, "9mm", bullet, 12, 5f, 1000);
        gun_img = R.drawable.pistol;
        gun_thumb = R.drawable.pistol;
        setSound(R.raw.gun, R.raw.reload);

        description = "From its ambidextrous safety to the extended beavertail, the Tactical II in 9mm will be love at first shot. The full length guide rod and snag free combat-style adjustable sights are built for performance. Comes in a fully parkerized finish. The Tactical II Model gives you everything you want in a 1911 at great value, plus the backing of the Rock Island Armory full lifetime warranty. *Caliber: 9mm *Action: Single *Barrel Length 5\" *Sights: Fiber optic front, Combat adjustable rear *Grips: G10 *Safety: Ambidextrous *Capacity: 9 Rounds";

        Bitmap resize = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pin_pistol),
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
