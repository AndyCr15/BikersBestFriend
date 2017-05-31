package com.androidandyuk.bikersbestfriend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.androidandyuk.bikersbestfriend.MainActivity.oneDecimal;

public class RaceTracks extends AppCompatActivity {
    static List<markedLocation> trackLocations = new ArrayList<>();
    static MyLocationAdapter myAdapter;

    private FirebaseAnalytics mFirebaseAnalytics;


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
            myAdapter.notifyDataSetChanged();
        }

    }

    public class MyLocationAdapter extends BaseAdapter {
        public List<markedLocation> locationDataAdapter;

        public MyLocationAdapter(List<markedLocation> locationDataAdapter) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_tracks);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Log.i("Race Tracks", "onCreate");

        ListView listView = (ListView) findViewById(R.id.listTracks);

        myAdapter = new MyLocationAdapter(trackLocations);

        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), LocationInfoActivity.class);
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


