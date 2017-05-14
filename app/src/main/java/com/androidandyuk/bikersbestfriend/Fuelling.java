package com.androidandyuk.bikersbestfriend;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.androidandyuk.bikersbestfriend.Garage.activeBike;
import static com.androidandyuk.bikersbestfriend.Garage.bikes;
import static com.androidandyuk.bikersbestfriend.MainActivity.sdf;
import static com.androidandyuk.bikersbestfriend.Maintenance.ed;

public class Fuelling extends AppCompatActivity {

    static ArrayAdapter arrayAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    static SharedPreferences sharedPreferences;
    static int lastHowManyFuels = 10;

    ListView listView;
    EditText milesDone;
    EditText petrolPrice;
    EditText litresUsed;
    TextView mpgView;
    View fuelingDetailsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fueling);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        sharedPreferences = this.getSharedPreferences("com.androidandyuk.bikersbestfriend", Context.MODE_PRIVATE);
        ed = sharedPreferences.edit();

        loadFuels();

        initiateList();


//        fuellingDetails test = new fuellingDetails(120, 1.199, 16.25);
//        bikes.get(activeBike).fuelings.add(test);
//        fuellingDetails test1 = new fuellingDetails(100, 1.199, 16.25);
//        fuelings.add(test1);
//        fuellingDetails test2 = new fuellingDetails(170, 1.199, 16.25);
//        fuelings.add(test2);
//        fuellingDetails test3 = new fuellingDetails(120, 1.199, 18.25);
//        fuelings.add(test3);
//        fuellingDetails test4 = new fuellingDetails(140, 1.199, 16.25);
//        fuelings.add(test4);
//        fuellingDetails test5 = new fuellingDetails(130, 1.199, 16.25);
//        fuelings.add(test5);
//        fuellingDetails test6 = new fuellingDetails(120, 1.199, 19.25);
//        fuelings.add(test6);
//        fuellingDetails test7 = new fuellingDetails(140, 1.199, 16.25);
//        fuelings.add(test7);
//        fuellingDetails test8 = new fuellingDetails(170, 1.199, 19.25);
//        fuelings.add(test8);


    }

    private void initiateList() {
        listView = (ListView) findViewById(R.id.maintList);

        fuelingDetailsLayout = findViewById(R.id.fuelingDetailsLayout);

        milesDone = (EditText) findViewById(R.id.milesDone);
        petrolPrice = (EditText) findViewById(R.id.petrolPrice);
        litresUsed = (EditText) findViewById(R.id.litresUsed);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, bikes.get(activeBike).fuelings);

        listView.setAdapter(arrayAdapter);
        aveMPG(activeBike, lastHowManyFuels);

        setTitle("Fuelling: " + bikes.get(activeBike).model);

    }

    public void showFueling(View view) {
        // opens the add fueling dialog
        Log.i("Fuelling", "Adding fuel up");
        fuelingDetailsLayout.setVisibility(View.VISIBLE);

    }

    public double aveMPG(int bikeID, int numberOfFuelings) {
        double totalMiles = 0;
        double totalLitres = 0;
        int count = 0;
        Bike thisBike = Garage.bikes.get(bikeID);
        if (numberOfFuelings > thisBike.fuelings.size()) {
            numberOfFuelings = thisBike.fuelings.size();
        }


        for (int i = 0; i < numberOfFuelings; i++) {
            count++;
            totalMiles += thisBike.fuelings.get(i).miles;
            totalLitres += thisBike.fuelings.get(i).litres;

        }
        Log.i("Calculating MPG", "" + count);
        double mpg = totalMiles / (totalLitres / 4.54609);
        return mpg;
    }

    public void addFueling(View view) {
        int miles = Integer.parseInt(milesDone.getText().toString());
        double price = Double.parseDouble(petrolPrice.getText().toString());
        double litres = Double.parseDouble(litresUsed.getText().toString());
        fuellingDetails today = new fuellingDetails(miles, price, litres);
        bikes.get(activeBike).fuelings.add(today);
        Collections.sort(bikes.get(activeBike).fuelings);
        arrayAdapter.notifyDataSetChanged();
        fuelingDetailsLayout.setVisibility(View.INVISIBLE);

        TextView mpgView = (TextView) findViewById(R.id.mpgView);

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        int fuelingsForAve = lastHowManyFuels;

        if (bikes.get(activeBike).fuelings.size() < fuelingsForAve) {
            fuelingsForAve = bikes.get(activeBike).fuelings.size();
        }
        mpgView.setText("Ave MPG over the last " + fuelingsForAve + " stops is " + df.format(aveMPG(activeBike, fuelingsForAve)) + " mpg");

        // clear previous entries
        milesDone.setText(null);
        milesDone.clearFocus();
        petrolPrice.setText(null);
        petrolPrice.clearFocus();
        litresUsed.setText(null);
        litresUsed.clearFocus();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bike_choice, menu);

        super.onCreateOptionsMenu(menu);
        for (int i = 0; i < bikes.size(); i++) {
            String bikeMakeMenu = bikes.get(i).model;
            menu.add(0, i, 0, bikeMakeMenu).setShortcut('3', 'c');
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                Log.i("Option", "0");
                activeBike = 0;
                initiateList();
                return true;
            case 1:
                Log.i("Option", "1");
                activeBike = 1;
                initiateList();
                return true;
            case 2:
                Log.i("Option", "2");
                activeBike = 2;
                initiateList();
                return true;
            case 4:
                Log.i("Option", "3");
                activeBike = 3;
                initiateList();
                return true;
            case 5:
                Log.i("Option", "4");
                activeBike = 4;
                initiateList();
                return true;
            case 6:
                Log.i("Option", "5");
                activeBike = 5;
                initiateList();
                return true;
            case 7:
                Log.i("Option", "6");
                activeBike = 6;
                initiateList();
                return true;
            case 8:
                Log.i("Option", "7");
                activeBike = 7;
                initiateList();
                return true;
            case 10:
                Log.i("Option", "9");
                activeBike = 9;
                initiateList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void saveFuels() {

        for (Bike thisBike : bikes) {

            Log.i("Saving Fuellings", "" + thisBike);
            try {
                ArrayList<String> fdates = new ArrayList<>();
                ArrayList<String> miles = new ArrayList<>();
                ArrayList<String> prices = new ArrayList<>();
                ArrayList<String> litres = new ArrayList<>();

                // I think these are new variables, so likely don't need clearing?
                fdates.clear();
                miles.clear();
                prices.clear();
                litres.clear();

                for (fuellingDetails thisLog : thisBike.fuelings) {

                    fdates.add(thisLog.date);
                    miles.add(Integer.toString(thisLog.miles));
                    prices.add(Double.toString(thisLog.price));
                    litres.add(Double.toString(thisLog.litres));

                }

                Log.i("Saving Fuels", "Size :" + fdates.size());
                ed.putString("fdates" + thisBike.bikeId, ObjectSerializer.serialize(fdates)).apply();
                ed.putString("miles" + thisBike.bikeId, ObjectSerializer.serialize(miles)).apply();
                ed.putString("prices" + thisBike.bikeId, ObjectSerializer.serialize(prices)).apply();
                ed.putString("litres" + thisBike.bikeId, ObjectSerializer.serialize(litres)).apply();

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Adding fuellings", "Failed attempt");
            }
        }
    }

    public static void loadFuels() {

        for (Bike thisBike : bikes) {
            thisBike.fuelings.clear();

            Log.i("Loading Fuels", "" + thisBike);

            ArrayList<String> fdates = new ArrayList<>();
            ArrayList<String> miles = new ArrayList<>();
            ArrayList<String> prices = new ArrayList<>();
            ArrayList<String> litres = new ArrayList<>();

            // I think these are new variables, so likely don't need clearing?
            fdates.clear();
            miles.clear();
            prices.clear();
            litres.clear();

            try {

                fdates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("fdates" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                miles = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("miles" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                prices = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("prices" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                litres = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("litres" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                Log.i("fDates for " + thisBike, "Count :" + fdates.size());
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Loading details", "Failed attempt");
            }

            Log.i("Retrieved info" + thisBike, "Log count :" + fdates.size());
            if (fdates.size() > 0 && miles.size() > 0 && prices.size() > 0 && litres.size() > 0) {
                // we've checked there is some info
                if (fdates.size() == miles.size() && miles.size() == prices.size() && prices.size() == litres.size()) {
                    // we've checked each item has the same amount of info, nothing is missing
                    for (int x = 0; x < fdates.size(); x++) {
                        Date thisDate = new Date();
                        try {
                            thisDate = sdf.parse(fdates.get(x));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        fuellingDetails newLog = new fuellingDetails(Integer.parseInt(miles.get(x)), Double.parseDouble(prices.get(x)), Double.parseDouble(litres.get(x)), thisDate);
                        Log.i("Adding", "" + x + "" + newLog);
                        thisBike.fuelings.add(newLog);
                    }

                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Fuelling Activity", "On Pause");
        saveFuels();
    }
}
