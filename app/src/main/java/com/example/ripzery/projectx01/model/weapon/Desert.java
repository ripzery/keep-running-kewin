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
    private final BitmapDescriptor icon;
    private LatLng latLng;
    private String id;

    public Desert(Context mContext, int bullet) {
        super(mContext, "Desert", bullet, 7, 25f, 1200);
        gun_img = R.drawable.desert_eagle;
        gun_thumb = R.drawable.desert_eagle;
        setSound(R.raw.high_powered_pistol, R.raw.reload);

        description = "The Desert Eagle is a large-framed gas-operated semi-automatic pistol designed by Magnum Research, Inc. (MRI) in the United States. Over the past 25 years, MRI has been responsible for the design and development of the Desert Eagle pistol. The design was refined and the actual pistols were manufactured by Israel Military Industries until 1995, when MRI shifted the manufacturing contract to Saco Defense in Saco, Maine. In 1998, MRI moved manufacturing back to IMI, which later reorganized under the name Israel Weapon Industries. Both Saco and IMI/IWI were strictly contractors: all of the intellectual property, including patents, copyrights and trademarks, are the property of Magnum Research. Since 2009, the Desert Eagle Pistol has been produced in the United States at MRI’s Pillager, MN facility. Kahr Arms acquired Magnum Research in the middle of 2010.[3] The Desert Eagle has been featured in roughly 500 motion pictures and TV films, along with several video games, considerably increasing its popularity and boosting sales.";

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
    public BitmapDescriptor getMarkerIcon() {
        return icon;
    }

    @Override
    public int getEffectTimeOut() {
        return 0;
    }

}
