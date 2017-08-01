package com.androidandyuk.bikersbestfriend;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.androidandyuk.bikersbestfriend.CarShows.loadShows;
import static com.androidandyuk.bikersbestfriend.Fuelling.loadFuels;
import static com.androidandyuk.bikersbestfriend.Fuelling.loadFuelsOld;
import static com.androidandyuk.bikersbestfriend.Fuelling.saveFuels;
import static com.androidandyuk.bikersbestfriend.Maintenance.loadLogs;
import static com.androidandyuk.bikersbestfriend.Maintenance.loadLogsOld;
import static com.androidandyuk.bikersbestfriend.Maintenance.saveLogs;
import static com.androidandyuk.bikersbestfriend.ToDo.loadToDos;
import static com.androidandyuk.bikersbestfriend.ToDo.loadToDosOld;
import static com.androidandyuk.bikersbestfriend.ToDo.saveToDos;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor ed;

    public static ArrayList<Bike> bikes = new ArrayList<>();

    private FirebaseAnalytics mFirebaseAnalytics;

    public static LocationManager locationManager;
    public static LocationListener locationListener;

    public static RelativeLayout main;

    public static LatLng userLatLng;
    public static JSONObject jsonObject;
    public static TextView weatherText;
    public static String currentForecast;
    public static int warningDays = 30;
    public static String currencySetting;
    public static String milesSetting;

    public static boolean updateNeeded;

    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");

    // to store if the user has given permission to storage and location
    public static boolean storageAccepted;
    public static boolean locationAccepted;

    static markedLocation user;
    static double conversion = 0.621;
    static Geocoder geocoder;

    public static int activeBike;

    public static int locationUpdatesTime;
    public static int lastHowManyFuels;
    public static boolean incCarEvents;
    public static boolean incBikeEvents;
    public static boolean backgroundsWanted;

    public static SQLiteDatabase vehiclesDB;

    public static String userLocationForWeather;

    public static final DecimalFormat precision = new DecimalFormat("0.00");
    public static final DecimalFormat oneDecimal = new DecimalFormat("0.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("Main Activity", "onCreate");

        // until I implement landscape view, lock the orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        sharedPreferences = this.getSharedPreferences("com.androidandyuk.autobuddy", Context.MODE_PRIVATE);
        ed = sharedPreferences.edit();


        vehiclesDB = this.openOrCreateDatabase("Vehicles", MODE_PRIVATE, null);

        vehiclesDB.execSQL("CREATE TABLE IF NOT EXISTS vehicles (make VARCHAR, model VARCHAR, reg VARCHAR, bikeId VARCHAR, VIN VARCHAR, serviceDue VARCHAR, MOTdue VARCHAR" +
                ", lastKnownService VARCHAR, lastKnownMOT VARCHAR, yearOfMan VARCHAR, notes VARCHAR, estMileage VARCHAR, MOTwarned VARCHAR, serviceWarned VARCHAR, taxDue VARCHAR)");

        loadSettings();

        // requesting permissions to access storage and location
        Log.i("storageAccepted " + storageAccepted, "locationAccepted " + locationAccepted);
        if (!storageAccepted || !locationAccepted) {
            String[] perms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION"};
            int permsRequestCode = 200;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(perms, permsRequestCode);
            }
        }

        checkUpdate();

        Log.i("updateNeeded", "" + updateNeeded);
        if (updateNeeded) {
            loadBikesOld();
            loadFuelsOld();
            loadLogsOld();
            loadToDosOld();
            saveFuels();
            saveLogs();
            saveToDos();
            getApplicationContext().getSharedPreferences("CREDENTIALS", 0).edit().clear().commit();
            updateNeeded = false;
            ed.putBoolean("updateNeeded3", false).apply();
            saveBikes();
            loadShows();
            activeBike = 0;
        } else {
            loadBikes();
            loadFuels();
            loadLogs();
            loadToDos();
            loadShows();
        }
        checkMOTwarning();
        checkServiceWarning();

        // check if there are any bikes
        if (bikes.size() == 0) {
            activeBike = -1;
        }

        //      download the weather
        weatherText = (TextView) findViewById(R.id.weatherView);
        DownloadTask task = new DownloadTask();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //centerMapOnLocation(location, "Your location");

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        userLatLng = new LatLng(51.5412794, -0.2799549);  //  default to Ace Cafe until location is overwritten

        user = new markedLocation("You", "", userLatLng, "");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10800000, 1000, locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Log.i("lastKnownLocation", "" + lastKnownLocation);
            if (lastKnownLocation != null) {
                user.setLocation(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
            }
        }

        if (user.location != null) {
            //change this to be users location
            double userLat = user.location.latitude;
            double userLon = user.location.longitude;
            String userLocation = "lat=" + userLat + "&lon=" + userLon;
            userLocationForWeather = "http://api.openweathermap.org/data/2.5/weather?" + userLocation + "&APPID=81e5e0ca31ad432ee9153dd761ed3b27";
            Log.i("Getting Weather", userLocationForWeather);
            task.execute(userLocationForWeather);

        }

        Favourites.loadFavs();
        Favourites.sortMyList();

        changeHeader();

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

        switch (permsRequestCode) {

            case 200:

                storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                locationAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                Log.i("PERMS storageAccepted " + storageAccepted, "locationAccepted " + locationAccepted);
                ed.putBoolean("locationAccepted", locationAccepted).apply();
                ed.putBoolean("storageAccepted", storageAccepted).apply();

                break;

        }

    }

    public void changeHeader() {
        TextView selectedVehicle = (TextView) findViewById(R.id.selectedVehicle);
        if (activeBike > -1) {
            selectedVehicle.setText(bikes.get(activeBike).yearOfMan + " " + bikes.get(activeBike).model);
        }
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
        Intent intent;
        switch (item.getItemId()) {
            case 0:
                Log.i("Option", "0");
                intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
//                Toast.makeText(MainActivity.this, "Settings not yet implemented", Toast.LENGTH_LONG).show();
                return true;
            case 1:
                Log.i("Option", "1");
                activeBike = 0;
                changeHeader();
                return true;
            case 2:
                Log.i("Option", "2");
                activeBike = 1;
                changeHeader();
                return true;
            case 3:
                Log.i("Option", "3");
                activeBike = 2;
                changeHeader();
                return true;
            case 4:
                Log.i("Option", "4");
                activeBike = 3;
                changeHeader();
                return true;
            case 5:
                Log.i("Option", "5");
                activeBike = 4;
                changeHeader();
                return true;
            case 6:
                Log.i("Option", "6");
                activeBike = 5;
                changeHeader();
                return true;
            case 7:
                Log.i("Option", "7");
                activeBike = 6;
                changeHeader();
                return true;
            case 8:
                Log.i("Option", "8");
                activeBike = 7;
                changeHeader();
                return true;
            case 9:
                Log.i("Option", "9");
                activeBike = 8;
                changeHeader();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static boolean checkInRange(String due, Calendar testDate) {
        // establish what date we're testing against
        testDate.add(Calendar.DAY_OF_YEAR, warningDays);
        // get the date this bikes MOT is due
        Calendar dueDate = new GregorianCalendar();
        try {
            dueDate.setTime(sdf.parse(due));
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i("MOT Check", "Date conversion failed");
        }

        Log.i("dueDate", "" + dueDate);
        Log.i("testDate", "" + testDate);
        if (dueDate.before(testDate)) {
            return true;
        }
        return false;
    }

    public void checkMOTwarning() {
        for (Bike thisBike : bikes) {
            Calendar testDate = new GregorianCalendar();
            if (checkInRange(thisBike.MOTdue, testDate)) {
                // this bike is within limits for a warning
                Toast.makeText(MainActivity.this, "MOT Due for " + thisBike, Toast.LENGTH_LONG).show();
                // give a notification if not had one before
                if (!thisBike.MOTwarned) {

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);

                    Notification notification = new Notification.Builder(getApplicationContext())
                            .setContentTitle("MOT Due!")
                            .setContentText("The MOT for " + thisBike + " is due within " + warningDays + " days")
                            .setContentIntent(pendingIntent)
//                            .addAction(android.R.drawable.btn_default, "Open App", pendingIntent)
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .build();

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    notificationManager.notify(1, notification);
                    thisBike.MOTwarned = true;

                }
            } else {
                thisBike.MOTwarned = false;
            }
        }
    }

    public void checkServiceWarning() {
        for (Bike thisBike : bikes) {
            Calendar testDate = new GregorianCalendar();
            if (checkInRange(thisBike.serviceDue, testDate)) {
                // this bike is within limits for a warning
                Toast.makeText(MainActivity.this, "Service Due for " + thisBike, Toast.LENGTH_LONG).show();
                // give a notification if not had one before
                if (!thisBike.serviceWarned) {

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);

                    Notification notification = new Notification.Builder(getApplicationContext())
                            .setContentTitle("Service Due!")
                            .setContentText("The Service for " + thisBike + " is due within " + warningDays + " days")
                            .setContentIntent(pendingIntent)
//                            .addAction(android.R.drawable.btn_default, "Open App", pendingIntent)
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .build();

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    notificationManager.notify(1, notification);
                    thisBike.serviceWarned = true;

                }
            } else {
                thisBike.serviceWarned = false;
            }

        }
    }

    public void goToLocations(View view) {
        Intent intent = new Intent(getApplicationContext(), Locations.class);
        startActivity(intent);
    }

    public void goToGarage(View view) {
        Intent intent = new Intent(getApplicationContext(), Garage.class);
        startActivity(intent);
    }

    public void goToAnnualReports(View view) {
        Intent intent = new Intent(getApplicationContext(), AnnualReports.class);
        startActivity(intent);
    }

    public void goToFueling(View view) {
        if (activeBike > -1) {
            Intent intent = new Intent(getApplicationContext(), Fuelling.class);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Add a bike in your Garage first", Toast.LENGTH_LONG).show();
        }
    }

    public void setAppTheme(int themeNum) {
        this.setTheme(R.style.AppTheme);
    }

    public void groupRideClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), GroupRide.class);
        startActivity(intent);
    }

    public void emergencyClicked(View view) {
        Toast.makeText(MainActivity.this, "Not yet implemented", Toast.LENGTH_LONG).show();
    }

    public void loadWeather(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bbc.co.uk/weather/"));
        startActivity(browserIntent);
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {

                    jsonObject = new JSONObject(result);

                    String weatherInfo = jsonObject.getString("weather");

                    Log.i("Weather content", weatherInfo);

                    JSONArray arr = new JSONArray(weatherInfo);

                    for (int i = 0; i < arr.length(); i++) {

                        JSONObject jsonPart = arr.getJSONObject(i);

                        Log.i("main", jsonPart.getString("main"));
                        Log.i("description", jsonPart.getString("description"));

                        currentForecast = jsonPart.getString("main");
                        weatherText.setText("Today's forecast: " + currentForecast);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    public static void checkUpdate() {
        updateNeeded = sharedPreferences.getBoolean("updateNeeded3", true);
        Log.i("updateNeeded3", " " + updateNeeded);
    }

    public void checkBackground() {
        main = (RelativeLayout) findViewById(R.id.main);
        if (backgroundsWanted) {
            int resID = getResources().getIdentifier("background_portrait", "drawable", this.getPackageName());
            Drawable drawablePic = getResources().getDrawable(resID);
            MainActivity.main.setBackground(drawablePic);
        } else {
            MainActivity.main.setBackgroundColor(getResources().getColor(R.color.background));
        }
    }

    public static void saveBikesOld() {
        Log.i("Main Activity", "New Saving Bikes");
        ed.putInt("bikeCount", Bike.bikeCount).apply();
        ed.putInt("bikesSize", bikes.size()).apply();

        ArrayList<String> make = new ArrayList<>();
        ArrayList<String> model = new ArrayList<>();
        ArrayList<String> reg = new ArrayList<>();
        ArrayList<String> bikeId = new ArrayList<>();
        ArrayList<String> VIN = new ArrayList<>();
        ArrayList<String> serviceDue = new ArrayList<>();
        ArrayList<String> MOTdue = new ArrayList<>();
        ArrayList<String> lastKnownService = new ArrayList<>();
        ArrayList<String> lastKnownMOT = new ArrayList<>();
        ArrayList<String> yearOfMan = new ArrayList<>();
        ArrayList<String> notes = new ArrayList<>();
        ArrayList<String> estMileage = new ArrayList<>();
        ArrayList<String> MOTwarned = new ArrayList<>();
        ArrayList<String> serviceWarned = new ArrayList<>();
        ArrayList<String> taxDue = new ArrayList<>();

        for (Bike thisBike : bikes) {
            Log.i("Saving Bikes", "" + thisBike);

            make.add(thisBike.make);
            model.add(thisBike.model);
            reg.add(thisBike.registration);
            bikeId.add(Integer.toString(thisBike.bikeId));
            VIN.add(thisBike.VIN);
            serviceDue.add(thisBike.serviceDue);
            MOTdue.add(thisBike.MOTdue);
            lastKnownService.add(thisBike.lastKnownService);
            lastKnownMOT.add(thisBike.lastKnownMOT);
            yearOfMan.add(thisBike.yearOfMan);
            notes.add(thisBike.notes);
            estMileage.add(Double.toString(thisBike.estMileage));
            MOTwarned.add(String.valueOf(thisBike.MOTwarned));
            serviceWarned.add(String.valueOf(thisBike.serviceWarned));
            taxDue.add(thisBike.taxDue);
        }
        Log.i("Saving Bikes", "Size :" + bikes.size());
        try {
            ed.putString("make", ObjectSerializer.serialize(make)).apply();
            ed.putString("model", ObjectSerializer.serialize(model)).apply();
            ed.putString("reg", ObjectSerializer.serialize(reg)).apply();
            ed.putString("bikeId", ObjectSerializer.serialize(bikeId)).apply();
            ed.putString("VIN", ObjectSerializer.serialize(VIN)).apply();
            ed.putString("serviceDue", ObjectSerializer.serialize(serviceDue)).apply();
            ed.putString("MOTdue", ObjectSerializer.serialize(MOTdue)).apply();
            ed.putString("lastKnownService", ObjectSerializer.serialize(lastKnownService)).apply();
            ed.putString("lastKnownMOT", ObjectSerializer.serialize(lastKnownMOT)).apply();
            ed.putString("yearOfMan", ObjectSerializer.serialize(yearOfMan)).apply();
            ed.putString("notes", ObjectSerializer.serialize(notes)).apply();
            ed.putString("estMileage", ObjectSerializer.serialize(estMileage)).apply();
            ed.putString("MOTwarned", ObjectSerializer.serialize(MOTwarned)).apply();
            ed.putString("serviceWarned", ObjectSerializer.serialize(serviceWarned)).apply();
            ed.putString("taxDue", ObjectSerializer.serialize(taxDue)).apply();
        } catch (IOException e) {
            Log.i("Adding details", "Failed attempt");
            e.printStackTrace();
        }
    }

    public static void saveBikes() {

        Log.i("Main Activity", "saveBikesDB");
        ed.putInt("bikeCount", Bike.bikeCount).apply();
        ed.putInt("bikesSize", bikes.size()).apply();

        ArrayList<String> make = new ArrayList<>();
        ArrayList<String> model = new ArrayList<>();
        ArrayList<String> reg = new ArrayList<>();
        ArrayList<String> bikeId = new ArrayList<>();
        ArrayList<String> VIN = new ArrayList<>();
        ArrayList<String> serviceDue = new ArrayList<>();
        ArrayList<String> MOTdue = new ArrayList<>();
        ArrayList<String> lastKnownService = new ArrayList<>();
        ArrayList<String> lastKnownMOT = new ArrayList<>();
        ArrayList<String> yearOfMan = new ArrayList<>();
        ArrayList<String> notes = new ArrayList<>();
        ArrayList<String> estMileage = new ArrayList<>();
        ArrayList<String> MOTwarned = new ArrayList<>();
        ArrayList<String> serviceWarned = new ArrayList<>();
        ArrayList<String> taxDue = new ArrayList<>();

        for (Bike thisBike : bikes) {
            Log.i("Saving BikesDB", "" + thisBike);

            make.add(thisBike.make);
            model.add(thisBike.model);
            reg.add(thisBike.registration);
            bikeId.add(Integer.toString(thisBike.bikeId));
            VIN.add(thisBike.VIN);
            serviceDue.add(thisBike.serviceDue);
            MOTdue.add(thisBike.MOTdue);
            lastKnownService.add(thisBike.lastKnownService);
            lastKnownMOT.add(thisBike.lastKnownMOT);
            yearOfMan.add(thisBike.yearOfMan);
            notes.add(thisBike.notes);
            estMileage.add(Double.toString(thisBike.estMileage));
            MOTwarned.add(String.valueOf(thisBike.MOTwarned));
            serviceWarned.add(String.valueOf(thisBike.serviceWarned));
            taxDue.add(thisBike.taxDue);
        }
        Log.i("saveBikesDB", "Size :" + bikes.size());

        try {


            vehiclesDB.delete("vehicles", null, null);

            vehiclesDB.execSQL("INSERT INTO vehicles (make, model, reg, bikeId, VIN, serviceDue, MOTdue, lastKnownService, lastKnownMOT, yearOfMan, notes, estMileage, MOTwarned, serviceWarned, taxDue) VALUES ('" +
                    ObjectSerializer.serialize(make) + "' , '" + ObjectSerializer.serialize(model) + "' , '" + ObjectSerializer.serialize(reg) + "' , '" +
                    ObjectSerializer.serialize(bikeId) + "' , '" + ObjectSerializer.serialize(VIN) + "' , '" + ObjectSerializer.serialize(serviceDue) + "' , '" + ObjectSerializer.serialize(MOTdue) + "' , '" +
                    ObjectSerializer.serialize(lastKnownService) + "' , '" + ObjectSerializer.serialize(lastKnownMOT) + "' , '" + ObjectSerializer.serialize(yearOfMan) + "' , '" + ObjectSerializer.serialize(notes) + "' , '" +
                    ObjectSerializer.serialize(estMileage) + "' , '" + ObjectSerializer.serialize(MOTwarned) + "' , '" + ObjectSerializer.serialize(serviceWarned) + "' , '" + ObjectSerializer.serialize(taxDue) + "')");

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public static void loadBikesOld() {
        Log.i("Main Activity", "loadBikes Old");
        int bikesSize = sharedPreferences.getInt("bikesSize", 0);

        Log.i("Bikes Size", "" + bikesSize);
        bikes.clear();

        ArrayList<String> make = new ArrayList<>();
        ArrayList<String> model = new ArrayList<>();
        ArrayList<String> reg = new ArrayList<>();
        ArrayList<String> bikeId = new ArrayList<>();
        ArrayList<String> VIN = new ArrayList<>();
        ArrayList<String> serviceDue = new ArrayList<>();
        ArrayList<String> MOTdue = new ArrayList<>();
        ArrayList<String> lastKnownService = new ArrayList<>();
        ArrayList<String> lastKnownMOT = new ArrayList<>();
        ArrayList<String> yearOfMan = new ArrayList<>();
        ArrayList<String> notes = new ArrayList<>();
        ArrayList<String> estMileage = new ArrayList<>();
        ArrayList<String> MOTwarned = new ArrayList<>();
        ArrayList<String> serviceWarned = new ArrayList<>();
        ArrayList<String> taxDue = new ArrayList<>();

        try {

            make = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("make", ObjectSerializer.serialize(new ArrayList<String>())));
            model = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("model", ObjectSerializer.serialize(new ArrayList<String>())));
            reg = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("reg", ObjectSerializer.serialize(new ArrayList<String>())));
            bikeId = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("bikeId", ObjectSerializer.serialize(new ArrayList<String>())));
            VIN = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("VIN", ObjectSerializer.serialize(new ArrayList<String>())));
            serviceDue = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("serviceDue", ObjectSerializer.serialize(new ArrayList<String>())));
            MOTdue = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("MOTdue", ObjectSerializer.serialize(new ArrayList<String>())));
            lastKnownService = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lastKnownService", ObjectSerializer.serialize(new ArrayList<String>())));
            lastKnownMOT = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lastKnownMOT", ObjectSerializer.serialize(new ArrayList<String>())));
            yearOfMan = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("yearOfMan", ObjectSerializer.serialize(new ArrayList<String>())));
            notes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("notes", ObjectSerializer.serialize(new ArrayList<String>())));
            estMileage = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("estMileage", ObjectSerializer.serialize(new ArrayList<String>())));
            MOTwarned = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("MOTwarned", ObjectSerializer.serialize(new ArrayList<String>())));
            serviceWarned = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("serviceWarned", ObjectSerializer.serialize(new ArrayList<String>())));
            taxDue = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("taxDue", ObjectSerializer.serialize(new ArrayList<String>())));

            Log.i("Bikes Restored ", "Count :" + make.size());
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Loading Bikes", "Failed attempt");
        }

        Log.i("Retrieved info", "Log count :" + make.size());
        if (make.size() > 0 && model.size() > 0 && bikeId.size() > 0) {
            // we've checked there is some info
            if (make.size() == model.size() && model.size() == bikeId.size()) {
                // we've checked each item has the same amount of info, nothing is missing
                for (int x = 0; x < make.size(); x++) {
                    Log.i("Retrieving", "Log " + x);
                    int thisId = Integer.parseInt(bikeId.get(x));
                    Log.i("Est Mileage", estMileage.get(x));
                    double thisEstMileage = Double.parseDouble(estMileage.get(x));
                    boolean thisMOTwarned = Boolean.parseBoolean(MOTwarned.get(x));
                    boolean thisServiceWarned = Boolean.parseBoolean(serviceWarned.get(x));
                    Bike newBike = new Bike(thisId, make.get(x), model.get(x), reg.get(x), VIN.get(x), serviceDue.get(x), MOTdue.get(x), lastKnownService.get(x), lastKnownMOT.get(x),
                            yearOfMan.get(x), notes.get(x), thisEstMileage, thisMOTwarned, thisServiceWarned, taxDue.get(x));
                    Log.i("Adding", "" + x + "" + newBike);
                    bikes.add(newBike);
                }
            }
        }
        Bike.bikeCount = sharedPreferences.getInt("bikeCount", 0);
        loadLogs();
        loadFuels();
    }

    public static void loadBikes() {

        Log.i("Main Activity", "New Bikes Loading");
        int bikesSize = sharedPreferences.getInt("bikesSize", 0);

        Log.i("Bikes Size", "" + bikesSize);
        bikes.clear();

        try {

            Cursor c = vehiclesDB.rawQuery("SELECT * FROM vehicles", null);

            int makeIndex = c.getColumnIndex("make");
            int modelIndex = c.getColumnIndex("model");
            int regIndex = c.getColumnIndex("reg");
            int bikeIdIndex = c.getColumnIndex("bikeId");
            int VINIndex = c.getColumnIndex("VIN");
            int serviceDueIndex = c.getColumnIndex("serviceDue");
            int MOTdueIndex = c.getColumnIndex("MOTdue");
            int lastKnownServiceIndex = c.getColumnIndex("lastKnownService");
            int lastKnownMOTIndex = c.getColumnIndex("lastKnownMOT");
            int yearOfManIndex = c.getColumnIndex("yearOfMan");
            int notesIndex = c.getColumnIndex("notes");
            int estMileageIndex = c.getColumnIndex("estMileage");
            int MOTwarnedIndex = c.getColumnIndex("MOTwarned");
            int serviceWarnedIndex = c.getColumnIndex("serviceWarned");
            int taxDueIndex = c.getColumnIndex("taxDue");

            c.moveToFirst();

            do {

                ArrayList<String> make = new ArrayList<>();
                ArrayList<String> model = new ArrayList<>();
                ArrayList<String> reg = new ArrayList<>();
                ArrayList<String> bikeId = new ArrayList<>();
                ArrayList<String> VIN = new ArrayList<>();
                ArrayList<String> serviceDue = new ArrayList<>();
                ArrayList<String> MOTdue = new ArrayList<>();
                ArrayList<String> lastKnownService = new ArrayList<>();
                ArrayList<String> lastKnownMOT = new ArrayList<>();
                ArrayList<String> yearOfMan = new ArrayList<>();
                ArrayList<String> notes = new ArrayList<>();
                ArrayList<String> estMileage = new ArrayList<>();
                ArrayList<String> MOTwarned = new ArrayList<>();
                ArrayList<String> serviceWarned = new ArrayList<>();
                ArrayList<String> taxDue = new ArrayList<>();

                try {

                    make = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(makeIndex));
                    model = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(modelIndex));
                    reg = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(regIndex));
                    bikeId = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(bikeIdIndex));
                    VIN = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(VINIndex));
                    serviceDue = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(serviceDueIndex));
                    MOTdue = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(MOTdueIndex));
                    lastKnownService = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(lastKnownServiceIndex));
                    lastKnownMOT = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(lastKnownMOTIndex));
                    yearOfMan = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(yearOfManIndex));
                    notes = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(notesIndex));
                    estMileage = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(estMileageIndex));
                    MOTwarned = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(MOTwarnedIndex));
                    serviceWarned = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(serviceWarnedIndex));
                    taxDue = (ArrayList<String>) ObjectSerializer.deserialize(c.getString(taxDueIndex));

                    Log.i("Bikes Restored ", "Count :" + make.size());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Loading Bikes", "Failed attempt");
                }

                Log.i("Retrieved info", "Log count :" + make.size());
                if (make.size() > 0 && model.size() > 0 && bikeId.size() > 0) {
                    // we've checked there is some info
                    if (make.size() == model.size() && model.size() == bikeId.size()) {
                        // we've checked each item has the same amount of info, nothing is missing
                        for (int x = 0; x < make.size(); x++) {
                            int thisId = Integer.parseInt(bikeId.get(x));
                            double thisEstMileage = Double.parseDouble(estMileage.get(x));
                            boolean thisMOTwarned = Boolean.parseBoolean(MOTwarned.get(x));
                            boolean thisServiceWarned = Boolean.parseBoolean(serviceWarned.get(x));
                            Bike newBike = new Bike(thisId, make.get(x), model.get(x), reg.get(x), VIN.get(x), serviceDue.get(x), MOTdue.get(x), lastKnownService.get(x), lastKnownMOT.get(x),
                                    yearOfMan.get(x), notes.get(x), thisEstMileage, thisMOTwarned, thisServiceWarned, taxDue.get(x));
                            Log.i("Adding", " " + x + " " + newBike);
                            bikes.add(newBike);
                        }
                    }
                }
            } while (c.moveToNext());

        } catch (Exception e) {

            Log.i("LoadingDB", "Caught Error");
            e.printStackTrace();

        }
        Bike.bikeCount = sharedPreferences.getInt("bikeCount", 0);
        loadLogs();
        loadFuels();
    }

    public static void loadSettings() {
        lastHowManyFuels = sharedPreferences.getInt("lastHowManyFuels", 10);
        locationUpdatesTime = sharedPreferences.getInt("locationUpdatesTime", 1200000);
        incCarEvents = sharedPreferences.getBoolean("incCarEvents", true);
        incBikeEvents = sharedPreferences.getBoolean("incBikeEvents", true);
        backgroundsWanted = sharedPreferences.getBoolean("backgroundsWanted", true);
        locationAccepted = sharedPreferences.getBoolean("locationAccepted", false);
        storageAccepted = sharedPreferences.getBoolean("storageAccepted", false);
        currencySetting = sharedPreferences.getString("currencySetting", "£");
        milesSetting = sharedPreferences.getString("milesSetting", "Miles");
        Log.i("LOAD storageAccepted " + storageAccepted, "locationAccepted " + locationAccepted);
    }

    public static void saveSettings() {
        ed.putInt("lastHowManyFuels", lastHowManyFuels).apply();
        ed.putInt("locationUpdatesTime", locationUpdatesTime).apply();
        ed.putBoolean("incCarEvents", incCarEvents).apply();
        ed.putBoolean("incBikeEvents", incBikeEvents).apply();
        ed.putBoolean("backgroundsWanted", backgroundsWanted).apply();
        ed.putBoolean("locationAccepted", locationAccepted).apply();
        ed.putBoolean("storageAccepted", storageAccepted).apply();
        ed.putString("currencySetting", currencySetting).apply();
        ed.putString("milesSetting", milesSetting).apply();
        Log.i("SAVE storageAccepted " + storageAccepted, "locationAccepted " + locationAccepted);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SendLogcatMail();
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
        loadSettings();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = settings.getString("theme", "1");
        Log.i("Theme", theme);
        setAppTheme(Integer.parseInt(theme));
        changeHeader();
        checkBackground();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Logs Activity", "On Pause");
        saveBikes();
        saveSettings();
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Logs Activity", "On Stop");
        saveBikes();
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
    }
}
