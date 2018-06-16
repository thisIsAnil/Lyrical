package com.infi.lyrical.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.infi.lyrical.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INFIi on 12/2/2017.
 */

public class LyricHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.lyric_tv)
    public TextView lyricTv;
    public LyricHolder(View v){
        super(v);
        ButterKnife.bind(this,v);
    }
}
