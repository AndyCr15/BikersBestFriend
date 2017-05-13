package com.androidandyuk.bikersbestfriend;

import android.icu.text.DecimalFormat;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.util.Date;

import static com.androidandyuk.bikersbestfriend.MainActivity.sdf;

/**
 * Created by AndyCr15 on 12/05/2017.
 */

public class maintenanceLogDetails implements Comparable<maintenanceLogDetails> {
    String date;
    String log;
    double price;


    public maintenanceLogDetails(String log) {
        this.log = log;
        Date logDate = new Date();
        this.date = sdf.format(logDate);
        this.price = 0;
    }

    public maintenanceLogDetails(String log, double price) {
        this.log = log;
        Date logDate = new Date();
        this.date = sdf.format(logDate);
        this.price = price;
    }

    public maintenanceLogDetails(String log, double price, Date date) {
        this.log = log;
        this.date = sdf.format(date);
        this.price = price;
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

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        String tempLog = log;
        if (log.length() > 60) {
            tempLog = log.substring(0, 59) + "...";
        }
        return this.date + " : " + tempLog;
    }
}
