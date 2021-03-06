package com.example.ripzery.projectx01.ar.detail;

import android.content.Context;

import com.example.ripzery.projectx01.R;

/**
 * Created by Rawipol on 1/9/15 AD.
 */
public class KingKong extends Monster {

    public KingKong(Context context, int mId) {
        super(context, mId);
        setModelResource(R.raw.ogro);
        HP = 20;
        attack = 5;
    }

    @Override
    public void playWalk() {
        mMonster.play("crwalk", true);
    }

    @Override
    public void playDead() {
        mMonster.play("death1", false);
        status = DEAD;
    }

    @Override
    public void playAttack() {
        mMonster.play("wave", true);
    }


}
