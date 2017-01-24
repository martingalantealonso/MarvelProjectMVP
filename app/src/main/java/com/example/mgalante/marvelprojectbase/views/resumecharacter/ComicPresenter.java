package com.example.mgalante.marvelprojectbase.views.resumecharacter;

import android.content.Context;
import android.util.Log;

import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.SendRequest;
import com.example.mgalante.marvelprojectbase.api.ServiceMarvel;
import com.example.mgalante.marvelprojectbase.api.apiServer;
import com.example.mgalante.marvelprojectbase.api.entities.BaseResponse;
import com.example.mgalante.marvelprojectbase.api.entities.Characters;
import com.example.mgalante.marvelprojectbase.api.entities.Comic;
import com.example.mgalante.marvelprojectbase.ormlite.DBHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mgalante on 19/01/17.
 */

public class ComicPresenter implements ComicContract.Presenter {

    private ComicContract.View view;
    private Context mContext;
    private DBHelper mDBHelper;

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

    public void saveComics(final Characters character) {
        apiServer server = ServiceMarvel.getService(mContext);
        mDBHelper = DBHelper.getHelper(mContext);
        final SendRequest request = SendRequest.create();
        Call<BaseResponse<Comic>> call = server.getComicsByCharacter(character.getId(), String.valueOf(request.getTimeStamp()), request.getPublicKey(), request.getHashSignature());
        call.enqueue(new Callback<BaseResponse<Comic>>() {
            @Override
            public void onResponse(Call<BaseResponse<Comic>> call, Response<BaseResponse<Comic>> response) {
                if (response.body().data != null && response.body().data.results.size() > 0) {
                    //view.saveComics(response.body().data.results);
                    try {
                        Dao comicDao;
                        comicDao = mDBHelper.getComicsDao();
                        for (Comic c : response.body().data.results) {
                            Comic comic = new Comic(c.getId(), c.getTitle(), c.getDescription(), character);
                            comicDao.create(comic);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        mDBHelper.close();
                    }
                } else {
                    //view.showMessage(mContext.getString(R.string.error_save_comics));
                    Log.e("ComicPresenter","Error guardando");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Comic>> call, Throwable t) {
                //view.showMessage(mContext.getString(R.string.error_save_comics));
                Log.e("ComicPresenter",t.getMessage());
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
