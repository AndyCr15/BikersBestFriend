package com.androidandyuk.bikersbestfriend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RaceTracks extends AppCompatActivity {
    static List<markedLocation> trackLocations = new ArrayList<>();
    static ArrayAdapter arrayAdapter;


    public void viewTracks(View view) {
        Log.i("View Race Tracks", "called");
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        // 9998 tells the Maps activity to show all the markers
        intent.putExtra("placeNumber", 9998);
        intent.putExtra("Type", "Track");
        startActivity(intent);
    }

    public void sortMyList() {
        Log.i("Sort List", "" + trackLocations.size());
        if (trackLocations.size() > 0) {
            Collections.sort(trackLocations);
            arrayAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_tracks);


        Log.i("Race Tracks", "onCreate");

        ListView listView = (ListView) findViewById(R.id.listTracks);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, trackLocations);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("placeNumber", i);
                intent.putExtra("Type", "Track");

                startActivity(intent);
            }

        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Race Tracks Activity", "On Pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Race Tracks Activity", "On Resume");
        sortMyList();
    }
}


