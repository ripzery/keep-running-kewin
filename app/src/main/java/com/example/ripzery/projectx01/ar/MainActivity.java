package com.example.ripzery.projectx01.ar;

import android.app.Dialog;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.app.Singleton;
import com.example.ripzery.projectx01.ar.adapter.WeaponAdapter;
import com.example.ripzery.projectx01.ar.detail.Me;
import com.example.ripzery.projectx01.ar.util.CameraPreview;
import com.example.ripzery.projectx01.interface_model.Monster;
import com.example.ripzery.projectx01.model.weapon.Gun;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.widget.HListView;

public class MainActivity extends RajawaliVRActivity {

    private final float pi = 3.14159265359f;
    private final float holding_value = 0f;
    private Renderer mRenderer;
    private ImageButton leftBtn;
    private ImageButton upBtn;
    private ImageButton rightBtn;
    private ImageButton downBtn;
    private ImageView mPointer;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentX = 0f;
    private float mCurrentZ = 0f;
    private float distance = 15;
    private float getx;
    private float gety;
    private int disp_width;
    private int disp_height;

    private CameraPreview mPreview;
    private Camera camera;
    private FrameLayout contentFrame;
    private Gun selectedGun;
    private ImageView selectedGunImg;
    private TextView remainBullet;
    private TextView bullet;
    private boolean isReload = false;

    private Dialog dialog;
    private ImageView bloodFilter;
    private FrameLayout ar_interface;
    private ImageButton shootBtn;
    private ImageButton reloadBtn;
    private FrameLayout weaponSelector;
    private ArrayList<Gun> guns;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setGLBackgroundTransparent(true);
        mRenderer = new Renderer(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        contentFrame = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_main, null);

        mRenderer.setSurfaceView(mSurfaceView);
        super.setRenderer(mRenderer);
        mLayout.addView(contentFrame, 0);

        /*leftBtn = (ImageButton)findViewById(R.id.left_btn);
        leftBtn.setId(0);
        leftBtn.setOnTouchListener(this);*/

        Singleton mon = Singleton.getInstance();
        ArrayList<Monster> m = mon.getAllMonsters();
        for (int i = 0; i < m.size(); i++) {
            Log.d("oakTag", m.get(i).getPoint().toString());
        }


        //addView();
        guns = new ArrayList<Gun>();
        for (int i = 0; i < Me.items.size(); i++) {
            if (Me.items.get(i) instanceof Gun) {
                guns.add((Gun) Me.items.get(i));
            }
        }


