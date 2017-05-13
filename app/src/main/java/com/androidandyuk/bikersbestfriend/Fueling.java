package com.androidandyuk.bikersbestfriend;

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

import java.util.Collections;

import static com.androidandyuk.bikersbestfriend.Garage.activeBike;
import static com.androidandyuk.bikersbestfriend.Garage.bikes;

public class Fueling extends AppCompatActivity {

    static ArrayAdapter arrayAdapter;
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

        initiateList();


//        fuelingDetails test = new fuelingDetails(120, 1.199, 16.25);
//        bikes.get(activeBike).fuelings.add(test);
//        fuelingDetails test1 = new fuelingDetails(100, 1.199, 16.25);
//        fuelings.add(test1);
//        fuelingDetails test2 = new fuelingDetails(170, 1.199, 16.25);
//        fuelings.add(test2);
//        fuelingDetails test3 = new fuelingDetails(120, 1.199, 18.25);
//        fuelings.add(test3);
//        fuelingDetails test4 = new fuelingDetails(140, 1.199, 16.25);
//        fuelings.add(test4);
//        fuelingDetails test5 = new fuelingDetails(130, 1.199, 16.25);
//        fuelings.add(test5);
//        fuelingDetails test6 = new fuelingDetails(120, 1.199, 19.25);
//        fuelings.add(test6);
//        fuelingDetails test7 = new fuelingDetails(140, 1.199, 16.25);
//        fuelings.add(test7);
//        fuelingDetails test8 = new fuelingDetails(170, 1.199, 19.25);
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

        setTitle("Fueling: " + bikes.get(activeBike).model);

    }

    public void showFueling(View view) {
        // opens the add fueling dialog
        Log.i("Fueling", "Adding fuel up");
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
        fuelingDetails today = new fuelingDetails(miles, price, litres);
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

}
