package com.example.ripzery.projectx01.util;

import android.content.Context;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.util.Log;

import com.example.ripzery.projectx01.app.MapsActivity;

/**
 * Created by visit on 1/25/15 AD.
 */
public class CheckLocation implements GpsStatus.Listener {
    private static final double THRESHOLD_ACC = 300; // กำหนด Accuracy ที่ยอมรับได้
    private static final long DURATION_TO_FIX_LOST_MS = 10000;
    private MapsActivity mapsActivity;
    private LocationManager locationManager;
    private boolean gpsFix;
    private long locationTime = 0;

    public CheckLocation(MapsActivity mapsActivity, LocationManager locationManager) {
        this.mapsActivity = mapsActivity;
        this.locationManager = locationManager;
    }

    public boolean isAccuracyAcceptable(double acc) {

        if (acc < THRESHOLD_ACC) {
            //acceptable
            return true;
        }
        return false;
    }


    public boolean isLocationEnabled() {
        LocationManager manager = (LocationManager) mapsActivity.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        } else return true;
    }

    public void setLocationTime(long locationTime) {
        this.locationTime = locationTime;
    }

    @Override
    public void onGpsStatusChanged(int changeType) {
        if (locationManager != null) {
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

    public String getGrade(int acc) {

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
