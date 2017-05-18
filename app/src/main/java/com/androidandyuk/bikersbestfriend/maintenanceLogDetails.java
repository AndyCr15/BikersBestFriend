package com.androidandyuk.bikersbestfriend;

import android.content.Context;
import android.icu.text.DecimalFormat;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;

import static com.androidandyuk.bikersbestfriend.MainActivity.activeBike;
import static com.androidandyuk.bikersbestfriend.MainActivity.sdf;

/**
 * Created by AndyCr15 on 12/05/2017.
 */

public class maintenanceLogDetails implements Comparable<maintenanceLogDetails> {
    String date;
    String log;
    double price;
    Boolean wasService;
    Boolean wasMOT;

    public maintenanceLogDetails(String log, Boolean wasService, Boolean wasMOT) {
        this.log = log;
        Date logDate = new Date();
        this.date = sdf.format(logDate);
        this.price = 0;
        this.wasService = wasService;
        this.wasMOT = wasMOT;
    }

    public maintenanceLogDetails(String log, double price, Date logDate, Boolean wasService, Boolean wasMOT) {
        this.log = log;
        this.date = sdf.format(logDate);
        this.price = price;
        this.wasService = wasService;
        this.wasMOT = wasMOT;
    }

    public maintenanceLogDetails(String log, double price, Boolean wasService, Boolean wasMOT, int mileage) {
        this.log = log;
        Date logDate = new Date();
        this.date = sdf.format(logDate);
        this.price = price;
        this.wasService = wasService;
        this.wasMOT = wasMOT;

        if (mileage != 0 && mileage > MainActivity.bikes.get(activeBike).estMileage) {
            MainActivity.bikes.get(activeBike).estMileage = mileage;
        } else if (mileage != 0) {
            Context context = App.getContext();
            Toast.makeText(context, "The mileage appears to be lower than current est mileage. Not applied", Toast.LENGTH_LONG).show();

        }
    }

    public maintenanceLogDetails(String log, double price, String date, Boolean wasService, Boolean wasMOT, int mileage) {
        this.log = log;
        this.date = date;
        this.price = price;
        this.wasService = wasService;
        this.wasMOT = wasMOT;

        // being sent in with a date means it's from an edit or a save
        Date todaysDate = new Date();
        String formattedDate = sdf.format(todaysDate);
        // check if it's still the same day, allow mileage to be changed
        if (date.equals(formattedDate)) {
            if (mileage != 0 && mileage > MainActivity.bikes.get(activeBike).estMileage) {
                MainActivity.bikes.get(activeBike).estMileage = mileage;
            } else if (mileage != 0) {
                Context context = App.getContext();
                Toast.makeText(context, "The mileage appears to be lower than current est mileage. Not applied", Toast.LENGTH_LONG).show();

            }
        } else {
            Context context = App.getContext();
            Toast.makeText(context, "You can't change mileage from previous days entries. It will update the next time you provide the mileage", Toast.LENGTH_LONG).show();
        }
    }

    public String getDate() {
        return date;
    }

    public String getLog() {
        return log;
    }

    public double getPrice() {
        return price;
    }

    public Boolean getWasService() {
        return wasService;
    }

    public Boolean getWasMOT() {
        return wasMOT;
    }

    @Override
    public int compareTo(@NonNull maintenanceLogDetails o) {

        Date thisDate = new Date();
        Date oDate = new Date();

        try {
            thisDate = sdf.parse(this.date);
            oDate = sdf.parse(o.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (thisDate.after(oDate)) {
            return -1;
        }

        if (oDate.after(thisDate)) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {

        String message = "";

        if (this.wasMOT) {
            message += "[MOT]";
        }

        if (this.wasService) {
            message += "[Service]";
        }


        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        String tempLog = log;
        if (log.length() > 60) {
            tempLog = log.substring(0, 59) + "...";
        }
        return this.date + " : " + message + tempLog;
    }
}
