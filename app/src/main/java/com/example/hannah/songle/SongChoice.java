package com.example.hannah.songle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by s1518196 on 27/11/17.
 */

public class SongChoice extends Activity {
    //Number of songs in KML
    int numButtons;
    String levelChoice;
    String lyricsURL = "";
    String KMLURL = "";
    String songName = "";
    ArrayList<DownloadXml.Entry> output = new ArrayList<DownloadXml.Entry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_choice);
        final Intent intent = new Intent(SongChoice.this, MapsActivity.class);
        levelChoice = getIntent().getStringExtra("levelChoice");
        String XMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml";
        DownloadXml downloadXml = new DownloadXml();
        downloadXml.execute(XMLURL);
        try {
            output = downloadXml.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        for (DownloadXml.Entry entry : output) {
            numButtons = Integer.parseInt(entry.number);
        }
        Random rand = new Random();
        final int number = rand.nextInt(numButtons) + 1;

        //Handle KMLURL and lyrics for random instance
        Button random = (Button) (findViewById(R.id.randomButton));
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (number < 10) {
                    lyricsURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + (String.valueOf(number)) + "/words.txt";
                    KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + Integer.parseInt(String.valueOf(number)) + levelChoice;
                } else {
                    lyricsURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + Integer.parseInt(String.valueOf(number)) + "/words.txt";
                    KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + Integer.parseInt(String.valueOf(number)) + levelChoice;
                }
                Log.e("KMLURL", KMLURL);
                DownloadLyrics downloadLyrics = new DownloadLyrics();
                String lyrics = "";
                downloadLyrics.execute(lyricsURL);
                DownloadKml downloadKml = new DownloadKml();
                ArrayList<DownloadKml.Point> result = new ArrayList<DownloadKml.Point>();
                downloadKml.execute(KMLURL);
                try {
                    lyrics = downloadLyrics.get();
                    result = downloadKml.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                //Pass song title for use in Maps Activity
                //For random instance
                for (DownloadXml.Entry entry : output) {
                    String num;
                    if (number < 10) {
                        num = "0" + (String.valueOf(number));
                    } else {
                        num = String.valueOf(number);
                    }
                    if(num.equals(entry.number)) {
                        songName = entry.title;
                    }
                }
                intent.putExtra("songName", songName);
                //Log.e("result", result.toString());
                intent.putExtra("lyrics", lyrics);
                intent.putParcelableArrayListExtra("parsedKml", result);
                startActivity(intent);
            }
        });

        for (int j = 1; j < numButtons +1; j++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayout);
            RelativeLayout rellayout = (RelativeLayout) findViewById(R.id.relativelayout);
            rellayout.setGravity(Gravity.CENTER);
            Button btn = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            row.setGravity(Gravity.CENTER);
            row.setPadding(0,40,0,0);
            btn.setLayoutParams(params);
            btn.setText("Song " + j);
            btn.setId(j);
            btn.setBackgroundColor(Color.parseColor("#cc6618"));
            row.addView(btn);
            final int finalJ = j;
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                if (finalJ < 10) {
                    lyricsURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + finalJ + "/words.txt";
                    KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + finalJ + levelChoice;
                } else {
                    lyricsURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + finalJ + "/words.txt";
                    KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + finalJ + levelChoice;

                }

                DownloadLyrics downloadLyrics = new DownloadLyrics();
                String lyrics = "";
                downloadLyrics.execute(lyricsURL);
                DownloadKml downloadKml = new DownloadKml();
                ArrayList<DownloadKml.Point> result = new ArrayList<DownloadKml.Point>();
                downloadKml.execute(KMLURL);
                try {
                    lyrics = downloadLyrics.get();
                    result = downloadKml.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                final Intent intent = new Intent(SongChoice.this, MapsActivity.class);
                for (DownloadXml.Entry entry : output) {
                    String num;
                    if (finalJ < 10) {
                        num = "0" + (finalJ);
                    } else {
                        num = String.valueOf(finalJ);
                    }
                    Log.e("num", num);
                    if(num.equals(entry.number)) {
                        songName = entry.title;
                    }
                }
                intent.putExtra("songName", songName);
                intent.putExtra("lyrics", lyrics);
                intent.putParcelableArrayListExtra("parsedKml", result);
                startActivity(intent);
                }
            });
            layout.addView(row);
        }
    }
}
