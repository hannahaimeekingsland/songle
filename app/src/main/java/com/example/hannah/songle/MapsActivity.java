package com.example.hannah.songle;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import java.util.ArrayList;
import java.util.HashMap;

import javax.security.auth.login.LoginException;

import static com.example.hannah.songle.DownloadKml.*;


public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    private ArrayList<DownloadKml.Point> points;
    String lyrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
            //mGoogleApiClient.connect();
        }
        final Intent toSettings = new Intent(MapsActivity.this, SettingsScreen.class);
        Button mapsButton = (Button) findViewById(R.id.mapButton);
        Log.e("mapsbutton", "clicked");
        mapsButton.setVisibility(View.INVISIBLE);
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(toSettings);
                overridePendingTransition  (R.animator.right_slide_in, R.animator.right_slide_out);
            }
        });
        //findViewById(R.id.guessButton).setOnClickListener(new HandleCorrect());
        findViewById(R.id.guessButton).setOnClickListener(new HandleClick());

        Bundle extras = getIntent().getExtras();
        points = getIntent().getParcelableArrayListExtra("parsedKml");
        lyrics = getIntent().getStringExtra("lyrics");
        Log.e("lyrics", lyrics);
        if (extras != null) {
            //System.out.println(">>>>>>>>>>>>>>>>>>> recieved parsedKml");
            points = getIntent().getExtras().getParcelableArrayList("parsedKml");
        }
        //System.out.println(">>>>>>>>>>>>>>" + points);
    }

    /*@Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }*/

    //Create onResume??

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        addPlacemarks(points);

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            //measure distance of marker from current location
            //turn marker green
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(marker))
                    marker.hideInfoWindow();
                //pick up marker if close enough to current location
                return true;
            }
        });

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                //mGoogleApiClient.connect();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            //mGoogleApiClient.connect();
            mGoogleMap.setMyLocationEnabled(true);
        }
        LatLng start = new LatLng(55.9533, -3.1883);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(start));
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
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));

    }

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
                            //mGoogleApiClient.connect();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public HashMap<String, String> getLyrics(String lyrics) {
        String splitNewLine[] = lyrics.split("\\r?\\n");
        for (String word : splitNewLine) {
            Log.e("splitNewLine", word);
        }
        HashMap<String, String> lyricsMap = new HashMap<String, String>();
        int lineNum = 1;
        for (String line : splitNewLine) {
            String splitStr[] = line.split("[^\\w']+");
            int place = 1;
            for (int i = 2; i < splitStr.length; i++) {
                lyricsMap.put(Integer.toString(lineNum) + ":" + Integer.toString(place), splitStr[i]);
//                Log.e("String1", Integer.toString(lineNum) + ":" + Integer.toString(place));
//                Log.e("String2", splitStr[i]);
                place++;

            }
            //Log.e("here", "here!");
            lineNum++;
        }
        return lyricsMap;
    }

    public void addPlacemarks(ArrayList<DownloadKml.Point> points) {
        HashMap<String, String> lyricsMap = getLyrics(lyrics);
        for (DownloadKml.Point entry : points) {
            String[] coords = new String[2];
            coords = entry.coordinates.split(",");
            //Log.e("styleurl", entry.styleurl);
            String lyric = "";
            if (lyricsMap.containsKey(entry.name)) {
                lyric = lyricsMap.get(entry.name);
            }
            LatLng marker = new LatLng(Double.parseDouble(coords[1]), Double.parseDouble(coords[0]));
            mGoogleMap.addMarker(new MarkerOptions().position(marker)
                    .title(lyric).icon(BitmapDescriptorFactory.fromResource(getIcon(entry))));
        }
    }

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

    public void inRange(Marker m) {


    }

    private class HandleCorrect implements View.OnClickListener {
        public void onClick(View arg0) {
            incorrectGuessPopup();
        }

    }
    private void correctGuessPopup(){
        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflater = (LayoutInflater) MapsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View layout = inflater.inflate(R.layout.correct_guess_popup,null);

        //Get the devices screen density to calculate correct pixel sizes
        float density=MapsActivity.this.getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        final PopupWindow pw = new PopupWindow(layout, (int)density*360, (int)density*150, true);
        //Button to close the pop-up
        final Intent toStart = new Intent(MapsActivity.this, MainActivity.class);
        ((Button) layout.findViewById(R.id.mainMenuButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(toStart);
            }
        });
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
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
        //final Intent toStart = new Intent(MapsActivity.this, MainActivity.class);
        ((Button) layout.findViewById(R.id.tryAgain)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //startActivity(toStart);
                pw.dismiss();
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

        //Get the devices screen density to calculate correct pixel sizes
        float density=MapsActivity.this.getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        final PopupWindow pw = new PopupWindow(layout, (int)density*240, (int)density*260, true);
        //Button to close the pop-up
        ((ImageView) layout.findViewById(R.id.closeButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
        // display the pop-up in the center
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }
}