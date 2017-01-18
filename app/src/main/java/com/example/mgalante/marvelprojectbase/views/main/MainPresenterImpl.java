package com.example.mgalante.marvelprojectbase.views.main;

import android.content.Context;

import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.SendRequest;
import com.example.mgalante.marvelprojectbase.api.ServiceMarvel;
import com.example.mgalante.marvelprojectbase.api.entities.BaseResponse;
import com.example.mgalante.marvelprojectbase.api.entities.Characters;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mgalante on 18/01/17.
 */

public class MainPresenterImpl implements MainContract.Presenter {

    private final ServiceMarvel mService;
    private MainContract.View mView;
    private Context mContext;

    public MainPresenterImpl(ServiceMarvel service) {
        this.mService = service;
    }

    @Override
    public void attach(Context context, MainContract.View view) {
        this.mContext = context;
        this.mView = view;
    }

    @Override
    public void getHeroes(String search) {
        //1ºCrear la request. Cadena que se manda al servidor de Marvel para que de acceso
        final SendRequest request = SendRequest.create();
        //2º Se crea lo que será la cadena para buscar heroes
        Call<BaseResponse<Characters>> call = mService.getService(mContext).getCharactersByStartsWith(search, "1", request.getPublicKey(), request.getHashSignature());
        //3ª Ahora que esta definida, se genera de forma asincrona para que no pete la aplicación
        call.enqueue(new Callback<BaseResponse<Characters>>() {
            @Override
            public void onResponse(Call<BaseResponse<Characters>> call, Response<BaseResponse<Characters>> response) {

                //4º Si la respuesta es correcta, obtenidos los datos de respuesta, se mandan para que aparezcan en la activity
                if (response.isSuccessful()) {
                    if (response.body().data != null && response.body().data.results.size() > 0){
                        mView.fillData(response.body().data.results);
                    }else{
                        mView.showMessage(mContext.getString(R.string.sin_resultados));
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Characters>> call, Throwable t) {
                mView.showMessage("Error al cargar la lista de heroes");
            }
        });

    }
}
