package com.example.hannah.songle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        requestPermission();
        //NetworkActivity.DownloadXmlTask download = new NetworkActivity.DownloadXmlTask();

        final Intent intent = new Intent(MainActivity.this, LevelChoice.class);
        Button playButton = (findViewById(R.id.button2));

        //Hide play button and show a Toast when no Internet connection
        if (!isOnline()) {
            playButton.setVisibility(View.GONE);
            Toast.makeText(this, "Songle cannot be played without an internet connection", Toast.LENGTH_LONG).show();
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
    }

    private void requestPermission() {
        //Function to request permission
        try {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
        } catch(SecurityException e) {
        /*Catch any Security Exceptions*/
        }
    }

    //Check for connection to the Internet
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
}



