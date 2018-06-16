package com.infi.lyrical;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.infi.lyrical.adapters.GridSpacingItemDecoration;
import com.infi.lyrical.adapters.LyricsActionListener;
import com.infi.lyrical.adapters.MenuAdapter;
import com.infi.lyrical.adapters.TaskAdapter;
import com.infi.lyrical.helper.FileLog;
import com.infi.lyrical.models.ui.MediaModel;
import com.infi.lyrical.task.DownloadTask;
import com.infi.lyrical.task.TaskCategory;
import com.infi.lyrical.task.TaskStatus;
import com.infi.lyrical.task.TaskStatusListener;
import com.infi.lyrical.util.AndroidUtils;
import com.infi.lyrical.util.AudioMime;
import com.infi.lyrical.util.LanguageModel;
import com.infi.lyrical.views.MusicLyriView;
import com.infi.lyrical.views.SpeechToTextView;
import com.infi.lyrical.views.VideoLyricsView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;

public class MainActivity extends AppCompatActivity implements MenuAdapter.onActionDelegate,LyricsActionListener {

    @BindView(R.id.menu_rv)
    RecyclerView menuRV;

    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingPaneLayout;

    @BindView(R.id.task_list)
    RecyclerView taskRv;

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.mini_panel)
    AppCompatTextView miniPanel;

    @BindView(R.id.main_frame)
    FrameLayout mainFrame;

    private TaskAdapter taskAdapter;
    private static String url="https://www.havenondemand.com/sample-content/videos/hpnext.mp4";

    private static int SPEAKPAD_REQUEST =36214;
    private static int WAI_REQUEST =46576;
    public static int SHOW_VIDEO_REQUEST=3546;
    public static int SHOW_MUSIC_REQUEST=242553;
    private String lastRequestPath;
    private VideoLyricsView videoLyricsView;
    private MusicLyriView musicLyriView;
    private SpeechToTextView speechToTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            ButterKnife.bind(this);
            toolbar.setTitle(R.string.app_name);
            toolbar.setTitleTextColor(AndroidUtils.getColor(R.color.white));
            setSupportActionBar(toolbar);

            setSlidingPanel();
            setMenuAdapter();
        }catch (Throwable t){
            FileLog.e(t);
        }
    }

    private void setSlidingPanel(){
        slidingPaneLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        setupTaskRV();

    }
    private void setMenuAdapter(){
        int spacing=(int)(AndroidUtils.isTablet()?AndroidUtils.PxToDp(20):AndroidUtils.isSmallTablet()?AndroidUtils.PxToDp(15):AndroidUtils.PxToDp(10));
        menuRV.setLayoutManager(new GridLayoutManager(this,2));
        menuRV.setHasFixedSize(true);
        menuRV.addItemDecoration(new GridSpacingItemDecoration(2,spacing,true));
        MenuAdapter menuAdapter =new MenuAdapter(this);
        menuRV.setAdapter(menuAdapter);

    }
    private void detachMenuAdapter(){
        menuRV.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }
    private void attachMenuAdapter(){
        menuRV.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void setupTaskRV(){
        taskRv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        List<DownloadTask> downloadTasks=new ArrayList<>();
        try {
            downloadTasks=FileLog.getTaskStatusList();
        }catch (IOException io){
            FileLog.write(io.getMessage());
        }
        if(downloadTasks==null)downloadTasks=new ArrayList<>();
        taskAdapter=new TaskAdapter(downloadTasks);
        taskRv.setAdapter(taskAdapter);
        resumeTasks(downloadTasks);
    }

    private void resumeTasks(List<DownloadTask> downloads){
        if(downloads==null||downloads.size()==0)return;
        List<DownloadTask> downloadTasks=AndroidUtils.getRunningTaskList(downloads);
        List<TaskStatusListener> statusListeners=new ArrayList<>();
        for(int i=0;i<downloadTasks.size();i++){
            TaskStatusListener listener=new TaskStatusListener() {
                @Override
                public void onTaskStatusUpdate(DownloadTask downloadTask) {
                        onTaskFinished(downloadTask);
                }

                @Override
                public void onTaskStarted(DownloadTask downloadTask) {

                }
            };
            statusListeners.add(listener);
        }
        AndroidUtils.resumePreviousTask(downloadTasks,statusListeners);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== SPEAKPAD_REQUEST||requestCode==WAI_REQUEST){
            if(resultCode==RESULT_OK){
                String category=requestCode==WAI_REQUEST?TaskCategory.CATEGORY_WAI:TaskCategory.CATEGORY_SPEAKPAD;
                        AndroidUtils.requestAPIForFile(lastRequestPath, AudioMime.WAV, LanguageModel.LANGUAGE_TELEPHONE, category, new TaskStatusListener() {
                    @Override
                    public void onTaskStatusUpdate(DownloadTask downloadTask) {

                        if(downloadTask!=null&&taskAdapter!=null){
                            taskAdapter.updateItem(downloadTask);
                        }
                        if(downloadTask!=null&&speechToTextView!=null){
                            try {
                                speechToTextView.onSpeechRecognizationComplete(downloadTask);
                            }catch (Exception e){
                                FileLog.e("onActivityResult",e);
                                speechToTextView.setConvertedText(AndroidUtils.getString(R.string.failed_to_analyse_sentiment));
                            }
                        }else speechToTextView.setConvertedText(AndroidUtils.getString(R.string.failed_to_analyse_sentiment));
                    }

                    @Override
                    public void onTaskStarted(DownloadTask downloadTask) {
                        if(taskAdapter!=null&&downloadTask!=null){
                            taskAdapter.addItem(downloadTask);
                        }

                    }
                });
                attachSpeechToTextView(category);
            }else {
                Toast.makeText(this,R.string.audio_record_error,Toast.LENGTH_LONG).show();
                speechToTextView.setConvertedText(AndroidUtils.getString(R.string.failed_to_analyse_sentiment));
            }
        }
    }
    private void attachSpeechToTextView(String title){
        speechToTextView=new SpeechToTextView(this);
        speechToTextView.setTitle(title);
        mainFrame.addView(speechToTextView,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    private void detachSpeechToTextView(){
        if(speechToTextView!=null) {
            mainFrame.removeView(speechToTextView);
            attachMenuAdapter();
            speechToTextView=null;
        }

    }
    private void onTaskFinished(DownloadTask downloadTask){
        FileLog.write("new Task completed");
        if(downloadTask!=null&&downloadTask.getStatus().equals(TaskStatus.STATUS_FINISHED)){
            if(taskAdapter!=null)taskAdapter.updateItem(downloadTask);
            switch (downloadTask.getCategory()){
                case TaskCategory.CATEGORY_VIDEO:
                    if(videoLyricsView!=null){
                        try {
                            videoLyricsView.updateList(new MediaModel(downloadTask.getUrl(), FileLog.getSpeechResponseForFile(downloadTask.getUrl())));
                        }catch (Exception e){
                            FileLog.e("error while updating videolist",e);
                        }
                    }
                    break;
                case TaskCategory.CATEGORY_MUSIC:
                    if(musicLyriView!=null){
                        try {
                            musicLyriView.addItem(new MediaModel(downloadTask.getUrl(), FileLog.getSpeechResponseForFile(downloadTask.getUrl())));
                        }catch (Exception e){
                            FileLog.e("error while updating music list",e);
                        }
                    }
            }
        }

    }
    private void showAudioRecorder(String path,int requestCode){
        try {
            if (!path.endsWith(".wav")) path += ".wav";
            lastRequestPath = path;
            int color = AndroidUtils.getColor(R.color.music_text_color);
            AndroidAudioRecorder.with(this)
                    .setFilePath(path)
                    .setColor(color)
                    .setRequestCode(requestCode)
                    .setSource(AudioSource.MIC)
                    .setChannel(AudioChannel.STEREO)
                    .setSampleRate(AudioSampleRate.HZ_48000)
                    .record();
        }catch (Throwable e){
            FileLog.write(e.getMessage());
        }
    }

    private void attachVideoLyricView(){
        detachMenuAdapter();
        videoLyricsView=new VideoLyricsView(this,this);
        mainFrame.addView(videoLyricsView,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    private void detachVideoLyricsView(){
        if(videoLyricsView!=null){
            mainFrame.removeView(videoLyricsView);
            videoLyricsView.onDestroy();
            videoLyricsView=null;
            attachMenuAdapter();
            System.gc();
        }
    }
    private void attachMusicLyricsView(){
        detachMenuAdapter();
        musicLyriView=new MusicLyriView(this,this);
        mainFrame.addView(musicLyriView,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    private void detachMusicLyricsView(){
        if(musicLyriView!=null){
            mainFrame.removeView(musicLyriView);
            musicLyriView.onDestroy();
            musicLyriView=null;
            attachMenuAdapter();
            System.gc();
        }
    }
    @Override
    public void openVideoLyrics() {
        attachVideoLyricView();
    }

    @Override
    public void openSpeakPad() {
        showAudioRecorder(System.currentTimeMillis()+".wav",SPEAKPAD_REQUEST);
    }

    @Override
    public void openAudioLyrics() {
        attachMusicLyricsView();
    }

    @Override
    public void openWAI() {
        showAudioRecorder(System.currentTimeMillis()+".wav",WAI_REQUEST);
    }

    @Override
    public void showMedia(MediaModel mediaModel,int requestCode) {
        if(requestCode==SHOW_VIDEO_REQUEST){
            if(videoLyricsView!=null){
                videoLyricsView.showMedia(mediaModel);
            }
        }else if(requestCode==SHOW_MUSIC_REQUEST){
            if(musicLyriView!=null){
                musicLyriView.showMedia(mediaModel);
            }
        }
    }

    @Override
    public void onAddTask(String url, String mimeType, String languageModel, String category) {
            AndroidUtils.requestAPIForFile(url, mimeType, languageModel, category, new TaskStatusListener() {
                @Override
                public void onTaskStatusUpdate(DownloadTask downloadTask) {
                    onTaskFinished(downloadTask);
                }

                @Override
                public void onTaskStarted(DownloadTask downloadTask) {
                    if(taskAdapter!=null){
                        taskAdapter.addItem(downloadTask);
                    }
                }
            });
    }

    @Override
    public void onBackPressed() {
        boolean ret=false;
        if(slidingPaneLayout!=null&&
                (slidingPaneLayout.getPanelState()== SlidingUpPanelLayout.PanelState.EXPANDED||
                        slidingPaneLayout.getPanelState()== SlidingUpPanelLayout.PanelState.ANCHORED)){
            slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            ret=true;
        }

        if(videoLyricsView!=null){
            if(!videoLyricsView.onBackPressed()){
                detachVideoLyricsView();
                ret = true;
            }
        }
        if(musicLyriView!=null){
            if(!musicLyriView.onBackPressed()){
                detachMusicLyricsView();
                ret=true;
            }
        }
        if(speechToTextView!=null){
            detachSpeechToTextView();
            ret=true;
        }
        if(!ret)super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if(videoLyricsView!=null)videoLyricsView.onPause();
        if(musicLyriView!=null)musicLyriView.onPause();
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        if(videoLyricsView!=null)videoLyricsView.onResume();
        if(musicLyriView!=null)musicLyriView.onResume();
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        if(videoLyricsView!=null)videoLyricsView.onDestroy();
        if(musicLyriView!=null)musicLyriView.onDestroy();
        super.onDestroy();
    }
}

