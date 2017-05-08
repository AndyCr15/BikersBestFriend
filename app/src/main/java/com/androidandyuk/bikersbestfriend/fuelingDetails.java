package com.androidandyuk.bikersbestfriend;

import java.util.Date;

/**
 * Created by AndyCr15 on 08/05/2017.
 */

public class fuelingDetails {
    Date date;
    int miles;
    double price;
    double litres;
    double mpg;

    public fuelingDetails(int miles, double price, double litres) {
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
        return "Date :" + date + ", mpg = " + mpg;
    }
}
