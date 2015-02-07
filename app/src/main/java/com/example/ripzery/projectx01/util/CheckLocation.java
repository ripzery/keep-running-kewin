package com.example.ripzery.projectx01.util;

import android.app.Activity;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by visit on 1/25/15 AD.
 */
public class CheckLocation implements GpsStatus.Listener {
    private static final double THRESHOLD_ACC = 300; // กำหนด Accuracy ที่ยอมรับได้
    private static final long DURATION_TO_FIX_LOST_MS = 10000;
    private Activity mapsActivity;
    private LocationManager locationManager;
    private boolean gpsFix;
    private long locationTime = 0;

    public CheckLocation(Activity mapsActivity, LocationManager locationManager) {
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
        int locationMode = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(mapsActivity.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return (locationMode != Settings.Secure.LOCATION_MODE_OFF && locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY); //check location mode
        } else {
            String locationProviders = Settings.Secure.getString(mapsActivity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
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
