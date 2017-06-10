package com.androidandyuk.bikersbestfriend;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static com.androidandyuk.bikersbestfriend.MainActivity.activeBike;
import static com.androidandyuk.bikersbestfriend.MainActivity.bikes;
import static com.androidandyuk.bikersbestfriend.MainActivity.oneDecimal;
import static com.androidandyuk.bikersbestfriend.MainActivity.sdf;
import static com.androidandyuk.bikersbestfriend.R.id.mileage;
import static com.androidandyuk.bikersbestfriend.MainActivity.ed;
import static com.androidandyuk.bikersbestfriend.MainActivity.sharedPreferences;

public class Fuelling extends AppCompatActivity {

    static MyFuelAdapter myAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    static int lastHowManyFuels = 10;
    private boolean showingAddFueling = false;

    private DatePickerDialog.OnDateSetListener fuelDateSetListener;

    String fuelingDate;

    ListView listView;
    EditText milesDone;
    EditText petrolPrice;
    EditText litresUsed;
    EditText mileageText;
    TextView setFuelDate;

    View fuelingDetailsLayout;

    // used to store what item might be being edited or deleted
    int itemLongPressedPosition = 0;
    fuellingDetails itemLongPressed;
    String editDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fueling);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // until I implement landscape view, lock the orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sharedPreferences = this.getSharedPreferences("com.androidandyuk.bikersbestfriend", Context.MODE_PRIVATE);
        ed = sharedPreferences.edit();

        // set the date for a new fueling to today
        setFuelDate = (TextView) findViewById(R.id.setFuelDate);
        Calendar date = Calendar.getInstance();
        String today = sdf.format(date.getTime());
        setFuelDate.setText(today);


        Log.i("Fuelling", "Loading Fuels");
        loadFuels();

        initiateList();

        //needed for editing a fueling

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // this is for editing a fueling, it stores the info in itemLongPressed
                itemLongPressedPosition = position;
                itemLongPressed = bikes.get(activeBike).fuelings.get(position);
                Log.i("Fuel List", "Tapped " + position);

                setFuelDate.setText(bikes.get(activeBike).fuelings.get(position).getDate());
                milesDone.setText(Double.toString(bikes.get(activeBike).fuelings.get(position).getMiles()));
                petrolPrice.setText(Double.toString(bikes.get(activeBike).fuelings.get(position).getPrice()));
                litresUsed.setText(Double.toString(bikes.get(activeBike).fuelings.get(position).getLitres()));
                editDate = bikes.get(activeBike).fuelings.get(position).getDate();
