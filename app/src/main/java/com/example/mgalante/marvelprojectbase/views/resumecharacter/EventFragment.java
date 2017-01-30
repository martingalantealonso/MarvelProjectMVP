package com.example.mgalante.marvelprojectbase.views.resumecharacter;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.entities.Event;
import com.example.mgalante.marvelprojectbase.control.adapters.EventsRecyclerViewAdapter;
import com.example.mgalante.marvelprojectbase.control.callbacks.CommonListCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mgalante on 19/01/17.
 */

public class EventFragment extends Fragment implements EventContract.View , CommonListCallBack {

    private static final String ARG_CHARACTER = "charId";
    private int characterId;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private EventsRecyclerViewAdapter mEventsRecyclerViewAdapter;
    private EventPresenterImpl mEventPresenter;

    //1º Contructor que necesita por defecto el Fragment Manager para que no pete al iniciar el Fragment
    public EventFragment() {
    }

    //2º Metodo para crear la nueva instancia del Fragment
    public static EventFragment newInstance(int characterId) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHARACTER, characterId);
        fragment.setArguments(args);
        return fragment;
    }

    //3º Sobreescribir el onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            characterId = getArguments().getInt(ARG_CHARACTER);
        }
    }

    //4º Sobreescribir el onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_resource, container, false);

        mContext = view.getContext();
        if (view instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            List<Event> entitites = new ArrayList<>();
            mEventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(mContext, entitites,this);
            mRecyclerView.setAdapter(mEventsRecyclerViewAdapter);
            this.mEventPresenter.getEvents(characterId);
        }
        return view;
    }

    @Override
    public void fillData(List<Event> list) {
        mEventsRecyclerViewAdapter.fillData(list);
        mEventsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void setPresenter(EventContract.Presenter presenter) {
        this.mEventPresenter = (EventPresenterImpl) presenter;
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLongClickElement(View v, Event item) {

    }
}
