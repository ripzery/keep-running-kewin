package com.example.ripzery.projectx01;

import android.app.ProgressDialog;
import android.graphics.Point;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ripzery.projectx01.util.MissionData;
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
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

public class MapsActivity extends FragmentActivity {

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
    private MaterialDialog builder, builder2;
    private long previousUpdateTime, currentUpdateTime;
    private double distanceGoal = 1000.0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        ActionBar actionBar = getActionBar();
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        setUpMapIfNeeded();
        initVar();
        initListener();
    }

    private void initVar() {
        Bundle bundle = getIntent().getExtras();
        MissionData missionData = (MissionData) bundle.getParcelable("missionData");
        distanceGoal = missionData.getDistance();

        mGhost1Status = (TextView) findViewById(R.id.tv1);
        mGhost2Status = (TextView) findViewById(R.id.tv2);
        mGhost3Status = (TextView) findViewById(R.id.tv3);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(false);

        playground = new LatLngBounds(new LatLng(13.787486, 100.316179), new LatLng(13.800875, 100.326897));

        progress = new ProgressDialog(this);
        progress = ProgressDialog.show(this, "Loading", "Wait while loading map...");

        mGhostBehavior = new Ghost();
        mGhostBehavior.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ant));
        mGhostBehavior.setSpeed(1);
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
                // Do something
                setCameraPosition(playground.getCenter(), 15, 20);
                progress.setMessage("Wait while getting your location");
            }
        });

        previousUpdateTime = System.currentTimeMillis();

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                if (mPreviousLatLng == null) {
                    mPreviousLatLng = mCurrentLatLng;
                }
                currentUpdateTime = location.getTime();
                mGhost1Status.setText("v : " + location.getSpeed() + " m/s " + "gps period : " + ((currentUpdateTime - previousUpdateTime) / 1000.0) + " s");
                mGhost2Status.setText(location.toString());

                if (mMap.getMyLocation() != null && builder == null) {

                    progress.dismiss();
                    builder = new MaterialDialog(MapsActivity.this);
                    builder.setMessage("Are you ready?");
                    builder.setTitle("Mission 1 start");
                    builder.setPositiveButton("YES", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                            playground = mMap.getProjection().getVisibleRegion().latLngBounds;
                            addGhost(mGhostBehavior);
                            tGhost.run();
                        }
                    });
                    setCameraPosition(mCurrentLatLng, 18, 20);
                    builder.show();

                } else {

                    distanceGoal -= getDistanceBetweenMarkersInMetres(location, mPreviousLatLng);
                    if (distanceGoal <= 0) {
                        distanceGoal = 0;
                    }
                    mGhost3Status.setText(distanceGoal + " m");
                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    setCameraPosition(mCurrentLatLng, 18, 20, (int) SphericalUtil.computeHeading(mPreviousLatLng, mCurrentLatLng));
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
        final long start = SystemClock.uptimeMillis();

        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long initDuration = (long) (getDistanceBetweenMarkersInMetres(marker, toPosition) / (speed / 1000.0)); // duration can be change when user is moving
        final Interpolator interpolator = new LinearInterpolator();

        runnable = new Runnable() {
            long adjustDuration = initDuration;
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / adjustDuration);
                if (mCurrentLatLng.latitude != toPosition.getLatitude() || mCurrentLatLng.longitude != toPosition.getLongitude()) {
                    Log.d("Change Location to ", "" + toPosition.toString());
                    adjustDuration = adjustDuration + ((long) (getDistanceBetweenMarkersInMetres(toPosition, mCurrentLatLng) / (speed / 1000.0)));
                    toPosition.setLatitude(mCurrentLatLng.latitude);
                    toPosition.setLongitude(mCurrentLatLng.longitude);

                }
                double lng = t * toPosition.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.getLatitude() + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {

                    Toast exit = Toast.makeText(MapsActivity.this, "Try again keep it up !", Toast.LENGTH_LONG);

                    LinearLayout parentText;
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
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}

