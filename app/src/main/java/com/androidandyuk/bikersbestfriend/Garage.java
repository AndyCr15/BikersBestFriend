package com.androidandyuk.bikersbestfriend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class Garage extends AppCompatActivity {

    public static ArrayList<Bike> bikes = new ArrayList<>();

    View addingBikeInfo;
    EditText bikeMake;
    EditText bikeModel;
    EditText bikeReg;

    TextView bikeTitle;
    TextView bikeTitleReg;

    public static int activeBike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage);

        // for testing
        Bike newBike = new Bike("KTM", "SDR", "SR66 YTW");
        bikes.add(newBike);
        Bike newBike2 = new Bike("Honda", "CB1000R", "REG1");
        bikes.add(newBike2);


        // for adding a new bike
        bikeMake = (EditText) findViewById(R.id.bikeMake);
        bikeModel = (EditText) findViewById(R.id.bikeModel);
        bikeReg = (EditText) findViewById(R.id.bikeReg);

        Log.i("Active Bike", "" + activeBike);

        garageSetup();

    }

    public void garageSetup() {
        bikeTitle = (TextView) findViewById(R.id.bikeTitle);
        bikeTitleReg = (TextView) findViewById(R.id.bikeTitleReg);

        if (bikes.size() > 0) {
            bikeTitle.setText(bikes.get(activeBike).make + " " + bikes.get(activeBike).model);
            bikeTitleReg.setText(bikes.get(activeBike).registration);
        }
    }

    public void addBike(View view) {
        Log.i("Bike", "Add bike");
        View addingBikeInfo = findViewById(R.id.addingBikeInfo);

        // clear out any previous details
        bikeMake.setText("");
        bikeModel.setText("");
        bikeReg.setText("");

        addingBikeInfo.setVisibility(View.VISIBLE);
    }

    public void addNewBike(View view) {
        View addingBikeInfo = findViewById(R.id.addingBikeInfo);
        addingBikeInfo.setVisibility(View.INVISIBLE);

        String make = bikeMake.getText().toString();
        String model = bikeModel.getText().toString();
        String reg = bikeReg.getText().toString();

        Bike newBike = new Bike(make, model, reg);

        bikes.add(newBike);

        activeBike = bikes.size() - 1;

        garageSetup();

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
                garageSetup();
                return true;
            case 1:
                Log.i("Option", "1");
                activeBike = 1;
                garageSetup();
                return true;
            case 2:
                Log.i("Option", "2");
                activeBike = 2;
                garageSetup();
                return true;
            case 4:
                Log.i("Option", "3");
                activeBike = 3;
                garageSetup();
                return true;
            case 5:
                Log.i("Option", "4");
                activeBike = 4;
                garageSetup();
                return true;
            case 6:
                Log.i("Option", "5");
                activeBike = 5;
                garageSetup();
                return true;
            case 7:
                Log.i("Option", "6");
                activeBike = 6;
                garageSetup();
                return true;
            case 8:
                Log.i("Option", "7");
                activeBike = 7;
                garageSetup();
                return true;
            case 10:
                Log.i("Option", "9");
                activeBike = 9;
                garageSetup();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
