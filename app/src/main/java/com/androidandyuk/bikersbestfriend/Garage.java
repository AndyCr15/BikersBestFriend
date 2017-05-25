package com.androidandyuk.bikersbestfriend;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.ParseException;
import java.util.Date;

import static com.androidandyuk.bikersbestfriend.MainActivity.activeBike;
import static com.androidandyuk.bikersbestfriend.MainActivity.bikes;
import static com.androidandyuk.bikersbestfriend.MainActivity.precision;
import static com.androidandyuk.bikersbestfriend.MainActivity.sdf;
import static com.androidandyuk.bikersbestfriend.Maintenance.loadLogs;

public class Garage extends AppCompatActivity {

//    public static ArrayList<Bike> bikes = new ArrayList<>();

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String TAG = "MainActivity";

    private AdView mAdView;

    View addingBikeInfo;
    EditText bikeMake;
    EditText bikeModel;
    EditText bikeYear;
    TextView aveMPG;
    TextView bikeEstMileage;
    TextView amountSpent;
    TextView myRegView;
    TextView MOTdue;
    TextView serviceDue;

    TextView bikeTitle;
    ViewSwitcher regSwitcher;
    EditText bikeNotes;

    private DatePickerDialog.OnDateSetListener MOTDateSetListener;
    private DatePickerDialog.OnDateSetListener serviceDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // until I implement landscape view, lock the orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        myRegView = (TextView) findViewById(R.id.clickable_reg_view);
        MOTdue = (TextView) findViewById(R.id.MOTdue);
        serviceDue = (TextView) findViewById(R.id.serviceDue);

        Log.i("Active Bike", "" + activeBike);

        loadLogs();
        garageSetup();

