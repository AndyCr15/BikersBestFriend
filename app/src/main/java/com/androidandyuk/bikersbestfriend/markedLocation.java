package com.androidandyuk.bikersbestfriend;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by AndyCr15 on 06/05/2017.
 */

public class markedLocation {
    String name;
    LatLng location;
    String address;
    String comment;

    public markedLocation(String name, String address, LatLng location, String comment) {
        Log.i("New markedLocation", name);
        this.name = name;
        this.address = address;
        this.location = location;
        this.comment = comment;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " : " + comment;
    }
}
