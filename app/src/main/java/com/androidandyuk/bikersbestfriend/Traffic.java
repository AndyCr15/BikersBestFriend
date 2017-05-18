package com.androidandyuk.bikersbestfriend;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
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

    private DownloadManager downloadManager;
    public static long downloadId;
    private Handler handlerUpdate;
    private static boolean updateTraffic = true;


    private Runnable runnableUpdate = new Runnable() {
        @Override
        public void run() {
            if (done)
                return;
            handlerUpdate.postDelayed(this, 1000);
        }
    };
    private volatile boolean done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);

        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(new DownloadReceiver(), intentFilter);

//        Log.i("Traffic","Setting update to True");
//        updateTraffic = true;

        setTitle("Traffic");

        // download traffic here
        Log.i("Traffic", "Download Here");

        if (updateTraffic) {
            downloadTraffic();
            updateTraffic = false;
        }

        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();

            SAXHandler handler = new SAXHandler();

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator +
                    "allevents.xml");
            Uri path = Uri.fromFile(file);

            Log.i("File", "" + file);
            Log.i("Path", "" + path);

//            Resources res = this.getResources();
            InputStream source = new FileInputStream("/storage/emulated/0/Download/allevents.xml");
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
        for (int i = 0; i < trafficEvents.size(); i++) {
            if (trafficEvents.get(i).delay.equals("No Delay")) {
                trafficEvents.remove(i);
//                break;
            }
            // if there's another entry, check if it's the same reason
            // if it is, remove it
            if (i + 1 < trafficEvents.size()) {
                if (trafficEvents.get(i).title.equals(trafficEvents.get(i).title)) {
                    trafficEvents.remove(i);
                }
            }
        }

        Log.i("Initiating List", "List size " + trafficEvents.size());
        trafficList = (ListView) findViewById(R.id.trafficList);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, trafficEvents);
        trafficList.setAdapter(arrayAdapter);
    }

    public void viewTrafficOnMap(View view) {
        Log.i("Traffic", "View on map called");
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        // 9998 tells the Maps activity to show all the markers
        intent.putExtra("placeNumber", 9998);
        intent.putExtra("Type", "Traffic");
        startActivity(intent);
    }

    public void downloadTraffic() {
        done = false;
        Uri uriDownload = Uri.parse("http://m.highways.gov.uk/feeds/rss/AllEvents.xml");
        downloadFile(uriDownload);
    }

    private void downloadFile(Uri uri) {
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("allevents.xml");
        request.setDescription("Downloading Traffic Demo");

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "allevents.xml");
        downloadId = downloadManager.enqueue(request);
        handlerUpdate = new Handler();
        handlerUpdate.post(runnableUpdate);
    }

    public class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long refId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (refId == downloadId) {
                Toast.makeText(context, "Traffic File Updated", Toast.LENGTH_SHORT).show();
                initiateList();
            }
        }
    }

    @Override
    protected void onStop() {
//        unregisterReceiver(DownloadReceiver);
        super.onStop();
    }
}
