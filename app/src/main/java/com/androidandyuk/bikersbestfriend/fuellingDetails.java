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

    public fuellingDetails(int miles, double price, double litres, String date, int mileage) {
        this.miles = miles;
        this.price = price;
        this.litres = litres;
        mpg = miles / (litres / 4.54609);
        this.date = sdf.format(date);
        // being sent in with a date means it's from an edit or a save
        Date todaysDate = new Date();
        String formattedDate = sdf.format(todaysDate);
        // check if it's still the same day, allow mileage to be changed
        if (date.equals(formattedDate)) {
            if (mileage != 0 && mileage > Garage.bikes.get(activeBike).estMileage) {
                Garage.bikes.get(activeBike).estMileage = mileage;
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

    public int getMileage() {
        return mileage;
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
