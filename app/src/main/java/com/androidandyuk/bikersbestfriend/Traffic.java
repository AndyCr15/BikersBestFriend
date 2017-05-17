package com.androidandyuk.bikersbestfriend;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Traffic extends AppCompatActivity {

    public static ArrayList<TrafficEvent> trafficEvents = new ArrayList<>();

    static ArrayAdapter arrayAdapter;
    ListView trafficList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);

//        DownloadTraffic task = new DownloadTraffic();
//        task.execute("http://m.highways.gov.uk/feeds/rss/AllEvents.xml");

        setTitle("Traffic");

        // download traffic here
        Log.i("Traffic","Download Here");

        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();

            SAXHandler handler = new SAXHandler();

            Resources res = this.getResources();
            InputStream source = res.openRawResource(R.raw.allevents);
            parser.parse(source, handler);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initiateList();

    }

    private void initiateList() {
        Collections.sort(trafficEvents);
        for(int i=0; i < trafficEvents.size();i++){
            if(trafficEvents.get(i).delay.equals("No Delay")){
                trafficEvents.remove(i);
            }
        }

        Log.i("Initiating List", "List size " + trafficEvents.size());
        trafficList = (ListView) findViewById(R.id.trafficList);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, trafficEvents);
        trafficList.setAdapter(arrayAdapter);
    }

    public void viewTrafficOnMap(View view){
        Log.i("Traffic","View on map called");
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        // 9998 tells the Maps activity to show all the markers
        intent.putExtra("placeNumber", 9998);
        intent.putExtra("Type", "Traffic");
        startActivity(intent);
    }

}
