package com.example.hannah.songle;

import java.util.Random;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NetworkActivity extends FragmentActivity implements DownloadCallback {
    static Random rand = new Random();
    static int number = rand.nextInt(18) + 1;
    private NetworkFragment mNetworkFragment;
    private boolean mDownloading = false;
    public String TAG = "Network Activity";

    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String XMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml";
    private static final String KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + Integer.parseInt(String.valueOf(number)) + "/map5.kml";


    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;
    public static String sPref = null;

    // Uses AsyncTask to download the XML feed from the webpage.
    public void loadPage() {

        if ((sPref.equals(ANY)) && (wifiConnected || mobileConnected)) {
            new DownloadXmlTask().execute(XMLURL);
            new DownloadKmlTask().execute(KMLURL);

        } else if ((sPref.equals(WIFI)) && (wifiConnected)) {
            new DownloadXmlTask().execute(XMLURL);
            new DownloadKmlTask().execute(KMLURL);
        } else {
            // show error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_network);
        //change
        if(getIntent().getStringExtra("unique").equals("xml")) {
            mNetworkFragment = NetworkFragment.getInstance(getFragmentManager(), "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml");
        } else if (getIntent().getStringExtra("unique").equals("kml")) {
            mNetworkFragment = NetworkFragment.getInstance(getFragmentManager(), "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + Integer.parseInt(String.valueOf(number)) + "/map" +
                    "5.kml");
        } else if (getIntent().getStringExtra("unique").equals("lyrics")) {
            mNetworkFragment = NetworkFragment.getInstance(getFragmentManager(), "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + Integer.parseInt(String.valueOf(number)) + "/lyrics.txt");
        }
    }

    private void startDownload() {
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment.startDownload();
            mDownloading = true;
        }
    }

    @Override
    public void updateFromDownload(Object result) {
        Intent intent = new Intent();
        //intent.putExtra();
        //arraylist
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch (progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:

                break;
            case Progress.CONNECT_SUCCESS:

                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:

                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }


    // Implementation of AsyncTask used to download XML feed from the webpage.
    public class DownloadXmlTask extends AsyncTask<String, Void, ArrayList<XmlParser.Entry>> {
        @Override
        protected ArrayList<XmlParser.Entry> doInBackground(String... urls) {
            try {
                InputStream stream = new ByteArrayInputStream(urls[0].getBytes(StandardCharsets.UTF_8.name()));
                XmlParser xml_parser = new XmlParser();
                return xml_parser.parse(stream);
            } catch (IOException e) {
                return null;
            } catch (XmlPullParserException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<XmlParser.Entry> result) {
            //setContentView(R.layout.activity_main);
            Log.d(TAG, "<Song>");
            for (XmlParser.Entry e : result) {
                System.out.println(e.number);
                System.out.println(e.artist);
                System.out.println(e.title);
                System.out.println(e.link);
            }
            System.out.println("</Song>");
        }
    }

    // Implementation of AsyncTask used to download XML feed from the webpage.
    private class DownloadKmlTask extends AsyncTask<String, Void, ArrayList<KmlParser.Point>> {
        @Override
        protected ArrayList<KmlParser.Point> doInBackground(String... urls) {
            try {
                InputStream stream = new ByteArrayInputStream(urls[0].getBytes(StandardCharsets.UTF_8.name()));
                KmlParser kml_parser = new KmlParser();
                return kml_parser.parse(stream);
            } catch (IOException e) {
                return null;
            } catch (XmlPullParserException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<KmlParser.Point> result) {
            //setContentView(R.layout.activity_main);
        }
    }
}

