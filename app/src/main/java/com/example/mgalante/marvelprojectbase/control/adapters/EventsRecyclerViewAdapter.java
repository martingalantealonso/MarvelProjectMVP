package com.example.mgalante.marvelprojectbase.control.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.entities.Event;
import com.example.mgalante.marvelprojectbase.control.callbacks.CommonListCallBack;

import java.util.List;

/**
 * Created by mgalante on 19/01/17.
 */

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<CommonViewHolder<Event>> {

    private final Context mContext;
    private List<Event> mValues;
    private CommonListCallBack mCommonListCallBack;

    public EventsRecyclerViewAdapter(Context mContext, List<Event> mValues, CommonListCallBack listener) {
        this.mContext = mContext;
        this.mValues = mValues;
        this.mCommonListCallBack = listener;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_resource, parent, false);
        return new CommonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommonViewHolder<Event> holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.name.setText(mValues.get(position).getTitle());
        holder.subname.setText(mValues.get(position).getDescription());

        String urlImage = mValues.get(position).getThumbnail().getPath() + "." + mValues.get(position).getThumbnail().getExtension();
        Glide.with(mContext).load(urlImage).into(holder.avatar);

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCommonListCallBack.onLongClickElement(v, mValues.get(position));
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void fillData(List<Event> list) {
        mValues = list;
    }
}
