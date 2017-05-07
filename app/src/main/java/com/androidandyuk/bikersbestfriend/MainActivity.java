package com.androidandyuk.bikersbestfriend;

import android.content.Intent;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    LocationManager locationManager;
    LocationListener locationListener;

    static markedLocation user;
    static double conversion = 0.621;
    static Geocoder geocoder;

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

    public void goToFavourites(View view) {
        Intent intent = new Intent(getApplicationContext(), Favourites.class);

        startActivity(intent);
    }

    public void goToTracks(View view) {
        Intent intent = new Intent(getApplicationContext(), RaceTracks.class);

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

        Log.i("Main Activity", "onCreate");

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        LatLng userLatLng = new LatLng(51.6516833, -0.1771449);  //  15 SC

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
}
