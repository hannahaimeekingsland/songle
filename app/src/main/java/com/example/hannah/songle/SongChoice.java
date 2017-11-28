package com.example.hannah.songle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
    String KMLURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_choice);
        Log.e("current class", "song choice");
        levelChoice = getIntent().getStringExtra("levelChoice");
        Log.e("running", "running");
        String XMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml";
        Log.e("running", "running");
        DownloadXml downloadXml = new DownloadXml();
        Log.e("running", "running");
        ArrayList<DownloadXml.Entry> output = new ArrayList<DownloadXml.Entry>();
        downloadXml.execute(XMLURL);
        try {
            output = downloadXml.get();
            Log.e("output", String.valueOf(output));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.e("running", "running");
        for (DownloadXml.Entry entry : output) {
            Log.e("running for loop", "running");
            Log.e("number tag", entry.number);
            numButtons = Integer.parseInt(entry.number);
            Log.e("number of songs", String.valueOf(numButtons));
        }
        Random rand = new Random();
        final int number = rand.nextInt(numButtons) + 1;
        //System.out.println(">>>>>>>>>>>>>>>>>>> Instantiated downloadXml");

        //Handle KMLURL for random instance
        Button random = (Button) (findViewById(R.id.random));
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numButtons < 10) {
                    //to put in SongChoice
                    //"http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + Integer.parseInt(String.valueOf(number))
                    KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + Integer.parseInt(String.valueOf(number)) + levelChoice;
                } else {
                    KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + Integer.parseInt(String.valueOf(number)) + levelChoice;
                }
                Log.e("KMLURL", KMLURL);
                DownloadKml downloadKml = new DownloadKml();
                ArrayList<DownloadKml.Point> result = new ArrayList<DownloadKml.Point>();
                downloadKml.execute(KMLURL);
                System.out.println(">>>>>>>>>>>>>>>>>>> executed KML");
                try {
                    result = downloadKml.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                final Intent intent = new Intent(SongChoice.this, MapsActivity.class);
                //Log.e("result", result.toString());
                intent.putParcelableArrayListExtra("parsedKml", result);
                startActivity(intent);
                System.out.println(">>>>>>>>>>>>>>>>>>> sent parsedKml");
            }
        });
        Log.e("here", "here");
        Log.e("KMLURL", KMLURL);

//        for (int i = 1; i <= numButtons; i++) {
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT);
//            Button btn = new Button(this);
//            btn.setId(i);
//            final int id_ = btn.getId();
//            btn.setText("song" + id_);
//            //What the fuck
//            btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary, Resources.Theme.AppCompat.NoActionBar));
//            linear.addView(btn, params);
//            btn1 = ((Button) findViewById(id_));
//            final int finalI = i;
//            btn1.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                if (finalI < 10) {
//                    //to put in SongChoice
//                    //"http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + Integer.parseInt(String.valueOf(number))
//                    KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + finalI + levelChoice;
//                } else {
//                    KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + finalI + levelChoice;
//
//                }
//                    Toast.makeText(view.getContext(),
//                            "Button clicked index = " + id_, Toast.LENGTH_SHORT)
//                            .show();
//                }
//            });
//        }
        LinearLayout layout = new LinearLayout(this);
        for (int i = 1; i <= numButtons; i++) {
            Button btn = new Button(this);
            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btn.setText("Song " + i);
            btn.setId(i);
            btn.setBackgroundColor(0xcc6618);
            layout.addView(btn);
            final int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                if (finalI < 10) {
                    //to put in SongChoice
                    //"http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + Integer.parseInt(String.valueOf(number))
                    KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + finalI + levelChoice;
                } else {
                    KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + finalI + levelChoice;

                }
                Toast.makeText(view.getContext(),
                        "Button clicked index = " + finalI, Toast.LENGTH_SHORT)
                        .show();
                Log.e("KMLURL", KMLURL);
                DownloadKml downloadKml = new DownloadKml();
                ArrayList<DownloadKml.Point> result = new ArrayList<DownloadKml.Point>();
                downloadKml.execute(KMLURL);
                System.out.println(">>>>>>>>>>>>>>>>>>> executed KML");
                try {
                    result = downloadKml.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                final Intent intent = new Intent(SongChoice.this, MapsActivity.class);
                //Log.e("result", result.toString());
                intent.putParcelableArrayListExtra("parsedKml", result);
                startActivity(intent);
                System.out.println(">>>>>>>>>>>>>>>>>>> sent parsedKml");
                }
            });
        }

    }
}
