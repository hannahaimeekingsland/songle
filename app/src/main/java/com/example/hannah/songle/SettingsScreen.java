package com.example.hannah.songle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class SettingsScreen extends Fragment {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ArrayList<String> markerTitles = new ArrayList<>();
    HashSet markerTitlesSet = new HashSet<String>();
    ArrayList<String> mtNoDuplicates = new ArrayList<>();
    ArrayList<String> markersToRemove = new ArrayList<>();
    TextView scoreStr;

    public static SettingsScreen newInstance(ArrayList<String> markerTitles) {
        SettingsScreen ssFragment = new SettingsScreen();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("markerTitles", markerTitles);
        ssFragment.setArguments(bundle);
        return ssFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markerTitles = getArguments().getStringArrayList("markerTitles");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                      Bundle savedInstanceState) {
        //getting rid of duplicate lyrics
        markerTitlesSet = new HashSet(markerTitles);
        mtNoDuplicates = new ArrayList<>(markerTitlesSet);

        //Get shared preferences for buying hint
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();
        editor.apply();

        return inflater.inflate(R.layout.settings, container, false);
    }

    public ArrayList<String> getRandomPlacemarks(ArrayList<String> mtNoDuplicates) {
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            int  n = rand.nextInt(mtNoDuplicates.size() - 1);
            mtNoDuplicates.remove(mtNoDuplicates.get(n));
            Log.e("markersToRemove", markersToRemove.toString());
            markersToRemove.add(mtNoDuplicates.get(n));
            Log.e("markersToRemove", markersToRemove.toString());
        }
        return markersToRemove;
    }

        // Set an onclick listener for when the button gets clicked
    private class HandleSettingsClick implements View.OnClickListener {
        public void onClick(View arg0) {
            hintPopup();
        }
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.getHintButton).setOnClickListener(new SettingsScreen.HandleSettingsClick());
        view.findViewById(R.id.goBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitPopup();
            }
        });
        view.findViewById(R.id.resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Dismiss the fragment
                Fragment selectedFragment = null;
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                if(getActivity().getSupportFragmentManager().findFragmentByTag("fragment") != null) {
                    getActivity().findViewById(R.id.score).setVisibility(View.VISIBLE);
                    transaction.remove(getActivity().getSupportFragmentManager().findFragmentByTag("fragment"));
                    transaction.commit();
                }
            }
        });
    }

    private void hintPopup(){
        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflater = (LayoutInflater) SettingsScreen.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View layout = inflater.inflate(R.layout.get_hint_popup,null);

        //Get the devices screen density to calculate correct pixel sizes
        float density=SettingsScreen.this.getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        final PopupWindow pw = new PopupWindow(layout, (int)density*295, (int)density*220, true);
        //Button to close the pop-up
        (layout.findViewById(R.id.closeButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        (layout.findViewById(R.id.buyButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (preferences.getInt("score", 0) >= 20 && mtNoDuplicates.size() >= 5) {
                    markersToRemove = getRandomPlacemarks(mtNoDuplicates);
                    Log.e("markersToRemove", markersToRemove.toString());
                    ((MapsActivity) getActivity()).removeMarkers(markersToRemove);
//                    scoreStr.setText(preferences.getInt("score", 0) - 20);
                    editor.putInt("score", preferences.getInt("score", 0) - 20);
                    editor.apply();
                    pw.dismiss();
                    Log.e("success", "bought 5 words");
                } else if (preferences.getInt("score", 0) < 20 && mtNoDuplicates.size() >= 5){
                    Toast.makeText(getActivity(), "You do not have enough points for this!", Toast.LENGTH_LONG).show();
                    //Handle if user does not have enough points for this
                } else if (preferences.getInt("score", 0) >= 20 && mtNoDuplicates.size() < 5) {
                    Toast.makeText(getActivity(), "You do not have enough markers left on the map for this!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "You cannot get a hint at this time, sorry!", Toast.LENGTH_LONG).show();
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
        final Intent toMain = new Intent(getActivity(), MainActivity.class);
        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflater = (LayoutInflater) SettingsScreen.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View layout = inflater.inflate(R.layout.quit_game_popup,null);

        //Get the devices screen density to calculate correct pixel sizes
        float density=SettingsScreen.this.getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        final PopupWindow pw = new PopupWindow(layout, (int)density*325, (int)density*250, true);
        //Button to close the pop-up
        (layout.findViewById(R.id.closeButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        (layout.findViewById(R.id.goBack)).setOnClickListener(new View.OnClickListener() {
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