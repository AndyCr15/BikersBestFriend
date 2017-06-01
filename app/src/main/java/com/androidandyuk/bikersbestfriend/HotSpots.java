package com.androidandyuk.bikersbestfriend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;

import static com.androidandyuk.bikersbestfriend.MainActivity.oneDecimal;

public class HotSpots extends AppCompatActivity {
    static ArrayList<markedLocation> hotspotLocations = new ArrayList<>();
    static MyLocationAdapter myAdapter;

    static SharedPreferences sharedPreferences;
    Button seeHotSpotMap;
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("Hot Spots", "onCreate");

        sharedPreferences = this.getSharedPreferences("com.androidandyuk.bikersbestfriend", Context.MODE_PRIVATE);

        setContentView(R.layout.activity_hot_spots);

        ListView listView = (ListView) findViewById(R.id.favsList);

        if (hotspotLocations.size() == 0) {
            Log.i("Favourites", "Initializing Locations");
            initialiseLocations();
        }


        myAdapter = new MyLocationAdapter(hotspotLocations);

        listView.setAdapter(myAdapter);

        sortMyList();

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
        intent.putExtra("Type", "Hot");
        startActivity(intent);
    }

    public void sortMyList() {
        Log.i("Sort List", "" + hotspotLocations.size());
        if (hotspotLocations.size() > 0) {
            Collections.sort(hotspotLocations);
            myAdapter.notifyDataSetChanged();
        }

    }

    public class MyLocationAdapter extends BaseAdapter {
        public ArrayList<markedLocation> locationDataAdapter;

        public MyLocationAdapter(ArrayList<markedLocation> locationDataAdapter) {
            this.locationDataAdapter = locationDataAdapter;
        }

        @Override
        public int getCount() {
            return locationDataAdapter.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = getLayoutInflater();
            View myView = mInflater.inflate(R.layout.location_listview, null);

            final markedLocation s = locationDataAdapter.get(position);

            TextView locationListDistance = (TextView) myView.findViewById(R.id.locationListDistance);
            locationListDistance.setText(oneDecimal.format(s.distance));

            TextView locationListName = (TextView) myView.findViewById(R.id.locationListName);
            locationListName.setText(s.name);

            return myView;
        }

    }

    public void initialiseLocations() {
        if (hotspotLocations.size() == 0) {
            hotspotLocations.add(new markedLocation("Ace Cafe", new LatLng(51.5412794, -0.2799549), "The world famous Ace Cafe. Food not the best though. Friday nights are always busy"));
            hotspotLocations.add(new markedLocation("High Beach", new LatLng(51.657176, 0.0349883), ""));
            hotspotLocations.add(new markedLocation("Rykers Cafe", new LatLng(51.255562, -0.3243657), ""));
            hotspotLocations.add(new markedLocation("Loomies Cafe", new LatLng(51.030443, -1.0779103), "Great roads lead to it. Nice burger once you get there!"));
            hotspotLocations.add(new markedLocation("H Cafe", new LatLng(51.658486, -1.1781097), ""));
            hotspotLocations.add(new markedLocation("On Yer Bike", new LatLng(51.854932, -0.968651), ""));
            hotspotLocations.add(new markedLocation("Revved Up", new LatLng(51.8500038, 1.274296), ""));
            hotspotLocations.add(new markedLocation("The Midway Truck Stop", new LatLng(52.9373479, -2.6643152), ""));
            hotspotLocations.add(new markedLocation("Finchingfield", new LatLng(51.96829, 0.4480183), "Beautiful scenery. Surrounded by great rounds."));
            hotspotLocations.add(new markedLocation("Bike Shed", new LatLng(51.527171, -0.0805737), "Own parking, often with security. Food can be pricey."));
            hotspotLocations.add(new markedLocation("Hartside Cafe", new LatLng(54.6360254, -2.5316498), ""));
        }
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
        Log.i("Hot Spots","On Stop");
    }
}
