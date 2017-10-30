package com.example.hannah.songle;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by s1518196 on 29/10/17.
 */

public class SettingsScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings);
    }
}
