package com.example.hannah.songle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.AbstractSet;

/**
 * Created by s1518196 on 18/10/17.
 */

//A typical BroadcastReceiver to conserve data use
public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
/*        AbstractSet networkPref;

        if (networkPref.equals(WIFI) && networkInfo != null
                && networkInfo.getType() ==
                ConnectivityManager.TYPE_WIFI) {
// Wifi is connected, so use Wifi
        } else if (networkPref.equals(ANY) && networkInfo != null) {
// Have a network connection and permission, so use data
        } else {
// No Wifi and no permission, or no network connection
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

     */
    }
}
