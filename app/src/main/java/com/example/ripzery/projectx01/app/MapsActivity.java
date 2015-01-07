package com.example.ripzery.projectx01.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.model.Ghost;
import com.example.ripzery.projectx01.util.DistanceCalculator;
import com.example.ripzery.projectx01.util.LatLngInterpolator;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements SensorEventListener {

    SensorManager sensorManager;
    private TextView mGhost1Status, mGhost2Status, mGhost3Status, mGhost4Status, mGhost5Status;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLngBounds playground;
    private FloatingActionButton mAdd;
    private ProgressDialog progress;
    private Thread tGhost;
    private LatLng mCurrentLatLng, mPreviousLatLng;
    private Handler handler = new Handler();
    private Runnable runnable;
    private ArrayList<String> listGhostName = new ArrayList<String>();
    private ArrayList<Thread> listTGhost = new ArrayList<Thread>();
    private Ghost mGhostBehavior;
    private AlertDialog.Builder builder, builder2;
    private long previousUpdateTime, currentUpdateTime;
    private Sensor accelerometerSensor;
    private Sensor magneticFieldSensor;
    private float[] accelerometerData = new float[3];
    private float[] magneticData = new float[3];
    private Marker myArrow;
    private double distanceGoal = 1000.0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //setup sensor เพื่อทำให้ลูกศรหมุนตามทิศที่หัน
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        setUpMapIfNeeded();

        initVar();
        initListener();
    }

    private void initVar() {
//        Bundle bundle = getIntent().getExtras();
//        MissionData missionData = bundle.getParcelable("missionData");
//        distanceGoal = missionData.getDistance();
        distanceGoal = 1000;

        mGhost1Status = (TextView) findViewById(R.id.tv1);
        mGhost2Status = (TextView) findViewById(R.id.tv2);
        mGhost3Status = (TextView) findViewById(R.id.tv3);
        mGhost4Status = (TextView) findViewById(R.id.tv4);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);

        //กำหนดขอบเขตการเล่น
