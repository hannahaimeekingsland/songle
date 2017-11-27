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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_choice);
        levelChoice = getIntent().getStringExtra("levelChoice");
        String XMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml";
        DownloadXml downloadXml = new DownloadXml();
        ArrayList<DownloadXml.Entry> output = new ArrayList<DownloadXml.Entry>();
        for (DownloadXml.Entry entry : output) {
            numButtons = Integer.parseInt(entry.number);
            Log.e("number of songs", String.valueOf(numButtons));
        }
        Random rand = new Random();
        final int number = rand.nextInt(numButtons) + 1;
        //System.out.println(">>>>>>>>>>>>>>>>>>> Instantiated downloadXml");
        downloadXml.execute(XMLURL);
        String KMLURL = "";

        //Handle KMLURL for random instance
        Button random = (Button) (findViewById(R.id.random));
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (number < 10) {
                //to put in SongChoice
                //"http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + Integer.parseInt(String.valueOf(number))
                KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + Integer.parseInt(String.valueOf(number)) + levelChoice;
            } else {
                KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + Integer.parseInt(String.valueOf(number)) + levelChoice;

            }
            }
        });

        for (int i = 1; i <= numButtons; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            Button btn = new Button(this);
            btn.setId(i);
            final int id_ = btn.getId();
            btn.setText("song" + id_);
            //What the fuck
            btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary, Resources.Theme.AppCompat.NoActionBar));
            linear.addView(btn, params);
            btn1 = ((Button) findViewById(id_));
            final int finalI = i;
            btn1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                if (finalI < 10) {
                    //to put in SongChoice
                    //"http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + Integer.parseInt(String.valueOf(number))
                    KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/0" + finalI + levelChoice;
                } else {
                    KMLURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + finalI + levelChoice;

                }
                    Toast.makeText(view.getContext(),
                            "Button clicked index = " + id_, Toast.LENGTH_SHORT)
                            .show();
                }
            });
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
        final Intent intent = new Intent(SongChoice.this, MapsActivity.class);
        Log.e("result", result.toString());
        intent.putParcelableArrayListExtra("parsedKml", result);
        System.out.println(">>>>>>>>>>>>>>>>>>> sent parsedKml");

    }
}
