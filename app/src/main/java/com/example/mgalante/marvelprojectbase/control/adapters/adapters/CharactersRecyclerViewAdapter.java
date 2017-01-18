package com.example.mgalante.marvelprojectbase.control.adapters.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.entities.Characters;
import com.example.mgalante.marvelprojectbase.control.adapters.callbacks.CharacterListCallBack;

import java.util.List;

/**
 * Created by mgalante on 18/01/17.
 */

public class CharactersRecyclerViewAdapter extends RecyclerView.Adapter<CommonViewHolder<Characters>> {

    private List<Characters> mValues;
    private final Context mContext;
    private final CharacterListCallBack mListener;

    public CharactersRecyclerViewAdapter(List<Characters> mValues, Context mContext, CharacterListCallBack listener) {
        this.mValues = mValues;
        this.mContext = mContext;
        this.mListener=listener;
    }

    @Override
    public CommonViewHolder<Characters> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_character, parent, false);
        return new CommonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommonViewHolder<Characters> holder, final int position) {
        holder.mItem = mValues.get(position);

        holder.name.setText(mValues.get(position).getName());
        holder.subname.setText(mValues.get(position).getDescription());


        String urlImage = mValues.get(position).getThumbnail().getPath() + "." + mValues.get(position).getThumbnail().getExtension();
        Glide.with(mContext).load(urlImage).into(holder.avatar);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickCharacter(view,mValues.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void fillData(List<Characters> characters) {
        mValues = characters;
    }

}
