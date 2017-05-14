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
    String yearOfMan;
    ArrayList<fuellingDetails> fuelings = new ArrayList<>();
    ArrayList<maintenanceLogDetails> maintenanceLogs = new ArrayList<>();

    public Bike(int bikeId, String make, String model, String registration, String VIN, String serviceDue, String MOTdue, String yearOfMan) {
        this.bikeId = bikeId;
        this.make = make;
        this.model = model;
        this.registration = registration;
        this.VIN = VIN;
        this.serviceDue = serviceDue;
        this.MOTdue = MOTdue;
        this.yearOfMan = yearOfMan;
        // no bikeCount increment as this is only used by loading bikes, which restores the old bikeCount anyway
    }

    public Bike(String make, String model, String year) {
        this.make = make;
        this.model = model;
        this.registration = "";
        this.bikeId = bikeCount;
        this.VIN = "";
        this.serviceDue = "";
        this.MOTdue = "";
        this.yearOfMan = year;
        bikeCount++;
    }

    public Bike(String make, String model) {
        this.make = make;
        this.model = model;
        this.bikeId = bikeCount;
        this.VIN = "";
        this.serviceDue = "";
        this.MOTdue = "";
        this.yearOfMan = "";
        bikeCount++;
    }



    @Override
    public String toString() {
        return yearOfMan + " " + model;
    }
}
