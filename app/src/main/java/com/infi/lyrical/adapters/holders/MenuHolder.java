package com.infi.lyrical.adapters.holders;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infi.lyrical.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INFIi on 12/1/2017.
 */

public class MenuHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.menu_gradient)
    public FrameLayout gradientFrame;
    @BindView(R.id.menu_title)
    public TextView menuTitle;
    @BindView(R.id.menu_icon)
    public AppCompatImageView menuIcon;

    public MenuHolder(View v){
        super(v);
        ButterKnife.bind(this,v);
    }
}
