package com.example.hannah.songle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class SongChoice extends Activity {
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
        //Download XML from server
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
        //Determine number of buttons needed to correspond to each song
        for (DownloadXml.Entry entry : output) {
            numButtons = Integer.parseInt(entry.number);
        }

        //Handle KMLURL and lyrics url for random instance
        Button random = (Button) (findViewById(R.id.randomButton));
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();
                final int number = rand.nextInt(numButtons) + 1;
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

        //Create dynamic buttons for the amount of songs in the XML song list
        //Handle KMLurl and lyrics url for each hidden song title
        for (int j = 1; j < numButtons +1; j++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayout);
            RelativeLayout rellayout = (RelativeLayout) findViewById(R.id.relativelayout);
            rellayout.setGravity(Gravity.CENTER);
            Button btn = new Button(this);
            float density=SongChoice.this.getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) density*130, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                //Pass title for use in MapsActivity
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
