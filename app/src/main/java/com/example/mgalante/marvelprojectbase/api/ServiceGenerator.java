package com.example.mgalante.marvelprojectbase.api;

import android.content.Context;

import com.example.mgalante.marvelprojectbase.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mgalante on 17/01/17.
 * Usamos Retrofit que sirve para GENERAR el Servicio que se conecta a la API de Marvel.
 * No lo generamos aqui, ya que llamaremos al método createService en otra clase más adelante.
 */


public class ServiceGenerator {
    private static OkHttpClient.Builder httClient = new OkHttpClient.Builder();
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(Constants.API_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass, Context mContext) {
        OkHttpClient.Builder client = httClient;
        client.connectTimeout(15, TimeUnit.SECONDS);
        client.readTimeout(15, TimeUnit.SECONDS);
        Retrofit retrofit = builder.client(client.build()).build();
        return retrofit.create(serviceClass);
    }
}
