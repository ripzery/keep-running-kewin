package com.example.ripzery.projectx01;

import android.app.ProgressDialog;
import android.hardware.Sensor;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements SensorEventListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLngBounds Mahidol = new LatLngBounds(new LatLng(13.787486,100.316179),new LatLng(13.800875,100.326897));
    private SensorManager mSensorManager;
    MarkerOptions mArrow;
    Marker mDirArrow;
    BitmapDescriptor bd;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        setMapCenter(Mahidol.getCenter(),15);

        progress =  new ProgressDialog(this);
        progress = ProgressDialog.show(this, "Loading", "Wait while loading map...");

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
                    mDirArrow = mMap.addMarker(mArrow);
                    setMapCenter(mArrow.getPosition(),17);
                    progress.dismiss();
                }else{
                    mDirArrow.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
                }
            }
        });

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        bd = BitmapDescriptorFactory.fromResource(R.drawable.nav);

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

        if(mDirArrow != null){
            mDirArrow.setAnchor((float) 0.5, (float) 0.5);
            mDirArrow.setRotation(degree);
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
