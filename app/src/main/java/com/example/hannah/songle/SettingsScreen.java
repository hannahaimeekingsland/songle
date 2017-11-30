package com.example.hannah.songle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

/**
 * Created by s1518196 on 29/10/17.
 */

public class SettingsScreen extends Activity {
    final Intent toMaps = new Intent(this, MapsActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings);
        findViewById(R.id.quitButton).setOnClickListener(new SettingsScreen.HandleQuitClick());
        findViewById(R.id.getHintButton).setOnClickListener(new SettingsScreen.HandleSettingsClick());
        findViewById(R.id.resume).setOnClickListener(new SettingsScreen.HandleResumeClick());
    }

        // set a onclick listener for when the button gets clicked
    private class HandleSettingsClick implements View.OnClickListener {
        public void onClick(View arg0) {
            hintPopup();
        }
    }

    private class HandleQuitClick implements View.OnClickListener {
        public void onClick(View arg0) {
            quitPopup();
        }
    }

    private class HandleResumeClick implements View.OnClickListener {
        public void onClick(View arg0) {
            startActivity(toMaps);
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