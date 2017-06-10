package com.androidandyuk.bikersbestfriend;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import static com.androidandyuk.bikersbestfriend.MainActivity.currentForecast;
import static com.androidandyuk.bikersbestfriend.MainActivity.weatherText;

public class Locations extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // requesting permissions to access storage and location
        String[] perms = {"android.permission.READ_EXTERNAL_STORAGE","android.permission.ACCESS_FINE_LOCATION"};
        int permsRequestCode = 200;
        requestPermissions(perms, permsRequestCode);



        weatherText = (TextView)findViewById(R.id.weatherView);
        // take weather found in MainActivity
        weatherText.setText("Today's forecast: " + currentForecast);
    }

    @Override

    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:

                MainActivity.storageAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;

                MainActivity.locationAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;

                Log.i("STRG " + MainActivity.storageAccepted,"LCTN " + MainActivity.locationAccepted);

                break;

        }

    }

    public void goToFavourites(View view) {
        Intent intent = new Intent(getApplicationContext(), Favourites.class);
        startActivity(intent);
    }

    public void goToTraffic(View view) {
        Intent intent = new Intent(getApplicationContext(), Traffic.class);
        startActivity(intent);
    }

    public void goToPetrol(View view) {
        Intent intent = new Intent(getApplicationContext(), PetrolPrices.class);
        startActivity(intent);
    }

    public void goToTracks(View view) {
        Intent intent = new Intent(getApplicationContext(), RaceTracks.class);

        startActivity(intent);
    }

    public void goToHotSpots(View view) {
        Intent intent = new Intent(getApplicationContext(), HotSpots.class);

        startActivity(intent);
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
        return super.onKeyDown(keyCode, event);
    }

}
