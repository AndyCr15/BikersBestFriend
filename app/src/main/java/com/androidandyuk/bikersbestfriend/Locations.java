package com.androidandyuk.bikersbestfriend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import static com.androidandyuk.bikersbestfriend.MainActivity.localForecast;
import static com.androidandyuk.bikersbestfriend.MainActivity.weatherText;

public class Locations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        weatherText = (TextView)findViewById(R.id.weatherView);
        // take weather found in MainActivity
        weatherText.setText("Today's forecast: " + localForecast);
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
