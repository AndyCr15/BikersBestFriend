package com.androidandyuk.bikersbestfriend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

public class Garage extends AppCompatActivity {

    public static ArrayList<Bike> bikes = new ArrayList<>();

    private FirebaseAnalytics mFirebaseAnalytics;

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

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (bikes.size() == 0) {
            // for testing
            Bike newBike = new Bike("KTM", "Superduke R", "2016");
            bikes.add(newBike);
            Bike newBike2 = new Bike("Honda", "CB1000R", "2011");
            bikes.add(newBike2);
        }

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
            bikeTitle.setText(bikes.get(activeBike).yearOfMan + " " + bikes.get(activeBike).model);
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

        // hide keyboard
        View viewAddBike = this.getCurrentFocus();
        if (viewAddBike != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewAddBike.getWindowToken(), 0);
        }
        onBackPressed();
    }

    public void deleteBike(View view) {
        Log.i("Delete Bike", "" + bikes.get(activeBike));

        // add warning
        new AlertDialog.Builder(Garage.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure?")
                .setMessage("You're about to remove this bike and all it's data forever...")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Removing", "Bike");
                        bikes.remove(activeBike);
                        MainActivity.saveBikes();
                        Maintenance.saveLogs();
                        Fuelling.saveFuels();
                        activeBike = bikes.size() - 1;
                        onBackPressed();
                        Toast.makeText(Garage.this,"Removed!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();





    }

    public void goToMaintenanceLog(View view) {
        Intent intent = new Intent(getApplicationContext(), Maintenance.class);
        startActivity(intent);
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
