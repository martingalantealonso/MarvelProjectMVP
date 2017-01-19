package com.example.mgalante.marvelprojectbase.views.resumecharacter;

import android.content.Context;

import com.example.mgalante.marvelprojectbase.api.entities.Event;
import com.example.mgalante.marvelprojectbase.views.BasePresenter;
import com.example.mgalante.marvelprojectbase.views.BaseView;

import java.util.List;



/**
 * Created by miguelprieto on 22/11/16.
 */
public interface EventContract {
    interface View extends BaseView<Presenter> {
        void fillData(List<Event> list);
    }
    interface Presenter extends BasePresenter<Context, View> {
        void getEvents(int characterId);
    }
}
