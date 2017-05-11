package com.androidandyuk.bikersbestfriend;

import java.util.ArrayList;

/**
 * Created by AndyCr15 on 09/05/2017.
 */

public class Bike {
    static int bikeCount;
    int bikeId;
    String make;
    String model;
    String registration;
    String VIN;
    String serviceDue;
    String MOTdue;
    ArrayList<fuelingDetails> fuelings = new ArrayList<>();
//    ArrayList<maintenanceLog> maintenanceLogs = new ArrayList<>();


    public Bike(String make, String model, String registration) {
        this.make = make;
        this.model = model;
        this.registration = registration;
        this.bikeId = bikeCount;
                bikeCount++;
    }

    public Bike(String make, String model) {
        this.make = make;
        this.model = model;
        this.bikeId = bikeCount;
        bikeCount++;
    }

    @Override
    public String toString() {
        return make + model;
    }
}
