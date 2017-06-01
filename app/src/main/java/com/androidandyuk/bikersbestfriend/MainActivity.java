package com.androidandyuk.bikersbestfriend;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

import static com.androidandyuk.bikersbestfriend.Fuelling.loadFuels;
import static com.androidandyuk.bikersbestfriend.Maintenance.loadLogs;
import static com.androidandyuk.bikersbestfriend.SplashScreen.ed;
import static com.androidandyuk.bikersbestfriend.SplashScreen.sharedPreferences;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<Bike> bikes = new ArrayList<>();

    private FirebaseAnalytics mFirebaseAnalytics;

    public static LocationManager locationManager;
    public static LocationListener locationListener;

    private static final String TAG = "MainActivity";

    private AdView mAdView;

    public static LatLng userLatLng;
    public static JSONObject jsonObject;
    public static TextView weatherText;
    public static String currentForecast;
    public static int warningDays = 30;

    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");

    // to store if the user has given permission to storage and location
    public static boolean storageAccepted;
    public static boolean locationAccepted;

    static markedLocation user;
    static double conversion = 0.621;
    static Geocoder geocoder;

    public static int activeBike;

    public static String userLocationForWeather;

    public static final DecimalFormat precision = new DecimalFormat("0.##");
    public static final DecimalFormat oneDecimal = new DecimalFormat("0.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("Main Activity", "onCreate");

        // until I implement landscape view, lock the orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        loadBikes();
        loadFuels();
        loadLogs();

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

        userLatLng = new LatLng(51.6516833, -0.1771449);  //  15 SC

        user = new markedLocation("You", "", userLatLng, "");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

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

        Intent intent;
        switch (item.getItemId()) {
            case 0:
                Log.i("Option", "0");
//                intent = new Intent(getApplicationContext(), SettingsActivity.class);
//                startActivity(intent);
                Toast.makeText(MainActivity.this, "Settings not yet implemented", Toast.LENGTH_LONG).show();
                return true;
            case 1:
                Log.i("Option", "1");
                // go to about me
                intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                return true;
            case 2:
                Log.i("Option", "2");
                activeBike = 0;
                return true;
            case 3:
                Log.i("Option", "3");
                activeBike = 1;
                return true;
            case 4:
                Log.i("Option", "4");
                activeBike = 2;
                return true;
            case 5:
                Log.i("Option", "5");
                activeBike = 3;
                return true;
            case 6:
                Log.i("Option", "6");
                activeBike = 4;
                return true;
            case 7:
                Log.i("Option", "7");
                activeBike = 5;
                return true;
            case 8:
                Log.i("Option", "8");
                activeBike = 6;
                return true;
            case 9:
                Log.i("Option", "9");
                activeBike = 7;
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static boolean checkInRange(Bike thisBike, Calendar testDate, Character type) {
        // establish what date we're testing against
//        Calendar testDate = new GregorianCalendar();
        testDate.add(Calendar.DAY_OF_YEAR, warningDays);

        // get the date this bikes MOT is due
        Calendar thisDate = new GregorianCalendar();
        try {
            if (type.equals('M')) {
                thisDate.setTime(sdf.parse(thisBike.MOTdue));
            } else if (type.equals('S')) {
                thisDate.setTime(sdf.parse(thisBike.serviceDue));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i("MOT Check", "Date conversion failed");
        }

        if (thisDate.before(testDate)) {
            return true;
        }
        return false;
    }

    public void checkMOTwarning() {
        for (Bike thisBike : bikes) {
            Calendar testDate = new GregorianCalendar();
            if (checkInRange(thisBike, testDate, 'M')) {
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
                            .setSmallIcon(android.R.drawable.alert_light_frame)
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
            if (checkInRange(thisBike, testDate, 'S')) {
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
                            .setSmallIcon(android.R.drawable.alert_light_frame)
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

    public void goToFueling(View view) {
        if (activeBike > -1) {
            Intent intent = new Intent(getApplicationContext(), Fuelling.class);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Add a bike in your Garage first", Toast.LENGTH_LONG).show();
        }
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

    public static void saveBikes() {
        Log.i("Main Activity", "Saving Bikes");
        ed.putInt("bikeCount", Bike.bikeCount).apply();
        ed.putInt("bikesSize", bikes.size()).apply();

        int i = 0;
        for (Bike thisBike : bikes) {
            Log.i("Saving Bikes", "" + thisBike);
            try {
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

                // I think these are new variables, so likely don't need clearing?
                make.clear();
                model.clear();
                reg.clear();
                bikeId.clear();
                VIN.clear();
                serviceDue.clear();
                MOTdue.clear();
                lastKnownService.clear();
                lastKnownMOT.clear();
                yearOfMan.clear();
                notes.clear();
                estMileage.clear();
                MOTwarned.clear();
                serviceWarned.clear();

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

                Log.i("Saving Bikes", "Size :" + bikes.size());
                ed.putString("make" + i, ObjectSerializer.serialize(make)).apply();
                ed.putString("model" + i, ObjectSerializer.serialize(model)).apply();
                ed.putString("reg" + i, ObjectSerializer.serialize(reg)).apply();
                ed.putString("bikeId" + i, ObjectSerializer.serialize(bikeId)).apply();
                ed.putString("VIN" + i, ObjectSerializer.serialize(VIN)).apply();
                ed.putString("serviceDue" + i, ObjectSerializer.serialize(serviceDue)).apply();
                ed.putString("MOTdue" + i, ObjectSerializer.serialize(MOTdue)).apply();
                ed.putString("lastKnownService" + i, ObjectSerializer.serialize(lastKnownService)).apply();
                ed.putString("lastKnownMOT" + i, ObjectSerializer.serialize(lastKnownMOT)).apply();
                ed.putString("yearOfMan" + i, ObjectSerializer.serialize(yearOfMan)).apply();
                ed.putString("notes" + i, ObjectSerializer.serialize(notes)).apply();
                ed.putString("estMileage" + i, ObjectSerializer.serialize(estMileage)).apply();
                ed.putString("MOTwarned" + i, ObjectSerializer.serialize(MOTwarned)).apply();
                ed.putString("serviceWarned" + i, ObjectSerializer.serialize(serviceWarned)).apply();

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Adding details", "Failed attempt");
            }
            i++;
        }
    }

    public static void loadBikes() {
        Log.i("Main Activity", "Bikes Loading");
        int bikesSize = sharedPreferences.getInt("bikesSize", 0);

        Log.i("Bikes Size", "" + bikesSize);
        bikes.clear();

        for (int i = 0; i < bikesSize; i++) {

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

            // I think these are new variables, so likely don't need clearing?
            make.clear();
            model.clear();
            reg.clear();
            bikeId.clear();
            VIN.clear();
            serviceDue.clear();
            MOTdue.clear();
            lastKnownService.clear();
            lastKnownMOT.clear();
            yearOfMan.clear();
            notes.clear();
            estMileage.clear();
            MOTwarned.clear();
            serviceWarned.clear();

            try {

                make = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("make" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                model = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("model" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                reg = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("reg" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                bikeId = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("bikeId" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                VIN = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("VIN" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                serviceDue = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("serviceDue" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                MOTdue = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("MOTdue" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                lastKnownService = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lastKnownService" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                lastKnownMOT = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lastKnownMOT" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                yearOfMan = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("yearOfMan" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                notes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("notes" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                estMileage = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("estMileage" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                MOTwarned = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("MOTwarned" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                serviceWarned = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("serviceWarned" + i, ObjectSerializer.serialize(new ArrayList<String>())));

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
                                yearOfMan.get(x), notes.get(x), thisEstMileage, thisMOTwarned, thisServiceWarned);
                        Log.i("Adding", "" + x + "" + newBike);
                        bikes.add(newBike);
                    }

                }
            }
            Bike.bikeCount = sharedPreferences.getInt("bikeCount", 0);
            loadLogs();
            loadFuels();
        }
    }

//    public void SendLogcatMail(){
//
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_LOGS) == PackageManager.PERMISSION_GRANTED) {
//            Log.d(getLocalClassName(), "Got READ_LOGS permissions");
//        } else {
//            Log.e(getLocalClassName(), "Don't have READ_LOGS permissions");
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_LOGS}, 103);
//            Log.i(getLocalClassName(), "new READ_LOGS permission: " + ContextCompat.checkSelfPermission(this, Manifest.permission.READ_LOGS));
//        }
//
//        // save logcat in file
//        File outputFile = new File(downloadsDir,
//                "logcat.txt");
//        Log.i("SendLoagcatMail: ", "logcat file is " + outputFile.getAbsolutePath());
//        try {
//            Runtime.getRuntime().exec(
//                    "logcat -f " + outputFile.getAbsolutePath());
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e(getLocalClassName(), "Alas error! ", e);
//        }
//
//        //send file using email
//        Intent emailIntent = new Intent(Intent.ACTION_SEND);
//        // Set type to "email"
//        emailIntent.setType("vnd.android.cursor.dir/email");
//        String to[] = {"vishvas.vasuki+STARDICTAPP@gmail.com"};
//        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
//        // the attachment
//        emailIntent .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outputFile));
//        // the mail subject
//        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Stardict Updater App Failure report.");
//        startActivity(Intent.createChooser(emailIntent , "Email failure report to maker?..."));
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SendLogcatMail();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Logs Activity", "On Pause");
        saveBikes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Logs Activity", "On Stop");
        saveBikes();
    }

}
