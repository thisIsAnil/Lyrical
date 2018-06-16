package com.infi.lyrical.adapters.holders;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.infi.lyrical.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INFIi on 12/1/2017.
 */

public class TaskHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.task_title)
    public TextView title;
    @BindView(R.id.task_category_icon)
    public AppCompatImageView category;
    @BindView(R.id.task_status)
    public TextView status;

    public TaskHolder(View v){
        super(v);
        ButterKnife.bind(this,v);
    }
}
