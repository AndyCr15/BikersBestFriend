package com.androidandyuk.bikersbestfriend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class Fueling extends AppCompatActivity {

    static ArrayList<fuelingDetails> fuelings = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    static SharedPreferences sharedPreferences;

    ListView listView;
    EditText milesDone;
    EditText petrolPrice;
    EditText litresUsed;
    View fuelingDetailsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fueling);

        listView = (ListView) findViewById(R.id.listView);

        fuelingDetailsLayout = (View)findViewById(R.id.fuelingDetailsLayout);

        milesDone = (EditText) findViewById(R.id.milesDone);
        petrolPrice = (EditText) findViewById(R.id.petrolPrice);
        litresUsed = (EditText) findViewById(R.id.litresUsed);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, fuelings);

        listView.setAdapter(arrayAdapter);

    }

    public void showFueling(View view) {
        // opens the add fueling dialog
        Log.i("Fueling", "Adding fuel up");
        fuelingDetailsLayout.setVisibility(View.VISIBLE);

    }

    public void addFueling(View view) {
        int miles = Integer.parseInt(milesDone.getText().toString());
        double price = Double.parseDouble(petrolPrice.getText().toString());
        double litres = Double.parseDouble(litresUsed.getText().toString());
        fuelingDetails today = new fuelingDetails(miles, price, litres);
        fuelings.add(today);
        arrayAdapter.notifyDataSetChanged();
        fuelingDetailsLayout.setVisibility(View.INVISIBLE);
    }

}
