package com.androidandyuk.bikersbestfriend;

import android.icu.util.Calendar;

import java.util.ArrayList;

import static com.androidandyuk.bikersbestfriend.MainActivity.sdf;

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
    String lastKnownService;
    String lastKnownMOT;
    String yearOfMan;
    String notes;
    double estMileage;
    ArrayList<fuellingDetails> fuelings = new ArrayList<>();
    ArrayList<maintenanceLogDetails> maintenanceLogs = new ArrayList<>();

    public Bike(int bikeId, String make, String model, String registration, String VIN, String serviceDue, String MOTdue, String lastKnownService, String lastKnownMOT, String yearOfMan, String notes, double estMileage) {
        this.bikeId = bikeId;
        this.make = make;
        this.model = model;
        this.registration = registration;
        this.VIN = VIN;
        this.serviceDue = serviceDue;
        this.MOTdue = MOTdue;
        this.lastKnownService = lastKnownService;
        this.lastKnownMOT = lastKnownMOT;
        this.yearOfMan = yearOfMan;
        this.notes = notes;
        this.estMileage = estMileage;
        // no bikeCount increment as this is only used by loading bikes, which restores the old bikeCount anyway
    }

    public Bike(String make, String model, String year) {

        // any new bike has it's MOT and service set to today
        Calendar cal = Calendar.getInstance();
//        int yearNow = cal.get(Calendar.YEAR);
//        int month = cal.get(Calendar.MONTH);
//        int day = cal.get(Calendar.DAY_OF_MONTH);
        String todaysDate = sdf.format(cal);

        this.make = make;
        this.model = model;
        this.registration = "unknown";
        this.bikeId = bikeCount;
        this.VIN = "";
        this.serviceDue = todaysDate;
        this.MOTdue = todaysDate;
        this.lastKnownService = "";
        this.lastKnownMOT = "";
        this.yearOfMan = year;
        this.estMileage = 0;
        bikeCount++;
    }


    @Override
    public String toString() {
        return yearOfMan + " " + model;
    }
}
