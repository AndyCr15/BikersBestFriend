package com.androidandyuk.bikersbestfriend;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.androidandyuk.bikersbestfriend.MainActivity.activeBike;
import static com.androidandyuk.bikersbestfriend.MainActivity.backgroundsWanted;
import static com.androidandyuk.bikersbestfriend.MainActivity.bikes;
import static com.androidandyuk.bikersbestfriend.MainActivity.checkUpdate;
import static com.androidandyuk.bikersbestfriend.MainActivity.currencySetting;
import static com.androidandyuk.bikersbestfriend.MainActivity.ed;
import static com.androidandyuk.bikersbestfriend.MainActivity.incBikeEvents;
import static com.androidandyuk.bikersbestfriend.MainActivity.incCarEvents;
import static com.androidandyuk.bikersbestfriend.MainActivity.lastHowManyFuels;
import static com.androidandyuk.bikersbestfriend.MainActivity.loadBikes;
import static com.androidandyuk.bikersbestfriend.MainActivity.locationUpdatesTime;
import static com.androidandyuk.bikersbestfriend.MainActivity.milesSetting;
import static com.androidandyuk.bikersbestfriend.MainActivity.saveSettings;
import static com.androidandyuk.bikersbestfriend.MainActivity.sdf;

public class Settings extends AppCompatActivity {

    public static RelativeLayout main;

    TextView locationUpdatesTimeTV;
    TextView lastHowManyFuelsTV;

    Switch incCarShows;
    Switch incBikeShows;
    Switch backgroundsWantedSW;

    Spinner currencySpinner;
    Spinner milesSpinner;

    View getDetails;
    public static String tag;
    public static String details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currencySpinner = (Spinner) findViewById(R.id.currencySpinner);
        currencySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Currency.values()));

        switch (currencySetting) {
            case "£":
                currencySpinner.setSelection(0);
                break;
            case "$":
                currencySpinner.setSelection(1);
                break;
            case "€":
                currencySpinner.setSelection(2);
                break;
        }


        milesSpinner = (Spinner) findViewById(R.id.milesSpinner);
        milesSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MilesKM.values()));

        switch (milesSetting) {
            case "Miles":
                milesSpinner.setSelection(0);
                break;
            case "Km":
                milesSpinner.setSelection(1);
                break;
        }

        lastHowManyFuelsTV = (TextView) findViewById(R.id.numberFuels);
        lastHowManyFuelsTV.setText(Integer.toString(lastHowManyFuels));

        locationUpdatesTimeTV = (TextView) findViewById(R.id.minutesBetween);
        locationUpdatesTimeTV.setText(Integer.toString(locationUpdatesTime / 60000));

        incCarShows = (Switch) findViewById(R.id.incCarShows);
        incCarShows.setChecked(incCarEvents);

        incBikeShows = (Switch) findViewById(R.id.incBikeShows);
        incBikeShows.setChecked(incBikeEvents);

        backgroundsWantedSW = (Switch) findViewById(R.id.backgroundsWanted);
        backgroundsWantedSW.setChecked(backgroundsWanted);
    }

    public void getDetailsClicked(View view) {
        tag = view.getTag().toString();
        getDetails(tag);
    }

    public void checkDetails() {
        Log.i("Checking Details", details);
        switch (tag) {
            case "fuels":
                try {
                    lastHowManyFuels = Integer.parseInt(details);
                    lastHowManyFuelsTV = (TextView) findViewById(R.id.numberFuels);
                    lastHowManyFuelsTV.setText(details);
                    ed.putInt("lastHowManyFuels", Integer.parseInt(details)).apply();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Not a valid entry", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case "minutes":
                try {
                    locationUpdatesTime = Integer.parseInt(details) * 60000;
                    locationUpdatesTimeTV = (TextView) findViewById(R.id.minutesBetween);
                    locationUpdatesTimeTV.setText(details);
                    ed.putInt("locationUpdatesTime", Integer.parseInt(details)).apply();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Not a valid entry", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case "fuels url":
                new MyAsyncTaskFuels().execute(details);
                break;
            case "maintenance url":
                new MyAsyncTaskMaint().execute(details);
                break;
        }
    }

    public void getDetails(String hint) {
        Log.i("Get Details", hint);
        getDetails = findViewById(R.id.getDetails);
        getDetails.setVisibility(View.VISIBLE);
        final EditText thisET = (EditText) findViewById(R.id.getDetailsText);
        thisET.setHint(hint);

        thisET.setFocusableInTouchMode(true);
        thisET.requestFocus();

        thisET.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    details = thisET.getText().toString();
                    Log.i("Details", details);
                    getDetails.setVisibility(View.INVISIBLE);
                    thisET.setText(null);
                    checkDetails();
                    return true;
                }
                return false;
            }
        });
    }

    public void submitPressed(View view){
        Log.i("submitPressed","Started");
        getDetails = findViewById(R.id.getDetails);
        EditText thisET = (EditText) findViewById(R.id.getDetailsText);
        details = thisET.getText().toString();
        Log.i("Details", details);
        getDetails.setVisibility(View.INVISIBLE);
        thisET.setText(null);
        checkDetails();
    }

