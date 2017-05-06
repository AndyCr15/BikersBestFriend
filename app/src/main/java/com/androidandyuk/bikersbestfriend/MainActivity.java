package com.androidandyuk.bikersbestfriend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<markedLocation> favouriteLocations = new ArrayList<>();
    static ArrayList<markedLocation> presetLocations = new ArrayList<>();
    static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
