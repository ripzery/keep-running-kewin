package com.example.ripzery.projectx01.ar.detail;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.ripzery.projectx01.ar.MainActivity;

import rajawali.Object3D;
import rajawali.animation.Animation;
import rajawali.animation.IAnimationListener;
import rajawali.animation.TranslateAnimation3D;
import rajawali.animation.mesh.VertexAnimationObject3D;
import rajawali.materials.Material;
import rajawali.materials.textures.TextureManager;
import rajawali.math.vector.Vector3;
import rajawali.parser.LoaderMD2;
import rajawali.primitives.Cube;

/**
 * Created by Rawipol on 1/9/15 AD.
 */
public abstract class Monster {
    public static int ALIVE = 1;
    public static int DEAD = 0;


    protected Context mContext;
    protected int mId;
    protected int mResource;
    protected int status = ALIVE;
    protected float HP;
    protected float attack;
    //protected int speed = 250;
    protected TranslateAnimation3D camAnim;
    protected VertexAnimationObject3D mMonster;
    protected Cube mCubeBox;

    public interface OnAttackListener{
        public void onAttack(float attack);
    }
    protected OnAttackListener mListener;
    public void setOnAttackListener(OnAttackListener listener) {
        this.mListener = listener;
    }




    public Monster(Context context, int mId) {
        this.mContext = context;
        this.mId = mId;
    }

    protected void setModelResource(int res){
        this.mResource = res;
    }

    public Object3D renderModel(TextureManager mTextureManager, double scale, double rotX, double rotY, double rotZ){
        LoaderMD2 parser = new LoaderMD2(mContext.getResources(),
                mTextureManager, mResource);
        try {
            parser.parse();

            Material material = new Material();
            material.enableLighting(false);


                mCubeBox = new Cube(5);
                mCubeBox.setMaterial(material);
                mCubeBox.setColor(Color.TRANSPARENT);
                mCubeBox.setTransparent(true);
                mCubeBox.setShowBoundingVolume(true);

                mMonster = (VertexAnimationObject3D) parser
                        .getParsedAnimationObject();
                mMonster.setScale(scale);
                mMonster.setRotX(rotX);
                mMonster.setRotY(rotY);
                mMonster.setRotZ(rotZ);
                //mOgre.setY(1);


               // mOgre.play("crwalk", true);

                mCubeBox.addChild(mMonster);


        }
        catch(Exception e){

        }

        return mCubeBox;
    }

    public abstract void playWalk();

    public abstract void playDead();

    public abstract void playAttack();

    public int getStatus() {
        return status;
    }

    public TranslateAnimation3D setTranslate(Vector3 fromPos, Vector3 toPos, int speed){
        mCubeBox.setPosition(fromPos);

        camAnim = new TranslateAnimation3D( fromPos, toPos);
               /* new Vector3(0, 0, -80),
                new Vector3(0, -3, -5));*/
        camAnim.setDurationMilliseconds(Math.round(Math.abs(fromPos.z - toPos.z )) * speed);
        camAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        //camAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
        camAnim.setTransformable3D(mCubeBox);
        //getCurrentScene().registerAnimation(camAnim);

        //camAnim.setIndex(i);

        camAnim.registerListener(new IAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                attackEnemy();
                //playAttack();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationUpdate(Animation animation, double interpolatedTime) {

            }
        });

        return camAnim;

    };

    public void damage(float power){
        HP -= power;
    }

    public float getHP(){
        return HP;
    }

    public void playTranslate(){
        if(camAnim != null)
            camAnim.play();
    }

    public void pauseTranslate(){
        if(camAnim != null)
            camAnim.pause();
    }

    public void resetTranslate(){
        if(camAnim != null)
            camAnim.reset();
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(getStatus() == ALIVE) {
                attackEnemy();
            }
        }
    };

    Handler attack_handler;

    public void attackEnemy(){
        if(Me.myHP > 0) {
            Log.d("oakTag", "attackEnemy " + getStatus());
            Log.d("oakTag", "inattackEnemy " + getStatus());

            playAttack();
            mListener.onAttack(attack);
            ((MainActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    attack_handler = new Handler();
                    attack_handler.postDelayed(runnable, 1000);
                }
            });
        }else{
            if(attack_handler != null)
                attack_handler.removeCallbacks(runnable);
        }
    }

    public void removeCallback(){
        if(attack_handler != null)
            attack_handler.removeCallbacks(runnable);
    }


    public float getAttack(){
        return attack;
    }

    public Cube getMonster() {
        return mCubeBox;
    }
}
