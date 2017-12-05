package com.example.hannah.songle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by s1518196 on 31/10/17.
 */

public class LevelChoice extends Activity {
    static String TAG = "LevelChoice";
    String levelChoice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_choice);
        Log.e("current class", "level choice");

        final Intent intent = new Intent(LevelChoice.this, SongChoice.class);

        //Send relevant map number to SongChoice depending on button click
        Button amateurButton = (Button) (findViewById(R.id.button4));
        amateurButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levelChoice = "/map5.kml";
                intent.putExtra("levelChoice", levelChoice);
                startActivity(intent);
            }
        });

        Button gstButton = (Button) (findViewById(R.id.button5));
        gstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levelChoice = "/map4.kml";
                intent.putExtra("levelChoice", levelChoice);
                startActivity(intent);
            }
        });

        Button fcoButton = (Button) (findViewById(R.id.getHintButton));
        fcoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levelChoice = "/map3.kml";
                intent.putExtra("levelChoice", levelChoice);
                startActivity(intent);
            }
        });

        Button difficultButton = (Button) (findViewById(R.id.button7));
        difficultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levelChoice = "/map2.kml";
                intent.putExtra("levelChoice", levelChoice);
                startActivity(intent);
            }
        });

        Button impossibleButton = (Button) (findViewById(R.id.button8));
        impossibleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levelChoice = "/map1.kml";
                intent.putExtra("levelChoice", levelChoice);
                startActivity(intent);
            }
        });
    }
}
