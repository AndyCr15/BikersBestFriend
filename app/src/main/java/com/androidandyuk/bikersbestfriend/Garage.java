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

import static com.androidandyuk.bikersbestfriend.MainActivity.precision;
import static com.androidandyuk.bikersbestfriend.Maintenance.calculateMaintSpend;

public class Garage extends AppCompatActivity {

    public static ArrayList<Bike> bikes = new ArrayList<>();

    private FirebaseAnalytics mFirebaseAnalytics;

    View addingBikeInfo;
    EditText bikeMake;
    EditText bikeModel;
    EditText bikeYear;
    TextView aveMPG;
    TextView bikeEstMileage;
    TextView amountSpent;

    TextView bikeTitle;
    TextView bikeTitleReg;
    EditText bikeNotes;

    public static int activeBike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

//        if (bikes.size() == 0) {
//            // for testing
//            Bike newBike = new Bike("KTM", "Superduke R", "2016");
//            bikes.add(newBike);
//            Bike newBike2 = new Bike("Honda", "CB1000R", "2011");
//            bikes.add(newBike2);
//        }

        // for adding a new bike
        bikeMake = (EditText) findViewById(R.id.bikeMake);
        bikeModel = (EditText) findViewById(R.id.bikeModel);
        bikeYear = (EditText) findViewById(R.id.bikeYear);
        bikeEstMileage = (TextView) findViewById(R.id.estMileage);
        amountSpent = (TextView) findViewById(R.id.amountSpent);

        Log.i("Active Bike", "" + activeBike);

        garageSetup();

    }

    public void garageSetup() {
        bikeTitle = (TextView) findViewById(R.id.bikeTitle);
        bikeTitleReg = (TextView) findViewById(R.id.bikeTitleReg);
        aveMPG = (TextView) findViewById(R.id.aveMPG);
        bikeEstMileage = (TextView) findViewById(R.id.estMileage);
        amountSpent = (TextView) findViewById(R.id.amountSpent);
        bikeNotes = (EditText) findViewById(R.id.bikeNotes);

        // check the user has a bike, then set all the views to it's current details
        if (bikes.size() > 0) {
            bikeTitle.setText(bikes.get(activeBike).yearOfMan + " " + bikes.get(activeBike).model);
            bikeTitleReg.setText(bikes.get(activeBike).registration);
            aveMPG.setText(Fuelling.aveMPG(activeBike, 10));
            // show only 2 decimal places.  Precision is declared in MainActivity to 2 decimal places
            String spend = "£" + precision.format(calculateMaintSpend(bikes.get(activeBike)));
            amountSpent.setText(spend);
            if (bikes.get(activeBike).estMileage > 0) {
                bikeEstMileage.setText(Integer.toString(bikes.get(activeBike).estMileage));
            }
            bikeNotes.setText(bikes.get(activeBike).notes);
        }
    }

    public void addBike(View view) {
        Log.i("Bike", "Add bike");
        View addingBikeInfo = findViewById(R.id.addingBikeInfo);

        // clear out any previous details
        bikeMake.setText("");
        bikeModel.setText("");
        bikeYear.setText("");

        addingBikeInfo.setVisibility(View.VISIBLE);
    }

    public void addNewBike(View view) {

        String make = bikeMake.getText().toString();
        String model = bikeModel.getText().toString();
        String year = bikeYear.getText().toString();

        // check enough details are entered
        if (make.isEmpty() || model.isEmpty() || year.isEmpty()) {

            Toast.makeText(Garage.this, "Please complete all necessary details", Toast.LENGTH_LONG).show();

        } else {

            // check the year looks correct
            if (Integer.parseInt(year) > 1900 && Integer.parseInt(year) < 2050) {
                Bike newBike = new Bike(make, model, year);

                bikes.add(newBike);

                activeBike = bikes.size() - 1;

                garageSetup();

                // hide keyboard
                View viewAddBike = this.getCurrentFocus();
                if (viewAddBike != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewAddBike.getWindowToken(), 0);
                }
                View addingBikeInfo = findViewById(R.id.addingBikeInfo);
                addingBikeInfo.setVisibility(View.INVISIBLE);
                onBackPressed();
            } else {

                Toast.makeText(Garage.this, "That year looks unlikely", Toast.LENGTH_LONG).show();

            }
        }
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
                        Toast.makeText(Garage.this, "Removed!", Toast.LENGTH_SHORT).show();
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

        // save any changes in Bike notes
        bikeNotes = (EditText) findViewById(R.id.bikeNotes);
        bikes.get(activeBike).notes = bikeNotes.getText().toString();

        // change to bike selected
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

    @Override
    protected void onPause() {
        super.onPause();
        bikeNotes = (EditText) findViewById(R.id.bikeNotes);
        bikes.get(activeBike).notes = bikeNotes.getText().toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        garageSetup();
    }
}
