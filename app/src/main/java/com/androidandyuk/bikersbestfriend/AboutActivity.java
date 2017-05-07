package com.androidandyuk.bikersbestfriend;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void sendFeedback(View view){
        Toast.makeText(this, "Until I implement this properly - email AndyCr15@gmail.com", Toast.LENGTH_LONG).show();
    }
}
