package com.example.ripzery.projectx01.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;

/**
 * Created by ripzery on 1/7/2015.
 */
public class DistanceCalculator {
    // คำนวณระยะห่างของ Marker หลายๆรูปแบบ
    public static double getDistanceBetweenMarkersInMetres(Marker fromLocation, Location toLocation) {
        double distance = SphericalUtil.computeDistanceBetween(fromLocation.getPosition(), new LatLng(toLocation.getLatitude(), toLocation.getLongitude()));
        return distance;
    }

    public static double getDistanceBetweenMarkersInMetres(Location fromLocation, LatLng toLocation) {
        double distance = SphericalUtil.computeDistanceBetween(new LatLng(fromLocation.getLatitude(), fromLocation.getLongitude()), toLocation);
        return distance;
    }

    public static double getDistanceBetweenMarkersInMetres(LatLng fromLocation, LatLng toLocation) {
        double distance = SphericalUtil.computeDistanceBetween(fromLocation, toLocation);
        return distance;
    }
}
