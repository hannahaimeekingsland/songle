package com.example.hannah.songle;

import android.Manifest;
import android.app.ActionBar;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.security.auth.login.LoginException;

import static com.example.hannah.songle.DownloadKml.*;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    ArrayList<DownloadKml.Point> points;
    ArrayList<Marker> markers = new ArrayList<>();
    String lyrics;
    String songName = "";
    String word = "";
    EditText mEdit;
    String input = "";
    final int settingsScreen = 1;
    final int wordListScreen = 2;
    ArrayList<String> wordList = new ArrayList<>();
    ArrayList<String> markerTitles = new ArrayList<>();
    int amountMarkers;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView scoreStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Setting the score
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        //editor.putInt("score", score);
        Log.e("score", String.valueOf(preferences.getInt("score", 0)));
        editor.apply();

        //Add TextView for score
        scoreStr = (TextView) findViewById(R.id.score);
        scoreStr.setText(String.valueOf(preferences.getInt("score", 0)));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

        //Get current song name, parsedKML in the form of points and lyrics through an intent from SongChoice
        songName = getIntent().getStringExtra("songName");
        Log.e("song name", songName);
        points = getIntent().getParcelableArrayListExtra("parsedKml");
        lyrics = getIntent().getStringExtra("lyrics");
        //Log.e("lyrics", lyrics);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        //Set navigation through the Bottom Navigation view
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        switch (item.getItemId()) {
                            case R.id.bottom_navigation_map:
                                if(getSupportFragmentManager().findFragmentByTag("fragment") != null) {
                                    //Show current score and guess button on the maps activity only
                                    scoreStr.setText(String.valueOf(preferences.getInt("score", 0)));
                                    findViewById(R.id.guessButton).setVisibility(View.VISIBLE);
                                    findViewById(R.id.score).setVisibility(View.VISIBLE);
                                    transaction.remove(getSupportFragmentManager().findFragmentByTag("fragment"));
                                    transaction.commit();
                                }
                                break;
                            case R.id.bottom_navigation_wordlist:
                                //Send wordList (ArrayList<String>) to the word list fragment
                                findViewById(R.id.guessButton).setVisibility(View.GONE);
                                findViewById(R.id.score).setVisibility(View.GONE);
                                selectedFragment = WordList.newInstance(wordList);
                                transaction.replace(R.id.frame_layout, selectedFragment, "fragment");
                                transaction.commit();
                                break;
                            case R.id.bottom_navigation_settings:
                                //Send titles for each marker to settings fragment to be used for the get hint functionality
                                findViewById(R.id.guessButton).setVisibility(View.GONE);
                                findViewById(R.id.score).setVisibility(View.GONE);
                                selectedFragment = SettingsScreen.newInstance(markerTitles);
                                transaction.replace(R.id.frame_layout, selectedFragment, "fragment");
                                transaction.commit();
                                break;
                        }
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame_layout, bottomNavigationView.newInstance());
//        transaction.commit();
        //Log.e("song name", songName);
        //Set on click listener for the guess button
        findViewById(R.id.guessButton).setOnClickListener(new HandleClick());

    }

    /*@Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }*/

    //Override the back button functionality
    @Override
    public void onBackPressed() {
        backPressedPopup();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        addPlacemarks(points);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                    mGoogleApiClient.connect();
                }
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();
            }
            mGoogleMap.setMyLocationEnabled(true);
        }
        LatLng start = new LatLng(55.9533, -3.1883);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(start));

        //When the marker is clicked, check the tag is 'green'
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                marker.hideInfoWindow();
                if (marker.getTag().equals("green")) {
                    //If tag is green (i.e. the current location is within range of the marker)
                    //add marker title to word list, remove marker from map and marker object
                    //from arraylist of markers and update score by 1 point
                    word = marker.getTitle();
                    wordList.add(word);
//                    Log.e("word", word);
//                    Log.e("word list", wordList.toString());
//                    Log.e("score", String.valueOf(preferences.getInt("score", 0)));
                    editor.putInt("score", preferences.getInt("score", 0)+1);
                    scoreStr.setText(String.valueOf(preferences.getInt("score", 0)+1));
                    editor.apply();
                    marker.remove();
                    markers.remove(marker);
                    markerTitles.remove(marker.getTitle());
                }
                return true;
            }
        });

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleApiClient.connect();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLastLocation = location;
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }
                    //Place current location marker
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    //markerOptions.title("Current Position");
                    //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    //mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                    //move map camera
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

                for (final Marker m : markers) {
                    LatLng loc = m.getPosition();
                    Location markerLoc = new Location("markerLoc");
                    markerLoc.setLatitude(loc.latitude);
                    markerLoc.setLongitude(loc.longitude);

                    //Compare current location to every marker : for markers that are within
                    //30 metres, turn the icon green and set the tag as 'green'
                    if (mLastLocation.distanceTo(markerLoc) < 30) {
                        m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.grnblank));
                        m.setTag("green");
                       }
                    }
                }
            });
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            mGoogleApiClient = new GoogleApiClient.Builder(this)
                                    .addConnectionCallbacks(this)
                                    .addOnConnectionFailedListener(this)
                                    .addApi(LocationServices.API)
                                    .build();
                            mGoogleApiClient.connect();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied. Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //Puts lyrics into a hashmap containing the individual lyric mapped to the place in the song
    //the lyric appears at (to override the current title of each marker)
    public HashMap<String, String> getLyrics(String lyrics) {
        String splitNewLine[] = lyrics.split("\\r?\\n");
        HashMap<String, String> lyricsMap = new HashMap<String, String>();
        int lineNum = 1;
        for (String line : splitNewLine) {
            //Ignores ' and - in the splitting of strings
            String splitStr[] = line.split("[^\\w'-]+");
            int place = 1;
            for (int i = 2; i < splitStr.length; i++) {
                lyricsMap.put(Integer.toString(lineNum) + ":" + Integer.toString(place), splitStr[i]);
                place++;
            }
            lineNum++;
        }
        return lyricsMap;
    }

    public void addPlacemarks(ArrayList<DownloadKml.Point> points) {
        HashMap<String, String> lyricsMap = getLyrics(lyrics);
        for (DownloadKml.Point entry : points) {
            String[] coords = new String[2];
            coords = entry.coordinates.split(",");
            //Gets the lyric associated to each 'name' tag in the xml - e.g. 41:1 = Manifico-o-o-o-o
            String lyric = "";
            if (lyricsMap.containsKey(entry.name)) {
                lyric = lyricsMap.get(entry.name);
            }
            //Places a marker at the location of the parsed coordinates
            LatLng marker = new LatLng(Double.parseDouble(coords[1]), Double.parseDouble(coords[0]));
            Marker m = mGoogleMap.addMarker(new MarkerOptions()
                    .position(marker)
                    .title(lyric)
                    .icon(BitmapDescriptorFactory.fromResource(getIcon(entry))));
            m.setTag("not green");
            //Add each Marker to the array list of Markers and each title String to the array list
            //of marker titles
            markers.add(m);
            markerTitles.add(m.getTitle());
        }
        amountMarkers = markers.size();
    }

    //Gets icon based on styleurl
    public int getIcon(Point p) {
        int output = 0;
        if (p.styleurl.equals("#unclassified")) {
            output = R.drawable.whtblank;
        } else if (p.styleurl.equals("#boring")) {
            output = R.drawable.ylwblank;
        } else if (p.styleurl.equals("#notboring")) {
            output = R.drawable.ylwcircle;
        } else if (p.styleurl.equals("#interesting")) {
            output = R.drawable.orangediamond;
        } else if (p.styleurl.equals("#veryinteresting")) {
            output = R.drawable.redstars;
        }
        return output;
    }

    //Removes 5 random markers for the get hint bonus function - this is called in the
    //SettingsScreen fragment
    public void removeMarkers(ArrayList<String> markersToRemove) {
//        Log.e("size before remove", String.valueOf(markers.size()));
        ArrayList<Marker> markerCopy = (ArrayList<Marker>)markers.clone();
        if (markersToRemove != null) {
            Log.e("markersToRemove (maps)", markerCopy.toString());
            Log.e("markers", markers.toString());
            for (Marker m : markerCopy) {
                //Iterate over all markers in a copy of the arraylist
                for (String t : markersToRemove) {
                    //Iterate over markersToRemove and check each string against the current Marker's title
                    if (m.getTitle().equals(t) && !(wordList.contains(t))) {
                        //If there is a match, add the title to the word list, remove the Marker
                        //from the map and from the array list of Markers
                        wordList.add(t);
                        m.remove();
                        markers.remove(m);
                        break;
                    }
                }
            }
        }
//        Log.e("size after remove", String.valueOf(markers.size()));
    }

    //Assess whether guess is correct or not - not case sensitive
    public void guess(String inputText) {
        //Removes all punctuation from string besides apostrophes
        String noPunct = songName.toLowerCase().replaceAll("[^\\w']+", "");
        Log.e("song name", songName);
        if (inputText.toLowerCase().equals(songName.toLowerCase()) || inputText.toLowerCase().equals(noPunct)) {
            correctGuessPopup();
        } else {
            incorrectGuessPopup();
        }
    }

    private class HandleClick implements View.OnClickListener {
        public void onClick(View arg0) {
            enableGuess();
        }
    }

    private void enableGuess(){
        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflater = (LayoutInflater) MapsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View layout = inflater.inflate(R.layout.guess_popup,null);
        mEdit = (EditText)layout.findViewById(R.id.guessText);

        //Get the devices screen density to calculate correct pixel sizes
        float density=MapsActivity.this.getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        final PopupWindow pw = new PopupWindow(layout, (int)density*240, (int)density*260, true);
        //Button to guess
        ((Button) layout.findViewById(R.id.submitGuess)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                input = (String) mEdit.getText().toString();
                Log.e("input", input);
                pw.dismiss();
                guess(input);
            }
        });
        //Button to close the pop-up
        ((ImageView) layout.findViewById(R.id.closeButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("close clicked", "clicked");
                pw.dismiss();
            }
        });
        //Set up touch closing outside of pop-up
        pw.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
        // Display the pop-up in the center
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

    private void backPressedPopup(){
        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflater = (LayoutInflater) MapsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View layout = inflater.inflate(R.layout.back_pressed,null);

        //Get the devices screen density to calculate correct pixel sizes
        float density=MapsActivity.this.getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        final PopupWindow pw = new PopupWindow(layout, (int)density*325, (int)density*250, true);
        //Button to close the pop-up
        ((ImageView) layout.findViewById(R.id.closeButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("close clicked", "clicked");
                pw.dismiss();
            }
        });
        //'Go back' button
        ((Button) layout.findViewById(R.id.goBack)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //If the user chooses to go back, call the original functionality for onBackPressed()
                MapsActivity.super.onBackPressed();
            }
        });
        pw.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }


    private void correctGuessPopup(){
        //Calculate the amount of bonus score to give the user upon a correct guess,
        //relative to how many markers they have picked up
        int bonusScore = 0;
        int currentAmount = markers.size();
        Log.e("amountMarkers", String.valueOf(amountMarkers));
        Log.e("currentAmount", String.valueOf(currentAmount));
        if (amountMarkers*0.2 >= currentAmount) {
            bonusScore = 0;
        } else if (amountMarkers*0.2 < currentAmount && amountMarkers*0.4 >= currentAmount) {
            bonusScore = 5;
        } else if (amountMarkers*0.4 < currentAmount && amountMarkers*0.6 >= currentAmount) {
            bonusScore = 10;
        } else if (amountMarkers*0.6 < currentAmount && amountMarkers*0.8 >= currentAmount) {
            bonusScore = 20;
        } else {
            bonusScore = 30;
        }
        editor.putInt("score", preferences.getInt("score", 0)+ bonusScore);
        editor.apply();
        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflater = (LayoutInflater) MapsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View layout = inflater.inflate(R.layout.correct_guess_popup,null);

        //Get the devices screen density to calculate correct pixel sizes
        float density=MapsActivity.this.getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        //Make popup window fill entire page so cannot be clicked away
        final PopupWindow pw = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        //Button to take user back to the MainActivity
        final Intent toStart = new Intent(MapsActivity.this, MainActivity.class);
        ((Button) layout.findViewById(R.id.mainMenuButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(toStart);
            }
        });
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
        pw.setOutsideTouchable(false);
        pw.setTouchable(false);
        pw.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private void incorrectGuessPopup(){
        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflater = (LayoutInflater) MapsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View layout = inflater.inflate(R.layout.incorrect_guess_popup,null);

        //Get the devices screen density to calculate correct pixel sizes
        float density=MapsActivity.this.getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        final PopupWindow pw = new PopupWindow(layout, (int)density*255, (int)density*210, true);
        //Button to close the pop-up
        ((ImageView) layout.findViewById(R.id.closeButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("close clicked", "clicked");
                pw.dismiss();
            }
        });
        //Try again button
        ((Button) layout.findViewById(R.id.tryAgain)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Dismiss this popup window before bringing up the guess popup again
                pw.dismiss();
                enableGuess();
            }
        });
        pw.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

}