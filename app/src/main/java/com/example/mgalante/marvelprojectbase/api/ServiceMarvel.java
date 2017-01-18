package com.example.mgalante.marvelprojectbase.api;

import android.content.Context;

/**
 * Created by mgalante on 17/01/17.
 * Aqui se crea el Servicio que se Genera en la clase ServiceGenerator.
 */

public class ServiceMarvel {

    private static apiServer service = null;

    public static apiServer getService(Context context) {
        service = ServiceGenerator.createService(apiServer.class, context);
        return service;
    }
}
