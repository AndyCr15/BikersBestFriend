package com.androidandyuk.bikersbestfriend;

import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;

//import static com.androidandyuk.bikersbestfriend.Favourites.favouriteLocations;
import static com.androidandyuk.bikersbestfriend.Garage.activeBike;
import static com.androidandyuk.bikersbestfriend.Garage.bikes;

public class Maintenance extends AppCompatActivity {

    static ArrayAdapter arrayAdapter;
    ListView maintList;

    View logDetails;

    EditText logString;
    EditText logCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_log);

        logDetails = findViewById(R.id.logDetails);

        logString = (EditText) findViewById(R.id.logString);
        logCost = (EditText) findViewById(R.id.logCost);

        maintenanceLogDetails newLog = new maintenanceLogDetails("Testing");
        bikes.get(activeBike).maintenanceLogs.add(newLog);

        initiateList();

        maintList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.i("Maint List","Tapped " + i);
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
                                Toast.makeText(context,"Deleted!", Toast.LENGTH_SHORT).show();

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
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    public static void saveLogs() {
        Log.i("Shared Prefs", "Saving Logs");
        
        try {

            ArrayList<String> dates = new ArrayList<>();
            ArrayList<String> logs = new ArrayList<>();
            ArrayList<String> cost = new ArrayList<>();

            for (maintenanceLogDetails thisLog : maintenanceLogs) {
                names.add(location.name);
                latitudes.add(Double.toString(location.location.latitude));
                longitudes.add(Double.toString(location.location.longitude));
                addresses.add(location.address);
                comments.add(location.comment);
            }

            sharedPreferences.edit().putString("names", ObjectSerializer.serialize(names)).apply();
            sharedPreferences.edit().putString("latitudes", ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("longitudes", ObjectSerializer.serialize(longitudes)).apply();
            sharedPreferences.edit().putString("addresses", ObjectSerializer.serialize(addresses)).apply();
            sharedPreferences.edit().putString("comments", ObjectSerializer.serialize(comments)).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadLogs() {
        Log.i("Shared Prefs", "Loading Favs");

        favouriteLocations.clear();

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();
        ArrayList<String> addresses = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();

        names.clear();
        latitudes.clear();
        longitudes.clear();
        addresses.clear();
        comments.clear();

        try {

            names = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("names", ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitudes", ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitudes", ObjectSerializer.serialize(new ArrayList<String>())));
            addresses = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("addresses", ObjectSerializer.serialize(new ArrayList<String>())));
            comments = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("comments", ObjectSerializer.serialize(new ArrayList<String>())));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (names.size() > 0 && latitudes.size() > 0 && longitudes.size() > 0) {
            // we've checked there is some info
            if (names.size() == latitudes.size() && latitudes.size() == longitudes.size()) {
                // we've checked each item has the same amout of info, nothing is missing

                for (int i = 0; i < names.size(); i++) {
                    LatLng pos = new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(longitudes.get(i)));
                    markedLocation newLoc = new markedLocation(names.get(i), addresses.get(i), pos, comments.get(i));
                    favouriteLocations.add(newLoc);
                }

            }
        }


    }


}
