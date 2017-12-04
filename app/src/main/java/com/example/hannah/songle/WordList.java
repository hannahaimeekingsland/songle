package com.example.hannah.songle;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
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


public class WordList extends ListFragment {
    //String word = "";
    ArrayList<String> words = new ArrayList<String>();

    public static WordList newInstance(ArrayList<String> words) {
        WordList wordListFragment = new WordList();
        Bundle bundle = new Bundle();
        Log.e("wordList", words.toString());
        bundle.putStringArrayList("wordList", words);
        wordListFragment.setArguments(bundle);
        return wordListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        words = getArguments().getStringArrayList("wordList");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //words = getActivity().getIntent().getStringArrayListExtra("wordList");
        //System.out.println(">>>>>>>>>> word:" + words);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, words);
        setListAdapter(adapter);
        //Log.e("words arraylist", words.toString());
//        final Intent toSettings = new Intent(this, SettingsScreen.class);
//        Button settingsButton = (Button) findViewById(R.id.settingsIcon);
//        //Log.e("mapsbutton", "clicked");
//        settingsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(toSettings);
//            }
//        });
//        final Intent toMaps = new Intent(this, MapsActivity.class);
//        Button mapsButton = (Button) findViewById(R.id.mapIcon);
//        //Log.e("mapsbutton", "clicked");
//        mapsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(toMaps);
//                overridePendingTransition(R.animator.right_slide_in, R.animator.right_slide_out);
//            }
//        });
        return inflater.inflate(R.layout.word_list, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
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

