package com.example.mgalante.marvelprojectbase.views.resumecharacter;

import android.content.Context;

import com.example.mgalante.marvelprojectbase.api.entities.Characters;
import com.example.mgalante.marvelprojectbase.api.entities.Comic;
import com.example.mgalante.marvelprojectbase.views.BasePresenter;
import com.example.mgalante.marvelprojectbase.views.BaseView;

import java.util.List;

/**
 * Created by mgalante on 19/01/17.
 */

public interface ComicContract {
    interface View extends BaseView<Presenter>{
        void fillData(List<Comic> list);

    }

    interface Presenter extends BasePresenter<Context,View>{
        void getComics(int characterId);
        void saveComics(Characters character);
    }

}
