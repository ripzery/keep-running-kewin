package com.example.ripzery.projectx01.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.reflect.Method;

/**
 * Created by visit on 1/24/15 AD.
 */
public class CheckConnectivity {
    Activity mapsActivity;

    public CheckConnectivity(Activity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }

    public boolean is3gConnected() {

        boolean mobileDataEnabled; // Assume disabled

        ConnectivityManager cm = (ConnectivityManager) mapsActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            return false;
        }
        return mobileDataEnabled;
    }

    public boolean isWifiConnected() {
        boolean wifiEnabled = false;
        ConnectivityManager connManager = (ConnectivityManager) mapsActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            // Do whatever
            wifiEnabled = true;
            return wifiEnabled;
        }
        return wifiEnabled;
    }
}

