package com.example.ripzery.projectx01.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.model.Ghost;
import com.example.ripzery.projectx01.util.DistanceCalculator;
import com.example.ripzery.projectx01.util.LatLngInterpolator;
import com.example.ripzery.projectx01.util.TypefaceSpan;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.pavlospt.CircleView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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

public class MapsActivity extends ActionBarActivity implements SensorEventListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    private static final double THRESHOLD_ROT_CAM = 20; // กำหนดระยะทางที่จะต้องวิ่งอย่างต่ำก่อนที่จะหันกล้องไปในทิศที่เราวิ่ง
    private static final double THRESHOLD_ROT_ARROW = 15; // กำหนดองศาที่หมุนโทรศัพท์อย่างน้อย ก่อนที่จะหมุนลูกศรตามทิศที่หัน (ป้องกันลูกศรสั่น)
    private final int MAX_GHOST_AT_ONCE = 5; // กำหนดจำนวนปีศาจมากที่สุดที่จะปรากฎตัวขึ้นพร้อมๆกัน
    SensorManager sensorManager;
    private int max_generate_ghost_timeout = 30; // กำหนดระยะเวลาสูงสุดที่ปีศาจจะโผล่ขึ้นมา หน่วยเป็นวินาที
    private int min_generate_ghost_timeout = 10; // กำหนดระยะเวลาต่ำสุดที่ปีศาจจะโผล่ขึ้นมา หน่วยเป็นวินาที
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
    private ArrayList<Marker> listMGhost = new ArrayList<Marker>();
    private Ghost mGhostBehavior;
    private AlertDialog.Builder builder;
    private long previousUpdateTime, currentUpdateTime;
    private Sensor accelerometerSensor;
    private Sensor magneticFieldSensor;
    private float[] accelerometerData = new float[3];
    private float[] magneticData = new float[3];
    private Marker myArrow;
    private double oldAzimuth = 0;
    private double distanceGoal = 1000.0;
    private double countDistanceToRotCam = 0;
    private boolean isLocatedSuccess = false;
    private ActionBar mActionBar;
    private CircleView mCvDistanceStatus;
    private CircleView mCvVelocityStatus;
    private Handler mHandler;
    private Runnable keepGenerate;
    private LocationClient locationClient;
    private LocationRequest locationrequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setShowHideAnimationEnabled(true);
        mActionBar.setElevation(5);
        SpannableString mStringTitle = new SpannableString("Mission X");
        mStringTitle.setSpan(new TypefaceSpan(this, "Roboto-Medium.ttf"), 0, mStringTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mActionBar.setTitle(mStringTitle);
        mActionBar.setHomeButtonEnabled(true);
//        mActionBar.setBackgroundDrawable(null);
        mActionBar.setDisplayHomeAsUpEnabled(true);

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
//        distanceGoal = 1000.0;

        mCvDistanceStatus = (CircleView) findViewById(R.id.cvTextM);
        mCvVelocityStatus = (CircleView) findViewById(R.id.cvTextV);

        mGhost1Status = (TextView) findViewById(R.id.tv1);
        mGhost2Status = (TextView) findViewById(R.id.tv2);
        mGhost3Status = (TextView) findViewById(R.id.tv3);
        mGhost4Status = (TextView) findViewById(R.id.tv4);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(true);

        //กำหนดขอบเขตการเล่น
//        playground = new LatLngBounds(new LatLng(13.787486, 100.316179), new LatLng(13.800875, 100.326897));

//        progress = new ProgressDialog(this);
//        progress = ProgressDialog.show(this, "Loading", "Wait while loading map...");

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
                if (listMGhost.size() < 5) {
                    addGhost(mGhostBehavior);
                }
                if (listMGhost.size() == 5) {
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
//                progress.setMessage("Wait while getting your location");
                int response = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapsActivity.this);
                if (response == ConnectionResult.SUCCESS) {
                    locationClient = new LocationClient(MapsActivity.this, MapsActivity.this, MapsActivity.this);
                    locationClient.connect();
                }
            }
        });

        // กำหนดค่าเริ่มต้นของ UpdateTime ไว้เป็นเวลาปัจจุบัน
        previousUpdateTime = System.currentTimeMillis();

        // กำหนด event เมื่อ Gps พบตำแหน่งของผู้ใช้
