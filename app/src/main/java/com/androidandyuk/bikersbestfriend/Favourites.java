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

public class Favourites extends AppCompatActivity {
    static List<markedLocation> favouriteLocations = new ArrayList<>();
    static ArrayAdapter arrayAdapter;

    public void addFav(View view) {
        Log.i("Add Favs", "");
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("Type", "Fav");
        startActivity(intent);
    }

    public void viewFavourites(View view) {
        Log.i("View Favs", "called");
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        // 9998 tells the Maps activity to show all the markers
        intent.putExtra("placeNumber", 9998);
        intent.putExtra("Type", "Fav");
        startActivity(intent);
    }

    public void sortMyList() {
        Log.i("Sort List", "" + favouriteLocations.size());
        if (favouriteLocations.size() > 0) {
            Collections.sort(favouriteLocations);
            arrayAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("Favourites", "onCreate");

//        sortMyList();

        setContentView(R.layout.activity_favourites);

        ListView listView = (ListView) findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, favouriteLocations);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("placeNumber", i);
                intent.putExtra("Type", "Fav");

                startActivity(intent);
            }

        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Favs Activity", "On Pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Favs Activity", "On Resume");
        sortMyList();
    }
}
