package com.androidandyuk.bikersbestfriend;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.androidandyuk.bikersbestfriend.MainActivity.activeBike;
import static com.androidandyuk.bikersbestfriend.MainActivity.bikes;
import static com.androidandyuk.bikersbestfriend.MainActivity.ed;
import static com.androidandyuk.bikersbestfriend.MainActivity.sdf;
import static com.androidandyuk.bikersbestfriend.MainActivity.sharedPreferences;

public class Maintenance extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    static MyMaintenanceAdapter myAdapter;
    ListView maintList;

    TextView setLogDate;

    String searchItem = "";

    private DatePickerDialog.OnDateSetListener logDateSetListener;

    // used to store what item might be being edited or deleted
    static int itemLongPressedPosition = 0;
    static maintenanceLogDetails itemLongPressed = null;
    String editDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // until I implement landscape view, lock the orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sharedPreferences = this.getSharedPreferences("com.androidandyuk.bikersbestfriend", Context.MODE_PRIVATE);
        ed = sharedPreferences.edit();

        loadLogs();
        initiateList();

        maintList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                itemLongPressedPosition = position;
                itemLongPressed = bikes.get(activeBike).maintenanceLogs.get(position);
                Log.i("Maint List", "Tapped " + position);

                Intent intent = new Intent(getApplicationContext(), MaintenanceLog.class);
                startActivity(intent);

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

        logDateSetListener = new DatePickerDialog.OnDateSetListener()

        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar date = Calendar.getInstance();
                date.set(year, month, day);
                String sdfDate = sdf.format(date.getTime());
                Log.i("Chosen Date", sdfDate);
                setLogDate.setText(sdfDate);
            }
        };

    }

    public void setSearch(View view) {
        EditText search = (EditText) findViewById(R.id.searchBox);

        searchItem = search.getText().toString();

        Log.i("Search for", searchItem);

        initiateList();
    }

    public void showAddLog(View view) {
        Intent intent = new Intent(getApplicationContext(), MaintenanceLog.class);
        startActivity(intent);
    }

    public void setLogDate(View view) {
        String thisDateString = "";
        // this sets what date will show when the date picker shows
        // first check if we're editing a current fueling
        if (itemLongPressed != null) {
            thisDateString = bikes.get(activeBike).maintenanceLogs.get(itemLongPressedPosition).getDate();
        }
        Date thisDate = new Date();
        try {
            thisDate = sdf.parse(thisDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // for some reason I can't getYear from thisDate, so will just use the current year
        Calendar cal = Calendar.getInstance();
        cal.setTime(thisDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                Maintenance.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                logDateSetListener,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bike_choice, menu);

        super.onCreateOptionsMenu(menu);

        menu.add(0, 0, 0, "Settings").setShortcut('3', 'c');

        for (int i = 0; i < bikes.size(); i++) {
            String bikeMakeMenu = bikes.get(i).model;
            menu.add(0, i + 1, 0, bikeMakeMenu).setShortcut('3', 'c');
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                Log.i("Option", "0");
                Toast.makeText(Maintenance.this, "Settings not yet implemented", Toast.LENGTH_LONG).show();
                return true;
            case 1:
                Log.i("Option", "1");
                activeBike = 0;
                initiateList();
                return true;
            case 2:
                Log.i("Option", "2");
                activeBike = 1;
                initiateList();
                return true;
            case 3:
                Log.i("Option", "3");
                activeBike = 2;
                initiateList();
                return true;
            case 4:
                Log.i("Option", "4");
                activeBike = 3;
                initiateList();
                return true;
            case 5:
                Log.i("Option", "5");
                activeBike = 4;
                initiateList();
                return true;
            case 6:
                Log.i("Option", "6");
                activeBike = 5;
                initiateList();
                return true;
            case 7:
                Log.i("Option", "7");
                activeBike = 6;
                initiateList();
                return true;
            case 8:
                Log.i("Option", "8");
                activeBike = 7;
                initiateList();
                return true;
            case 9:
                Log.i("Option", "9");
                activeBike = 8;
                initiateList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initiateList() {
        maintList = (ListView) findViewById(R.id.maintList);

        myAdapter = new MyMaintenanceAdapter(bikes.get(activeBike).maintenanceLogs);

        maintList.setAdapter(myAdapter);


        setTitle("Maintenance: " + bikes.get(activeBike).model);
    }

    public class MyMaintenanceAdapter extends BaseAdapter {
        public ArrayList<maintenanceLogDetails> maintDataAdapter;

        public MyMaintenanceAdapter(ArrayList<maintenanceLogDetails> maintDataAdapter) {
            this.maintDataAdapter = maintDataAdapter;
        }

        @Override
        public int getCount() {
            return maintDataAdapter.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final maintenanceLogDetails s = maintDataAdapter.get(position);

            if (searchItem == "" || s.log.toLowerCase().contains(searchItem.toLowerCase())) {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.maintenance_listview, null);

                TextView maintenanceListDate = (TextView) myView.findViewById(R.id.maintenanceListDate);
                maintenanceListDate.setText(s.date);

                TextView maintenanceListMileage = (TextView) myView.findViewById(R.id.maintenanceListMileage);
                maintenanceListMileage.setText("Mi:" + Double.toString(s.mileage));

                TextView maintenanceListLog = (TextView) myView.findViewById(R.id.maintenanceListLog);
                maintenanceListLog.setText(s.log);

                TextView maintenanceListCost = (TextView) myView.findViewById(R.id.maintenanceListCost);
                maintenanceListCost.setText("£" + Double.toString(s.price));

                return myView;
            } else {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.blank, null);
                return myView;
            }
        }
    }


    public static void saveLogs() {

        for (Bike thisBike : bikes) {

            ArrayList<String> dates = new ArrayList<>();
            ArrayList<String> logs = new ArrayList<>();
            ArrayList<String> costs = new ArrayList<>();
            ArrayList<String> mileage = new ArrayList<>();
            ArrayList<String> wasService = new ArrayList<>();
            ArrayList<String> wasMOT = new ArrayList<>();
            ArrayList<String> brakePads = new ArrayList<>();
            ArrayList<String> brakeDiscs = new ArrayList<>();
            ArrayList<String> frontTyre = new ArrayList<>();
            ArrayList<String> rearTyre = new ArrayList<>();
            ArrayList<String> oilChange = new ArrayList<>();
            ArrayList<String> newBattery = new ArrayList<>();
            ArrayList<String> coolantChange = new ArrayList<>();
            ArrayList<String> sparkPlugs = new ArrayList<>();
            ArrayList<String> airFilter = new ArrayList<>();
            ArrayList<String> brakeFluid = new ArrayList<>();

            Log.i("Saving Logs", "" + thisBike);
            try {


//                // I think these are new variables, so likely don't need clearing?
//                dates.clear();
//                logs.clear();
//                costs.clear();
//                wasService.clear();
//                wasMOT.clear();

                for (maintenanceLogDetails thisLog : thisBike.maintenanceLogs) {

                    dates.add(thisLog.date);
                    logs.add(thisLog.log);
                    costs.add(Double.toString(thisLog.price));
                    mileage.add(Double.toString(thisLog.mileage));
                    wasService.add(String.valueOf(thisLog.wasService));
                    wasMOT.add(String.valueOf(thisLog.wasMOT));
                    brakePads.add(String.valueOf(thisLog.brakePads));
                    brakeDiscs.add(String.valueOf(thisLog.brakeDiscs));
                    frontTyre.add(String.valueOf(thisLog.frontTyre));
                    rearTyre.add(String.valueOf(thisLog.rearTyre));
                    oilChange.add(String.valueOf(thisLog.oilChange));
                    newBattery.add(String.valueOf(thisLog.newBattery));
                    coolantChange.add(String.valueOf(thisLog.coolantChange));
                    sparkPlugs.add(String.valueOf(thisLog.sparkPlugs));
                    airFilter.add(String.valueOf(thisLog.airFilter));
                    brakeFluid.add(String.valueOf(thisLog.brakeFluid));

                }

                ed.putString("dates" + thisBike.bikeId, ObjectSerializer.serialize(dates)).apply();
                ed.putString("logs" + thisBike.bikeId, ObjectSerializer.serialize(logs)).apply();
                ed.putString("costs" + thisBike.bikeId, ObjectSerializer.serialize(costs)).apply();
                ed.putString("mileage" + thisBike.bikeId, ObjectSerializer.serialize(mileage)).apply();
                ed.putString("wasService" + thisBike.bikeId, ObjectSerializer.serialize(wasService)).apply();
                ed.putString("wasMOT" + thisBike.bikeId, ObjectSerializer.serialize(wasMOT)).apply();
                ed.putString("brakePads" + thisBike.bikeId, ObjectSerializer.serialize(brakePads)).apply();
                ed.putString("brakeDiscs" + thisBike.bikeId, ObjectSerializer.serialize(brakeDiscs)).apply();
                ed.putString("frontTyre" + thisBike.bikeId, ObjectSerializer.serialize(frontTyre)).apply();
                ed.putString("rearTyre" + thisBike.bikeId, ObjectSerializer.serialize(rearTyre)).apply();
                ed.putString("oilChange" + thisBike.bikeId, ObjectSerializer.serialize(oilChange)).apply();
                ed.putString("newBattery" + thisBike.bikeId, ObjectSerializer.serialize(newBattery)).apply();
                ed.putString("coolantChange" + thisBike.bikeId, ObjectSerializer.serialize(coolantChange)).apply();
                ed.putString("sparkPlugs" + thisBike.bikeId, ObjectSerializer.serialize(sparkPlugs)).apply();
                ed.putString("airFilter" + thisBike.bikeId, ObjectSerializer.serialize(airFilter)).apply();
                ed.putString("brakeFluid" + thisBike.bikeId, ObjectSerializer.serialize(brakeFluid)).apply();

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Adding details", "Failed attempt");
            }
        }
    }

    public static void loadLogs() {

        for (Bike thisBike : bikes) {
            thisBike.maintenanceLogs.clear();

            ArrayList<String> dates = new ArrayList<>();
            ArrayList<String> logs = new ArrayList<>();
            ArrayList<String> costs = new ArrayList<>();
            ArrayList<String> mileage = new ArrayList<>();
            ArrayList<String> wasService = new ArrayList<>();
            ArrayList<String> wasMOT = new ArrayList<>();
            ArrayList<String> brakePads = new ArrayList<>();
            ArrayList<String> brakeDiscs = new ArrayList<>();
            ArrayList<String> frontTyre = new ArrayList<>();
            ArrayList<String> rearTyre = new ArrayList<>();
            ArrayList<String> oilChange = new ArrayList<>();
            ArrayList<String> newBattery = new ArrayList<>();
            ArrayList<String> coolantChange = new ArrayList<>();
            ArrayList<String> sparkPlugs = new ArrayList<>();
            ArrayList<String> airFilter = new ArrayList<>();
            ArrayList<String> brakeFluid = new ArrayList<>();

            Log.i("Loading Logs", "" + thisBike);

            // I think these are new variables, so likely don't need clearing?
//            dates.clear();
//            logs.clear();
//            costs.clear();
//            wasService.clear();
//            wasMOT.clear();

            try {

                dates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("dates" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                logs = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("logs" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                costs = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("costs" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                mileage = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("mileage" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                wasService = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("wasService" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                wasMOT = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("wasMOT" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                brakePads = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("brakePads" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                brakeDiscs = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("brakeDiscs" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                frontTyre = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("frontTyre" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                rearTyre = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("rearTyre" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                oilChange = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("oilChange" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                newBattery = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("newBattery" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                coolantChange = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("coolantChange" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                sparkPlugs = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("sparkPlugs" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                airFilter = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("airFilter" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                brakeFluid = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("brakeFluid" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Loading Maint Logs", "Failed attempt");
            }

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
                        Log.i("Trying to Add", "" + x);

                        Boolean wasItAService = Boolean.valueOf(wasService.get(x));
                        Boolean wasItAMOT = Boolean.valueOf(wasMOT.get(x));
                        Boolean wasBrakePads = Boolean.valueOf(brakePads.get(x));
                        Boolean wasBrakeDiscs = Boolean.valueOf(brakeDiscs.get(x));
                        Boolean wasFrontTyre = Boolean.valueOf(frontTyre.get(x));
                        Boolean wasRearTyre = Boolean.valueOf(rearTyre.get(x));
                        Boolean wasOilChange = Boolean.valueOf(oilChange.get(x));
                        Boolean wasNewBattery = Boolean.valueOf(newBattery.get(x));
                        Boolean wasCoolantChange = Boolean.valueOf(coolantChange.get(x));
                        Boolean wasSparkPlugs = Boolean.valueOf(sparkPlugs.get(x));
                        Boolean wasAirFilter = Boolean.valueOf(airFilter.get(x));
                        Boolean wasBrakeFluid = Boolean.valueOf(brakeFluid.get(x));

                        maintenanceLogDetails newLog = new maintenanceLogDetails(thisDate, logs.get(x), Double.parseDouble(costs.get(x)), Double.parseDouble(mileage.get(x)), wasItAService, wasItAMOT, wasBrakePads
                                , wasBrakeDiscs, wasFrontTyre, wasRearTyre, wasOilChange, wasNewBattery, wasCoolantChange, wasSparkPlugs, wasAirFilter, wasBrakeFluid);

                        Log.i("Added", "" + x + "" + newLog);
                        thisBike.maintenanceLogs.add(newLog);
                    }

                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // this must be empty as back is being dealt with in onKeyDown
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            finish();
            return true;
        }
        myAdapter.notifyDataSetChanged();
        return super.onKeyDown(keyCode, event);
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