//        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(Location location) {
//                //รับค่าพิกัดปัจจุบัน
//                mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//                // ถ้ายังไม่มีการสร้าง Marker ลูกศรบอกตำแหน่งและทิศทางของผู้ใช้ให้ทำการสร้าง
//                // ถ้าเจอตำแหน่งผู้ใช้ครั้งแรกให้ตำแหน่งก่อนหน้าเท่ากับตำแหน่งปัจจุบัน
//                if (myArrow == null) {
//                    mPreviousLatLng = mCurrentLatLng;
//                    myArrow = mMap.addMarker(new MarkerOptions()
//                            .position(mCurrentLatLng)
//                            .anchor((float) 0.5, (float) 0.5)
//                            .flat(true)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.dir)));
////                    setCameraPosition(mCurrentLatLng, 19, 30);
//                }
//
//                // เลื่อนกล้องให้มาที่ตำแหน่งของผู้ใช้
////                setCameraPosition(mCurrentLatLng, 19, 20);
//                // รับค่าเวลาปัจจุบันที่พบตำแหน่งผู้ใช้ หน่วยเป็น millisec
//                currentUpdateTime = location.getTime();
//
//                // แสดงความเร็วและความแม่นยำ
//                mGhost1Status.setText("v : " + location.getSpeed());
//                mGhost2Status.setText("Acc : " + location.getAccuracy() + " m.");
//                mCvVelocityStatus.setTitleText(String.format("%.2f", location.getSpeed() * 3.6));
//
//                if (isLocatedSuccess) { // ถ้าไม่ใช่การพบตำแหน่งผู้ใช้ครั้งแรก
//
//                    // อัพเดตระยะทางที่ต้องวิ่ง
//                    double distance = DistanceCalculator.getDistanceBetweenMarkersInMetres(mCurrentLatLng, mPreviousLatLng);
//                    countDistanceToRotCam += distance;
//                    distanceGoal -= distance;
//                    if (distanceGoal <= 0) {
//                        distanceGoal = 0;
//                    }
//
//                    // แสดงระยะทางที่เหลือ
//                    mGhost3Status.setText(distanceGoal + " m");
//                    mCvDistanceStatus.setTitleText((int) distanceGoal + " m");
//
//
//                    // เลื่อนตำแหน่งของลูกษรใหม่
//                    myArrow.setPosition(mCurrentLatLng);
//
//                    // กำหนดตำแหน่งของกล้องใหม่
////                    setCameraPosition(mCurrentLatLng, 19, 20);
//
//                    //ให้ตำแหน่งก่อนหน้าเท่ากับตำแหน่งปัจจุบัน
//                    mPreviousLatLng = mCurrentLatLng;
//                }
//                previousUpdateTime = currentUpdateTime;
//            }
//        });
    }


    public void animateMarker(final Marker marker, final Location toPosition,
                              final boolean hideMarker, final double speed) {
        //define default user speed is 1.0 m/s
        // speed = distance/durationWait while getting your location

        // กำหนด Handler ในการเคลื่อนตัวปีศาจ
        handler = new Handler();
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
            // กำหนด flag ว่า marker ตัวนี้ได้แจ้งสั่นผู้ใช้เมื่อเข้าใกล้ไปแล้วหรือยัง
            boolean isVibrate = false;

            LatLng newStartLatLng = startLatLng;

            //ปรับเวลาเริ่มต้นการเคลื่อนที่ marker ถ้าผู้ใช้เลื่อนตำแหน่ง
            long adjustStartTime = start;

            // กำหนดค่าเริ่มต้นของ adjustDuration (จะต้องปรับค่านี้ถ้าผู้เล่นเคลื่อนที่) ให้เท่ากับค่าเริ่มต้น
            long adjustDuration = initDuration;
            PolylineOptions polylineOptions = new PolylineOptions();
            @Override
            public void run() {

                // ให้เวลาที่ผ่านไป = เวลาปัจจุบัน - เวลาเริ่มต้น animate
                long elapsed = SystemClock.uptimeMillis() - adjustStartTime;

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

                    // แก้ไขตำแหน่งเริ่มต้นของ marker ปีศาจเมื่อผู้ใช้เคลื่อนที่ ทำการปรับเวลา elapse เป็น 0 (เริ่มต้นใหม่) และปรับ adjustDuration ให้ลดลง
                    newStartLatLng = marker.getPosition();
                    adjustStartTime = SystemClock.uptimeMillis();
                    adjustDuration = adjustDuration - elapsed;
                    elapsed = 0;
//
//                    Log.d("adjustDuration",""+adjustDuration);
//                    Log.d("elapse",""+elapsed);
                }


                // แสดงเวลาที่ผีต้องเลื่อนไปหาผู้ใช้
                mGhost4Status.setText("time left : " + (adjustDuration - elapsed));

                // คำนวณค่า t ที่ใช้ในการเลื่อนตำแหน่งของผีโดยคำนวณจาก elapsed และ adjustDuration และปรับ tranparency ของผี
                float t = interpolator.getInterpolation((float) elapsed
                        / adjustDuration);
//                Log.d("t",""+t);
                marker.setPosition(spherical.interpolate(t, newStartLatLng, new LatLng(toPosition.getLatitude(), toPosition.getLongitude())));
//                Point ghostPoint = mMap.getProjection().toScreenLocation(marker.getPosition());
//                Point userPoint = mMap.getProjection().toScreenLocation(myArrow.getPosition());
//                marker.setSnippet("x:" + (ghostPoint.x - userPoint.x) + ", y: " + (userPoint.y - ghostPoint.y));
//                marker.showInfoWindow();
//                marker.setAlpha(t);

                if (DistanceCalculator.getDistanceBetweenMarkersInMetres(toPosition, marker.getPosition()) < 50 && !isVibrate) {
                    Vibrator v = (Vibrator) MapsActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);
                    isVibrate = true;
                }

                //ถ้าเลื่อนไม่ถึงผู้เล่นก็ให้เลื่อนต่อไปเรื่อยๆในทุกๆ 16 ms (จะได้ 60fps)
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else { // เลื่อนจนถึงผู้เล่รแล้ว
//                    mMap.addPolyline(polylineOptions);
//                    Toast exit = Toast.makeText(MapsActivity.this, "Try again keep it up !", Toast.LENGTH_LONG);
//                    exit.show();
                    if (!listMGhost.isEmpty() && !listGhostName.isEmpty()) {
                        listMGhost.remove(0);
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

    public void keepGeneratingGhost() {
        mHandler = new Handler();
        keepGenerate = new Runnable() {
            @Override
            public void run() {
                int timeout;
                if (listMGhost.size() < MAX_GHOST_AT_ONCE) {
                    int range = max_generate_ghost_timeout - min_generate_ghost_timeout + 1;
                    timeout = (int) ((Math.random() * range) + min_generate_ghost_timeout);
                    timeout = timeout * 1000; // convert to millisec
                    addGhost(mGhostBehavior);
                } else {
                    timeout = 1000;
                }
                mHandler.postDelayed(this, timeout);
            }
        };
        mHandler.postDelayed(keepGenerate, 30000);


    }

    // เลื่อนตำแหน่งของกล้อง
    private void setCameraPosition(LatLng Location, int zoomLevel, int tilt) {
        CameraPosition camPos = new CameraPosition.Builder()
                .target(Location)
                .zoom(zoomLevel)
                .tilt(tilt)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                // ถ้ายังไม่มีการสร้าง AlertDialog ให้ทำการสร้าง
                if (builder == null) {
                    builder = new AlertDialog.Builder(MapsActivity.this).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //เมื่อทำการคลิก "yes" ให้กำหนดขอบเขตการเล่นและเพิ่ม Ghost มาวิ่งไล่ผู้เล่น
                            playground = mMap.getProjection().getVisibleRegion().latLngBounds;
                            addGhost(mGhostBehavior);
                            isLocatedSuccess = true;
                            keepGeneratingGhost();
                        }
                    });

                    // ให้ ProgressDialog หายไปและแสดง AlertDialog แทน
//                    progress.dismiss();
                    builder.setMessage("Are you ready?");
                    builder.setTitle("Mission 1 start");
                    builder.show();
                }
            }

            @Override
            public void onCancel() {

            }
        });
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

        //กำหนดชื่อให้ปีศาจแต่ละตัว
        String name = "Ghost";
        for (int i = 1; i <= 5; i++) {
            if (!listGhostName.contains(name + i)) {
                ghost.setName(name + i);
                listGhostName.add(name + i);
                break;
            }
        }

        final Marker mGhost = mMap.addMarker(getRandomMarker(playground).title(ghost.getName()));
        animateMarker(mGhost, mMap.getMyLocation(), true, ghost.getSpeed());
        listMGhost.add(mGhost);
    }

    // เรียกส่งตำแหน่ง(บนหน้าจอ ไม่ใช่ latlng)ปัจจุบันของปีศาจทุกตัวเป็น arraylist<Point>
    public ArrayList<Point> getAllGhostPosition() {
        ArrayList<Point> allGhostPoint = new ArrayList<>();
        for (Marker m : listMGhost) {
            Point ghostPoint = mMap.getProjection().toScreenLocation(m.getPosition());
            Point userPoint = mMap.getProjection().toScreenLocation(myArrow.getPosition());
            allGhostPoint.add(new Point(ghostPoint.x - userPoint.x, userPoint.y - ghostPoint.y));
        }
        return allGhostPoint;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (runnable != null && keepGenerate != null) {
            handler.removeCallbacks(runnable);
            mHandler.removeCallbacks(keepGenerate);
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
        if (myArrow != null && listMGhost.size() > 0) {
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

                    if (Math.abs(azimut - oldAzimuth) >= THRESHOLD_ROT_ARROW) {
                        Log.d("azimuth", "" + azimut);
                        myArrow.setRotation(azimut);
                        oldAzimuth = azimut;
                    }

                    // ถ้าวิ่งจนได้ระยะทางเกินค่าที่กำหนดไว้ให้อัพเดตหมุนกล้องให้ตรงกับทิศที่วิ่ง
                    if (countDistanceToRotCam >= THRESHOLD_ROT_CAM) {
                        setCameraPosition(mCurrentLatLng, 19, 0, azimut);
//                        CameraPosition cameraPosition = CameraPosition.builder().target(mCurrentLatLng).bearing(azimut).build();
//                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        myArrow.setPosition(mCurrentLatLng);
                        myArrow.setRotation(azimut);
                        countDistanceToRotCam = 0;
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map. instance
        if (mMap == null) {
            mMap = (((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map)).getMap());
            // Check if we were successful in obtaining the map.
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("status", "connected");
//        setCameraPosition(new LatLng(locationClient.getLastLocation().getLatitude(), locationClient.getLastLocation().getLongitude()), 18, 60);
        mCurrentLatLng = new LatLng(locationClient.getLastLocation().getLatitude(), locationClient.getLastLocation().getLongitude());
        if (myArrow == null) {
            mPreviousLatLng = mCurrentLatLng;
            myArrow = mMap.addMarker(new MarkerOptions()
                    .position(mCurrentLatLng)
                    .anchor((float) 0.5, (float) 0.5)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.dir)));
            setCameraPosition(mCurrentLatLng, 18, 0);
        }
        locationrequest = LocationRequest.create();
        locationrequest.setInterval(100);
        locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationClient.requestLocationUpdates(locationrequest, this);
    }

    @Override
    public void onDisconnected() {
        Log.d("status", "disconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("status", "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        currentUpdateTime = location.getTime();

        // แสดงความเร็วและความแม่นยำ
        mGhost1Status.setText("v : " + location.getSpeed());
        mGhost2Status.setText("Acc : " + location.getAccuracy() + " m.");
        mCvVelocityStatus.setTitleText(String.format("%.2f", location.getSpeed() * 3.6));

        if (isLocatedSuccess) { // ถ้าไม่ใช่การพบตำแหน่งผู้ใช้ครั้งแรก
            mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            // อัพเดตระยะทางที่ต้องวิ่ง
            double distance = DistanceCalculator.getDistanceBetweenMarkersInMetres(mCurrentLatLng, mPreviousLatLng);
            countDistanceToRotCam += distance;
            distanceGoal -= distance;
            if (distanceGoal <= 0) {
                distanceGoal = 0;
            }

            // แสดงระยะทางที่เหลือ
            mGhost3Status.setText(distanceGoal + " m");
            mCvDistanceStatus.setTitleText((int) distanceGoal + " m");

            // เลื่อนตำแหน่งของลูกษรใหม่
            myArrow.setPosition(mCurrentLatLng);

            // กำหนดตำแหน่งของกล้องใหม่
            setCameraPosition(mCurrentLatLng, 18, 0);

            //ให้ตำแหน่งก่อนหน้าเท่ากับตำแหน่งปัจจุบัน
            mPreviousLatLng = mCurrentLatLng;
        }
        previousUpdateTime = currentUpdateTime;
    }
}

