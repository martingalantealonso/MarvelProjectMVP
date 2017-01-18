package com.example.mgalante.marvelprojectbase.control.adapters.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mgalante.marvelprojectbase.R;


/**
 * Created by mgalante on 16/01/17.
 */
public class CommonViewHolder<T> extends RecyclerView.ViewHolder {


    public final View mView;
    public final LinearLayout mHolder;
    public final TextView name;
    public final TextView subname;
    public final ImageView avatar;

    public T mItem;

    public CommonViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mHolder=(LinearLayout)itemView.findViewById(R.id.main_information_holder);
        subname = (TextView) itemView.findViewById(R.id.character_desc);
        name = (TextView) itemView.findViewById(R.id.character_name);
        avatar = (ImageView) itemView.findViewById(R.id.character_image);
    }

    @Override
    public String toString() {
        return super.toString() + "CommonViewHolder{" +
                "mView=" + mView +
                "holder" + mHolder +
                ", name=" + name +
                ", subname=" + subname +
                ", avatar=" + avatar +
                '}';
    }
}
