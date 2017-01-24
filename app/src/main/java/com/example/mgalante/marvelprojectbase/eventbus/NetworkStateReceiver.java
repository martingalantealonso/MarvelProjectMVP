package com.example.mgalante.marvelprojectbase.eventbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mgalante on 24/01/17.
 */

public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                EventBus.getDefault().post(new NetworkStateChanged(true));
                //Toast.makeText(context.getApplicationContext(), "Conexión a internet", Toast.LENGTH_SHORT).show();
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                EventBus.getDefault().post(new NetworkStateChanged(false));
                // /Toast.makeText(context.getApplicationContext(), "Sin conexión a internet", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
