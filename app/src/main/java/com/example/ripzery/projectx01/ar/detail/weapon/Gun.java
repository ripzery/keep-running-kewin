package com.example.ripzery.projectx01.ar.detail.weapon;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.interface_model.Item;


/**
 * Created by Rawipol on 1/11/15 AD.
 */
public abstract class Gun implements Item {
    protected String name;
    protected int bullet = 0; // bullet in magazine
    protected int remain_bullet; //remain bullet
    protected int max_bullet; // max in magazine
    protected float damage;
    protected int gun_img;
    protected int gun_thumb;
    protected int reload_time;
    protected Context mContext;
    float actVolume, maxVolume, volume;
    AudioManager audioManager;
    private int soundShoot;
    private int soundReload;
    private SoundPool soundPool;
    private int soundEmpty;

    public Gun(Context mContext, String name, int total_bullet, int max_bullet, float damage, int reload_time) {
        this.mContext = mContext;
        this.name = name;
        this.damage = damage;
        this.max_bullet = max_bullet;
        this.reload_time = reload_time;
        insertMagazine(total_bullet);

    }

    public void insertMagazine(int total_bullet) {
        if (bullet == 0) {
            if (total_bullet - max_bullet > 0) {
                remain_bullet = total_bullet - max_bullet;
                bullet = max_bullet;
            } else {
                bullet = total_bullet;
            }
        } else {
            remain_bullet += total_bullet;
        }

    }

    public int shoot(int shooted) {
        if (bullet > 0) {
            bullet -= shooted;
        }
        return bullet;
    }

    public int getReload_time() {
        return reload_time;
    }

    public void reload() {
        int bullet_may_remain = remain_bullet - (max_bullet - bullet);
        if (bullet_may_remain < 0) {
            bullet += remain_bullet;
            remain_bullet = 0;
        } else {
            bullet = max_bullet;
            remain_bullet = bullet_may_remain;
        }
    }


    protected void setSound(int shoot_sound, int reload_sound) {
        audioManager = (AudioManager) mContext.getSystemService(mContext.AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        //Hardware buttons setting to adjust the media sound
        ((Activity) mContext).setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundShoot = soundPool.load(mContext, shoot_sound, 1);
        soundReload = soundPool.load(mContext, reload_sound, 2);
        soundEmpty = soundPool.load(mContext, R.raw.handgun_dry_fire, 3);
    }

    public String getName() {
        return name;
    }

    public int getBullet() {
        return bullet;
    }

    public int getRemain_bullet() {
        return remain_bullet;
    }

    public int get_img() {
        return gun_img;
    }

    public int get_thumb() {
        return gun_thumb;
    }

    public float getDamage() {
        return damage;
    }

    public void playShootSound() {
        soundPool.play(soundShoot, volume, volume, 1, 0, 1f);
    }

    public void playReloadSound() {
        soundPool.play(soundReload, volume, volume, 1, 0, 1f);
    }

    public void playEmptySound() {
        soundPool.play(soundEmpty, volume, volume, 1, 0, 1f);
    }
}
