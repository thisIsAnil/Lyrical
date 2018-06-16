package com.infi.lyrical.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.infi.lyrical.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INFIi on 12/2/2017.
 */

public class MediaHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.media_details)
    public TextView mediaDetails;
    @BindView(R.id.media_layout_frame)
    public FrameLayout frameLayout;
    @BindView(R.id.media_icon)
    public ImageView mediaIcon;
    @BindView(R.id.media_name)
    public TextView mediaName;
    public MediaHolder(View v){
        super(v);
        ButterKnife.bind(this,v);
    }
}
