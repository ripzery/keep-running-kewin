package com.example.ripzery.projectx01.ar;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;


import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.app.Singleton;
import com.example.ripzery.projectx01.ar.detail.KingKong;
import com.example.ripzery.projectx01.ar.detail.Me;
import com.example.ripzery.projectx01.ar.detail.Monster;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rajawali.animation.Animation;
import rajawali.animation.IAnimationListener;
import rajawali.animation.TranslateAnimation3D;
import rajawali.animation.mesh.VertexAnimationObject3D;
import rajawali.bounds.IBoundingVolume;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.methods.SpecularMethod;
import rajawali.math.Quaternion;
import rajawali.math.vector.Vector3;
import rajawali.parser.LoaderOBJ;
import rajawali.parser.ParsingException;
import rajawali.primitives.Cube;
import rajawali.primitives.Sphere;

/**
 * Created by Rawipol on 1/5/15 AD.
 */
public class Renderer extends RajawaliVRRenderer implements Monster.OnAttackListener {
    private DirectionalLight mLight;
    private VertexAnimationObject3D mOgre;
    //private TranslateAnimation3D camAnim;
    private Sphere[] bullets = new Sphere[10];
    //private Sphere mBullet;
    //private Cube mCubeBox;
    private ArrayList<Monster> mMonsters = new ArrayList<Monster>();
    private int bulletIndex = 0;

    private float weaponDamage;
    private ArrayList<com.example.ripzery.projectx01.interface_model.Monster> arrMons;

    private int scaleSpeed = 5;
    private int orientation;


    public Renderer(Context context) {
        super(context);
        setFrameRate(60);


    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
    }


