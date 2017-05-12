package com.androidandyuk.bikersbestfriend;

import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by AndyCr15 on 12/05/2017.
 */

public class maintenanceLogDetails implements Comparable<maintenanceLogDetails> {
    Date date;
    String log;
    double price;

    public maintenanceLogDetails(String log) {
        this.log = log;
        this.date = new Date();
        this.price = 0;
    }

    public maintenanceLogDetails(String log, double price) {
        this.log = log;
        this.date = new Date();
        this.price = price;
    }

    @Override
    public int compareTo(@NonNull maintenanceLogDetails o) {
        if (this.date.after(o.date)) {
            return -1;
        }

        if (o.date.after(this.date)) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        String stringDate = DateFormat.getDateInstance().format(date);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        String tempLog = log;
        if (log.length() > 60) {
            tempLog = log.substring(0, 59) + "...";
        }
        return stringDate + " : " + tempLog;
    }
}
