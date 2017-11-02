package com.example.hannah.songle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by s1518196 on 31/10/17.
 */

public class LevelChoice extends Activity {
    static String TAG = "LevelChoice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_choice);
        final Intent intent = new Intent(LevelChoice.this, MapsActivity.class);
        intent.putExtra("parsedKml", getIntent().getExtras().getParcelableArrayList("parsedKml"));
        Button amateurButton = (Button) (findViewById(R.id.button4));
        amateurButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        Button gstButton = (Button) (findViewById(R.id.button5));
        gstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        Button fcoButton = (Button) (findViewById(R.id.button6));
        fcoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        Button difficultButton = (Button) (findViewById(R.id.button7));
        difficultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        Button impossibleButton = (Button) (findViewById(R.id.button8));
        impossibleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
    }
}
