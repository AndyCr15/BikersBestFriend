package com.androidandyuk.bikersbestfriend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Collections;

import static com.androidandyuk.bikersbestfriend.MainActivity.favouriteLocations;
import static com.androidandyuk.bikersbestfriend.SplashScreen.sharedPreferences;

public class Favourites extends AppCompatActivity {

    static ArrayAdapter arrayAdapter;

    private FirebaseAnalytics mFirebaseAnalytics;
    Button seeFavsMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("Favourites", "onCreate");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        sharedPreferences = this.getSharedPreferences("com.androidandyuk.bikersbestfriend", Context.MODE_PRIVATE);

        loadFavs();

        setContentView(R.layout.activity_favourites);

        ListView listView = (ListView) findViewById(R.id.maintList);

        Button seeFavsMap = (Button) findViewById(R.id.seeFavsMap);

        seeFavsMap.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // add some kind of check (boolean that means two pressed?)
                Log.i("Favourites", "Reset List");
                // code to rest here

                // draw the list again
                return true;
            }
        });

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, favouriteLocations);

        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), LocationInfoActivity.class);
                intent.putExtra("placeNumber", i);
                intent.putExtra("Type", "Fav");

                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Removing", "" + favouriteLocations.get(position));


                final int favPosition = position;
                final Context context = App.getContext();

                new AlertDialog.Builder(Favourites.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("You're about to remove this favourite forever...")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("Removing", "Location");
                                favouriteLocations.remove(favPosition);
                                Favourites.arrayAdapter.notifyDataSetChanged();
                                Toast.makeText(context,"Removed!", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }

        });

    }

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

    public static void sortMyList() {
        Log.i("Sort List", "" + favouriteLocations.size());
        if (favouriteLocations.size() > 0) {
            Collections.sort(favouriteLocations);
//            arrayAdapter.notifyDataSetChanged();
        }
    }

    public static void saveFavs() {
        Log.i("Shared Prefs", "Saving Favs");
        try {

            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> latitudes = new ArrayList<>();
            ArrayList<String> longitudes = new ArrayList<>();
            ArrayList<String> addresses = new ArrayList<>();
            ArrayList<String> comments = new ArrayList<>();

            for (markedLocation location : favouriteLocations) {
                names.add(location.name);
                latitudes.add(Double.toString(location.location.latitude));
                longitudes.add(Double.toString(location.location.longitude));
                addresses.add(location.address);
                comments.add(location.comment);
            }

            sharedPreferences.edit().putString("names", ObjectSerializer.serialize(names)).apply();
            sharedPreferences.edit().putString("latitudes", ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("longitudes", ObjectSerializer.serialize(longitudes)).apply();
            sharedPreferences.edit().putString("addresses", ObjectSerializer.serialize(addresses)).apply();
            sharedPreferences.edit().putString("comments", ObjectSerializer.serialize(comments)).apply();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Adding Favs", "Failed attempt");
        }
    }

    public static void loadFavs() {
        Log.i("Shared Prefs", "Loading Favs");

        favouriteLocations.clear();

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();
        ArrayList<String> addresses = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();

        names.clear();
        latitudes.clear();
        longitudes.clear();
        addresses.clear();
        comments.clear();

        try {

            names = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("names", ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitudes", ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitudes", ObjectSerializer.serialize(new ArrayList<String>())));
            addresses = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("addresses", ObjectSerializer.serialize(new ArrayList<String>())));
            comments = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("comments", ObjectSerializer.serialize(new ArrayList<String>())));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (names.size() > 0 && latitudes.size() > 0 && longitudes.size() > 0) {
            // we've checked there is some info
            if (names.size() == latitudes.size() && latitudes.size() == longitudes.size()) {
                // we've checked each item has the same amout of info, nothing is missing

                for (int i = 0; i < names.size(); i++) {
                    LatLng pos = new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(longitudes.get(i)));
                    markedLocation newLoc = new markedLocation(names.get(i), addresses.get(i), pos, comments.get(i));
                    favouriteLocations.add(newLoc);
                }

            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Favs Activity", "On Pause");

        saveFavs();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Favs Activity","On Stop");
        saveFavs();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Favs Activity", "On Resume");

        sortMyList();
        arrayAdapter.notifyDataSetChanged();
    }


}
