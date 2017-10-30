package com.example.hannah.songle;

/**
 * Created by s1518196 on 29/10/17.
 */

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

/**
 * Created by s1518196 on 22/10/17.
 */

public class WordCollectionPopup extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wordcollection);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.7), (int) (height*0.25));
    }
}
