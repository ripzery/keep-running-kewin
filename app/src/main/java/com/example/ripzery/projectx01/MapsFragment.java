package com.example.ripzery.projectx01;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.cengalabs.flatui.views.FlatButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.Timer;
import java.util.TimerTask;

import me.drakeet.materialdialog.MaterialDialog;

public class MapsFragment extends Fragment {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLngBounds playground = new LatLngBounds(new LatLng(13.787486, 100.316179), new LatLng(13.800875, 100.326897));
    private TextView mBlinkyStatus;
    private FlatButton mRestart;
    private Marker mBlinky;
    private View mRootView;
    private ProgressDialog progress;
    private Thread threadBlinky;
    private LatLng currentPosition;
    private Ghost blinky, pinky, inky, clyde; // These names is from the four ghosts in Pac-Man are Blinky, Pinky, Inky, and Clyde.
    private MaterialDialog builder;

    public MapsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
//        getActivity().setContentView(R.layout.fragment_maps);
        mRootView = view;
        setUpMapIfNeeded();
        initVar();
        initListener();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Fragment f = getFragmentManager().findFragmentById(R.id.map);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
        mMap.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initVar() {
        mBlinkyStatus = (TextView) mRootView.findViewById(R.id.tv1);
        getmMap().setMyLocationEnabled(true);
        getmMap().getUiSettings().setMyLocationButtonEnabled(false);
        getmMap().getUiSettings().setCompassEnabled(true);
        getmMap().getUiSettings().setZoomGesturesEnabled(false);
        getmMap().getUiSettings().setRotateGesturesEnabled(true);
        progress = new ProgressDialog(getActivity());
        progress = ProgressDialog.show(getActivity(), "Loading", "Wait while loading map...");

        blinky = new Ghost();
        blinky.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ant));
        blinky.setName("Blinky");
        blinky.setSpeed(100);

        final Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
        mBlinkyStatus.setTypeface(tf);

        threadBlinky = new Thread(new Runnable() {
            @Override
            public void run() {
                setmBlinky(getmMap().addMarker(getRandomMarker(getPlayground())));
                animateMarker(getmBlinky(), getmMap().getMyLocation(), false, blinky.getSpeed());
                LatLng currentLatLng = new LatLng(getmMap().getMyLocation().getLatitude(), getmMap().getMyLocation().getLongitude());
                PolylineOptions test = new PolylineOptions().add(currentLatLng).add(mBlinky.getPosition()).width(7).color(Color.RED);
                Polyline test2 = getmMap().addPolyline(test);
                setCameraPosition(currentLatLng, 18, 20, (int) SphericalUtil.computeHeading(getmBlinky().getPosition(), currentLatLng));
            }
        });

        mRestart = (FlatButton) mRootView.findViewById(R.id.btnRestart);
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
                    Log.d("Hello", "playground");
                }
            }
        });

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

                if (getmMap().getMyLocation() != null && getmBlinky() == null && builder == null) {
                    setCameraPosition(new LatLng(location.getLatitude(), location.getLongitude()), 18, 20);
                    progress.dismiss();
                    currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    builder = new MaterialDialog(getActivity());
                    builder.setMessage("Are you ready?");
                    builder.setTitle("Mission 1 start");
                    builder.setPositiveButton("YES", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                            final MaterialDialog builder2 = new MaterialDialog(getActivity());
                            builder2.setMessage("Good luck");
                            builder2.setTitle("Run for your life !!!");
                            builder2.setPositiveButton("BEGIN", new View.OnClickListener() {

                                @Override
                                public void onClick(View v2) {
                                    builder2.dismiss();
                                    threadBlinky.run();
                                }
                            });
                            builder2.show();
                        }
                    });

                    builder.show();
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
                if (currentPosition.latitude != toPosition.getLatitude() || currentPosition.longitude != toPosition.getLongitude()) {
                    Log.d("Change Location to ", "" + toPosition.toString());
                    toPosition.setLatitude(currentPosition.latitude);
                    toPosition.setLongitude(currentPosition.longitude);
                }
                double lng = t * toPosition.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.getLatitude() + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                mBlinkyStatus.setText("Blinky Status : Coming In " + (int) getDistanceBetweenMarkersInMetres(marker, toPosition) + " metres !");

                if (t < 1.0) {
                    // Post again 96ms later.
                    mRestart.setEnabled(false);
                    handler.postDelayed(this, 96);
                } else {
                    Toast exit = Toast.makeText(getActivity(), "Try again keep it up !", Toast.LENGTH_LONG);
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
        MarkerOptions ghostMarkerPosition = new MarkerOptions().position(ghostLatLng).icon(blinky.getIcon()).flat(true);
        return ghostMarkerPosition;
    }

    @Override
    public void onPause() {
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

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map. instance
        if (getmMap() == null) {
            setmMap((((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap()));
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

    public void setmBlinky(Marker mBlinky) {
        this.mBlinky = mBlinky;
    }

    public LatLngBounds getPlayground() {
        return playground;
    }

    public void setPlayground(LatLngBounds playground) {
        this.playground = playground;
    }
}

