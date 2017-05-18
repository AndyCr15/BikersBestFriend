package com.androidandyuk.bikersbestfriend;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

import static com.androidandyuk.bikersbestfriend.MainActivity.favouriteLocations;
import static com.androidandyuk.bikersbestfriend.HotSpots.hotspotLocations;
import static com.androidandyuk.bikersbestfriend.MainActivity.jsonObject;
import static com.androidandyuk.bikersbestfriend.MapsActivity.showMarkers;
import static com.androidandyuk.bikersbestfriend.RaceTracks.trackLocations;

public class LocationInfoActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    markedLocation temp;
    public static String thisForecast;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.i("Location Info Activity", "On Map Ready");

        EditText locationName = (EditText) findViewById(R.id.locationName);
        EditText locationAddress = (EditText) findViewById(R.id.locationAddress);
        EditText locationComment = (EditText) findViewById(R.id.locationComment);


        // read in the reason the map has been called
        Intent intent = getIntent();
        String type = "";
        if (intent.getStringExtra("Type") != null) {
            type = intent.getStringExtra("Type");
        }
        int favItem = intent.getIntExtra("placeNumber", 9999);

        if (type.equals("Fav")) {
            if (favItem < 9998) {
                // focus on favourite location
                temp = favouriteLocations.get(favItem);
                centerMapOnLocation(temp.location, temp.name);
                Log.i("Fav selected", "" + temp.name);

                // download the weather for this location
                String thisLoc = "lat=" + temp.location.latitude + "&lon=" + temp.location.longitude;
                Log.i("Fav location", thisLoc);
                WeatherDownload task = new WeatherDownload();
                task.execute("http://api.openweathermap.org/data/2.5/weather?" + thisLoc + "&APPID=81e5e0ca31ad432ee9153dd761ed3b27");

                locationName.setText(temp.name);
                locationAddress.setText(temp.address);
                locationComment.setText(temp.comment);
            } else if (favItem == 9998) {
                showMarkers(favouriteLocations, 0);
            }
        }

        if (type.equals("Hot")) {
            if (favItem < 9998) {
                // focus on favourite location
                temp = hotspotLocations.get(favItem);
                centerMapOnLocation(temp.location, temp.name);
                Log.i("Hot Spot selected", "" + temp.name);
                locationName.setText(temp.name);
                locationAddress.setText(temp.address);
                locationComment.setText(temp.comment);
            } else if (favItem == 9998) {
                showMarkers(hotspotLocations, 0);
            }
        }

        if (type.equals("Track")) {
            if (favItem < 9998) {
                // focus on favourite location
                temp = trackLocations.get(favItem);
                centerMapOnLocation(temp.location, temp.name);
                Log.i("Track selected", "" + temp.name);
                locationName.setText(temp.name);
                locationAddress.setText(temp.address);
                locationComment.setText(temp.comment);
            } else if (favItem == 9998) {
                showMarkers(trackLocations, 0);
            }
        }

    }

    public void saveChanges(View view) {
        // saves the changes made while viewing the location info
        Log.i("Location Info Activity", "Saving Changes");

        EditText locationName = (EditText) findViewById(R.id.locationName);
        EditText locationAddress = (EditText) findViewById(R.id.locationAddress);
        EditText locationComment = (EditText) findViewById(R.id.locationComment);

        // find a way to return the edited text into the correct object
        temp.name = locationName.getText().toString();
        temp.address = locationAddress.getText().toString();
        temp.comment = locationComment.getText().toString();

        Toast.makeText(this, "Info updated for " + temp.name, Toast.LENGTH_LONG).show();

    }

    public void centerMapOnLocation(LatLng latLng, String title) {

        Log.i("Maps Activity", "Center on map - latLng");

        mMap.clear();

        if (title != "Your location") {

            mMap.addMarker(new MarkerOptions().position(latLng).title(title));

        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

    }

    public void centerMapOnLocation(Location location, String title) {

        Log.i("Maps Activity", "Center on map - location");

        LatLng selectedLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.clear();

        if (title != "Your location") {

            mMap.addMarker(new MarkerOptions().position(selectedLatLng).title(title));

        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 14));

    }

    public class WeatherDownload extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            Log.i("Weather Download","doInBackground called");
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
            Log.i("Weather Download","onPostExecute called");
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

                        thisForecast = jsonPart.getString("main");

                        TextView locationWeather = (TextView) findViewById(R.id.locationWeather);
                        locationWeather.setText("Weather here is " + thisForecast);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

}
