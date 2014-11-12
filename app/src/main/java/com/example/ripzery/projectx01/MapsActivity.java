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
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
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
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLngBounds playground;
    private FloatingActionButton mAdd;
    private Marker mBlinky;
    private ProgressDialog progress;
    private Thread tGhost;
    private LatLng mCurrentLatLng;
    private Handler handler = new Handler();
    private Runnable runnable;
    private ArrayList<String> listGhostName = new ArrayList<String>();
    private ArrayList<Thread> listTGhost = new ArrayList<Thread>();
    private ArrayList<TextView> listGhostStatus = new ArrayList<TextView>();
    private Ghost mGhostBehavior; // These names is from the four ghosts in Pac-Man are Blinky, Pinky, Inky, and Clyde.
    private MaterialDialog builder, builder2;

    public MapsActivity() {

    }

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
        TextView mGhost1Status, mGhost2Status, mGhost3Status, mGhost4Status, mGhost5Status;
        mGhost1Status = (TextView) findViewById(R.id.tv1);
        mGhost2Status = (TextView) findViewById(R.id.tv2);
        mGhost3Status = (TextView) findViewById(R.id.tv3);
        mGhost4Status = (TextView) findViewById(R.id.tv4);
        mGhost5Status = (TextView) findViewById(R.id.tv5);

        listGhostStatus.add(mGhost1Status);
        listGhostStatus.add(mGhost2Status);
        listGhostStatus.add(mGhost3Status);
        listGhostStatus.add(mGhost4Status);
        listGhostStatus.add(mGhost5Status);

        getmMap().setMyLocationEnabled(true);
        getmMap().getUiSettings().setMyLocationButtonEnabled(false);
        getmMap().getUiSettings().setCompassEnabled(false);
        getmMap().getUiSettings().setZoomGesturesEnabled(false);
        getmMap().getUiSettings().setZoomControlsEnabled(false);
        getmMap().getUiSettings().setRotateGesturesEnabled(true);
        getmMap().getUiSettings().setScrollGesturesEnabled(false);

        setPlayground(new LatLngBounds(new LatLng(13.787486, 100.316179), new LatLng(13.800875, 100.326897)));

        progress = new ProgressDialog(this);
        progress = ProgressDialog.show(this, "Loading", "Wait while loading map...");

        mGhostBehavior = new Ghost();
        mGhostBehavior.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ant));
        mGhostBehavior.setSpeed(5);

        final Typeface tf = Typeface.createFromAsset(this.getAssets(), "font/RobotoCondensed-Bold.ttf");
        mGhost1Status.setTypeface(tf);
        mGhost2Status.setTypeface(tf);
        mGhost3Status.setTypeface(tf);
        mGhost4Status.setTypeface(tf);
        mGhost5Status.setTypeface(tf);

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

    private void addGhost(final Ghost ghost) {
        String name = "Ghost";
        for (int i = 1; i <= 5; i++) {
            if (!listGhostName.contains(name + i)) {
                ghost.setName(name + i);
                listGhostName.add(name + i);
                LinearLayout parentText = (LinearLayout) listGhostStatus.get(i - 1).getParent();
                parentText.setVisibility(View.VISIBLE);
                break;
            }
        }
        final Marker mGhost = getmMap().addMarker(getRandomMarker(getPlayground()).title(ghost.getName()));

        tGhost = new Thread(new Runnable() {
            @Override
            public void run() {
                animateMarker(mGhost, getmMap().getMyLocation(), true, ghost.getSpeed());
//                LatLng currentLatLng = new LatLng(getmMap().getMyLocation().getLatitude(), getmMap().getMyLocation().getLongitude());
//                PolylineOptions test = new PolylineOptions().add(currentLatLng).add(mGhost.getPosition()).width(7).color(Color.RED);
//                Polyline test2 = getmMap().addPolyline(test);
//                setCameraPosition(currentLatLng, 18, 20, (int) SphericalUtil.computeHeading(mGhost.getPosition(), currentLatLng));
            }
        });
        tGhost.setName(ghost.getName());
        listTGhost.add(tGhost);
    }

    private void initListener() {

        getmMap().setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // Do something
                setCameraPosition(getPlayground().getCenter(), 15, 20);
                progress.setMessage("Wait while getting your location");
            }
        });


        getmMap().setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (getmMap().getMyLocation() != null && getmBlinky() == null && builder == null) {
                    progress.dismiss();
                    builder = new MaterialDialog(MapsActivity.this);
                    builder.setMessage("Are you ready?");
                    builder.setTitle("Mission 1 start");
                    builder.setPositiveButton("YES", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                            builder2 = new MaterialDialog(MapsActivity.this);
                            builder2.setMessage("Good luck");
                            builder2.setTitle("Run for your life !!!");
                            builder2.setPositiveButton("BEGIN", new View.OnClickListener() {

                                @Override
                                public void onClick(View v2) {
                                    builder2.dismiss();
                                    setPlayground(getmMap().getProjection().getVisibleRegion().latLngBounds);
                                    addGhost(mGhostBehavior);
                                    tGhost.run();
                                }
                            });
                            builder2.show();
                        }
                    });
                    setCameraPosition(mCurrentLatLng, 18, 20);
                    builder.show();
                } else {
                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    setCameraPosition(mCurrentLatLng, 18, 20);
                }
            }
        });
    }

    public void animateMarker(final Marker marker, final Location toPosition,
                              final boolean hideMarker, final double speed) {
        //define default men speed is 10 m/s
        // speed = distance/duration
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        Projection proj = getmMap().getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = (long) (getDistanceBetweenMarkersInMetres(marker, toPosition) / (speed / 1000));
        final Interpolator interpolator = new LinearInterpolator();

        runnable = new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                if (mCurrentLatLng.latitude != toPosition.getLatitude() || mCurrentLatLng.longitude != toPosition.getLongitude()) {
                    Log.d("Change Location to ", "" + toPosition.toString());
                    toPosition.setLatitude(mCurrentLatLng.latitude);
                    toPosition.setLongitude(mCurrentLatLng.longitude);
                }
                double lng = t * toPosition.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.getLatitude() + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (marker.getTitle().equals("Ghost1")) {
                    listGhostStatus.get(0).setText("Ghost1 distance " + (int) getDistanceBetweenMarkersInMetres(marker, toPosition) + " metres");
                } else if (marker.getTitle().equals("Ghost2")) {
                    listGhostStatus.get(1).setText("Ghost2 distance " + (int) getDistanceBetweenMarkersInMetres(marker, toPosition) + " metres");
                } else if (marker.getTitle().equals("Ghost3")) {
                    listGhostStatus.get(2).setText("Ghost3 distance " + (int) getDistanceBetweenMarkersInMetres(marker, toPosition) + " metres");
                } else if (marker.getTitle().equals("Ghost4")) {
                    listGhostStatus.get(3).setText("Ghost4 distance " + (int) getDistanceBetweenMarkersInMetres(marker, toPosition) + " metres");
                } else {
                    listGhostStatus.get(4).setText("Ghost5 distance " + (int) getDistanceBetweenMarkersInMetres(marker, toPosition) + " metres");
                }

                if (t < 1.0) {
                    // Post again 96ms later.
                    handler.postDelayed(this, 96);
                } else {

                    Toast exit = Toast.makeText(MapsActivity.this, "Try again keep it up !", Toast.LENGTH_LONG);

                    LinearLayout parentText;
                    if (!listTGhost.isEmpty() && !listGhostName.isEmpty()) {
                        listTGhost.remove(0);
                        listGhostName.remove(marker.getTitle());
                    }
                    if (marker.getTitle().equals("Ghost1")) {
                        parentText = (LinearLayout) listGhostStatus.get(0).getParent();
                    } else if (marker.getTitle().equals("Ghost2")) {
                        parentText = (LinearLayout) listGhostStatus.get(1).getParent();
                    } else if (marker.getTitle().equals("Ghost3")) {
                        parentText = (LinearLayout) listGhostStatus.get(2).getParent();
                    } else if (marker.getTitle().equals("Ghost4")) {
                        parentText = (LinearLayout) listGhostStatus.get(3).getParent();
                    } else {
                        parentText = (LinearLayout) listGhostStatus.get(4).getParent();
                    }
                    if (!mAdd.isShown())
                        mAdd.setVisibility(View.VISIBLE);
                    parentText.setVisibility(View.GONE);
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

    public double getDistanceBetweenMarkersInMetres(Marker mMarker1, Location toLocation) {
        double distance = SphericalUtil.computeDistanceBetween(mMarker1.getPosition(), new LatLng(toLocation.getLatitude(), toLocation.getLongitude()));
        return distance;
    }


    public MarkerOptions getRandomMarker(LatLngBounds bound) {
        double latMin = bound.southwest.latitude;
        double latRange = bound.northeast.latitude - latMin;
        double lonMin = bound.southwest.longitude;
        double lonRange = bound.northeast.longitude - lonMin;
        LatLng ghostLatLng = new LatLng(latMin + (Math.random() * latRange), lonMin + (Math.random() * lonRange));
        MarkerOptions ghostMarkerPosition = new MarkerOptions().position(ghostLatLng).icon(mGhostBehavior.getIcon()).flat(true);
        return ghostMarkerPosition;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
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

    private void setCameraPosition(LatLng Location, int zoomLevel, int tilt) {
        CameraPosition camPos = new CameraPosition.Builder()
                .target(Location)
                .zoom(zoomLevel)
                .tilt(tilt)
                .build();
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

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map. instance
        if (getmMap() == null) {
            setmMap((((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map)).getMap()));
            // Check if we were successful in obtaining the map.
            if (getmMap() != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
//        mTest = mMap.addMarker(new MarkerOptions().position(playground.getCenter()).title("Marker"));

        // Note : playground.getCenter() return LatLng object
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

    public LatLngBounds getPlayground() {
        return playground;
    }

    public void setPlayground(LatLngBounds playground) {
        this.playground = playground;
    }
}

