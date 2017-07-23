package com.androidandyuk.bikersbestfriend;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.androidandyuk.bikersbestfriend.MainActivity.activeBike;
import static com.androidandyuk.bikersbestfriend.MainActivity.backgroundsWanted;
import static com.androidandyuk.bikersbestfriend.MainActivity.bikes;
import static com.androidandyuk.bikersbestfriend.MainActivity.conversion;
import static com.androidandyuk.bikersbestfriend.MainActivity.milesSetting;
import static com.androidandyuk.bikersbestfriend.MainActivity.oneDecimal;


public class PartsLog extends AppCompatActivity {

    public static RelativeLayout main;

    TextView bikeTitle;

    TextView padsMiles;
    TextView padsDate;
    TextView discsMiles;
    TextView discsDate;
    TextView frontMiles;
    TextView frontDate;
    TextView rearMiles;
    TextView rearDate;
    TextView oilMiles;
    TextView oilDate;
    TextView batteryMiles;
    TextView batteryDate;
    TextView coolantMiles;
    TextView coolantDate;
    TextView sparksMiles;
    TextView sparksDate;
    TextView airMiles;
    TextView airDate;
    TextView fluidMiles;
    TextView fluidDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parts_log);

        bikeTitle = (TextView) findViewById(R.id.bikeTitle);

        padsMiles = (TextView) findViewById(R.id.padsMiles);
        padsDate = (TextView) findViewById(R.id.padsDate);
        discsMiles = (TextView) findViewById(R.id.discsMiles);
        discsDate = (TextView) findViewById(R.id.discsDate);
        frontMiles = (TextView) findViewById(R.id.frontMiles);
        frontDate = (TextView) findViewById(R.id.frontDate);
        rearMiles = (TextView) findViewById(R.id.rearMiles);
        rearDate = (TextView) findViewById(R.id.rearDate);
        oilMiles = (TextView) findViewById(R.id.oilMiles);
        oilDate = (TextView) findViewById(R.id.oilDate);
        batteryMiles = (TextView) findViewById(R.id.batteryMiles);
        batteryDate = (TextView) findViewById(R.id.batteryDate);
        coolantMiles = (TextView) findViewById(R.id.coolantMiles);
        coolantDate = (TextView) findViewById(R.id.coolantDate);
        sparksMiles = (TextView) findViewById(R.id.sparksMiles);
        sparksDate = (TextView) findViewById(R.id.sparksDate);
        airMiles = (TextView) findViewById(R.id.airMiles);
        airDate = (TextView) findViewById(R.id.airDate);
        fluidMiles = (TextView) findViewById(R.id.fluidMiles);
        fluidDate = (TextView) findViewById(R.id.fluidDate);


        bikeTitle.setText(bikes.get(activeBike).toString());

        Bike thisBike = bikes.get(activeBike);
        for (int i = (thisBike.maintenanceLogs.size()-1); i > -1; i--) {
            maintenanceLogDetails thisLog = thisBike.maintenanceLogs.get(i);

            Double miles = thisBike.estMileage - thisLog.mileage;
            // check what setting the user has, Miles or Km
            // if Km, convert to Miles for display
            if(milesSetting.equals("Km")){
                miles = miles / conversion;
            }
            String milesSince = oneDecimal.format(miles) + " ";

            if(thisLog.brakePads){
                padsMiles.setText(milesSince + milesSetting + " ago");
                padsDate.setText(thisLog.date);
            }

            if(thisLog.brakeDiscs){
                discsMiles.setText(milesSince + milesSetting + " ago");
                discsDate.setText(thisLog.date);
            }

            if(thisLog.frontTyre){
                frontMiles.setText(milesSince + milesSetting + " ago");
                frontDate.setText(thisLog.date);
            }

            if(thisLog.rearTyre){
                rearMiles.setText(milesSince + milesSetting + " ago");
                rearDate.setText(thisLog.date);
            }

            if(thisLog.oilChange){
                oilMiles.setText(milesSince + milesSetting + " ago");
                oilDate.setText(thisLog.date);
            }

            if(thisLog.newBattery){
                batteryMiles.setText(milesSince + milesSetting + " ago");
                batteryDate.setText(thisLog.date);
            }

            if(thisLog.coolantChange){
                coolantMiles.setText(milesSince + milesSetting + " ago");
                coolantDate.setText(thisLog.date);
            }

            if(thisLog.sparkPlugs){
                sparksMiles.setText(milesSince + milesSetting + " ago");
                sparksDate.setText(thisLog.date);
            }

            if(thisLog.airFilter){
                airMiles.setText(milesSince + milesSetting + " ago");
                airDate.setText(thisLog.date);
            }

            if(thisLog.brakeFluid){
                fluidMiles.setText(milesSince + milesSetting + " ago");
                fluidDate.setText(thisLog.date);
            }
        }

    }

    public void checkBackground() {
        main = (RelativeLayout) findViewById(R.id.main);
        if(backgroundsWanted){
            int resID = getResources().getIdentifier("background_portrait", "drawable",  this.getPackageName());
            Drawable drawablePic = getResources().getDrawable(resID);
            PartsLog.main.setBackground(drawablePic);
        } else {
            PartsLog.main.setBackgroundColor(getResources().getColor(R.color.background));
        }
    }

    @Override
    public void onBackPressed() {
        // this must be empty as back is being dealt with in onKeyDown
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBackground();
    }
}
