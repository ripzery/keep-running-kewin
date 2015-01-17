package com.example.ripzery.projectx01.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.interface_model.Monster;
import com.example.ripzery.projectx01.model.Ant;
import com.example.ripzery.projectx01.util.DistanceCalculator;
import com.example.ripzery.projectx01.util.LatLngInterpolator;
import com.example.ripzery.projectx01.util.TypefaceSpan;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.pavlospt.CircleView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import com.squareup.picasso.Picasso;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

//import android.location.LocationListener;

public class MapsActivity extends ActionBarActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GpsStatus.Listener {

    public static final int AR_REQ = 123;
    private static final long DURATION_TO_FIX_LOST_MS = 10000;
    private static final double THRESHOLD_ROT_CAM = 10; // กำหนดระยะทางที่จะต้องวิ่งอย่างต่ำก่อนที่จะหันกล้องไปในทิศที่เราวิ่ง
    private static final double THRESHOLD_ROT_ARROW = 15; // กำหนดองศาที่หมุนโทรศัพท์อย่างน้อย ก่อนที่จะหมุนลูกศรตามทิศที่หัน (ป้องกันลูกศรสั่น)
    private static final double THRESHOLD_ACC = 100; // กำหนด Accuracy ที่ยอมรับได้
    private static final int DATA_ENABLED_REQ = 1;
    private static final int LOCATION_ENABLED_REQ = 2;
    private final int MAX_GHOST_AT_ONCE = 5; // กำหนดจำนวนปีศาจมากที่สุดที่จะปรากฎตัวขึ้นพร้อมๆกัน
    SensorManager sensorManager;
    @InjectView(R.id.tv1)
    TextView mGhost1Status;
    @InjectView(R.id.tv2)
    TextView mGhost2Status;
    @InjectView(R.id.tv3)
    TextView mGhost3Status;
    @InjectView(R.id.tv4)
    TextView mGhost4Status;
    @InjectView(R.id.btnBag)
    ImageView mBag;
    @InjectView(R.id.cvTextM)
    CircleView mCvDistanceStatus;
    @InjectView(R.id.cvTextV)
    CircleView mCvVelocityStatus;
    private int max_generate_ghost_timeout = 30; // กำหนดระยะเวลาสูงสุดที่ปีศาจจะโผล่ขึ้นมา หน่วยเป็นวินาที
    private int min_generate_ghost_timeout = 10; // กำหนดระยะเวลาต่ำสุดที่ปีศาจจะโผล่ขึ้นมา หน่วยเป็นวินาที
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLngBounds playground;
    private ProgressDialog progress;
    private LatLng mCurrentLatLng, mPreviousLatLng;
    private Handler handler = new Handler();
    private Runnable runnable;
    private ArrayList<String> listGhostName = new ArrayList<String>();
    private ArrayList<Marker> listMGhost = new ArrayList<Marker>();
    private Ant mMonster;
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
    private ActionBar mActionBar;
    private Handler mHandler;
    private Runnable keepGenerate;
    private boolean isGameStart = false;
    private LocationRequest locationrequest;
    private ArrayList<Monster> allMonsters = new ArrayList<>();
    private ArrayList<Runnable> allRunnableMonster = new ArrayList<>();
    private int currentBearing = 0;
    private GoogleApiClient mGoogleApiClient;
    private int timeout = 30000;
    private LocationManager locationManager;
    private boolean gpsFix;
    private long locationTime = 0;
    private Toolbar toolbar;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        AlertDialog.Builder setting_mobile_data = new AlertDialog.Builder(this)
                .setTitle("Mobile data is not enabled yet")
                .setMessage("Go to setting again")
                .setCancelable(false)
                .setNegativeButton("Exit game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MapsActivity.this.finish();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS), DATA_ENABLED_REQ);
                    }
                });

        AlertDialog.Builder setting_location = new AlertDialog.Builder(this)
                .setTitle("Set location mode to high accuracy")
                .setMessage("Go to setting again")
                .setCancelable(false)
                .setNegativeButton("Exit game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MapsActivity.this.finish();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_ENABLED_REQ);
                    }
                });

        // if recieve from data_enabled request
        if (requestCode == DATA_ENABLED_REQ) {

            if (!isNetworkConnected()) {
                // Mobile data isn't enable yet.
                Log.d("Mobile data status : ", "disabled");
                setting_mobile_data.show();
            } else if (!isLocationEnabled()) {
                setting_location.show();
            } else {
                setUpMapIfNeeded();
                initVar();
                initListener();
            }
        } else if (requestCode == LOCATION_ENABLED_REQ) {
            if (!isLocationEnabled()) {
                //Location is not enable yet.

                Log.d("Location status : ", "disabled");
                setting_location.show();
            } else if (!isNetworkConnected()) {
                setting_mobile_data.show();
            } else {
                setUpMapIfNeeded();
                initVar();
                initListener();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
//        toolbar.setLogo(R.drawable.ic_launcher);
        toolbar.setSubtitle("Accuracy : Good!");
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

//        eventBus = EventBus.builder().logNoSubscriberMessages(false).sendNoSubscriberEvent(false).installDefaultEventBus();

        if (!isNetworkConnected()) {
            AlertDialog.Builder setting = new AlertDialog.Builder(this)
                    .setTitle("Mobile data is disabled")
                    .setMessage("Go to setting")
                    .setCancelable(false)
                    .setNegativeButton("Exit game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MapsActivity.this.finish();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS), DATA_ENABLED_REQ);
                        }
                    });
            setting.show();
        } else if (!isLocationEnabled()) {
            AlertDialog.Builder setting = new AlertDialog.Builder(this)
                    .setTitle("Set location mode to high accuracy")
                    .setMessage("Go to setting")
                    .setCancelable(false)
                    .setNegativeButton("Exit game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MapsActivity.this.finish();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_ENABLED_REQ);
                        }
                    });
            setting.show();
        }

        if (isLocationEnabled() && isNetworkConnected()) {
            setUpMapIfNeeded();
            initVar();
//            initListener();
        }

    }

    private void initVar() {
//        Bundle bundle = getIntent().getExtras();
//        MissionData missionData = bundle.getParcelable("missionData");
//        distanceGoal = missionData.getDistance();
//        distanceGoal = 1000.0;
        ButterKnife.inject(this);

        Picasso.with(this)
                .load(R.drawable.bag_flat_ic)
                .resize(240, 240)
                .centerCrop()
                .into(mBag);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(true);


//        progress = new ProgressDialog(this);
//        progress = ProgressDialog.show(this, "Loading", "Wait while loading map...");

        mGhost3Status.setText(distanceGoal + " m");
        FloatingActionButton actionE = new FloatingActionButton(getBaseContext());
        actionE.setTitle("Hide/Show Action A");
        actionE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MapsActivity.this, "ClickA", Toast.LENGTH_SHORT).show();
                passAllMonster();
            }
        });
