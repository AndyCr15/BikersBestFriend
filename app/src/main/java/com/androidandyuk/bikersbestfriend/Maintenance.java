package com.androidandyuk.bikersbestfriend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.androidandyuk.bikersbestfriend.Garage.activeBike;
import static com.androidandyuk.bikersbestfriend.Garage.bikes;
import static com.androidandyuk.bikersbestfriend.MainActivity.sdf;

public class Maintenance extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor ed;

    static ArrayAdapter arrayAdapter;

    ListView maintList;

    View logDetails;

    EditText logString;
    EditText logCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_log);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        logDetails = findViewById(R.id.logDetails);

        logString = (EditText) findViewById(R.id.logString);
        logCost = (EditText) findViewById(R.id.logCost);

        sharedPreferences = this.getSharedPreferences("com.androidandyuk.bikersbestfriend", Context.MODE_PRIVATE);
        ed = sharedPreferences.edit();

        loadLogs();


        //for testing purposes
        // remove once tested!
//        if (bikes.get(activeBike).maintenanceLogs.size() == 0) {
//            maintenanceLogDetails newLog = new maintenanceLogDetails("Testing");
//            bikes.get(activeBike).maintenanceLogs.add(newLog);
//        }

        initiateList();

        maintList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.i("Maint List", "Tapped " + i);
            }
        });

        maintList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int logPosition = position;
                final Context context = App.getContext();

                new AlertDialog.Builder(Maintenance.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("You're about to delete this log forever...")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("Removing", "Log " + logPosition);
                                bikes.get(activeBike).maintenanceLogs.remove(logPosition);
                                initiateList();
                                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();


                return true;
            }

        });


    }

    public void showAddLog(View view) {
        // opens the add log dialog
        Log.i("Maintenance", "Adding a log");
        logDetails.setVisibility(View.VISIBLE);
    }

    public void addLog(View view) {
        Log.i("Maintenance", "Taking details and adding");
        Double cost = 0d;

        try {
            cost = Double.parseDouble(logCost.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String logInfo = logString.getText().toString();

        // check information has been entered
        if (logInfo.isEmpty()) {

            Toast.makeText(Maintenance.this, "Please complete all necessary details", Toast.LENGTH_LONG).show();

        } else {

            maintenanceLogDetails today = new maintenanceLogDetails(logInfo, cost);
            bikes.get(activeBike).maintenanceLogs.add(today);
            Collections.sort(bikes.get(activeBike).maintenanceLogs);
            arrayAdapter.notifyDataSetChanged();
            logDetails.setVisibility(View.INVISIBLE);

            logString.setText(null);
            logString.clearFocus();
            logCost.setText(null);
            logCost.clearFocus();

            // Check if no view has focus:
            View logDetails = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
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

    private void initiateList() {
        maintList = (ListView) findViewById(R.id.maintList);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, bikes.get(activeBike).maintenanceLogs);

        maintList.setAdapter(arrayAdapter);

        setTitle("Maintenance: " + bikes.get(activeBike).model);
    }

    public static double calculateMaintSpend(Bike bike){
        Log.i("Garage","Calculating Spend on " + bike);
        Log.i("Number of logs","" + bike.maintenanceLogs.size());
        double spend = 0;
        for(maintenanceLogDetails log : bike.maintenanceLogs){
            Log.i("Price","" + log.price);
            spend += log.price;
        }
        return spend;
    }

    public static void saveLogs() {

        for (Bike thisBike : bikes) {

            Log.i("Saving Logs", "" + thisBike);
            try {
                ArrayList<String> dates = new ArrayList<>();
                ArrayList<String> logs = new ArrayList<>();
                ArrayList<String> costs = new ArrayList<>();

                // I think these are new variables, so likely don't need clearing?
                dates.clear();
                logs.clear();
                costs.clear();

                for (maintenanceLogDetails thisLog : thisBike.maintenanceLogs) {

                    dates.add(thisLog.date);
                    logs.add(thisLog.log);
                    costs.add(Double.toString(thisLog.price));

                }

                Log.i("Saving Logs", "Size :" + dates.size());
                ed.putString("dates" + thisBike.bikeId, ObjectSerializer.serialize(dates)).apply();
                ed.putString("logs" + thisBike.bikeId, ObjectSerializer.serialize(logs)).apply();
                ed.putString("costs" + thisBike.bikeId, ObjectSerializer.serialize(costs)).apply();

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Adding details", "Failed attempt");
            }
        }
    }

    public static void loadLogs() {

        for (Bike thisBike : bikes) {
            thisBike.maintenanceLogs.clear();

            Log.i("Loading Logs", "" + thisBike);

            ArrayList<String> dates = new ArrayList<>();
            ArrayList<String> logs = new ArrayList<>();
            ArrayList<String> costs = new ArrayList<>();

            // I think these are new variables, so likely don't need clearing?
            dates.clear();
            logs.clear();
            costs.clear();

            try {

                dates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("dates" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                logs = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("logs" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                costs = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("costs" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                Log.i("Dates for " + thisBike, "Count :" + dates.size());
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Loading Maint Logs", "Failed attempt");
            }

            Log.i("Retrieved info" + thisBike, "Log count :" + dates.size());
            if (dates.size() > 0 && logs.size() > 0 && costs.size() > 0) {
                // we've checked there is some info
                if (dates.size() == logs.size() && logs.size() == costs.size()) {
                    // we've checked each item has the same amount of info, nothing is missing
                    for (int x = 0; x < dates.size(); x++) {
                        Date thisDate = new Date();
                        try {
                            thisDate = sdf.parse(dates.get(x));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        maintenanceLogDetails newLog = new maintenanceLogDetails(logs.get(x), Double.parseDouble(costs.get(x)), thisDate);
                        Log.i("Adding", "" + x + "" + newLog);
                        thisBike.maintenanceLogs.add(newLog);
                    }

                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Maintenance Activity", "On Pause");
        saveLogs();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Maintenance Activity", "On Stop");
        saveLogs();
    }

}
