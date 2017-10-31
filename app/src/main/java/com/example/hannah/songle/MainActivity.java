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

public class MainActivity extends AppCompatActivity {
    static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //NetworkActivity.DownloadXmlTask download = new NetworkActivity.DownloadXmlTask();
//        String URL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml";
//        DownloadXML download = new DownloadXML();
//        ArrayList<DownloadXML.Entry> output = new ArrayList<DownloadXML.Entry>();
//        download.execute(URL);

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



