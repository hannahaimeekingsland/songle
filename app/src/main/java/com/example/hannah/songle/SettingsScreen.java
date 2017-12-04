package com.example.hannah.songle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Created by s1518196 on 29/10/17.
 */

public class SettingsScreen extends Activity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ArrayList<String> markerTitles = new ArrayList<String>();
    HashSet<String> markerTitlesSet = new HashSet<String>();
    ArrayList<String> mtNoDuplicates = new ArrayList<>();
    ArrayList<String> markersToRemove = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings);
        markerTitles = getIntent().getStringArrayListExtra("markerTitles");
        //getting rid of duplicate lyrics
        markerTitlesSet = new HashSet(markerTitles);
        mtNoDuplicates = new ArrayList<>(markerTitlesSet);

        //Get shared preferences for buying hint
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        final Intent toMaps = new Intent(this, MapsActivity.class);
        findViewById(R.id.quitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMaps.putStringArrayListExtra("markersToRemove", markersToRemove);
                startActivity(toMaps);
                overridePendingTransition  (R.animator.right_slide_in, R.animator.right_slide_out);
            }
        });
        findViewById(R.id.getHintButton).setOnClickListener(new SettingsScreen.HandleSettingsClick());
        findViewById(R.id.resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitPopup();
            }
        });

        final Intent toWordList = new Intent(this, WordList.class);
        Button wordList = (Button) findViewById(R.id.wordListIcon);
        //Log.e("mapsbutton", "clicked");
        wordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(toWordList);
                overridePendingTransition  (R.animator.right_slide_in, R.animator.right_slide_out);
            }
        });

        Button mapsButton = (Button) findViewById(R.id.mapIcon);
        //Log.e("mapsbutton", "clicked");
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(toMaps);
                overridePendingTransition  (R.animator.right_slide_in, R.animator.right_slide_out);
            }
        });
    }

    public ArrayList<String> getRandomPlacemarks(ArrayList<String> mtNoDuplicates) {
        Random rand = new Random();
        int  n = rand.nextInt(mtNoDuplicates.size());
        for (int i = 0; i < 3; i++) {
            mtNoDuplicates.remove(mtNoDuplicates.get(n));
            markersToRemove.add(mtNoDuplicates.get(n));
        }
        return markersToRemove;
    }

        // set a onclick listener for when the button gets clicked
    private class HandleSettingsClick implements View.OnClickListener {
        public void onClick(View arg0) {
            hintPopup();
        }
    }


    private void hintPopup(){
        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflater = (LayoutInflater) SettingsScreen.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View layout = inflater.inflate(R.layout.get_hint_popup,null);

        //Get the devices screen density to calculate correct pixel sizes
        float density=SettingsScreen.this.getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        final PopupWindow pw = new PopupWindow(layout, (int)density*295, (int)density*150, true);
        //Button to close the pop-up
        ((ImageView) layout.findViewById(R.id.closeButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        ((Button) layout.findViewById(R.id.buyButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (preferences.getInt("score", 0) >= 20 && mtNoDuplicates.size() >= 3) {
                    markersToRemove = getRandomPlacemarks(mtNoDuplicates);
                    editor.putInt("score", preferences.getInt("score", 0) - 20);
                    editor.apply();
                } else if (preferences.getInt("score", 0) < 20 && mtNoDuplicates.size() >= 3){
                    Toast.makeText(SettingsScreen.this, "You do not have enough points for this!", Toast.LENGTH_LONG).show();
                    //Handle if user does not have enough points for this
                } else if (preferences.getInt("score", 0) >= 20 && mtNoDuplicates.size() < 3) {
                    Toast.makeText(SettingsScreen.this, "You do not have enough markers left on the map for this!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SettingsScreen.this, "You cannot get a hint at this time, sorry!", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Set up touch closing outside of pop-up
        //pw.setBackgroundDrawable(new ColorDrawable());
        pw.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });
        pw.setOutsideTouchable(true);
        // display the pop-up in the center
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

    }

    private void quitPopup(){
        final Intent toMain = new Intent(this, MainActivity.class);
        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflater = (LayoutInflater) SettingsScreen.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View layout = inflater.inflate(R.layout.quit_game_popup,null);

        //Get the devices screen density to calculate correct pixel sizes
        float density=SettingsScreen.this.getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        final PopupWindow pw = new PopupWindow(layout, (int)density*295, (int)density*150, true);
        //Button to close the pop-up
        ((ImageView) layout.findViewById(R.id.closeButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        ((Button) layout.findViewById(R.id.quitButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(toMain);
            }
        });
        //Set up touch closing outside of pop-up
        //pw.setBackgroundDrawable(new ColorDrawable());
        pw.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });
        pw.setOutsideTouchable(true);
        // display the pop-up in the center
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

    }
}