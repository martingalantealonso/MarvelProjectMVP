package com.example.mgalante.marvelprojectbase.wear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.mgalante.marvelprojectbase.R;

/**
 * Example shell activity which simply broadcasts to our receiver and exits.
 */
public class MyStubBroadcastActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = new Intent();
        i.setAction("com.example.mgalante.marvelprojectbase.wear.SHOW_NOTIFICATION");
        i.putExtra(MyPostNotificationReceiver.CONTENT_KEY, getString(R.string.title));
        sendBroadcast(i);
        finish();
    }
}
