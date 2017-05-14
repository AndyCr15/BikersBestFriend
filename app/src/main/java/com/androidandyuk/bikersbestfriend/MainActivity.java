package com.androidandyuk.bikersbestfriend;

import android.content.Context;
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
import java.util.ArrayList;

import static com.androidandyuk.bikersbestfriend.Fuelling.loadFuels;
import static com.androidandyuk.bikersbestfriend.Garage.bikes;
import static com.androidandyuk.bikersbestfriend.Maintenance.loadLogs;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    LocationManager locationManager;
    LocationListener locationListener;

    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor ed;

    public static LatLng userLatLng;
    public static JSONObject jsonObject;
    public static TextView weatherText;
    public static String localForecast;

    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
    public static SimpleDateFormat sdfShort = new SimpleDateFormat("dd MMM");

    static markedLocation user;
    static double conversion = 0.621;
    static Geocoder geocoder;
//    static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherText = (TextView) findViewById(R.id.weatherView);

        sharedPreferences = this.getSharedPreferences("com.androidandyuk.bikersbestfriend", Context.MODE_PRIVATE);
        ed = sharedPreferences.edit();

        loadBikes();

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


    public void initialiseTracks() {

        if (RaceTracks.trackLocations.size() == 0) {
            Log.i("Initialising Tracks", "Started");
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
                ArrayList<String> yearOfMan = new ArrayList<>();

                // I think these are new variables, so likely don't need clearing?
                make.clear();
                model.clear();
                reg.clear();
                bikeId.clear();
                VIN.clear();
                serviceDue.clear();
                MOTdue.clear();
                yearOfMan.clear();

                make.add(thisBike.make);
                model.add(thisBike.model);
                reg.add(thisBike.registration);
                bikeId.add(Integer.toString(thisBike.bikeId));
                VIN.add(thisBike.VIN);
                serviceDue.add(thisBike.serviceDue);
                MOTdue.add(thisBike.MOTdue);
                yearOfMan.add(thisBike.yearOfMan);

                Log.i("Saving Bikes", "Size :" + bikes.size());
                ed.putString("make" + i, ObjectSerializer.serialize(make)).apply();
                ed.putString("model" + i, ObjectSerializer.serialize(model)).apply();
                ed.putString("reg" + i, ObjectSerializer.serialize(reg)).apply();
                ed.putString("bikeId" + i, ObjectSerializer.serialize(bikeId)).apply();
                ed.putString("VIN" + i, ObjectSerializer.serialize(VIN)).apply();
                ed.putString("serviceDue" + i, ObjectSerializer.serialize(serviceDue)).apply();
                ed.putString("MOTdue" + i, ObjectSerializer.serialize(MOTdue)).apply();
                ed.putString("yearOfMan" + i, ObjectSerializer.serialize(yearOfMan)).apply();

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Adding details", "Failed attempt");
            }
            i++;
        }
    }

    public static void loadBikes() {

        int bikesSize = sharedPreferences.getInt("bikesSize", 0);

        bikes.clear();

        for (int i = 0; i < bikesSize; i++) {

            ArrayList<String> make = new ArrayList<>();
            ArrayList<String> model = new ArrayList<>();
            ArrayList<String> reg = new ArrayList<>();
            ArrayList<String> bikeId = new ArrayList<>();
            ArrayList<String> VIN = new ArrayList<>();
            ArrayList<String> serviceDue = new ArrayList<>();
            ArrayList<String> MOTdue = new ArrayList<>();
            ArrayList<String> yearOfMan = new ArrayList<>();

            // I think these are new variables, so likely don't need clearing?
            make.clear();
            model.clear();
            reg.clear();
            bikeId.clear();
            VIN.clear();
            serviceDue.clear();
            MOTdue.clear();
            yearOfMan.clear();

            try {

                make = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("make" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                model = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("model" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                reg = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("reg" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                bikeId = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("bikeId" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                VIN = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("VIN" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                serviceDue = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("serviceDue" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                MOTdue = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("MOTdue" + i, ObjectSerializer.serialize(new ArrayList<String>())));
                yearOfMan = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("yearOfMan" + i, ObjectSerializer.serialize(new ArrayList<String>())));

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
                        Bike newBike = new Bike(thisId, make.get(x), model.get(x), reg.get(x), VIN.get(x), serviceDue.get(x), MOTdue.get(x), yearOfMan.get(x));
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
