/*
 *   ~           _MMMMM`
 *   ~     __MMMMMMMMM`       J        openTrends Solucions i Sistemes, S.L.
 *   ~ JMMMMMMMMMMMMF       JM         http://www.opentrends.net
 *   ~ MMMMMMMMMMF       _JMM`         info@opentrends.net
 *   ~ MMMMMMMF`    .JMMMMF`
 *   ~ """")    _JMMMMMMF`             Veneçuela, 105 1-A
 *   ~ _MMMMMMMMMMMMMMM`      .M)      Barcelona, 08019
 *   ~ MMMMMMMMMMMMMF`     .JMM`       Spain
 *   ~ MMMMMMMMMM"     _MMMMMF
 *   ~ M4MMM""`   ._MMMMMMMM`          *************************************
 *   ~ _______MMMMMMMMMMMF             BP002-Bonpreu
 *   ~ MMMMMMMMMMMMMMMM"               *************************************
 *   ~ MMMMMMMMMMMMF"                  Copyright (C) 2015 openTrends, Tots els drets reservats
 *   ~ MMMMMMMM""                      Copyright (C) 2015 openTrends, All Rights Reserved
 *   ~
 *   ~                                 This program is free software; you can redistribute it and/or modify
 *   ~                                 it under the terms of the GNU General Public License as published by
 *   ~                                 the Free Software Foundation; either version 2 of the License, or
 *   ~                                 (at your option) any later version.
 *   ~
 *   ~                                 This program is distributed in the hope that it will be useful,
 *   ~                                 but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   ~                                 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   ~                                 GNU General Public License for more details.
 *   ~
 *   ~                                 You should have received a copy of the GNU General Public License along
 *   ~                                 with this program; if not, write to the Free Software Foundation, Inc.,
 *   ~                                 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.example.mgalante.marvelprojectbase.views.resumecharacter;


import android.content.Context;

import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.SendRequest;
import com.example.mgalante.marvelprojectbase.api.ServiceMarvel;
import com.example.mgalante.marvelprojectbase.api.apiServer;
import com.example.mgalante.marvelprojectbase.api.entities.BaseResponse;
import com.example.mgalante.marvelprojectbase.api.entities.Event;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventPresenterImpl implements EventContract.Presenter  {

    private EventContract.View mView;
    private Context mContext;

    public EventPresenterImpl() {

    }

    public void attach(Context context, EventContract.View view) {
        this.mContext = context;
        this.mView = view;
        mView.setPresenter(this);
    }

    public void getEvents(int characterId){
        apiServer server = ServiceMarvel.getService(mContext);
        final SendRequest request = SendRequest.create();
        Call<BaseResponse<Event>> call = server.getEventsByCharacter(characterId, String.valueOf(request.getTimeStamp()), request.getPublicKey(),request.getHashSignature());
        call.enqueue(new Callback<BaseResponse<Event>>() {
            @Override
            public void onResponse(Call<BaseResponse<Event>> call, Response<BaseResponse<Event>> response) {
                if (response.isSuccessful()){
                    if (response.body().data != null && response.body().data.results.size() > 0){
                        mView.fillData(response.body().data.results);
                    }else{
                        mView.showMessage(mContext.getString(R.string.sin_resultados));
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Event>> call, Throwable t) {
                mView.showMessage(mContext.getString(R.string.error));
            }
        });
    }
}

