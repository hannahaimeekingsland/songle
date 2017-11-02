package com.example.hannah.songle;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    static String TAG = "MainActivity";
    static Random rand = new Random();
    static int number = rand.nextInt(18) + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        System.out.println(">>>>>>>>>>>>>>>>>>> In main activity");

        //NetworkActivity.DownloadXmlTask download = new NetworkActivity.DownloadXmlTask();
        String XMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml";
        DownloadXml downloadXml = new DownloadXml();
        ArrayList<DownloadXml.Entry> output = new ArrayList<DownloadXml.Entry>();
        //System.out.println(">>>>>>>>>>>>>>>>>>> Instantiated downloadXml");
        downloadXml.execute(XMLURL);
        String KMLURL = "";
        if (number < 10) {
            KMLURL ="http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + Integer.parseInt(String.valueOf(number)) + "/map1.kml";
        } else {
            KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + Integer.parseInt(String.valueOf(number)) + "/map1.kml";
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>" + KMLURL);
        DownloadKml downloadKml = new DownloadKml();
        ArrayList<DownloadKml.Point> result = new ArrayList<DownloadKml.Point>();
        downloadKml.execute(KMLURL);
        System.out.println(">>>>>>>>>>>>>>>>>>> executed KML");
        try {
            output = downloadXml.get();
            result = downloadKml.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra("parsedKml", result);
        System.out.println(">>>>>>>>>>>>>>>>>>> sent parsedKml");


        Button playButton = (Button) (findViewById(R.id.button2));
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LevelChoice.class));
            }
        });

        /*ImageView settingsButton = (ImageView) (findViewById(R.id.imageView4));
        // set a onclick listener for when the button gets clicked
        settingsButton.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, SettingsScreen.class);
                startActivity(mainIntent);
            }
        });*/
    }


}



