package com.example.ripzery.projectx01;

import android.app.ProgressDialog;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.cengalabs.flatui.views.FlatButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLngBounds Mahidol = new LatLngBounds(new LatLng(13.787486, 100.316179), new LatLng(13.800875, 100.326897));
    private TextView mBlinkyStatus;
    private FlatButton mRestart;
    private Marker mBlinky;
    private ProgressDialog progress;
    private Thread threadBlinky;
    private Ghost blinky, pinky, inky, clyde; // These names is from the four ghosts in Pac-Man are Blinky, Pinky, Inky, and Clyde.
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setUpMapIfNeeded();
        initVar();
        initListener();
    }

    private void initVar() {
        mBlinkyStatus = (TextView) findViewById(R.id.tv1);
        getmMap().setMyLocationEnabled(true);
        getmMap().getUiSettings().setMyLocationButtonEnabled(false);
        getmMap().getUiSettings().setCompassEnabled(true);
        getmMap().getUiSettings().setRotateGesturesEnabled(true);
        progress = new ProgressDialog(this);
        progress = ProgressDialog.show(this, "Loading", "Wait while loading map...");

        blinky = new Ghost();
        blinky.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.nav));
        blinky.setName("Blinky");
        blinky.setSpeed(100);

        final Typeface tf = Typeface.createFromAsset(getAssets(), "font/Roboto-Regular.ttf");
        mBlinkyStatus.setTypeface(tf);

        threadBlinky = new Thread(new Runnable() {
            @Override
            public void run() {
                setmBlinky(getmMap().addMarker(getRandomMarker(Mahidol)));
                animateMarker(getmBlinky(), getmMap().getMyLocation(), false, blinky.getSpeed());
                setCameraPosition(getmBlinky().getPosition(), 17, 20, (int) SphericalUtil.computeHeading(getmBlinky().getPosition(), new LatLng(getmMap().getMyLocation().getLatitude(), getmMap().getMyLocation().getLongitude())));
            }
        });

        mRestart = (FlatButton) findViewById(R.id.btnRestart);
        mRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getmBlinky() != null)
                    getmBlinky().remove();
                threadBlinky.run();
            }
        });
    }

    private void initListener() {
        getmMap().setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (getmBlinky() == null) {
                    Log.d("Hello", "Mahidol");
                }
            }
        });

        getmMap().setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // Do something
                setCameraPosition(Mahidol.getCenter(), 15, 20);
                progress.setMessage("Wait while getting your location");
            }
        });


        getmMap().setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                setCameraPosition(new LatLng(location.getLatitude(), location.getLongitude()), 17, 20);
                if (getmMap().getMyLocation() != null && getmBlinky() == null) {
                    progress.dismiss();
                    threadBlinky.run();
                }
            }
        });
    }

    public void animateMarker(final Marker marker, final Location toPosition,
                              final boolean hideMarker, final double speed) {
        //define default men speed is 10 KMPH
        // speed = distance/duration
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        Projection proj = getmMap().getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = (long) (getDistanceBetweenMarkersInMetres(marker, toPosition) / (speed / 3600));
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.getLatitude() + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                mBlinkyStatus.setText("threadBlinky Status : Coming In " + (int) getDistanceBetweenMarkersInMetres(marker, toPosition) + " metres !");

                if (t < 1.0) {
                    // Post again 96ms later.
                    mRestart.setEnabled(false);
                    handler.postDelayed(this, 96);
                } else {
                    Toast exit = Toast.makeText(MapsActivity.this, "Try again keep it up !", Toast.LENGTH_LONG);
                    mRestart.setEnabled(true);
                    mBlinkyStatus.setText("Game Over...");
                    exit.show();
                    Timer a = new Timer();
                    TimerTask b = new TimerTask() {
                        @Override
                        public void run() {
//                            android.os.Process.killProcess(android.os.Process.myPid());
//                            System.exit(1);
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

    public double getDistanceBetweenMarkersInMetres(Marker mMarker1, Location toLocation) {
        double distance = SphericalUtil.computeDistanceBetween(mMarker1.getPosition(), new LatLng(toLocation.getLatitude(), toLocation.getLongitude()));
//        Toast.makeText(MapsActivity.this, "The threadBlinky are after you come within " + distance + " metres !!!", Toast.LENGTH_SHORT).show();
        return distance;
    }


    public MarkerOptions getRandomMarker(LatLngBounds bound) {
        double latMin = bound.southwest.latitude;
        double latRange = bound.northeast.latitude - latMin;
        double lonMin = bound.southwest.longitude;
        double lonRange = bound.northeast.longitude - lonMin;
        LatLng ghostLatLng = new LatLng(latMin + (Math.random() * latRange), lonMin + (Math.random() * lonRange));
        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.pacman);
        MarkerOptions ghostMarkerPosition = new MarkerOptions().position(ghostLatLng).icon(bd);
        return ghostMarkerPosition;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    private void setCameraPosition(LatLng Location, int zoomLevel, int tilt) {
        CameraPosition camPos = new CameraPosition.Builder().target(Location).zoom(zoomLevel).tilt(tilt).build();
        getmMap().animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }


    private void setCameraPosition(LatLng Location, int zoomLevel, int tilt, int bearing) {

        CameraPosition camPos = new CameraPosition.Builder()
                .target(Location)
                .zoom(zoomLevel)
                .tilt(tilt)
                .bearing(bearing)
                .build();

        getmMap().animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }


//    public static final void setAppFont(ViewGroup mContainer, Typeface mFont) {
//        if (mContainer == null || mFont == null) return;
//
//        final int mCount = mContainer.getChildCount();
//
//        // Loop through all of the children.
//        for (int i = 0; i < mCount; ++i) {
//            final View mChild = mContainer.getChildAt(i);
//            if (mChild instanceof TextView) {
//                // Set the font if it is a TextView.
//                ((TextView) mChild).setTypeface(mFont);
//            } else if (mChild instanceof ViewGroup) {
//                // Recursively attempt another ViewGroup.
//                setAppFont((ViewGroup) mChild, mFont);
//            }
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        // for the system's orientation sensor registered listeners
//        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
//                SensorManager.SENSOR_DELAY_GAME);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map. instance
        if (getmMap() == null) {
            // Try to obtain the map from the SupportMapFragment.
            setmMap(((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap());

            // Check if we were successful in obtaining the map.
            if (getmMap() != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
//        mTest = mMap.addMarker(new MarkerOptions().position(Mahidol.getCenter()).title("Marker"));

        // Note : Mahidol.getCenter() return LatLng object
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public Marker getmBlinky() {
        return mBlinky;
    }

    public void setmBlinky(Marker mBlinky) {
        this.mBlinky = mBlinky;
    }
}

