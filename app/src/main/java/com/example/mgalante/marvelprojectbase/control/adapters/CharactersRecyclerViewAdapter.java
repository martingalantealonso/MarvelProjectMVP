package com.example.mgalante.marvelprojectbase.control.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.entities.Characters;
import com.example.mgalante.marvelprojectbase.control.callbacks.CharacterListCallBack;

import java.util.List;

/**
 * Created by mgalante on 18/01/17.
 */

public class CharactersRecyclerViewAdapter extends RecyclerView.Adapter<CommonViewHolder<Characters>> {

    private List<Characters> mValues;
    private final Context mContext;
    private final CharacterListCallBack mListener;
    private int lastPosition = -1;

    public CharactersRecyclerViewAdapter(List<Characters> mValues, Context mContext, CharacterListCallBack listener) {
        this.mValues = mValues;
        this.mContext = mContext;
        this.mListener = listener;
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
        String urlImage = null;
        if (mValues.get(position).getThumbnail() != null) {
            urlImage = mValues.get(position).getThumbnail().getPath() + "." + mValues.get(position).getThumbnail().getExtension();
        } else {
            urlImage = mValues.get(position).getImageUrl();
        }
        Glide.with(mContext).load(urlImage).into(holder.avatar);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickCharacter(view, mValues.get(position));
            }
        });

        //setAnimation(holder.itemView, position);

    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.up_from_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void fillData(List<Characters> characters) {
        mValues = characters;
    }

}
