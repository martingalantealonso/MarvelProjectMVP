package com.example.mgalante.marvelprojectbase.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;


/**
 * Created by mgalante on 16/01/17.
 *
 * 4.-Una vez completados los pasos anteriores ya estamos listos para inyectar nuestras dependencias
 *    en el Activity, para ello tenemos que hacernos con una referencia a nuestro SystemComponent.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
    }

    public abstract int getLayoutId();
}
