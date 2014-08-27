package com.example.ripzery.projectx01;

import android.app.ProgressDialog;
import android.graphics.Point;
import android.hardware.Sensor;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements SensorEventListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLngBounds Mahidol = new LatLngBounds(new LatLng(13.787486,100.316179),new LatLng(13.800875,100.326897));
    private SensorManager mSensorManager;
    private TextView mGhostStatus;
    MarkerOptions mArrow;
    Marker mMyLocation, mGhost;
    BitmapDescriptor bd;
    private ProgressDialog progress;
    private Thread ghost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        initVar();
        initListener();
    }

    private void initVar() {
        mGhostStatus = (TextView) findViewById(R.id.tv1);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        setMapCenter(Mahidol.getCenter(),15);

        progress =  new ProgressDialog(this);
        progress = ProgressDialog.show(this, "Loading", "Wait while loading map...");

        ghost = new Thread(new Runnable() {
            @Override
            public void run() {
                mGhost = mMap.addMarker(getRandomMarker(Mahidol));
                animateMarker(mGhost, mMyLocation, false);
            }
        });

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        bd = BitmapDescriptorFactory.fromResource(R.drawable.nav);
    }

    private void initListener() {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                progress.setMessage("Wait while getting your location");
            }
        });

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if(mArrow == null){
                    mArrow = new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude()))
                            .title("Current Location")
                            .icon(bd);
                    mMyLocation = mMap.addMarker(mArrow);
                    setMapCenter(mArrow.getPosition(),17);
                    progress.dismiss();
                }else{
                    mMyLocation.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                    if (mGhost == null) {
                        ghost.run();
                    }
                }
            }
        });
    }

    public void animateMarker(final Marker marker, final Marker toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 20000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.getPosition().longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.getPosition().latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                mGhostStatus.setText("Ghost Status : Coming In " + (int) getDistanceBetweenMarkersInMetres(mMyLocation, marker) + " metres !");

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    Toast exit = Toast.makeText(MapsActivity.this, "Go hell bitch !!!", Toast.LENGTH_LONG);
                    exit.show();
                    Timer a = new Timer();
                    TimerTask b = new TimerTask() {
                        @Override
                        public void run() {
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    };
                    a.schedule(b, 1500);
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    public double getDistanceBetweenMarkersInMetres(Marker mMarker1, Marker mMarker2) {
        double distance = SphericalUtil.computeDistanceBetween(mMarker1.getPosition(), mMarker2.getPosition());
//        Toast.makeText(MapsActivity.this, "The ghost are after you come within " + distance + " metres !!!", Toast.LENGTH_SHORT).show();
        return distance;
    }

    public MarkerOptions getRandomMarker(LatLngBounds bound) {
        double latMin = bound.southwest.latitude;
        double latRange = bound.northeast.latitude - latMin;
        double lonMin = bound.southwest.longitude;
        double lonRange = bound.northeast.longitude - lonMin;
        LatLng ghostLatLng = new LatLng(latMin + (Math.random() * latRange), lonMin + (Math.random() * lonRange));
        MarkerOptions ghostMarkerPosition = new MarkerOptions().position(ghostLatLng);
        return ghostMarkerPosition;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        if (mMyLocation != null) {
            mMyLocation.setAnchor((float) 0.5, (float) 0.5);
            mMyLocation.setRotation(degree);
        }

//        Log.d("Degrees : ",""+degree);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void setMapCenter(LatLng Location,int zoomLevel){
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(Location, zoomLevel);
        mMap.animateCamera(update);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

}
