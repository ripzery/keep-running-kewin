package com.example.ripzery.projectx01.ar.detail;

import android.content.Context;

import com.oakraw.testmagnetic.R;

/**
 * Created by Rawipol on 1/9/15 AD.
 */
public class Ghost extends Monster {

    public Ghost(Context context, int mId) {
        super(context, mId);
        setModelResource(R.raw.ogro);
    }

    @Override
    public void playWalk() {
        //mMonster.play("crwalk", true);
    }

    @Override
    public void playDead() {
//        mMonster.play("death1", false);
//        status = DEAD;
    }

    @Override
    public void playAttack() {
        //mMonster.play("wave", true);
    }
}
