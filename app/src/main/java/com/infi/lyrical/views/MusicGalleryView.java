package com.infi.lyrical.views;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.infi.lyrical.R;
import com.infi.lyrical.adapters.MusicAdapter;
import com.infi.lyrical.models.ui.AudioData;
import com.infi.lyrical.util.AndroidUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INFIi on 12/2/2017.
 */

public class MusicGalleryView  extends FrameLayout{
    @BindView(R.id.music_gallery_back)
    AppCompatImageButton back;
    @BindView(R.id.music_list)
    ListView musicListView;
    private MusicAdapter musicAdapter;
    public MusicGalleryView(Context context){
        super(context);
        View root= LayoutInflater.from(context).inflate(R.layout.music_gallery,null,false);
        addView(root);
        ButterKnife.bind(this);
    }

    public void showList(Context context,MusicAdapter.MusicActionListener musicActionListener){
        List<AudioData> dataList= AndroidUtils.getMusicList();
        musicAdapter=new MusicAdapter(context,dataList,-1,musicActionListener);
        musicListView.setAdapter(musicAdapter);
    }

    public void onPause(){
        if(musicAdapter!=null)musicAdapter.onPause();
    }
    public void onResume(){
        if(musicAdapter!=null)musicAdapter.onResume();
    }
    public void onDestroy(){
        if(musicAdapter!=null)musicAdapter.onDestroy();
    }
}