        initView();

    }

    public void initView() {
        ar_interface = (FrameLayout) getLayoutInflater().inflate(R.layout.ar_interface, null);
        mLayout.addView(ar_interface);
        bloodFilter = (ImageView) ar_interface.findViewById(R.id.blood_filter);
        selectedGunImg = (ImageView) ar_interface.findViewById(R.id.selected_gun);
        bullet = (TextView) ar_interface.findViewById(R.id.bullet);
        remainBullet = (TextView) ar_interface.findViewById(R.id.remain_bullet);
        shootBtn = (ImageButton) ar_interface.findViewById(R.id.shoot_btn);
        reloadBtn = (ImageButton) ar_interface.findViewById(R.id.reload_btn);
        weaponSelector = (FrameLayout) ar_interface.findViewById(R.id.weapon_selector);

        bloodFilter.setImageAlpha(0);

        addView(Me.guns.get(Me.chosenGun));
    }

    public void addView(Gun selected) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.hide();
            }
        }

        this.selectedGun = selected;
        selectedGunImg.setImageResource(selectedGun.getThumb());
        updateBullet();

        shootBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isReload) {
                            if (selectedGun.getBullet() != 0) {
                                mRenderer.transitionAnimation(0, selectedGun.getDamage());
                                selectedGun.playShootSound();
                                selectedGun.shoot(1);
                            } else
                                selectedGun.playEmptySound();
                            updateBullet();
                        } else {

                        }
                        break;
                    default:
                        mRenderer.transitionAnimation(-1, 0);
                }
                return false;
            }
        });

        reloadBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selectedGun.playReloadSound();
                        isReload = true;
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                selectedGun.reload();
                                updateBullet();
                                isReload = false;
                            }
                        }, selectedGun.getReload_time());

                        break;
                    default:
                        mRenderer.transitionAnimation(-1, 0);
                }
                return false;
            }
        });

        weaponSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.weapon_selection);
                dialog.setCancelable(true);

                HListView hListView = (HListView) dialog.findViewById(R.id.hListView1);


                hListView.setAdapter(new WeaponAdapter(MainActivity.this));
                hListView.setSelector(R.drawable.round_corner_press);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;

               /* Button button1 = (Button)dialog.findViewById(R.id.button1);
                button1.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext()
                                , "Close dialog", Toast.LENGTH_SHORT);
                        dialog.cancel();
                    }
                });

                TextView textView1 = (TextView)dialog.findViewById(R.id.textView1);
                textView1.setText("Custom Dialog");
                TextView textView2 = (TextView)dialog.findViewById(R.id.textView2);
                textView2.setText("Try it yourself");*/

                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });
    }

    public void bloodShed() {
        if (bloodFilter != null) {

            int alpha = Math.round(255 - (Me.myHP * (255 / Me.myMaxHP)));
            Log.d("oakTag", "alpha " + alpha);
            bloodFilter.setImageAlpha(alpha);
            if (Me.myHP <= 0) {
                finish();
            }
        }
    }

    private void updateBullet() {
        bullet.setText("" + selectedGun.getBullet());
        remainBullet.setText("" + selectedGun.getRemain_bullet());
    }


    @Override
    public void onResume() {
        //mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        //mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        startCamera();
        super.onResume();


    }

    @Override
    public void onPause() {
        //mSensorManager.unregisterListener(this, mAccelerometer);
        //mSensorManager.unregisterListener(this, mMagnetometer);
        stopCamera();
        super.onPause();

    }


    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception e) {
        }
        mRenderer.onSurfaceDestroyed();
    }

    private void startCamera() {
        int numCams = Camera.getNumberOfCameras();

        if (numCams > 0) {
            try {
                camera = Camera.open(0);
                camera.startPreview();

                FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
                if (camera != null) {
                    Camera.Parameters params = camera.getParameters();
                    List<Camera.Size> mSupportedPreviewSizes = params.getSupportedPreviewSizes();
                    Camera.Size mPreviewSize = mSupportedPreviewSizes.get(0);
                    //requestLayout();

                    // mCamera.setDisplayOrientation(90);

                    // get Camera parameters
                    params.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                    params.setPictureSize(mPreviewSize.width, mPreviewSize.height);
                    camera.setParameters(params);

                }
                mPreview = new CameraPreview(this, camera);
                preview.addView(mPreview);

            } catch (RuntimeException ex) {
                //Toast.makeText(this, "Camera not found", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Camera not found", Toast.LENGTH_LONG).show();
        }
    }

    private void stopCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            camera.release();
            camera = null;
            mPreview = null;


        }

    }





    /*@Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);


            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);


            float azimuthInX = mOrientation[0];
            float azimuthInZ = mOrientation[2];
            float azimuthInDegressX = (float) (Math.toDegrees(azimuthInX) ) % 360;
            float azimuthInDegressZ = (float) (Math.toDegrees(azimuthInZ) ) % 360;


            float new_distanceX = 2 * pi * distance * azimuthInDegressX / 360;
            float new_distanceZ = 2 * pi * distance * azimuthInDegressZ / 360;

            if(Math.abs(mCurrentX - new_distanceX) <= holding_value ){
                new_distanceX = mCurrentX;
            }

            if(Math.abs(mCurrentZ - new_distanceZ) <= holding_value){
                new_distanceZ = mCurrentZ;
            }

            Log.d("oakVision", azimuthInDegressX + " " + azimuthInDegressZ);


            float toX = new_distanceX - mCurrentX;
            float toZ = new_distanceZ - mCurrentZ;

            //mRenderer.translateMonster(toX,toZ);

            mCurrentX = new_distanceX;
            mCurrentZ = new_distanceZ;

            mLastAccelerometerSet = false;
            mLastMagnetometerSet = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }*/
}