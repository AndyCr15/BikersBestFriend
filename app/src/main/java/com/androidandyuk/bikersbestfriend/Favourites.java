package com.androidandyuk.bikersbestfriend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import static com.androidandyuk.bikersbestfriend.MainActivity.arrayAdapter;
import static com.androidandyuk.bikersbestfriend.MainActivity.favouriteLocations;

public class Favourites extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        ListView listView = (ListView) findViewById(R.id.listView);

        favouriteLocations.add(new markedLocation("Add a new place...", new LatLng(0, 0), ""));

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, favouriteLocations);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("placeNumber", i);

                startActivity(intent);
            }

        });


    }
}
