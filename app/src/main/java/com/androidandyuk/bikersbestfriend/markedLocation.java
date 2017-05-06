package com.androidandyuk.bikersbestfriend;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by AndyCr15 on 06/05/2017.
 */

public class markedLocation {
    String name;
    LatLng location;
    String address;
    String comment;

    public markedLocation(String name, LatLng location, String comment) {
        this.name = name;
        this.location = location;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "markedLocation{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }



}
