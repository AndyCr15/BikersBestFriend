package com.androidandyuk.bikersbestfriend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import static com.androidandyuk.bikersbestfriend.Garage.bikes;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    LocationManager locationManager;
    LocationListener locationListener;

        public static LatLng userLatLng;
    public static JSONObject jsonObject;
    public static TextView weatherText;
    public static String localForecast;

    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
    public static SimpleDateFormat sdfShort = new SimpleDateFormat("dd MMM");

    static markedLocation user;
    static double conversion = 0.621;
    static Geocoder geocoder;
    static SharedPreferences sharedPreferences;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                Log.i("Menu Item Selected", "Settings");
                Toast.makeText(MainActivity.this, "Not yet implemented", Toast.LENGTH_LONG).show();
                return true;
            case R.id.about:
                Log.i("Menu Item Selected", "About");
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.otherapps:
                Log.i("Menu Item Selected", "Other Apps");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=AAUK")));
                return true;
            case R.id.youtube:
                Log.i("Menu Item Selected", "My YouTube Channel");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/user/AndroidAndyUK")));
                return true;
            default:
                return false;
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
        Intent intent = new Intent(getApplicationContext(), Fuelling.class);
        startActivity(intent);
    }

    public void groupRideClicked(View view) {
        Toast.makeText(MainActivity.this, "Not yet implemented", Toast.LENGTH_LONG).show();
    }

    public void emergencyClicked(View view) {
        Toast.makeText(MainActivity.this, "Not yet implemented", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherText = (TextView)findViewById(R.id.weatherView);

        Log.i("Main Activity", "onCreate");

        // download the weather
        DownloadTask task = new DownloadTask();

        task.execute("http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&APPID=81e5e0ca31ad432ee9153dd761ed3b27");

//        if(user.location != null) {
//            //change this to be users location
//            double userLat = user.location.latitude;
//            double userLon = user.location.longitude;
//            String userLocation = "lat=" + userLat + "&lon=" + userLon;
//            task.execute("http://api.openweathermap.org/data/2.5/weather?" + userLocation + "&APPID=81e5e0ca31ad432ee9153dd761ed3b27");
//
//        }


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        userLatLng = new LatLng(51.6516833, -0.1771449);  //  15 SC

        user = new markedLocation("You", "", userLatLng, "");

        initialiseTracks();

    }




    public void initialiseTracks() {

        if (RaceTracks.trackLocations.size() == 0) {
            Log.i("Initialising Tracks","Started");
            RaceTracks.trackLocations.add(new markedLocation("Brands Hatch", new LatLng(51.3598711, 0.2586481), ""));
            RaceTracks.trackLocations.add(new markedLocation("Silverstone", new LatLng(52.0733006, -1.0168521), ""));
            RaceTracks.trackLocations.add(new markedLocation("Snetterton", new LatLng(52.4636482, 0.9436173), ""));
            RaceTracks.trackLocations.add(new markedLocation("Oulton Park", new LatLng(53.178469, -2.6189947), ""));
            RaceTracks.trackLocations.add(new markedLocation("Donington Park", new LatLng(52.8305468, -1.381029), ""));
            RaceTracks.trackLocations.add(new markedLocation("Anglesey", new LatLng(53.191994, -4.5038327), ""));
            RaceTracks.trackLocations.add(new markedLocation("Bedford Autodrome", new LatLng(52.2211337, -0.4819822), ""));
            RaceTracks.trackLocations.add(new markedLocation("Cadwell Park", new LatLng(53.3108261, -0.0737291), ""));
            RaceTracks.trackLocations.add(new markedLocation("Croft", new LatLng(54.4554809, -1.5580811), ""));
            RaceTracks.trackLocations.add(new markedLocation("Lydden Hill", new LatLng(51.1771493, 1.1987867), ""));
            RaceTracks.trackLocations.add(new markedLocation("Mallory Park", new LatLng(52.6006262, -1.3344846), ""));
            RaceTracks.trackLocations.add(new markedLocation("Rockingham", new LatLng(52.5156871, -0.6600846), ""));
        }
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

                        localForecast = jsonPart.getString("main");
                        weatherText.setText("Today's forecast: " + localForecast);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }
    public static void saveBikes() {
        Log.i("Shared Prefs", "Saving Logs");

        try {

            ArrayList<String> dates = new ArrayList<>();
            ArrayList<String> logs = new ArrayList<>();
            ArrayList<String> costs = new ArrayList<>();

            for (int i = 0; i < bikes.size(); i++) {

                for (maintenanceLogDetails thisLog : bikes.get(i).maintenanceLogs) {

                    dates.add(thisLog.date);
                    logs.add(thisLog.log);
                    costs.add(Double.toString(thisLog.price));

                }

                sharedPreferences.edit().putString("dates", ObjectSerializer.serialize(dates)).apply();
                sharedPreferences.edit().putString("logs", ObjectSerializer.serialize(logs)).apply();
                sharedPreferences.edit().putString("costs", ObjectSerializer.serialize(costs)).apply();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadBikes() {
        Log.i("Shared Prefs", "Loading Logs");

        ArrayList<String> bikeID = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> logs = new ArrayList<>();
        ArrayList<String> costs = new ArrayList<>();

        // I think these are new variables, so likely don't need clearing?
        bikeID.clear();
        dates.clear();
        logs.clear();
        costs.clear();

        for (int i = 0; i < bikes.size(); i++) {
            bikes.get(i).maintenanceLogs.clear();

            try {

                bikeID = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("bikeID", ObjectSerializer.serialize(new ArrayList<String>())));
                dates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("dates", ObjectSerializer.serialize(new ArrayList<String>())));
                logs = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("logs", ObjectSerializer.serialize(new ArrayList<String>())));
                costs = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("costs", ObjectSerializer.serialize(new ArrayList<String>())));

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bikeID.size() > 0 && dates.size() > 0 && logs.size() > 0 && costs.size() > 0) {
                // we've checked there is some info
                if (bikeID.size() == dates.size() && logs.size() == costs.size()) {
                    // we've checked each item has the same amount of info, nothing is missing

                    for (int x = 0; x < bikeID.size(); x++) {

                        Date thisDate = new Date();
                        try {
                            thisDate = sdf.parse(dates.get(x));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        maintenanceLogDetails newLog = new maintenanceLogDetails(logs.get(x), Double.parseDouble(costs.get(x)), thisDate);
                        bikes.get(i).maintenanceLogs.add(newLog);
                    }

                }
            }
        }
    }
}
