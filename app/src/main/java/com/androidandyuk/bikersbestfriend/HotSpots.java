package com.androidandyuk.bikersbestfriend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;

import static com.androidandyuk.bikersbestfriend.Favourites.favouriteLocations;

public class HotSpots extends AppCompatActivity {
    static ArrayList<markedLocation> hotspotLocations = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    static SharedPreferences sharedPreferences;
    Button seeHotSpotMap;
    ListView listview;

    public void addHotSpot(View view) {
        Log.i("Add Hot Spots", "");
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("Type", "Hot");
        startActivity(intent);
    }

    public void viewHotSpots(View view) {
        Log.i("View Hot Spots", "called");
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        // 9998 tells the Maps activity to show all the markers
        intent.putExtra("placeNumber", 9998);
        intent.putExtra("Type", "Fav");
        startActivity(intent);
    }

    public void sortMyList() {
        Log.i("Sort List", "" + hotspotLocations.size());
        if (hotspotLocations.size() > 0) {
            Collections.sort(hotspotLocations);
            arrayAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("Hot Spots", "onCreate");

        sharedPreferences = this.getSharedPreferences("com.androidandyuk.bikersbestfriend", Context.MODE_PRIVATE);



        setContentView(R.layout.activity_hot_spots);

        ListView listView = (ListView) findViewById(R.id.maintList);

        if (favouriteLocations.size() == 0) {
            Log.i("Favourites", "Initializing Locations");
            initialiseLocations();
        }


        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, favouriteLocations);

        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), LocationInfoActivity.class);
                intent.putExtra("placeNumber", i);
                intent.putExtra("Type", "Hot");

                startActivity(intent);
            }
        });


    }

    public void initialiseLocations() {
        favouriteLocations.add(new markedLocation("Ace Cafe", new LatLng(51.5412794, -0.2799549), "The world famous Ace Cafe. Food not the best though. Friday nights are always busy"));
        favouriteLocations.add(new markedLocation("High Beach", new LatLng(51.657176, 0.0349883), ""));
        favouriteLocations.add(new markedLocation("Rykers Cafe", new LatLng(51.255562, -0.3243657), ""));
        favouriteLocations.add(new markedLocation("Loomies Cafe", new LatLng(51.030443, -1.0779103), "Great roads lead to it. Nice burger once you get there!"));
        favouriteLocations.add(new markedLocation("H Cafe", new LatLng(51.658486, -1.1781097), ""));
        favouriteLocations.add(new markedLocation("On Yer Bike", new LatLng(51.854932, -0.968651), ""));
        favouriteLocations.add(new markedLocation("Revved Up", new LatLng(51.8500038,1.274296), ""));
        favouriteLocations.add(new markedLocation("The Midway Truck Stop", new LatLng(52.9373479,-2.6643152), ""));
        favouriteLocations.add(new markedLocation("Finchingfield", new LatLng(51.96829,0.4480183), "Beautiful scenery. Surrounded by great rounds."));
        favouriteLocations.add(new markedLocation("Bike Shed", new LatLng(51.527171,-0.0805737), "Own parking, often with security. Food can be pricey."));
        favouriteLocations.add(new markedLocation("Hartside Cafe", new LatLng(54.6360254,-2.5316498), ""));
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Hot Spot Activity", "On Pause");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Hot Spot Activity", "On Resume");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("On Stop", "Called");


    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
