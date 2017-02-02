package com.example.mgalante.marvelprojectbase.firebase;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.mgalante.marvelprojectbase.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.mgalante.marvelprojectbase.utils.Constants.LOGTAG;

/**
 * Created by mgalante on 1/02/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage != null) {

            String titulo = remoteMessage.getNotification().getTitle();
            String texto = remoteMessage.getNotification().getBody();

            Log.d(LOGTAG, "NOTIFICATION RECEIVED");
            Log.d(LOGTAG, "Title: " + titulo);
            Log.d(LOGTAG, "Text: " + texto);

            //Mostramos la notificacion
            showNotification(titulo, texto);

        }
    }

    private void showNotification(String title, String text) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.drive_icon)
                .setContentTitle(title)
                .setContentText(text);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

    }
}
