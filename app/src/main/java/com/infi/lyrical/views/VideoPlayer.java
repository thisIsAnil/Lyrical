package com.infi.lyrical.views;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.infi.lyrical.R;
import com.infi.lyrical.adapters.LyricsViewerAdapter;
import com.infi.lyrical.models.ui.LyricModel;
import com.infi.lyrical.models.ui.MediaModel;
import com.infi.lyrical.util.AndroidUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INFIi on 12/2/2017.
 */

public class VideoPlayer extends FrameLayout {

    @BindView(R.id.video_player)
    VideoView mediaPlayer;
    @BindView(R.id.video_subtitle_rv)
    RecyclerView lyricsRv;
    @BindView(R.id.video_name)
    TextView videoName;

    private LyricsViewerAdapter lyricsViewerAdapter;
    private Timer timer;
    private MediaModel mediaModel;
    private Context context;
    public VideoPlayer(Context context){
        super(context);
        this.context=context;
        View root= LayoutInflater.from(context).inflate(R.layout.video_player_layout,null,false);
        addView(root);
        ButterKnife.bind(this);

    }


    public void loadVideoForModel(MediaModel mediaModel) throws IOException{
        this.mediaModel=mediaModel;
        setupRV();
        setVideoPlayer();
        loadName();
    }
    private void loadName(){
        File mediaFile=new File(mediaModel.getUrl());
        videoName.setText(mediaFile.getName());
    }
    private void setVideoPlayer(){
        mediaPlayer.setVideoPath(mediaModel.getUrl());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                startTimer();
            }
        });
    }
    private void setupRV(){
        new AsyncTask<Void,Void,Void>(){
            List<LyricModel> lyricModelList;
            @Override
            protected Void doInBackground(Void... voids) {
                lyricModelList=AndroidUtils.convertMediaModelToLyricModel(mediaModel);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                lyricsViewerAdapter=new LyricsViewerAdapter(context, lyricModelList, new LyricsViewerAdapter.onLyricsChangedListener() {
                    @Override
                    public void onLyricsChanged(int position) {
                        if(lyricsViewerAdapter!=null)lyricsViewerAdapter.setCurrentPosition(position);
                    }
                });
                lyricsRv.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
                lyricsRv.setAdapter(lyricsViewerAdapter);
            }
        }.execute();
    }
    private boolean isPlaying(){
        try {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        } catch (Exception e){
            return false;
        }
    }

    private void startTimer(){
        stopTimer();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        }, 500, 500);
    }

    private void stopTimer(){
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
    private void updateTimer(){
        if (lyricsViewerAdapter != null && isPlaying()) {
            final int currentPosition=mediaPlayer.getCurrentPosition();
            AndroidUtils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    lyricsViewerAdapter.setCurrentPosition(currentPosition);
                }
            });
        }

    }
    private void pausePlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.pause();
            stopTimer();
        }
    }
    private void resumePlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.resume();
            startTimer();
        }
    }
    public void onPause(){
        pausePlayer();

    }
    public void onResume(){
        resumePlayer();
    }

    public void onDestroy(){
        stopTimer();
        if(mediaPlayer!=null){
            mediaPlayer.stopPlayback();
            mediaPlayer=null;
        }
    }

}
