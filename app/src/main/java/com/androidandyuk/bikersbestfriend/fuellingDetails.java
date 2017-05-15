package com.androidandyuk.bikersbestfriend;

import android.icu.text.DecimalFormat;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.util.Date;

import static com.androidandyuk.bikersbestfriend.MainActivity.activeBike;
import static com.androidandyuk.bikersbestfriend.MainActivity.sdf;

/**
 * Created by AndyCr15 on 08/05/2017.
 */

public class fuellingDetails implements Comparable<fuellingDetails> {
    String date;
    int miles;
    double price;
    double litres;
    double mpg;
    int mileage = 0;

    public fuellingDetails(int miles, double price, double litres, int mileage) {
        this.miles = miles;
        this.price = price;
        this.litres = litres;
        mpg = miles / (litres / 4.54609);
        Date fuelDate = new Date();
        this.date = sdf.format(fuelDate);
        if(mileage == 0) {
            Garage.bikes.get(activeBike).estMileage += miles;
            return;
        } else if (mileage > 0) {
            Garage.bikes.get(activeBike).estMileage = mileage;
        }
    }

    public fuellingDetails(int miles, double price, double litres, Date date, int mileage) {
        this.miles = miles;
        this.price = price;
        this.litres = litres;
        mpg = miles / (litres / 4.54609);
        this.date = sdf.format(date);
        this.mileage = mileage;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return this.date + " - Miles : " + miles + " - mpg = " + df.format(mpg);
    }

    @Override
    public int compareTo(@NonNull fuellingDetails o) {
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
}
