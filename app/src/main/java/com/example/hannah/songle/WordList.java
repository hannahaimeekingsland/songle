package com.example.hannah.songle;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by s1518196 on 07/11/17.
 */

public class WordList extends ListActivity{
    //String word = "";
    ArrayList<String> words = new ArrayList<String>();
    String word = getIntent().getStringExtra("word");


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);
        Log.e("word", word);
        word = getIntent().getStringExtra("word");
        Log.e("word", word);
        words.add(word);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, words);
        setListAdapter(adapter);
        //Log.e("words arraylist", words.toString());
        final Intent toSettings = new Intent(this, SettingsScreen.class);
        Button settingsButton = (Button) findViewById(R.id.settingsIcon);
        //Log.e("mapsbutton", "clicked");
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(toSettings);
                overridePendingTransition  (R.animator.right_slide_in, R.animator.right_slide_out);
            }
        });
        final Intent toMaps = new Intent(this, MapsActivity.class);
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
    }

 //   ListView list = (ListView) findViewById(R.id.list);
    /*list.setOnTouchListener(new View.OnTouchListener() {
        // Setting on Touch Listener for handling the touch inside ScrollView
        @Override
        public boolean onTouch(View v, MotionEvent event){
            // Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }
    });*/

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}