//                bikes.get(activeBike).fuelings.remove(position);
                fuelingDetailsLayout.setVisibility(View.VISIBLE);
                showingAddFueling = true;

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int fuelPosition = position;
                final Context context = App.getContext();

                new AlertDialog.Builder(Fuelling.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("You're about to delete this log forever...")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("Removing", "Log " + fuelPosition);
                                bikes.get(activeBike).fuelings.remove(fuelPosition);
                                initiateList();
                                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }


        });


        fuelDateSetListener = new DatePickerDialog.OnDateSetListener()

        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar date = Calendar.getInstance();
                date.set(year, month, day);
                String sdfDate = sdf.format(date.getTime());
                Log.i("Chosen Date", sdfDate);
                setFuelDate.setText(sdfDate);
            }
        };

    }

    public void setFuelDate(View view) {
        String thisDateString = "";
        // this sets what date will show when the date picker shows
        // first check if we're editing a current fueling
        if (itemLongPressed != null) {
            thisDateString = bikes.get(activeBike).fuelings.get(itemLongPressedPosition).getDate();
        }
        Date thisDate = new Date();
        try {
            thisDate = sdf.parse(thisDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // for some reason I can't getYear from thisDate, so will just use the current year
        Calendar cal = Calendar.getInstance();
        cal.setTime(thisDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                Fuelling.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                fuelDateSetListener,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private class MyFuelAdapter extends BaseAdapter {
        public ArrayList<fuellingDetails> fuelDataAdapter;

        public MyFuelAdapter(ArrayList<fuellingDetails> fuelDataAdapter) {
            this.fuelDataAdapter = fuelDataAdapter;
        }

        @Override
        public int getCount() {
            return fuelDataAdapter.size();
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
            View myView = mInflater.inflate(R.layout.fuel_listview, null);

            final fuellingDetails s = fuelDataAdapter.get(position);

            TextView fuelDate = (TextView) myView.findViewById(R.id.fuelDate);
            fuelDate.setText(s.date.substring(0, s.date.length() - 5));

            TextView milesDone = (TextView) myView.findViewById(R.id.milesDone);
            milesDone.setText(oneDecimal.format(s.miles) + " miles");

            TextView fuelMPG = (TextView) myView.findViewById(R.id.fuelMPG);
            fuelMPG.setText(oneDecimal.format(s.mpg) + "mpg");

            return myView;
        }

    }

    private void initiateList() {
        Log.i("Fuelling", "Initiating List");
        listView = (ListView) findViewById(R.id.fuelList);

        fuelingDetailsLayout = findViewById(R.id.fuelingDetailsLayout);

        setFuelDate = (TextView) findViewById(R.id.setFuelDate);
        milesDone = (EditText) findViewById(R.id.milesDone);
        petrolPrice = (EditText) findViewById(R.id.petrolPrice);
        litresUsed = (EditText) findViewById(R.id.litresUsed);
        mileageText = (EditText) findViewById(mileage);


        Log.i("Fuelling", "Setting myAdapter");
        myAdapter = new MyFuelAdapter(bikes.get(activeBike).fuelings);

        listView.setAdapter(myAdapter);

        updateAveMPG();

        setTitle("Fuelling: " + bikes.get(activeBike).model);

    }

    public void showFueling(View view) {
        // opens the add fueling dialog
        Log.i("Fuelling", "Adding fuel up");
        fuelingDetailsLayout.setVisibility(View.VISIBLE);
        showingAddFueling = true;

        // reset all info in the box. If it's an edit, this will be overwritten with info anyway
        // set the date for a new fueling to today
        setFuelDate = (TextView) findViewById(R.id.setFuelDate);
        Calendar date = Calendar.getInstance();
        String today = sdf.format(date.getTime());
        setFuelDate.setText(today);
        // clear previous entries
        milesDone.setText(null);
        milesDone.clearFocus();
        petrolPrice.setText(null);
        petrolPrice.clearFocus();
        litresUsed.setText(null);
        litresUsed.clearFocus();


    }

    public static String aveMPG(int bikeID, int numberOfFuelings) {
        double totalMiles = 0;
        double totalLitres = 0;
        int count = 0;
        Bike thisBike = MainActivity.bikes.get(bikeID);
        if (numberOfFuelings > thisBike.fuelings.size()) {
            numberOfFuelings = thisBike.fuelings.size();
        }


        for (int i = 0; i < numberOfFuelings; i++) {
            count++;
            totalMiles += thisBike.fuelings.get(i).miles;
            totalLitres += thisBike.fuelings.get(i).litres;

        }
        Log.i("Calculating MPG", "" + count);
        if (totalLitres > 0) {
            double mpg = totalMiles / (totalLitres / 4.54609);
            DecimalFormat numberFormat = new DecimalFormat("#.0");
            return numberFormat.format(mpg);
        } else return "No Fuels";
    }

    public void addFuelingClicked(View view) {
        addFueling();
    }

    public void addFueling() {
        // only add the details if all three important details have information in
        if (milesDone.getText().toString().isEmpty() || petrolPrice.getText().toString().isEmpty() || litresUsed.getText().toString().isEmpty()) {

            Toast.makeText(Fuelling.this, "Please complete all necessary details", Toast.LENGTH_LONG).show();

        } else {

            // check if we're adding as it was being edited
            if (itemLongPressed != null) {
                bikes.get(activeBike).fuelings.remove(itemLongPressed);
                itemLongPressed = null;
            }

            String date = setFuelDate.getText().toString();
            double miles = Double.parseDouble(milesDone.getText().toString());
            double price = Double.parseDouble(petrolPrice.getText().toString());
            double litres = Double.parseDouble(litresUsed.getText().toString());
            int mileage;
            if (mileageText.getText().toString().isEmpty()) {
                mileage = 0;
            } else {
                mileage = Integer.parseInt(mileageText.getText().toString());
            }
            fuellingDetails today = new fuellingDetails(miles, price, litres, date, mileage);
            bikes.get(activeBike).fuelings.add(today);
            Collections.sort(bikes.get(activeBike).fuelings);
            myAdapter.notifyDataSetChanged();
            fuelingDetailsLayout.setVisibility(View.INVISIBLE);
            showingAddFueling = false;

            updateAveMPG();

            View thisView = this.getCurrentFocus();
            if (thisView != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(thisView.getWindowToken(), 0);
            }

            // clear previous entries
            milesDone.setText(null);
            milesDone.clearFocus();
            petrolPrice.setText(null);
            petrolPrice.clearFocus();
            litresUsed.setText(null);
            litresUsed.clearFocus();
        }
    }

    public void updateAveMPG() {

        TextView mpgView = (TextView) findViewById(R.id.mpgView);

        int fuelingsForAve = lastHowManyFuels;

        if (bikes.get(activeBike).fuelings.size() < fuelingsForAve) {
            fuelingsForAve = bikes.get(activeBike).fuelings.size();
        }
        String mpg = aveMPG(activeBike, fuelingsForAve);
        if (mpg.equals("No Fuels")) {
            mpgView.setText("Your average will appear here once you've recorded a refuel");
        } else {
            mpgView.setText("Ave MPG over the last " + fuelingsForAve + " stops is " + mpg + " mpg");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bike_choice, menu);

        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "Settings").setShortcut('3', 'c');

        for (int i = 0; i < bikes.size(); i++) {
            String bikeMakeMenu = bikes.get(i).model;
            menu.add(0, i + 1, 0, bikeMakeMenu).setShortcut('3', 'c');
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                Log.i("Option", "0");
                Toast.makeText(Fuelling.this, "Settings not yet implemented", Toast.LENGTH_LONG).show();
                return true;
            case 1:
                Log.i("Option", "1");
                activeBike = 0;
                initiateList();
                return true;
            case 2:
                Log.i("Option", "2");
                activeBike = 1;
                initiateList();
                return true;
            case 3:
                Log.i("Option", "3");
                activeBike = 2;
                initiateList();
                return true;
            case 4:
                Log.i("Option", "4");
                activeBike = 3;
                initiateList();
                return true;
            case 5:
                Log.i("Option", "5");
                activeBike = 4;
                initiateList();
                return true;
            case 6:
                Log.i("Option", "6");
                activeBike = 5;
                initiateList();
                return true;
            case 7:
                Log.i("Option", "7");
                activeBike = 6;
                initiateList();
                return true;
            case 8:
                Log.i("Option", "8");
                activeBike = 7;
                initiateList();
                return true;
            case 9:
                Log.i("Option", "9");
                activeBike = 8;
                initiateList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void saveFuels() {

        for (Bike thisBike : bikes) {

            Log.i("Saving Fuellings", "" + thisBike);
            try {
                ArrayList<String> fdates = new ArrayList<>();
                ArrayList<String> miles = new ArrayList<>();
                ArrayList<String> prices = new ArrayList<>();
                ArrayList<String> litres = new ArrayList<>();
                ArrayList<String> mileage = new ArrayList<>();

                // I think these are new variables, so likely don't need clearing?
                fdates.clear();
                miles.clear();
                prices.clear();
                litres.clear();
                mileage.clear();

                for (fuellingDetails thisLog : thisBike.fuelings) {

                    fdates.add(thisLog.date);
                    miles.add(Double.toString(thisLog.miles));
                    prices.add(Double.toString(thisLog.price));
                    litres.add(Double.toString(thisLog.litres));
                    mileage.add(Double.toString(thisLog.mileage));

                }

                Log.i("Saving Fuels", "Size :" + fdates.size());
                ed.putString("fdates" + thisBike.bikeId, ObjectSerializer.serialize(fdates)).apply();
                ed.putString("miles" + thisBike.bikeId, ObjectSerializer.serialize(miles)).apply();
                ed.putString("prices" + thisBike.bikeId, ObjectSerializer.serialize(prices)).apply();
                ed.putString("litres" + thisBike.bikeId, ObjectSerializer.serialize(litres)).apply();
                ed.putString("mileage" + thisBike.bikeId, ObjectSerializer.serialize(mileage)).apply();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Adding fuellings", "Failed attempt");
            }
        }
    }

    public static void loadFuels() {

        for (Bike thisBike : bikes) {
            thisBike.fuelings.clear();

            Log.i("Loading Fuels", "" + thisBike);

            ArrayList<String> fdates = new ArrayList<>();
            ArrayList<String> miles = new ArrayList<>();
            ArrayList<String> prices = new ArrayList<>();
            ArrayList<String> litres = new ArrayList<>();
            ArrayList<String> mileage = new ArrayList<>();

            // I think these are new variables, so likely don't need clearing?
            fdates.clear();
            miles.clear();
            prices.clear();
            litres.clear();
            mileage.clear();

            try {

                fdates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("fdates" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                miles = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("miles" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                prices = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("prices" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                litres = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("litres" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                mileage = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("mileage" + thisBike.bikeId, ObjectSerializer.serialize(new ArrayList<String>())));
                Log.i("fDates for " + thisBike, "Count :" + fdates.size());
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Loading details", "Failed attempt");
            }

            Log.i("Retrieved info" + thisBike, "Log count :" + fdates.size());
            if (fdates.size() > 0 && miles.size() > 0 && prices.size() > 0 && litres.size() > 0) {
                // we've checked there is some info
                if (fdates.size() == miles.size() && miles.size() == prices.size() && prices.size() == litres.size()) {
                    // we've checked each item has the same amount of info, nothing is missing
                    for (int x = 0; x < fdates.size(); x++) {
                        Date thisDate = new Date();
                        try {
                            thisDate = sdf.parse(fdates.get(x));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        fuellingDetails newLog = new fuellingDetails(Double.parseDouble(miles.get(x)), Double.parseDouble(prices.get(x)), Double.parseDouble(litres.get(x)), thisDate, Double.parseDouble(mileage.get(x)));
                        Log.i("Adding", "" + x + "" + newLog);
                        thisBike.fuelings.add(newLog);
                    }

                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // this must be empty as back is being dealt with in onKeyDown
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            // check if the back button was pressed with the add item view showing
            // if it was, hide this view.  If not, carry on as normal.
            if (showingAddFueling) {

                showingAddFueling = false;
                fuelingDetailsLayout.setVisibility(View.INVISIBLE);
                // reset the fuelling text boxes?

            } else {
                finish();
                return true;
            }
        }
        myAdapter.notifyDataSetChanged();
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Fuelling Activity", "On Pause");
        saveFuels();
    }
}
