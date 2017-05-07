package com.androidandyuk.bikersbestfriend;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.androidandyuk.bikersbestfriend.Favourites.favouriteLocations;
import static com.androidandyuk.bikersbestfriend.MapsActivity.showMarkers;
import static com.androidandyuk.bikersbestfriend.RaceTracks.trackLocations;

public class LocationInfoActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    markedLocation temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                locationName.setText(temp.name);
                locationAddress.setText(temp.address);
                locationComment.setText(temp.comment);
            } else if (favItem == 9998) {
                showMarkers(favouriteLocations, 0);
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

    public void saveChanges(View view){
        // saves the changes made while viewing the location info
        Log.i("Location Info Activity", "Saving Changes");

        EditText locationName = (EditText)findViewById(R.id.locationName);
        EditText locationAddress = (EditText)findViewById(R.id.locationAddress);
        EditText locationComment = (EditText)findViewById(R.id.locationComment);

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
}
