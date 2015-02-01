package com.example.ripzery.projectx01.util;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.app.MapsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by visit on 1/25/15 AD.
 */
public class ConnectGoogleApiClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private MapsActivity mapsActivity;
    private LocationRequest locationrequest;

    public ConnectGoogleApiClient(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("status", "connected");
        if (LocationServices.FusedLocationApi.getLastLocation(mapsActivity.mGoogleApiClient) == null) {
            locationrequest = LocationRequest.create();
            locationrequest.setInterval(1000);
//            locationrequest.setExpirationTime(60000);
            locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            final LocationListener firstGetLocation = new LocationListener() {
                int numberOfUpdate = 0;

                @Override
                public void onLocationChanged(Location location) {
                    numberOfUpdate++;
                    if (mapsActivity.checkLocation.isAccuracyAcceptable(location.getAccuracy())) {
                        mapsActivity.mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        if (mapsActivity.myArrow == null) {
                            mapsActivity.mPreviousLatLng = mapsActivity.mCurrentLatLng;
                            mapsActivity.setCameraPosition(mapsActivity.mCurrentLatLng, 18, 0);
                            mapsActivity.myArrow = mapsActivity.mMap.addMarker(new MarkerOptions()
                                    .position(mapsActivity.mCurrentLatLng)
                                    .anchor((float) 0.5, (float) 0.5)
                                    .flat(false)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.dir)));

                        }
                        LocationServices.FusedLocationApi.removeLocationUpdates(mapsActivity.mGoogleApiClient, this);
                    } else {
                        mapsActivity.progress.setMessage("Waiting for gps accuracy lower than " + mapsActivity.THRESHOLD_ACC + " metres");
                        Log.d("numupdate", numberOfUpdate + "");
                        if (numberOfUpdate > 5) {
                            mapsActivity.progress.setMessage("You may be have to go outside or fix your gps by using gps fix application");
                        }
                    }
                }
            };
            LocationServices.FusedLocationApi.requestLocationUpdates(mapsActivity.mGoogleApiClient, locationrequest, firstGetLocation);
        } else {
            mapsActivity.mCurrentLatLng = new LatLng(LocationServices.FusedLocationApi.getLastLocation(mapsActivity.mGoogleApiClient).getLatitude(), LocationServices.FusedLocationApi.getLastLocation(mapsActivity.mGoogleApiClient).getLongitude());
            if (mapsActivity.myArrow == null) {
                mapsActivity.mPreviousLatLng = mapsActivity.mCurrentLatLng;
                mapsActivity.setCameraPosition(mapsActivity.mCurrentLatLng, 18, 0);
                mapsActivity.myArrow = mapsActivity.mMap.addMarker(new MarkerOptions()
                        .position(mapsActivity.mCurrentLatLng)
                        .anchor((float) 0.5, (float) 0.5)
                        .flat(false)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dir)));

            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
