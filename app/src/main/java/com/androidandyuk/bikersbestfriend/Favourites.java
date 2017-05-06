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
import java.util.List;

public class Favourites extends AppCompatActivity {
    static List<markedLocation> favouriteLocations = new ArrayList<>();
    static ArrayAdapter arrayAdapter;

    public void addFav(View view){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("Favourites", "onCreate");

        setContentView(R.layout.activity_favourites);

        ListView listView = (ListView) findViewById(R.id.listView);

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

//    public String toString() {
//        return "markedLocation{" +
//                "name='" + markedLocation.class.toString() + '\'' +
//                ", address='" + this.address + '\'' +
//                ", comment='" + this.comment + '\'' +
//                '}';
//    }
}
