package com.androidandyuk.bikersbestfriend;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Collections;

import static com.androidandyuk.bikersbestfriend.Garage.activeBike;
import static com.androidandyuk.bikersbestfriend.Garage.bikes;

public class Maintenance extends AppCompatActivity {

    static ArrayAdapter arrayAdapter;
    ListView maintList;

    View logDetails;

    EditText logString;
    EditText logCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_log);

        logDetails = findViewById(R.id.logDetails);

        logString = (EditText) findViewById(R.id.logString);
        logCost = (EditText) findViewById(R.id.logCost);

        maintenanceLogDetails newLog = new maintenanceLogDetails("Testing");
        bikes.get(activeBike).maintenanceLogs.add(newLog);

        maintList = (ListView) findViewById(R.id.maintenanceList);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, bikes.get(activeBike).maintenanceLogs);

        maintList.setAdapter(arrayAdapter);
    }

    public void showAddLog(View view) {
        // opens the add log dialog
        Log.i("Maintenance", "Adding a log");
        logDetails.setVisibility(View.VISIBLE);
    }

    public void addLog(View view) {
        Log.i("Maintenance", "Taking details and adding");
        Double cost = 0d;

        try {
            cost = Double.parseDouble(logCost.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String logInfo = logString.getText().toString();
        maintenanceLogDetails today = new maintenanceLogDetails(logInfo, cost);
        bikes.get(activeBike).maintenanceLogs.add(today);
        Collections.sort(bikes.get(activeBike).maintenanceLogs);
        arrayAdapter.notifyDataSetChanged();
        logDetails.setVisibility(View.INVISIBLE);

        logString.setText(null);
        logString.clearFocus();
        logCost.setText(null);
        logCost.clearFocus();

        // Check if no view has focus:
        View logDetails = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
}
