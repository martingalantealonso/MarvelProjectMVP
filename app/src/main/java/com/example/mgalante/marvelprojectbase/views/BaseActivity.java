package com.example.mgalante.marvelprojectbase.views;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.widget.Toast;

import com.example.mgalante.marvelprojectbase.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import butterknife.ButterKnife;

import static com.example.mgalante.marvelprojectbase.utils.Constants.LOGTAG;
import static com.example.mgalante.marvelprojectbase.utils.Constants.cacheSize;
import static com.example.mgalante.marvelprojectbase.utils.Constants.mMemoryCache;


/**
 * Created by mgalante on 16/01/17.
 *
 * 4.-Una vez completados los pasos anteriores ya estamos listos para inyectar nuestras dependencias
 *    en el Activity, para ello tenemos que hacernos con una referencia a nuestro SystemComponent.
 */


public abstract class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);


        Constants.apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)
                .build();

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    //GoogleDrive
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Error de conexion!", Toast.LENGTH_SHORT).show();
        Log.e(LOGTAG, "OnConnectionFailed: " + connectionResult);
    }

    public abstract int getLayoutId();
}
