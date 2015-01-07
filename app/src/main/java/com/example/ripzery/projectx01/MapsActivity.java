package com.example.ripzery.projectx01;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements SensorEventListener {

    float[] mGravity;
    SensorManager sensorManager;
    float[] mGeomagnetic;
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
    private Ghost mGhostBehavior; // These names is from the four ghosts in Pac-Man are Blinky, Pinky, Inky, and Clyde.
    private AlertDialog.Builder builder, builder2;
    private long previousUpdateTime, currentUpdateTime;
    private Sensor accelerometerSensor;
    private Sensor magneticFieldSensor;
    private float[] accelerometerData = new float[3];
    ;
    private float[] magneticData = new float[3];
    private int adjustHeading;
    private Marker myArrow;
    private double distanceGoal = 1000.0;
    private float[] mRotationMatrix;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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

        playground = new LatLngBounds(new LatLng(13.787486, 100.316179), new LatLng(13.800875, 100.326897));

        progress = new ProgressDialog(this);
        progress = ProgressDialog.show(this, "Loading", "Wait while loading map...");

        mGhostBehavior = new Ghost();
        mGhostBehavior.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ant));
        mGhostBehavior.setSpeed(2);
        mGhost3Status.setText(distanceGoal + " m");

        mAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listTGhost.size() < 5) {
                    addGhost(mGhostBehavior);
                    Log.d("GhostName", mGhostBehavior.getName());
                    listTGhost.get(listTGhost.size() - 1).run();
                }
                if (listTGhost.size() == 5) {
                    mAdd.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initListener() {

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                progress.setMessage("Wait while getting your location");
            }
        });

        previousUpdateTime = System.currentTimeMillis();

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (myArrow == null)
                    myArrow = mMap.addMarker(new MarkerOptions()
                            .position(mCurrentLatLng)
                            .anchor((float) 0.5, (float) 0.5)
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.dir)));
                setCameraPosition(mCurrentLatLng, 19, 20);
                if (mPreviousLatLng == null) {
                    mPreviousLatLng = mCurrentLatLng;
                }
                currentUpdateTime = location.getTime();
                mGhost1Status.setText("v : " + location.getSpeed());
                mGhost2Status.setText("Acc : " + location.getAccuracy() + " m.");

                if (mMap.getMyLocation() != null && builder == null) {
                    builder = new AlertDialog.Builder(MapsActivity.this).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            builder.dismiss();
                            playground = mMap.getProjection().getVisibleRegion().latLngBounds;
                            addGhost(mGhostBehavior);
                            tGhost.run();
                        }
                    });
                    progress.dismiss();
                    builder.setMessage("Are you ready?");
                    builder.setTitle("Mission 1 start");
                    builder.show();


                } else {

                    // อัพเดตระยะทางที่ต้องวิ่ง
                    distanceGoal -= getDistanceBetweenMarkersInMetres(location, mPreviousLatLng);
                    if (distanceGoal <= 0) {
                        distanceGoal = 0;
                    }

                    mGhost3Status.setText(distanceGoal + " m");

                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    myArrow.setPosition(mCurrentLatLng);

                    // อัพเดตทิศที่หัน
//                    if((int) SphericalUtil.computeHeading(mPreviousLatLng, mCurrentLatLng) < 0){
//                        adjustHeading = 180 + -1 * (int) SphericalUtil.computeHeading(mPreviousLatLng, mCurrentLatLng);
//                    }else{
//                        adjustHeading = (int) SphericalUtil.computeHeading(mPreviousLatLng, mCurrentLatLng);
//                    }
//                    if(Math.abs(adjustHeading - mMap.getCameraPosition().bearing) > 60){
//                        setCameraPosition(mCurrentLatLng, 19, 20, adjustHeading);


                    setCameraPosition(mCurrentLatLng, 19, 20);
//                    }
                    mPreviousLatLng = mCurrentLatLng;
                }
                previousUpdateTime = currentUpdateTime;
            }
        });
    }


    public void animateMarker(final Marker marker, final Location toPosition,
                              final boolean hideMarker, final double speed) {
        //define default user speed is 1.0 m/s
        // speed = distance/duration
        final Handler handler = new Handler();
        marker.setTitle("Hi Kewin!");

        final long start = SystemClock.uptimeMillis();
        final LatLngInterpolator.Linear spherical = new LatLngInterpolator.Linear();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long initDuration = (long) (getDistanceBetweenMarkersInMetres(marker, toPosition) / (speed / 1000.0)); // duration can be change when user is moving
        final Interpolator interpolator = new LinearInterpolator();

        runnable = new Runnable() {
            long adjustDuration = initDuration;
            PolylineOptions polylineOptions = new PolylineOptions();
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - start;
                if (mCurrentLatLng.latitude != toPosition.getLatitude() || mCurrentLatLng.longitude != toPosition.getLongitude()) {
                    if (getDistanceBetweenMarkersInMetres(marker, toPosition) > getDistanceBetweenMarkersInMetres(marker.getPosition(), mCurrentLatLng)) {
                        adjustDuration = adjustDuration - ((long) (getDistanceBetweenMarkersInMetres(toPosition, mCurrentLatLng) / (speed / 1000.0)));
                    } else {
                        adjustDuration = adjustDuration + ((long) (getDistanceBetweenMarkersInMetres(toPosition, mCurrentLatLng) / (speed / 1000.0)));
                    }
                    toPosition.setLatitude(mCurrentLatLng.latitude);
                    toPosition.setLongitude(mCurrentLatLng.longitude);
                }
                mGhost4Status.setText("time left : " + (adjustDuration - elapsed));
                float t = interpolator.getInterpolation((float) elapsed
                        / adjustDuration);
                marker.setPosition(spherical.interpolate(t, startLatLng, new LatLng(toPosition.getLatitude(), toPosition.getLongitude())));
                marker.setAlpha(t);
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
//                    mMap.addPolyline(polylineOptions);
                    Toast exit = Toast.makeText(MapsActivity.this, "Try again keep it up !", Toast.LENGTH_LONG);
                    if (!listTGhost.isEmpty() && !listGhostName.isEmpty()) {
                        listTGhost.remove(0);
                        listGhostName.remove(marker.getTitle());
                    }
                    if (!mAdd.isShown())
                        mAdd.setVisibility(View.VISIBLE);
                    exit.show();
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

    private void setCameraPosition(LatLng Location, int zoomLevel, int tilt) {
        CameraPosition camPos = new CameraPosition.Builder()
                .target(Location)
                .zoom(zoomLevel)
                .tilt(tilt)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }


    private void setCameraPosition(LatLng Location, int zoomLevel, int tilt, int bearing) {

        CameraPosition camPos = new CameraPosition.Builder()
                .target(Location)
                .zoom(zoomLevel)
                .tilt(tilt)
                .bearing(bearing)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

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

    public double getDistanceBetweenMarkersInMetres(Marker fromLocation, Location toLocation) {
        double distance = SphericalUtil.computeDistanceBetween(fromLocation.getPosition(), new LatLng(toLocation.getLatitude(), toLocation.getLongitude()));
        return distance;
    }

    public double getDistanceBetweenMarkersInMetres(Location fromLocation, LatLng toLocation) {
        double distance = SphericalUtil.computeDistanceBetween(new LatLng(fromLocation.getLatitude(), fromLocation.getLongitude()), toLocation);
        return distance;
    }

    public double getDistanceBetweenMarkersInMetres(LatLng fromLocation, LatLng toLocation) {
        double distance = SphericalUtil.computeDistanceBetween(fromLocation, toLocation);
        return distance;
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