    @Override
    public void initScene() {
        getCurrentScene().setBackgroundColor(0);

        DirectionalLight light = new DirectionalLight(0.2f, -1f, 0f);
        light.setPower(.7f);
        getCurrentScene().addLight(light);

        light = new DirectionalLight(0.2f, 1f, 0f);
        light.setPower(1f);
        getCurrentScene().addLight(light);

        try {

            Material mat = new Material();
            mat.enableLighting(true);
            LoaderOBJ parserObj = new LoaderOBJ(mContext.getResources(),
                    mTextureManager, R.raw.arrow);
            parserObj.parse();
            arrow = parserObj.getParsedObject();
            arrow.setMaterial(mat);
            arrow.setScale(.01f);
            arrow.setColor(Color.DKGRAY);
            arrow.setPosition(new Vector3(0, 0, 0));
            //arrow.setRotY(-30);
            getCurrentScene().addChild(arrow);

            /*LoaderMD2 parser = new LoaderMD2(mContext.getResources(),
                    mTextureManager, R.raw.ogro);
            parser.parse();

            Material material = new Material();
            material.enableLighting(false);*/

            orientation = getscreenOrientation();
            Log.d("oakTag","orientation "+orientation);



            Singleton mSing = Singleton.getInstance();
            arrMons =  mSing.getAllMonsters();

            Log.d("oakTag","Monster num : "+arrMons.size());


            for (int i = 0; i< arrMons.size();i++) {

                Log.d("monsterdetail",arrMons.get(i).getPoint()+"");

                Point point = arrMons.get(i).getPoint();
                KingKong kingKong = new KingKong(mContext, i);
                kingKong.setOnAttackListener(this);
                getCurrentScene().addChild((kingKong.renderModel(mTextureManager, .1f, 0, 90, 90)));


                point = convertPoint(point);

                Vector3 fromPos =  new Vector3(point.x/scaleSpeed , 0, point.y / scaleSpeed); //*-1 ให้ y เพราะ แกน z กับ y สลับทิศทางกัน
                double toz = -5;
                if(point.y < 0){
                    toz = 5;
                }
                Vector3 toPos =  new Vector3(0, -3, toz);
                getCurrentScene().registerAnimation(kingKong.setTranslate(fromPos, toPos, 500));

                kingKong.playWalk();
                kingKong.playTranslate();

                mMonsters.add(kingKong);


            }

            final MainActivity activity = (MainActivity) mContext;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.initView();
                }
            });




        } catch (ParsingException e) {
            e.printStackTrace();
        }

        super.initScene();
    }

    private Point convertPoint(Point point) {
        if(orientation == 1){
            if(point.x >= 0 && point.y >= 0){
                //Q1
                point.x *= -1;
                point.y *= -1;
            }else if(point.x < 0 && point.y >= 0){
                //Q2
            }else if(point.x < 0 && point.y < 0){
                //Q3
                point.x *= -1;
                point.y *= -1;
            }else{
                //Q4
            }
        }else{
            point.y *= -1;
        }

        return point;
    }

    public int getscreenOrientation()
    {
        Display getOrient = ((MainActivity)mContext).getWindowManager().getDefaultDisplay();

        int orientation = getOrient.getOrientation();

        // Sometimes you may get undefined orientation Value is 0
        // simple logic solves the problem compare the screen
        // X,Y Co-ordinates and determine the Orientation in such cases
        if(orientation== Configuration.ORIENTATION_UNDEFINED){

            Configuration config = mContext.getResources().getConfiguration();
            orientation = config.orientation;

            if(orientation==Configuration.ORIENTATION_UNDEFINED){
                //if height and widht of screen are equal then
                // it is square orientation
                if(getOrient.getWidth()==getOrient.getHeight()){
                    orientation = Configuration.ORIENTATION_SQUARE;
                }else{ //if widht is less than height than it is portrait
                    if(getOrient.getWidth() < getOrient.getHeight()){
                        orientation = Configuration.ORIENTATION_PORTRAIT;
                    }else{ // if it is not any of the above it will defineitly be landscape
                        orientation = Configuration.ORIENTATION_LANDSCAPE;
                    }
                }
            }
        }
        return orientation; // return value 1 is portrait and 2 is Landscape Mode
    }


    @Override
    public void onAttack(float attack) {
        Me.myHP -= attack;
        Log.d("oakTag", "be attacked "+Me.myHP);

        Log.d("oakTag", "Game Over : you are dead!!");
        final MainActivity activity = (MainActivity) mContext;
        activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.bloodShed();
                }
            });
            //Toast.makeText(mContext, "Game Over : you are dead!!", Toast.LENGTH_LONG).show();

    }

    public void transitionAnimation(int id, float damage){

        switch(id) {
            case 0:
                weaponDamage = damage;
                //final Object3D mSphere;
                if (arrow != null) ;
                arrow.getOrientation(mCameraOrientation);

                Vector3 camPos = getCurrentCamera().getRotation();
                final Sphere mBullet = new Sphere(0.1f, 24, 24);
                Material spikeMaterial = new Material();
                spikeMaterial.enableLighting(true);
                spikeMaterial.setColor(Color.DKGRAY);
                spikeMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
                spikeMaterial.setSpecularMethod(new SpecularMethod.Phong());
                mBullet.setMaterial(spikeMaterial);
                mBullet.setColor(Color.RED);
                mBullet.setPosition(new Vector3(0, 0, 0));
                mBullet.setShowBoundingVolume(true);

                if (bulletIndex > 9) {
                    bulletIndex = 0;
                }

                if (bullets[bulletIndex] != null) {
                    getCurrentScene().removeChild(bullets[bulletIndex]);
                    bullets[bulletIndex] = null;
                }
                bullets[bulletIndex] = mBullet;

                Vector3 endOfBullet = new Vector3(0, 0, -85);

                Quaternion bulletOrientation = mCameraOrientation;
                bulletOrientation.x *= -1;
                bulletOrientation.y *= -1;
                bulletOrientation.z *= -1;
                endOfBullet.transform(bulletOrientation);


                final TranslateAnimation3D bulletAnim = new TranslateAnimation3D(
                        new Vector3(0, 0, 0),
                        endOfBullet);


                bulletAnim.setDurationMilliseconds(600);
                bulletAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                //camAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
                bulletAnim.setTransformable3D(mBullet);
                getCurrentScene().registerAnimation(bulletAnim);
                bulletAnim.setIndex(bulletIndex);

                bulletAnim.registerListener(new IAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        getCurrentScene().removeChild(bullets[bulletAnim.getIndex()]);
                        bullets[bulletAnim.getIndex()] = null;
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

                bulletAnim.play();
                //soundPool.play(soundShoot, volume, volume, 1, 0, 1f);
                //counter = counter++;


                getCurrentScene().addChild(mBullet);

                bulletIndex++;

                break;

        }

    }


    @Override
    public void onDrawFrame(GL10 glUnused) {
        try {
            super.onDrawFrame(glUnused);
        }
        catch(Exception e){

        }

        for(int i=0;i<bullets.length;i++) {

            for(int j = 0 ;j < mMonsters.size(); j++) {

                if (mMonsters.get(j) != null && bullets[i] != null) {
                    if(mMonsters.get(j).getStatus() == Monster.ALIVE) {
                        IBoundingVolume bbox = mMonsters.get(j).getMonster().getGeometry().getBoundingBox();
                        bbox.transform(mMonsters.get(j).getMonster().getModelMatrix());

                        IBoundingVolume bbox2 = bullets[i].getGeometry().getBoundingBox();
                        bbox2.transform(bullets[i].getModelMatrix());

                        if (bbox2.intersectsWith(bbox)) {
                            Log.d("oakTag", "hit");
                            mMonsters.get(j).damage(weaponDamage);
                            if(mMonsters.get(j).getHP() <= 0) {
                                mMonsters.get(j).playDead();
                                mMonsters.get(j).getMonster().setShowBoundingVolume(false);
                                mMonsters.get(j).pauseTranslate();
                                arrMons.get(j).setDie(true);

                            }

                            getCurrentScene().removeChild(bullets[i]);
                            bullets[i] = null;

                        }
                    }
                }
            }
        }

    }


    @Override
    public void onSurfaceDestroyed() {
        if(mMonsters != null) {
            for (int i = 0; i < mMonsters.size(); i++) {
                Vector3 vector = mMonsters.get(i).getMonster().getPosition();
                Log.d("oakTag_", vector.x + " " + vector.z);
                Point point = new Point((int) Math.round(vector.x * 5), (int) Math.round(vector.z * 5));
                point = convertPoint(point);
                Singleton.getAllMonsters().get(i).setPoint(point);

                mMonsters.get(i).removeCallback();
            }
        }
        mMonsters = null;
        bullets = null;
        System.gc();
        super.onSurfaceDestroyed();
    }
}
