package com.androidandyuk.bikersbestfriend;

import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by AndyCr15 on 08/05/2017.
 */

public class fuelingDetails implements Comparable<fuelingDetails> {
    Date date;
    int miles;
    double price;
    double litres;
    double mpg;
//    int bikeId;

    public fuelingDetails(int miles, double price, double litres) {
//        this.bikeId = bikeId;
        this.miles = miles;
        this.price = price;
        this.litres = litres;
        mpg = miles / (litres / 4.54609);
        date = new Date();
    }

    public int getMiles() {
        return miles;
    }

    public double getPrice() {
        return price;
    }

    public double getLitres() {
        return litres;
    }

    public double getMpg() {
        return mpg;
    }

    @Override
    public String toString() {
        String stringDate = DateFormat.getDateInstance().format(date);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return stringDate + " - Miles : " + miles + " - mpg = " + df.format(mpg);
    }

    @Override
    public int compareTo(@NonNull fuelingDetails o) {
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
        if (this.date.after(o.date)) {
            return -1;
        }

        if (o.date.after(this.date)) {
            return 1;
        }

        return 0;
    }
}
