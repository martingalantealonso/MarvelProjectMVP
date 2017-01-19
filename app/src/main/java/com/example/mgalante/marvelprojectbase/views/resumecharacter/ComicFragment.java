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
import com.example.mgalante.marvelprojectbase.api.entities.Comic;
import com.example.mgalante.marvelprojectbase.control.adapters.ComicsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mgalante on 18/01/17.
 */

public class ComicFragment extends Fragment implements ComicContract.View {

    private static final String ARG_CHARACTER = "charId";
    private int characterId;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ComicsRecyclerViewAdapter adapter;
    private ComicPresenter mPresenter;

    public ComicFragment() {
    }

    public static ComicFragment newInstance(int characterId) {
        ComicFragment fragment = new ComicFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHARACTER, characterId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            characterId = getArguments().getInt(ARG_CHARACTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resource_list, container, false);

        //Set the adapter
        mContext = view.getContext();
        if (view instanceof RecyclerView) {

            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            List<Comic> entities = new ArrayList<>();
            adapter = new ComicsRecyclerViewAdapter(mContext, entities);
            mRecyclerView.setAdapter(adapter);
            this.mPresenter.getComics(characterId);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void setPresenter(ComicContract.Presenter presenter) {
        this.mPresenter = (ComicPresenter) presenter;
    }

    @Override
    public void fillData(List<Comic> list) {
        adapter.fillData(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
    }

}
