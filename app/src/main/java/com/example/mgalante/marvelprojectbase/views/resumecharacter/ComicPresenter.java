package com.example.mgalante.marvelprojectbase.views.resumecharacter;

import android.content.Context;

import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.SendRequest;
import com.example.mgalante.marvelprojectbase.api.ServiceMarvel;
import com.example.mgalante.marvelprojectbase.api.apiServer;
import com.example.mgalante.marvelprojectbase.api.entities.BaseResponse;
import com.example.mgalante.marvelprojectbase.api.entities.Comic;
import com.example.mgalante.marvelprojectbase.views.resumecharacter.ComicContract;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mgalante on 19/01/17.
 */

public class ComicPresenter implements ComicContract.Presenter {

    private ComicContract.View view;
    private Context mContext;


    @Override
    public void getComics(int characterId) {
        apiServer server = ServiceMarvel.getService(mContext);
        final SendRequest request = SendRequest.create();
        Call<BaseResponse<Comic>> call = server.getComicsByCharacter(characterId, String.valueOf(request.getTimeStamp()), request.getPublicKey(), request.getHashSignature());
        call.enqueue(new Callback<BaseResponse<Comic>>() {
            @Override
            public void onResponse(Call<BaseResponse<Comic>> call, Response<BaseResponse<Comic>> response) {
                if (response.isSuccessful()) {
                    if (response.body().data != null && response.body().data.results.size() > 0) {
                                                view.fillData(response.body().data.results);
                    } else {
                        view.showMessage(mContext.getString(R.string.sin_resultados));
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Comic>> call, Throwable t) {
                view.showMessage(mContext.getString(R.string.error));
            }
        });
    }

    @Override
    public void attach(Context context, ComicContract.View view) {
        this.view = view;
        this.mContext = context;
        view.setPresenter(this);
    }
}