        MOTDateSetListener = new DatePickerDialog.OnDateSetListener()

        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Log.i("MOT Date was: ", bikes.get(activeBike).MOTdue);
                Calendar date = new GregorianCalendar();
                date.set(year,month,day);
                String sdfDate = sdf.format(date);
                bikes.get(activeBike).MOTdue = sdfDate;
                Log.i("MOT Date now: ", bikes.get(activeBike).MOTdue);
                garageSetup();
            }
        };

        serviceDateSetListener = new DatePickerDialog.OnDateSetListener()

        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Log.i("Service Date was: ", bikes.get(activeBike).serviceDue);
                Calendar date = new GregorianCalendar();
                date.set(year,month,day);
                String sdfDate = sdf.format(date);
                bikes.get(activeBike).serviceDue = sdfDate;
                Log.i("Service Date now: ", bikes.get(activeBike).serviceDue);
                garageSetup();
            }
        };

    }

    public void garageSetup() {
        bikeTitle = (TextView) findViewById(R.id.bikeTitle);
        regSwitcher = (ViewSwitcher) findViewById(R.id.regSwitcher);
        aveMPG = (TextView) findViewById(R.id.aveMPG);
        bikeEstMileage = (TextView) findViewById(R.id.estMileage);
        amountSpent = (TextView) findViewById(R.id.amountSpent);
        bikeNotes = (EditText) findViewById(R.id.bikeNotes);
        bikeNotes.setSelected(false);
        myRegView = (TextView) findViewById(R.id.clickable_reg_view);
        MOTdue = (TextView) findViewById(R.id.MOTdue);
        serviceDue = (TextView) findViewById(R.id.serviceDue);


        // check the user has a bike, then set all the views to it's current details
        if (bikes.size() > 0) {
            bikeTitle.setText(bikes.get(activeBike).yearOfMan + " " + bikes.get(activeBike).model);
            aveMPG.setText(Fuelling.aveMPG(activeBike, 10));

            Log.i("Active Bike Reg", " " + (bikes.get(activeBike).registration));

            myRegView.setText((bikes.get(activeBike).registration));
            myRegView.requestFocus();

            // show only 2 decimal places.  Precision is declared in MainActivity to 2 decimal places
            String spend = "£" + precision.format(calculateMaintSpend(bikes.get(activeBike)));
            amountSpent.setText(spend);
            if (bikes.get(activeBike).estMileage > 0) {
                bikeEstMileage.setText(Double.toString(bikes.get(activeBike).estMileage));
            }
            bikeNotes.setText(bikes.get(activeBike).notes);

            // check if an MOT date is set
            Log.i("MOT Due ", bikes.get(activeBike).MOTdue);
//            if (bikes.get(activeBike).MOTdue != null) {
                MOTdue.setText(bikes.get(activeBike).MOTdue);
//            }

            // check if a Service date is set
            Log.i("Service Due ", bikes.get(activeBike).serviceDue);
//            if (bikes.get(activeBike).serviceDue != null) {
                serviceDue.setText(bikes.get(activeBike).serviceDue);
//            }
        }
    }

    public void setMOTdue(View view) {
        // this sets what date will show when the date picker shows
        Date thisDate = new Date();
        try {
            thisDate = sdf.parse(bikes.get(activeBike).MOTdue);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // for some reasone I can't getYear from thisDate, so will just use the current year
        Calendar cal = Calendar.getInstance();
        cal.setTime(thisDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                Garage.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                MOTDateSetListener,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void setServiceDue(View view) {
        // this sets what date will show when the date picker shows
        Date thisDate = new Date();
        try {
            thisDate = sdf.parse(bikes.get(activeBike).serviceDue);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // for some reasone I can't getYear from thisDate, so will just use the current year
        Calendar cal = Calendar.getInstance();
        cal.setTime(thisDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                Garage.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                serviceDateSetListener,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    public static double calculateMaintSpend(Bike bike) {
        Log.i("Garage", "Calculating Spend on " + bike);
        Log.i("Number of logs", "" + bike.maintenanceLogs.size());
        double spend = 0;
        for (maintenanceLogDetails log : bike.maintenanceLogs) {
            Log.i("Price", "" + log.price);
            spend += log.price;
        }
        return spend;
    }

    public void TextViewClicked(View view) {
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.regSwitcher);
        switcher.showNext(); //or switcher.showPrevious();
        myRegView = (TextView) switcher.findViewById(R.id.clickable_reg_view);
        myRegView.setText("");
        myRegView.setSelected(true);
        myRegView.requestFocus();
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
                invalidateOptionsMenu();
            } else {

                Toast.makeText(Garage.this, "That year looks unlikely", Toast.LENGTH_LONG).show();

            }
        }
    }

    public void deleteBike(View view) {

        if (activeBike > -1) {
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
    }

    public void goToMaintenanceLog(View view) {
        if (activeBike > -1) {
            Intent intent = new Intent(getApplicationContext(), Maintenance.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bike_choice, menu);

        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "Settings").setShortcut('3', 'c');
        menu.add(0, 1, 0, "About").setShortcut('3', 'c');

        for (int i = 0; i < bikes.size(); i++) {
            String bikeMakeMenu = bikes.get(i).model;
            menu.add(0, i + 2, 0, bikeMakeMenu).setShortcut('3', 'c');
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
                Toast.makeText(Garage.this, "Settings not yet implemented", Toast.LENGTH_LONG).show();
                return true;
            case 1:
                Log.i("Option", "1");
                // go to about me
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                return true;
            case 2:
                Log.i("Option", "2");
                activeBike = 0;
                garageSetup();
                return true;
            case 3:
                Log.i("Option", "3");
                activeBike = 1;
                garageSetup();
                return true;
            case 4:
                Log.i("Option", "4");
                activeBike = 2;
                garageSetup();
                return true;
            case 5:
                Log.i("Option", "5");
                activeBike = 3;
                garageSetup();
                return true;
            case 6:
                Log.i("Option", "6");
                activeBike = 4;
                garageSetup();
                return true;
            case 7:
                Log.i("Option", "7");
                activeBike = 5;
                garageSetup();
                return true;
            case 8:
                Log.i("Option", "8");
                activeBike = 6;
                garageSetup();
                return true;
            case 9:
                Log.i("Option", "9");
                activeBike = 7;
                garageSetup();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bikeNotes = (EditText) findViewById(R.id.bikeNotes);
        // check there's actually a bike before saving the notes
        if (bikeNotes != null && bikes.size() > 0) {
            bikes.get(activeBike).notes = bikeNotes.getText().toString();
        }

        myRegView = (EditText) findViewById(R.id.hidden_reg_view);
        // check there's actually a bike before saving the notes
        if (myRegView != null && bikes.size() > 0) {
            bikes.get(activeBike).registration = myRegView.getText().toString();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        garageSetup();
    }
}