package com.infi.lyrical.views;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infi.lyrical.R;
import com.infi.lyrical.adapters.LyricsActionListener;
import com.infi.lyrical.models.ui.MediaModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by INFIi on 12/2/2017.
 */

public abstract class LyricsView extends FrameLayout {

    @BindView(R.id.lyrics_add)
    AppCompatImageButton add;
    @BindView(R.id.lyrics_not_found)
    TextView lyricsNotFound;
    @BindView(R.id.lyrics_rv)
    RecyclerView lyricsRv;

    @BindView(R.id.lyrics_loading)
    LinearLayout loadingFrame;

    @BindView(R.id.lyrics_titlebar)
    TextView titlebar;
    protected Context context;
    protected List<MediaModel> mediaModelList;
    protected View root;
    protected LyricsActionListener lyricsActionListener;
    public LyricsView(Context context,LyricsActionListener lyricsActionListener){
        super(context);
        this.context=context;
        this.lyricsActionListener=lyricsActionListener;
        root= LayoutInflater.from(context).inflate(R.layout.fragment_video_lyrics,null,false);
        addView(root);
        ButterKnife.bind(this);
    }
    protected void showNotFound(){
        loadingFrame.setVisibility(GONE);
        lyricsNotFound.setVisibility(VISIBLE);
        lyricsRv.setVisibility(GONE);
    }
    protected void hideNotFound(){
        loadingFrame.setVisibility(GONE);
        lyricsNotFound.setVisibility(GONE);
        lyricsRv.setVisibility(VISIBLE);
    }

    @OnClick(R.id.lyrics_add)
    void onClickAdd(){
        onAddClicked();
    }
    protected abstract void onAddClicked();

}
