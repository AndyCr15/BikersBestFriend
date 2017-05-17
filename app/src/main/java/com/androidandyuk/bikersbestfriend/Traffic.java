package com.androidandyuk.bikersbestfriend;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Traffic extends AppCompatActivity {

    public static ArrayList<TrafficEvent> trafficEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);

//        DownloadTraffic task = new DownloadTraffic();
//        task.execute("http://m.highways.gov.uk/feeds/rss/AllEvents.xml");

        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();

            SAXHandler handler = new SAXHandler();
            InputSource source = new InputSource(new FileReader("xml/allevents.xml"));
            source.setEncoding("utf-8");
            parser.parse(source, handler);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.i("Traffic Size", "" + trafficEvents.size());

    }

    public class DownloadTraffic extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            Log.i("Download Traffic" , "doInBackground");

            String result = "";
            URL url = null;


            try {

                url = new URL("http://m.highways.gov.uk/feeds/rss/AllEvents.xml");
                URLConnection urlConnection = url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                InputStreamReader reader = new InputStreamReader(in);
                int data;

                data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    try {
                        data = reader.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            Log.i("Download Traffic" , "onPostExecute");

            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = null;
            try {
                parser = parserFactory.newSAXParser();
                SAXHandler handler = new SAXHandler();
                parser.parse(result, handler);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
