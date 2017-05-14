package com.androidandyuk.bikersbestfriend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import static com.androidandyuk.bikersbestfriend.MainActivity.currentForecast;
import static com.androidandyuk.bikersbestfriend.MainActivity.weatherText;

public class Locations extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        weatherText = (TextView)findViewById(R.id.weatherView);
        // take weather found in MainActivity
        weatherText.setText("Today's forecast: " + currentForecast);
    }

    public void goToFavourites(View view) {
        Intent intent = new Intent(getApplicationContext(), Favourites.class);

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

}
