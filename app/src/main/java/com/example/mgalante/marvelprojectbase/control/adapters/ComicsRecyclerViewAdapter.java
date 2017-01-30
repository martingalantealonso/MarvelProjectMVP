package com.example.mgalante.marvelprojectbase.control.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.entities.Comic;

import java.util.List;

/**
 * Created by mgalante on 19/01/17.
 */

public class ComicsRecyclerViewAdapter extends RecyclerView.Adapter<ComicsRecyclerViewAdapter.ViewHolder> {
    private final Context mContext;
    private List<Comic> mValues;
    OnItemClickListener mItemClickListener;  //No olvidarse de añadirlo en el constructor del ViewHolder

    public ComicsRecyclerViewAdapter(Context mContext, List<Comic> mValues) {
        this.mContext = mContext;
        this.mValues = mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.name.setText(mValues.get(position).getTitle());

        holder.subname.setText(mValues.get(position).getDescription());

        String urlImage = mValues.get(position).getThumbnail().getPath() + "." + mValues.get(position).getThumbnail().getExtension();
        Glide.with(mContext).load(urlImage).into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    //Esto es lo que se llama en el ComicDetail para mandar los comics
    public void fillData(List<Comic> list) {
        mValues = list;
    }

    public class ViewHolder<T> extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public final View mView;
        public final LinearLayout mHolder;
        public final TextView name;
        public final TextView subname;
        public final ImageView avatar;

        public T mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mHolder = (LinearLayout) itemView.findViewById(R.id.main_information_holder);
            subname = (TextView) itemView.findViewById(R.id.subname);
            name = (TextView) itemView.findViewById(R.id.name);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            mView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
            return true;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}
