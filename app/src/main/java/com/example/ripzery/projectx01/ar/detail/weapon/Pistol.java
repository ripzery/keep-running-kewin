package com.example.ripzery.projectx01.ar.detail.weapon;

import android.content.Context;

import com.oakraw.testmagnetic.R;

/**
 * Created by Rawipol on 1/11/15 AD.
 */
public class Pistol extends Gun {
    public Pistol(Context mContext, int bullet) {
        super(mContext, "9mm", bullet, 12, 5f, 1000);
        gun_img = R.drawable._9mm_full;
        gun_thumb = R.drawable._9mm_thumb;
        setSound(R.raw.gun, R.raw.reload);
    }
}
