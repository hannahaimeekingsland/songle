package com.example.hannah.songle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by s1518196 on 07/11/17.
 */

public class NoConnection extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_not_connected);
        Toast.makeText(this, "Songle cannot be played without an Internet connection", Toast.LENGTH_LONG).show();
    }
}