//        mBag.addButton(actionC);
//        mBag.addButton(actionD);
//        mBag.addButton(actionE);
    }

    private void initListener() {

        // เมื่อแผนที่โหลดเสร็จเรียบร้อยให้เปลี่ยนข้อความ progress จาก Wait while loading map... เป็น Wait while getting your location
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                progress.setMessage("Waiting for GPS ...");
                int response = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapsActivity.this);
                if (response == ConnectionResult.SUCCESS) {
                    mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(MapsActivity.this)
                            .addOnConnectionFailedListener(MapsActivity.this)
                            .build();
                    mGoogleApiClient.connect();
                }
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.addGpsStatusListener(this);

        // กำหนดค่าเริ่มต้นของ UpdateTime ไว้เป็นเวลาปัจจุบัน
        previousUpdateTime = System.currentTimeMillis();

    }


    public void animateMarker(final Monster monster, final Marker marker, final Location toPosition,
                              final boolean hideMarker, final double speed) {

        // กำหนดเวลาเริ่มต้น
//        final long start = SystemClock.uptimeMillis();

        // ใช้การ animate แบบ Linear (v คงที่)
        final LatLngInterpolator.Linear spherical = new LatLngInterpolator.Linear();

        // แปลงตำแหน่งของ marker จาก Location เป็น onScreen
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        monster.setPoint(startPoint);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        // กำหนดระยะเวลาที่จะ animate โดยขึ้นอยู่กับความเร็วของปีศาจนั้นๆ
        final long initDuration = (long) (DistanceCalculator.getDistanceBetweenMarkersInMetres(marker, toPosition) / (speed / 1000.0));

        // สร้าง interpolator
        final Interpolator interpolator = new LinearInterpolator();

        // สร้าง runnable สำหรับเลื่อตำแหน่งของปีศาจ
        runnable = new Runnable() {
            // กำหนดเวลาวิ่งเริ่มต้น ซึ่งแต่ละตัวจะใช้เวลาวิ่งต่างกัน
            int run_time = 1;

            // กำหนด flag ว่า marker ตัวนี้ได้แจ้งสั่นผู้ใช้เมื่อเข้าใกล้ไปแล้วหรือยัง
            boolean isVibrate = false;

            LatLng newStartLatLng = startLatLng;

            //ปรับเวลาเริ่มต้นการเคลื่อนที่ marker ถ้าผู้ใช้เลื่อนตำแหน่ง
            long adjustStartTime = 0;

            // กำหนดค่าเริ่มต้นของ adjustDuration (จะต้องปรับค่านี้ถ้าผู้เล่นเคลื่อนที่) ให้เท่ากับค่าเริ่มต้น
            long adjustDuration = initDuration;
            PolylineOptions polylineOptions = new PolylineOptions();

            @Override
            public void run() {

                // ให้เวลาที่ผ่านไป = เวลาปัจจุบัน - เวลาเริ่มต้น animate
                long elapsed = (run_time * 16) - adjustStartTime;

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
                    adjustStartTime = run_time * 16;
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
                Point monsterPoint = mMap.getProjection().toScreenLocation(marker.getPosition());
                Point userPoint = mMap.getProjection().toScreenLocation(myArrow.getPosition());
//                Log.d("id",""+monster.getId());
                monster.setPoint(new Point(monsterPoint.x - userPoint.x, userPoint.y - monsterPoint.y));
                monster.setLatLng(marker.getPosition());

//                Singleton.getInstance().setAllMonsters(allMonsters);
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
                    run_time++;

                } else { // เลื่อนจนถึงผู้เล่นแล้ว
                    if (!listMGhost.isEmpty() && !listGhostName.isEmpty()) {
                        listMGhost.remove(0);
                        listGhostName.remove(marker.getTitle());
                        allMonsters.remove(monster);
                        updateMonsterId();
                        allRunnableMonster.remove(this);
                    }

                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        };
        /*
             สิ่งที่ต้องเก็บ adjustDuration,adjustStartTime,toPosition
         */

        allRunnableMonster.add(runnable);

        handler.post(runnable);
    }

    public void keepGeneratingGhost() {
        mHandler = new Handler();
        keepGenerate = new Runnable() {
            @Override
            public void run() {

                if (listMGhost.size() < MAX_GHOST_AT_ONCE) {
                    int range = max_generate_ghost_timeout - min_generate_ghost_timeout + 1;
                    timeout = (int) ((Math.random() * range) + min_generate_ghost_timeout);
                    timeout = timeout * 1000; // convert to millisec
                    mMonster = new Ant();
                    mMonster.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ant));
                    mMonster.setSpeed(3);
                    addMonster(mMonster);
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
                if (!isGameStart) {
                    locationrequest = LocationRequest.create();
                    locationrequest.setInterval(1000);
                    locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationrequest, MapsActivity.this);
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
    public MarkerOptions getRandomMarker(LatLngBounds bound, Monster monster) {
        double latMin = bound.southwest.latitude;
        double latRange = bound.northeast.latitude - latMin;
        double lonMin = bound.southwest.longitude;
        double lonRange = bound.northeast.longitude - lonMin;

        LatLng ghostLatLng = new LatLng(latMin + (Math.random() * latRange), lonMin + (Math.random() * lonRange));
        MarkerOptions ghostMarkerPosition = new MarkerOptions().position(ghostLatLng).icon(monster.getIcon());
        monster.setLatLng(ghostLatLng);
        return ghostMarkerPosition;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        locationClient.removeLocationUpdates(MapsActivity.this);
                        MapsActivity.this.finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setTitle("Stop playing?");
                dialog.setMessage("Your current progress won't saved");
                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // เพิ่มปีศาจ
    private void addMonster(final Monster ghost) {

        //กำหนดชื่อให้ปีศาจแต่ละตัว
        String name = "Ghost";
        for (int i = 1; i <= 5; i++) {
            if (!listGhostName.contains(name + i)) {
                ghost.setType(name + i);
                listGhostName.add(name + i);
                break;
            }
        }

        final Marker mGhost = mMap.addMarker(getRandomMarker(playground, mMonster).title(ghost.getType()));
        Location location = new Location("Start");
        location.setLatitude(mCurrentLatLng.latitude);
        location.setLongitude(mCurrentLatLng.longitude);
        location.setTime(new Date().getTime());
        allMonsters.add(mMonster);
        updateMonsterId();

        Log.d("Monster Count", "" + allMonsters.size());
        for (int i = 0; i < allMonsters.size(); i++) {
            Log.d("id", allMonsters.get(i).getId() + "");
        }
        animateMarker(allMonsters.get(allMonsters.size() - 1), mGhost, location, true, ghost.getSpeed());
        listMGhost.add(mGhost);
    }

    // เรียกส่งตำแหน่ง(บนหน้าจอ ไม่ใช่ latlng)ปัจจุบันของปีศาจทุกตัวเป็น arraylist<Point>
//    public ArrayList<Point> getAllGhostPosition() {
//        ArrayList<Point> allGhostPoint = new ArrayList<>();
//        for (Marker m : listMGhost) {
//            Point ghostPoint = mMap.getProjection().toScreenLocation(m.getPosition());
//            Point userPoint = mMap.getProjection().toScreenLocation(myArrow.getPosition());
//            allGhostPoint.add(new Point(ghostPoint.x - userPoint.x, userPoint.y - ghostPoint.y));
//        }
//        return allGhostPoint;
//    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometerSensor);
        sensorManager.unregisterListener(this, magneticFieldSensor);
        for (Runnable r : allRunnableMonster) {
            handler.removeCallbacks(r);
        }
        if (keepGenerate != null)
            mHandler.removeCallbacks(keepGenerate);
        if (locationManager != null)
            locationManager.removeGpsStatusListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_UI);
        }

        if (locationManager != null) {
            locationManager.addGpsStatusListener(this);
        }

        for (Runnable r : allRunnableMonster) {
            handler.post(r);
        }
        Log.d("Timeout", timeout + "");
        if (keepGenerate != null) {
            mHandler.postDelayed(keepGenerate, timeout);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (allRunnableMonster.size() > 0 && keepGenerate != null) {
            for (Runnable r : allRunnableMonster) {
                handler.removeCallbacks(r);
            }
            mHandler.removeCallbacks(keepGenerate);
        }
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

        if (locationManager != null) {
            locationManager.removeGpsStatusListener(this);
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
//                        Log.d("azimuth", "" + azimut);
                        myArrow.setRotation(azimut);
                        oldAzimuth = azimut;
                    }

                    // ถ้าวิ่งจนได้ระยะทางเกินค่าที่กำหนดไว้ให้อัพเดตหมุนกล้องให้ตรงกับทิศที่วิ่ง
                    if (countDistanceToRotCam >= THRESHOLD_ROT_CAM) {
                        setCameraPosition(mCurrentLatLng, 18, 0, azimut);
                        currentBearing = azimut;
                        myArrow.setPosition(mCurrentLatLng);
                        myArrow.setRotation(azimut);
                        countDistanceToRotCam = 0;
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MapsActivity.super.onBackPressed();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.setTitle("Stop playing?");
        dialog.setMessage("Your current progress won't saved");
        dialog.show();
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
        if (LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient) == null) {
            locationrequest = LocationRequest.create();
            locationrequest.setInterval(1000);
            locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            final LocationListener firstGetLocation = new LocationListener() {
                int numberOfUpdate = 0;
                @Override
                public void onLocationChanged(Location location) {
                    numberOfUpdate++;
                    if (isAccuracyAcceptable(location.getAccuracy())) {
                        mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        if (myArrow == null) {
                            mPreviousLatLng = mCurrentLatLng;
                            setCameraPosition(mCurrentLatLng, 18, 0);
                            myArrow = mMap.addMarker(new MarkerOptions()
                                    .position(mCurrentLatLng)
                                    .anchor((float) 0.5, (float) 0.5)
                                    .flat(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.dir)));

                        }
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                    } else {
                        progress.setMessage("Waiting for gps accuracy lower than " + THRESHOLD_ACC + " metres");
                        Log.d("numupdate", numberOfUpdate + "");
                        if (numberOfUpdate > 5) {
                            progress.setMessage("You may be have to go outside or fix your gps by using gps fix application");
                        }
                    }
                }
            };
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationrequest, firstGetLocation);
        } else {
            mCurrentLatLng = new LatLng(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLatitude(), LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLongitude());
            if (myArrow == null) {
                mPreviousLatLng = mCurrentLatLng;
                setCameraPosition(mCurrentLatLng, 18, 0);
                myArrow = mMap.addMarker(new MarkerOptions()
                        .position(mCurrentLatLng)
                        .anchor((float) 0.5, (float) 0.5)
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dir)));

            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("status", "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {


        // แสดงความเร็วและความแม่นยำ
        mGhost1Status.setText("v : " + location.getSpeed());
        mGhost2Status.setText("Acc : " + location.getAccuracy() + " m.");
//        Log.d("Accuracy Grade", getGrade((int) location.getAccuracy()));
        toolbar.setSubtitle("Accuracy : " + getGrade((int) location.getAccuracy()));
        mCvVelocityStatus.setTitleText(String.format("%.2f", location.getSpeed() * 3.6));

        if (progress.isShowing() && builder == null) {
            builder = new AlertDialog.Builder(MapsActivity.this).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //เมื่อทำการคลิก "yes" ให้กำหนดขอบเขตการเล่นและเพิ่ม Ghost มาวิ่งไล่ผู้เล่น
                    playground = mMap.getProjection().getVisibleRegion().latLngBounds;
                    mMonster = new Ant();
                    mMonster.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ant));
                    mMonster.setSpeed(3);
                    addMonster(mMonster);
                    keepGeneratingGhost();
                    isGameStart = true;
                }
            });

            // ให้ ProgressDialog หายไปและแสดง AlertDialog แทน
            progress.dismiss();
            builder.setMessage("Are you ready?");
            builder.setTitle("Mission 1 start");
            builder.show();
        }

        mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (isGameStart) {
            locationTime = location.getTime();
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
            setCameraPosition(mCurrentLatLng, 18, 0, currentBearing);

            //ให้ตำแหน่งก่อนหน้าเท่ากับตำแหน่งปัจจุบัน+
            mPreviousLatLng = mCurrentLatLng;
        }
    }

    public boolean isAccuracyAcceptable(double acc) {
        if (acc < THRESHOLD_ACC) {
            //acceptable
            return true;
        }
        return false;
    }

    public boolean isNetworkConnected() {

        boolean mobileDataEnabled; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            return false;
        }
        return mobileDataEnabled;
    }

    public void updateMonsterId() {
        for (int i = 0; i < allMonsters.size(); i++) {
            allMonsters.get(i).setId(i);
            Log.d("updateMonsterId1st", "" + allMonsters.get(i).getId());
        }
    }

    public boolean isLocationEnabled() {
        LocationManager manager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        } else return true;

    }

    public void passAllMonster() {
        Intent i = new Intent(this, MainActivity2.class);
        Singleton.getInstance().setAllMonsters(allMonsters);
        startActivityForResult(i, AR_REQ);

    }

    @Override
    public void onGpsStatusChanged(int changeType) {
        if (locationManager != null) {
            GpsStatus status = locationManager.getGpsStatus(null);
            switch (changeType) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    gpsFix = true;
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    // if it has been more then 10 seconds since the last update, consider the fix lost
                    gpsFix = System.currentTimeMillis() - locationTime < DURATION_TO_FIX_LOST_MS;
                    break;
                case GpsStatus.GPS_EVENT_STARTED: // GPS turned on
                    gpsFix = false;
                    break;
                case GpsStatus.GPS_EVENT_STOPPED: // GPS turned off
                    gpsFix = false;
                    break;
                default:
                    Log.w("..", "unknown GpsStatus event type. " + changeType);
                    return;

            }
        }
    }

    private String getGrade(int acc) {

        if (!isLocationEnabled()) {
            return "Disabled";
        } else if (!gpsFix) {
            return "Waiting for Fix";
        } else if (acc <= 5) {
            return "Excellent";
        } else if (acc <= 10) {
            return "Good";
        } else if (acc <= 30) {
            return "Fair";
        } else if (acc <= 100) {
            return "Bad";
        }
        return "Unusable";
    }
}