//    public void getDetailsMins(String hint) {
//        Log.i("Get Details", hint);
////        final String[] detail = new String[1];
//        final View getDetails = findViewById(getDetails);
//        getDetails.setVisibility(View.VISIBLE);
//        final EditText thisET = (EditText) findViewById(R.id.getDetailsText);
//        thisET.setHint(hint);
//
//        thisET.setFocusableInTouchMode(true);
//        thisET.requestFocus();
//
//        thisET.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                // If the event is a key-down event on the "enter" button
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    // Perform action on key press
//                    String details = thisET.getText().toString().toUpperCase();
//                    Log.i("Details", details);
//                    getDetails.setVisibility(View.INVISIBLE);
//
//                    locationUpdatesTime = Integer.parseInt(details) * 60000;
//                    locationUpdatesTimeTV = (TextView) findViewById(R.id.minutesBetween);
//                    locationUpdatesTimeTV.setText(details);
//                    ed.putInt("locationUpdatesTime", Integer.parseInt(details)).apply();
//
//                    return true;
//                }
//                return false;
//            }
//        });
//    }

    public void exportDB(View view) {
        Log.i("exportDB", "Starting");
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;

        File dir = new File(Environment.getExternalStorageDirectory()+"/AutoBuddy/");
//        Log.i("dir is ", "" + dir);
//        dir.mkdir();
        try{
            if(dir.mkdir()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Directory is not created");
            }
        }catch(Exception e){
            e.printStackTrace();
            Log.i("Creating Dir Error", "" + e);
        }

        String currentDBPath = "/data/com.androidandyuk.autobuddy/databases/Vehicles";
        String backupDBPath = "AutoBuddy/Vehicles.db";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Exported Failed!", Toast.LENGTH_LONG).show();
        }
    }

    public void importDB(View view) {
        Log.i("ImportDB", "Started");
        try {
            String DB_PATH = "/data/data/com.androidandyuk.autobuddy/databases/Vehicles";

            File sdcard = Environment.getExternalStorageDirectory();
            String yourDbFileNamePresentInSDCard = sdcard.getAbsolutePath() + File.separator + "AutoBuddy/Vehicles.db";

            Log.i("ImportDB", "SDCard File " + yourDbFileNamePresentInSDCard);

            File file = new File(yourDbFileNamePresentInSDCard);
            // Open your local db as the input stream
            InputStream myInput = new FileInputStream(file);

            // Path to created empty db
            String outFileName = DB_PATH;

            // Opened assets database structure
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            Log.i("ImportDB", "Exception Caught" + e);
        }
        loadBikes();
        Fuelling.loadFuels();
        Maintenance.loadLogs();
        ToDo.loadToDos();
        Toast.makeText(this, "Data Imported. Close app and reopen", Toast.LENGTH_LONG).show();
        if (bikes.size() > 0) {
            activeBike = 0;
        }
    }

    public void goToAbout(View view) {
        // go to about me
        Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(intent);
    }

    // get fuels from fuelio
    public class MyAsyncTaskFuels extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //before works
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.i("Import Fuels", "doInBackground");
                String NewsData;
                //define the url we have to connect with
                URL url = new URL(params[0]);
                //make connect with url and send request
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //waiting for 7000ms for response
                urlConnection.setConnectTimeout(15000);//set timeout to 15 seconds

                try {
                    //getting the response data
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    //convert the stream to string
                    NewsData = ConvertInputToStringNoChange(in);
                    //send to display data
                    publishProgress(NewsData);
                } finally {
                    //end connection
                    urlConnection.disconnect();
                }

            } catch (Exception ex) {
                Log.i("Exception Caught ", "" + ex);
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            try {
                Log.i("Car Shows", "Getting JSON");
                JSONArray json = new JSONArray(progress[0]);
                Log.i("JSON size", "" + json.length());

                bikes.get(activeBike).fuelings.clear();

                for (int i = json.length() - 2; i >= 0; i--) {
                    JSONObject thisFuel = json.getJSONObject(i);
                    JSONObject lastFuel = json.getJSONObject(i + 1);

                    String startDate = thisFuel.getString("Data");
                    String[] parts = startDate.split("-");
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1])-1;
                    int day = Integer.parseInt(parts[2]);
                    GregorianCalendar cal = new GregorianCalendar();
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, day);
                    Date theDate = cal.getTime();

                    String date = sdf.format(theDate);

                    Double mileage = Double.parseDouble(thisFuel.getString("Odo (mi)"));
                    Double miles = mileage - Double.parseDouble(lastFuel.getString("Odo (mi)"));
                    Double litres = Double.parseDouble(lastFuel.getString("Fuel (litres)"));
                    Double price = Double.parseDouble(thisFuel.getString("VolumePrice"));
                    Log.i("Adding notes ", date + " " + miles + " " + mileage + " " + litres + " " + price);
                    fuellingDetails theseDetails = new fuellingDetails(miles, price, litres, date, mileage);
                    bikes.get(activeBike).fuelings.add(theseDetails);
                }
            } catch (Exception ex) {
                Log.i("JSON failed", "" + ex);
            }

            Collections.sort(bikes.get(activeBike).fuelings);
            Fuelling.saveFuels();
        }

        protected void onPostExecute(String result2) {
            checkUpdate();
        }

    }

    // get fuels from fuelio
    public class MyAsyncTaskMaint extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //before works
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.i("Import Fuels", "doInBackground");
                String NewsData;
                //define the url we have to connect with
                URL url = new URL(params[0]);
                //make connect with url and send request
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //waiting for 7000ms for response
                urlConnection.setConnectTimeout(15000);//set timeout to 15 seconds

                try {
                    //getting the response data
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    //convert the stream to string
                    NewsData = ConvertInputToStringNoChange(in);
                    //send to display data
                    publishProgress(NewsData);
                } finally {
                    //end connection
                    urlConnection.disconnect();
                }

            } catch (Exception ex) {
                Log.i("Exception Caught ", "" + ex);
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            try {
                Log.i("Importing Maintenance", "Getting JSON");
                JSONArray json = new JSONArray(progress[0]);
                Log.i("JSON size", "" + json.length());

                bikes.get(activeBike).maintenanceLogs.clear();

                for (int i = 0; i<json.length(); i++) {
                    JSONObject thisCost = json.getJSONObject(i);

                    String startDate = thisCost.getString("Date");
                    String[] parts = startDate.split("-");
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1])-1;
                    int day = Integer.parseInt(parts[2]);
                    GregorianCalendar cal = new GregorianCalendar();
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, day);
                    Date theDate = cal.getTime();
                    String date = sdf.format(theDate);

                    Double mileage = Double.parseDouble(thisCost.getString("Odo"));
                    String title = thisCost.getString("CostTitle");
                    String notes = thisCost.getString("Notes");
                    Double cost = Double.parseDouble(thisCost.getString("Cost"));

                    String fullNotes = "** " + title + " ** : " + notes;
                    Log.i("Adding maintenance ", date + " " + fullNotes +" " + mileage + " " + cost);
                    maintenanceLogDetails theseDetails = new maintenanceLogDetails(date, fullNotes, cost, mileage);
                    bikes.get(activeBike).maintenanceLogs.add(theseDetails);
                }
            } catch (Exception ex) {
                Log.i("JSON failed", "" + ex);
            }
            Collections.sort(bikes.get(activeBike).maintenanceLogs);
            Maintenance.saveLogs();
        }

        protected void onPostExecute(String result2) {
            checkUpdate();
        }

    }

    // this method convert any stream to string
    public static String ConvertInputToStringNoChange(InputStream inputStream) {

        BufferedReader bureader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String linereultcal = "";

        try {
            while ((line = bureader.readLine()) != null) {

                linereultcal += line;

            }
            inputStream.close();


        } catch (Exception ex) {
        }

        return linereultcal;
    }

    public void checkBackground() {
        main = (RelativeLayout) findViewById(R.id.main);
        if (backgroundsWanted) {
            int resID = getResources().getIdentifier("background_portrait", "drawable", this.getPackageName());
            Drawable drawablePic = getResources().getDrawable(resID);
            Settings.main.setBackground(drawablePic);
        } else {
            Settings.main.setBackgroundColor(getResources().getColor(R.color.background));
        }
    }

    @Override
    public void onBackPressed() {
        // this must be empty as back is being dealt with in onKeyDown
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getDetails = findViewById(R.id.getDetails);
            if (getDetails.isShown()) {
                getDetails.setVisibility(View.INVISIBLE);
            } else {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Settings Activity", "On Pause");
        incCarEvents = incCarShows.isChecked();
        incBikeEvents = incBikeShows.isChecked();
        backgroundsWanted = backgroundsWantedSW.isChecked();

        currencySpinner = (Spinner) findViewById(R.id.currencySpinner);
        currencySetting = currencySpinner.getSelectedItem().toString();
        milesSpinner = (Spinner) findViewById(R.id.milesSpinner);
        milesSetting = milesSpinner.getSelectedItem().toString();
        saveSettings();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Settings Activity", "On Stop");
//        saveLogs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBackground();
    }
}
