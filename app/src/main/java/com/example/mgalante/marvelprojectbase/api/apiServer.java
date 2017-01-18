package com.example.mgalante.marvelprojectbase.api;


import com.example.mgalante.marvelprojectbase.api.entities.BaseResponse;
import com.example.mgalante.marvelprojectbase.api.entities.Characters;
import com.example.mgalante.marvelprojectbase.api.entities.Comic;
import com.example.mgalante.marvelprojectbase.api.entities.Event;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mgalante on 17/01/17.
 * <p>
 * Interfaz donde se alojan los metodos GET que se lanzarán a la API de Marvel
 */

public interface apiServer {


    interface LoadCharactersCallBack {
        void onCharactersLoaded(BaseResponse<Characters> post);
    }

    @GET("/v1/public/characters")
    Call<BaseResponse<Characters>> getCharactersByStartsWith(@Query("nameStartsWith") String nameStartsWith
            , @Query("ts") String timestamp
            , @Query("apikey") String apikey
            , @Query("hash") String hashSignature);

    @GET("/v1/public/characters/{characterId}/comics")
    Call<BaseResponse<Comic>> getComicsByCharacter(@Path("characterId") int characterId
            , @Query("ts") String timestamp
            , @Query("apikey") String apikey
            , @Query("hash") String hashSignature);
    @GET("/v1/public/characters/{characterId}/events")
    Call<BaseResponse<Event>> getEventsByCharacter(@Path("characterId") int characterId
            , @Query("ts") String timestamp
            , @Query("apikey") String apikey
            , @Query("hash") String hashSignature);
}
