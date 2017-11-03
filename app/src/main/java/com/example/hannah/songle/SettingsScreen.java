package com.example.hannah.songle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

/**
 * Created by s1518196 on 29/10/17.
 */

public class SettingsScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings);
        ImageView closeButton = (ImageView) (findViewById(R.id.closeButton));
        // set a onclick listener for when the button gets clicked
        /*closeButton.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                Intent mainIntent = new Intent(SettingsScreen.this, MapsActivity.class);
                startActivity(mainIntent);
            }
       });*/
    }
}