package com.example.ripzery.projectx01.ar.detail.weapon;

import android.content.Context;

import com.oakraw.testmagnetic.R;

/**
 * Created by Rawipol on 1/11/15 AD.
 */
public class Desert extends Gun {
    public Desert(Context mContext, int bullet) {
        super(mContext, "Desert Eagle", bullet, 7, 25f, 1200);
        gun_img = R.drawable.desert_full;
        gun_thumb = R.drawable.desert_thumb;
        setSound(R.raw.high_powered_pistol, R.raw.reload);
    }
}
