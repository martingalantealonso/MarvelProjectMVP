package com.example.mgalante.marvelprojectbase.views.main;

import android.content.Context;

import com.example.mgalante.marvelprojectbase.api.entities.Characters;
import com.example.mgalante.marvelprojectbase.views.BasePresenter;
import com.example.mgalante.marvelprojectbase.views.BaseView;

import java.util.List;

/**
 * Created by mgalante on 18/01/17.
 */

public interface MainContract {

    interface View extends BaseView<Presenter> {
        void fillData(List<Characters> characters);
    }

    interface Presenter extends BasePresenter<Context,View> {
        void getHeroes(String search);
    }
}