//        playground = new LatLngBounds(new LatLng(13.787486, 100.316179), new LatLng(13.800875, 100.326897));

        progress = new ProgressDialog(this);
        progress = ProgressDialog.show(this, "Loading", "Wait while loading map...");

        //กำหนด property ของ Ghost
        mGhostBehavior = new Ghost();
        mGhostBehavior.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ant));
        mGhostBehavior.setSpeed(2);
        mGhost3Status.setText(distanceGoal + " m");

        // กำหนด Listener ของปุ่ม Add เพิ่มผี
        mAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 * ถ้ามีผีเท่ากับ 5 ตัวแล้วจะซ่อนปุ่ม Add
                 * ถ้าน้อยกว่าจะเพิ่มผีลงไป
                 */
                if (listTGhost.size() < 5) {
                    addGhost(mGhostBehavior);
                    listTGhost.get(listTGhost.size() - 1).run();
                }
                if (listTGhost.size() == 5) {
                    mAdd.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initListener() {

        // เมื่อแผนที่โหลดเสร็จเรียบร้อยให้เปลี่ยนข้อความ progress จาก Wait while loading map... เป็น Wait while getting your location
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                progress.setMessage("Wait while getting your location");
            }
        });

        // กำหนดค่าเริ่มต้นของ UpdateTime ไว้เป็นเวลาปัจจุบัน
        previousUpdateTime = System.currentTimeMillis();

        // กำหนด event เมื่อ Gps พบตำแหน่งของผู้ใช้
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                //รับค่าพิกัดปัจจุบัน
                mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                // ถ้ายังไม่มีการสร้าง Marker ลูกศรบอกตำแหน่งและทิศทางของผู้ใช้ให้ทำการสร้าง
                if (myArrow == null)
                    myArrow = mMap.addMarker(new MarkerOptions()
                            .position(mCurrentLatLng)
                            .anchor((float) 0.5, (float) 0.5)
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.dir)));

                // เลื่อนกล้องให้มาที่ตำแหน่งของผู้ใช้
                setCameraPosition(mCurrentLatLng, 19, 20);

                // ถ้าเจอตำแหน่งผู้ใช้ครั้งแรกให้ตำแหน่งก่อนหน้าเท่ากับตำแหน่งปัจจุบัน
                if (mPreviousLatLng == null) {
                    mPreviousLatLng = mCurrentLatLng;
                }

                // รับค่าเวลาปัจจุบันที่พบตำแหน่งผู้ใช้ หน่วยเป็น millisec
                currentUpdateTime = location.getTime();

                // แสดงความเร็วและความแม่นยำ
                mGhost1Status.setText("v : " + location.getSpeed());
                mGhost2Status.setText("Acc : " + location.getAccuracy() + " m.");

                // ถ้ายังไม่มีการสร้าง AlertDialog ให้ทำการสร้าง
                if (builder == null) {
                    builder = new AlertDialog.Builder(MapsActivity.this).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //เมื่อทำการคลิก "yes" ให้กำหนดขอบเขตการเล่นและเพิ่ม Ghost มาวิ่งไล่ผู้เล่น
                            playground = mMap.getProjection().getVisibleRegion().latLngBounds;
                            addGhost(mGhostBehavior);
                            tGhost.run();
                        }
                    });

                    // ให้ ProgressDialog หายไปและแสดง AlertDialog แทน
                    progress.dismiss();
                    builder.setMessage("Are you ready?");
                    builder.setTitle("Mission 1 start");
                    builder.show();

                } else { // ถ้าไม่ใช่การพบตำแหน่งผู้ใช้ครั้งแรก

                    // อัพเดตระยะทางที่ต้องวิ่ง
                    distanceGoal -= DistanceCalculator.getDistanceBetweenMarkersInMetres(location, mPreviousLatLng);
                    if (distanceGoal <= 0) {
                        distanceGoal = 0;
                    }

                    // แสดงระยะทางที่เหลือ
                    mGhost3Status.setText(distanceGoal + " m");

                    // เลื่อนตำแหน่งของลูกษรใหม่
                    myArrow.setPosition(mCurrentLatLng);

                    // กำหนดตำแหน่งของกล้องใหม่
                    setCameraPosition(mCurrentLatLng, 19, 20);

                    //ให้ตำแหน่งก่อนหน้าเท่ากับตำแหน่งปัจจุบัน
                    mPreviousLatLng = mCurrentLatLng;
                }

                previousUpdateTime = currentUpdateTime;
            }
        });
    }


    public void animateMarker(final Marker marker, final Location toPosition,
                              final boolean hideMarker, final double speed) {
        //define default user speed is 1.0 m/s
        // speed = distance/durationWait while getting your location

        // กำหนด Handler ในการเคลื่อนตัวปีศาจ
        final Handler handler = new Handler();
//        marker.setTitle("Hi Kewin!");

        // กำหนดเวลาเริ่มต้น
        final long start = SystemClock.uptimeMillis();

        // ใช้การ animate แบบ Linear (v คงที่)
        final LatLngInterpolator.Linear spherical = new LatLngInterpolator.Linear();

        // แปลงตำแหน่งของ marker จาก Location เป็น onScreen
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        // กำหนดระยะเวลาที่จะ animate โดยขึ้นอยู่กับความเร็วของปีศาจนั้นๆ
        final long initDuration = (long) (DistanceCalculator.getDistanceBetweenMarkersInMetres(marker, toPosition) / (speed / 1000.0));

        // สร้าง interpolator
        final Interpolator interpolator = new LinearInterpolator();

        // สร้าง runnable สำหรับเลื่อตำแหน่งของปีศาจ
        runnable = new Runnable() {

            // กำหนดค่าเริ่มต้นของ adjustDuration (จะต้องปรับค่านี้ถ้าผู้เล่นเคลื่อนที่) ให้เท่ากับค่าเริ่มต้น
            long adjustDuration = initDuration;
            PolylineOptions polylineOptions = new PolylineOptions();
            @Override
            public void run() {

                // ให้เวลาที่ผ่านไป = เวลาปัจจุบัน - เวลาเริ่มต้น animate
                long elapsed = SystemClock.uptimeMillis() - start;

                // ถ้าตำแหน่งปัจจุบันของผู้ใช้ != ตำแหน่งที่ปีศาจจะเลื่อนไป
                if (mCurrentLatLng.latitude != toPosition.getLatitude() || mCurrentLatLng.longitude != toPosition.getLongitude()) {

                    // ถ้าระยะห่างของผีกับตำแหน่งที่จะเลื่อนไปหา มากกว่า ระยะห่างของผีกับตำแหน่งผู้ใช้ ให้ปรับ adjustDuration มีค่าน้อยลง
                    if (DistanceCalculator.getDistanceBetweenMarkersInMetres(marker, toPosition) > DistanceCalculator.getDistanceBetweenMarkersInMetres(marker.getPosition(), mCurrentLatLng)) {

                        adjustDuration = adjustDuration - ((long) (DistanceCalculator.getDistanceBetweenMarkersInMetres(toPosition, mCurrentLatLng) / (speed / 1000.0)));

                    } else { // ถ้าระยะห่างของผีกับตำแหน่งที่จะเลื่อนไปหา มากกว่า ระยะห่างของผีกับตำแหน่งผู้ใช้ ให้ปรับ adjustDuration มีค่ามากขึ้น

                        adjustDuration = adjustDuration + ((long) (DistanceCalculator.getDistanceBetweenMarkersInMetres(toPosition, mCurrentLatLng) / (speed / 1000.0)));
                    }

                    // ปรับตำแหน่งปัจจุบันของผู้ใช้ == ตำแหน่งที่ปีศาจจะเลื่อนไป
                    toPosition.setLatitude(mCurrentLatLng.latitude);
                    toPosition.setLongitude(mCurrentLatLng.longitude);
                }

                // แสดงเวลาที่ผีต้องเลื่อนไปหาผู้ใช้
                mGhost4Status.setText("time left : " + (adjustDuration - elapsed));

                // คำนวณค่า t ที่ใช้ในการเลื่อนตำแหน่งของผีโดยคำนวณจาก elapsed และ adjustDuration และปรับ tranparency ของผี
                float t = interpolator.getInterpolation((float) elapsed
                        / adjustDuration);
                marker.setPosition(spherical.interpolate(t, startLatLng, new LatLng(toPosition.getLatitude(), toPosition.getLongitude())));
                marker.setAlpha(t);

                //ถ้าเลื่อนไม่ถึงผู้เล่นก็ให้เลื่อนต่อไปเรื่อยๆในทุกๆ 16 ms (จะได้ 60fps)
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else { // เลื่อนจนถึงผู้เล่รแล้ว
//                    mMap.addPolyline(polylineOptions);
//                    Toast exit = Toast.makeText(MapsActivity.this, "Try again keep it up !", Toast.LENGTH_LONG);
//                    exit.show();
                    if (!listTGhost.isEmpty() && !listGhostName.isEmpty()) {
                        listTGhost.remove(0);
                        listGhostName.remove(marker.getTitle());
                    }
                    if (!mAdd.isShown())
                        mAdd.setVisibility(View.VISIBLE);

//                    Timer a = new Timer();
//                    TimerTask b = new TimerTask() {
//                        @Override
//                        public void run() {
//                            android.os.Process.killProcess(android.os.Process.myPid());
//                            System.exit(1);
//                        }
//                    };
//                    a.schedule(b, 1500);
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        };
        handler.post(runnable);
    }

    // เลื่อนตำแหน่งของกล้อง
    private void setCameraPosition(LatLng Location, int zoomLevel, int tilt) {
        CameraPosition camPos = new CameraPosition.Builder()
                .target(Location)
                .zoom(zoomLevel)
                .tilt(tilt)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    // เลื่อนตำแหน่งของกล้องโดยตั้งค่า bearing ด้วย
    private void setCameraPosition(LatLng Location, int zoomLevel, int tilt, int bearing) {

        CameraPosition camPos = new CameraPosition.Builder()
                .target(Location)
                .zoom(zoomLevel)
                .tilt(tilt)
                .bearing(bearing)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    // สุ่ม marker ปีศาจ
    public MarkerOptions getRandomMarker(LatLngBounds bound) {
        double latMin = bound.southwest.latitude;
        double latRange = bound.northeast.latitude - latMin;
        double lonMin = bound.southwest.longitude;
        double lonRange = bound.northeast.longitude - lonMin;

        LatLng ghostLatLng = new LatLng(latMin + (Math.random() * latRange), lonMin + (Math.random() * lonRange));
        MarkerOptions ghostMarkerPosition = new MarkerOptions().position(ghostLatLng).icon(mGhostBehavior.getIcon());
        return ghostMarkerPosition;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // เพิ่มปีศาจ
    private void addGhost(final Ghost ghost) {

        String name = "Ghost";
        for (int i = 1; i <= 5; i++) {
            if (!listGhostName.contains(name + i)) {
                ghost.setName(name + i);
                listGhostName.add(name + i);
                break;
            }
        }

        final Marker mGhost = mMap.addMarker(getRandomMarker(playground).title(ghost.getName()));
        tGhost = new Thread(new Runnable() {
            @Override
            public void run() {
                animateMarker(mGhost, mMap.getMyLocation(), true, ghost.getSpeed());
            }
        });
        tGhost.setName(ghost.getName());
        listTGhost.add(tGhost);
    }



    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map. instance
        if (mMap == null) {
            mMap = (((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map)).getMap());
            // Check if we were successful in obtaining the map.
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        sensorManager.unregisterListener(this, accelerometerSensor);
        sensorManager.unregisterListener(this, magneticFieldSensor);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (myArrow != null) {
            int sensorType = event.sensor.getType();
            if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                accelerometerData = event.values;
            } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticData = event.values;
            }

            if (accelerometerData != null && magneticData != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, accelerometerData,
                        magneticData);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    int azimut = (int) Math.round(Math.toDegrees(orientation[0]));
                    myArrow.setRotation(azimut);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

