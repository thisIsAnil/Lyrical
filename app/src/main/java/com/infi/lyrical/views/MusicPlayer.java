package com.infi.lyrical.views;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;
import com.infi.lyrical.R;
import com.infi.lyrical.adapters.LyricsViewerAdapter;
import com.infi.lyrical.helper.FileLog;
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
import butterknife.OnClick;

/**
 * Created by INFIi on 12/2/2017.
 */

public class MusicPlayer extends FrameLayout {

    @BindView(R.id.music_player_content)
    RelativeLayout content;
    @BindView(R.id.music_artist)
    TextView artistName;
    @BindView(R.id.music_title)
    TextView musicTitle;
    @BindView(R.id.music_player_play)
    ImageButton playPause;
    @BindView(R.id.music_player_visualizer_frame)
    FrameLayout visualFrame;
    @BindView(R.id.music_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.lyrics_viewer_rv)
    RecyclerView lyricsRv;

    private AudioVisualization audioVisualization;
    private LyricsViewerAdapter lyricsViewerAdapter;
    private MediaModel mediaModel;
    private MediaPlayer mediaPlayer;
    private Context context;
    private Timer timer;
    private GLAudioVisualizationView audioVisualizationView;

    public MusicPlayer(Context context){
        super(context);
        View root= LayoutInflater.from(context).inflate(R.layout.music_player_layout,null,false);
        addView(root);
        ButterKnife.bind(this);
        this.context=context;
        audioVisualizationView=new GLAudioVisualizationView.Builder(getContext())
                .setBubblesSize(R.dimen.bubble_size)
                .setBubblesRandomizeSize(true)
                .setWavesHeight(R.dimen.wave_height)
                .setWavesFooterHeight(R.dimen.footer_height)
                .setWavesCount(7)
                .setLayersCount(4)
                .setBackgroundColorRes(R.color.av_color_bg)
                .setLayerColors(R.array.av_colors)
                .setBubblesPerLayer(8)
                .build();
        visualFrame.addView(audioVisualizationView);
        audioVisualization=(AudioVisualization)audioVisualizationView;
    }


    @OnClick(R.id.music_player_restart)
    public void onRestart(){
        seekTo(0);
    }

    @OnClick(R.id.music_player_play)
    void onPlayPause(){
        if(mediaPlayer.isPlaying()){
            pauseMediaPlayer();
        }else resumeMediaPlayer();
    }
    public void loadMusicForModel(MediaModel mediaModel) throws IOException{
        this.mediaModel=mediaModel;
        setuplyricAdapter();
        setMediaPlayer();
        setMediaName();
    }
    private void setMediaName(){
        File mediaFile=new File(mediaModel.getUrl());
        musicTitle.setText(mediaFile.getName());
        artistName.setText(AndroidUtils.getAudioMeta(mediaFile.getAbsolutePath()));
    }
    private void setMediaPlayer() throws IOException{
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setDataSource(mediaModel.getUrl());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                try {
                    progressBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    audioVisualization.linkTo(DbmHandler.Factory.newVisualizerHandler(context, mediaPlayer));
                    playPause.setImageResource(R.drawable.aar_ic_pause);
                    startTimer();
                }catch (Exception e){
                    FileLog.e(e);
                }
            }
        });
        AndroidUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.prepare();
                }catch (IOException io){
                  FileLog.e(io);
                }
            }
        },500);
    }
    private void setuplyricAdapter(){
        new AsyncTask<Void,Void,Void>(){
            List<LyricModel> lyricModelList;
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    lyricModelList = AndroidUtils.convertMediaModelToLyricModel(mediaModel);
                }catch (Exception e){
                    FileLog.e("MusicPlayer#setuplyricAdapter ",e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    lyricsViewerAdapter = new LyricsViewerAdapter(context, lyricModelList, new LyricsViewerAdapter.onLyricsChangedListener() {
                        @Override
                        public void onLyricsChanged(int position) {
                            if(lyricsViewerAdapter!=null)lyricsViewerAdapter.setCurrentPosition(position);
                        }
                    });
                    lyricsRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    lyricsRv.setAdapter(lyricsViewerAdapter);
                }catch (Exception e){
                    FileLog.e("MusicPlayer#setuplyricAdapter ",e);
                }
            }
        }.execute();
    }

    private void pauseMediaPlayer(){
        if(mediaPlayer!=null){
            playPause.setImageResource(R.drawable.aar_ic_play);
            mediaPlayer.pause();
            audioVisualization.onPause();
        }
    }
    private void resumeMediaPlayer(){
        if(mediaPlayer!=null){
            playPause.setImageResource(R.drawable.aar_ic_pause);
            mediaPlayer.start();
            audioVisualization.onResume();
        }
    }
    private void seekTo(int ms){
        if(mediaPlayer!=null){
            mediaPlayer.seekTo(ms);
        }
    }
    public void onPause(){
        stopTimer();
        pauseMediaPlayer();
    }
    public void onResume(){
        resumeMediaPlayer();
        startTimer();
    }

    public void destroy(){
        stopTimer();
        pauseMediaPlayer();
        mediaPlayer=null;
        audioVisualization.release();
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
                    progressBar.setProgress(currentPosition);
                }
            });
        }

    }

}
