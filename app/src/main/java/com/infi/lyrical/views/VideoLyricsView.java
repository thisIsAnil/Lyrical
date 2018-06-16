package com.infi.lyrical.views;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.infi.lyrical.R;
import com.infi.lyrical.adapters.LyricsActionListener;
import com.infi.lyrical.adapters.VideoLyricsAdapter;
import com.infi.lyrical.helper.FileLog;
import com.infi.lyrical.models.SpeechResponse;
import com.infi.lyrical.models.ui.MediaModel;
import com.infi.lyrical.task.DownloadTask;
import com.infi.lyrical.task.TaskCategory;
import com.infi.lyrical.task.TaskStatus;
import com.infi.lyrical.util.AndroidUtils;
import com.infi.lyrical.util.LanguageModel;
import com.infi.lyrical.util.TempFolderMaker;
import com.infi.lyrical.views.Gallery.Gallery_view;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by INFIi on 12/2/2017.
 */

public class VideoLyricsView extends LyricsView{

    private VideoLyricsAdapter videoLyricsAdapter;
    private VideoPlayer videoPlayer;
    private Gallery_view gallery_view;
    public VideoLyricsView(Context context,LyricsActionListener actionListener){
        super(context,actionListener);
        titlebar.setText(AndroidUtils.getString(R.string.lyrical_video));
        generateMediaModel();
    }


    @Override
    protected void onAddClicked() {
        gallery_view=new Gallery_view(context);
        gallery_view.setSelect1(true);
        gallery_view.setDelegate(new Gallery_view.Delegate() {
            @Override
            public void onBackPressed() {
                if(gallery_view.getPlaying()){
                    gallery_view.stopPlaying();
                }else VideoLyricsView.this.onBackPressed();
            }

            @Override
            public void onFilesSelected(List<String> files) {
                removeView(gallery_view);
                root.setVisibility(VISIBLE);
                Toast.makeText(context,"Task will be added shortly",Toast.LENGTH_LONG).show();
                //if(FileLog.DEBUG)debugPlayerView();
                lyricsActionListener.onAddTask(files.get(0),"video/*", LanguageModel.LANGUAGE_US,TaskCategory.CATEGORY_VIDEO);
            }

            @Override
            public void selectFromGallery(boolean select1) {

            }
        });
        root.setVisibility(GONE);
        addView(gallery_view);
    }

    private void debugPlayerView(){
        try{
            SpeechResponse speechResponse=FileLog.getSpeechResponseForFile(TempFolderMaker.getCache().getAbsolutePath()+"/texts/sample.json");
            MediaModel mediaModel=new MediaModel("/sdcard/sample.mp4",speechResponse);
            showMedia(mediaModel);
        }catch (Exception e){
            FileLog.write(e.getMessage());
        }

    }
    private void generateMediaModel(){
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected void onPreExecute() {
                loadingFrame.setVisibility(VISIBLE);
                lyricsRv.setVisibility(GONE);
                lyricsNotFound.setVisibility(GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    List<DownloadTask> downloadTasks = FileLog.getTaskStatusList();
                    mediaModelList = new ArrayList<>();
                    for (
                            DownloadTask d : downloadTasks)

                    {
                        if (d.getCategory().equals(TaskCategory.CATEGORY_VIDEO) && d.getStatus().equals(TaskStatus.STATUS_FINISHED)) {
                            mediaModelList.add(new MediaModel(d.getUrl(), FileLog.getSpeechResponseForFile(d.getUrl())));
                        }
                    }
                }catch (Exception e){
                    FileLog.e(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(mediaModelList==null||mediaModelList.size()==0){
                    showNotFound();
                }else {
                    hideNotFound();
                    setupRV();
                }
            }
        }.execute();
    }
    public void updateList(MediaModel model){
        if(mediaModelList==null){
            mediaModelList=new ArrayList<>();
            mediaModelList.add(model);
            hideNotFound();
            setupRV();
            return;
        }
        if(videoLyricsAdapter!=null){
            videoLyricsAdapter.addItem(model);
        }
    }
    private void setupRV(){
        videoLyricsAdapter=new VideoLyricsAdapter(context,mediaModelList,lyricsActionListener);
        lyricsRv.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        lyricsRv.setAdapter(videoLyricsAdapter);
    }

    public void onPause(){
        if(videoPlayer!=null){
            videoPlayer.onPause();
        }
    }
    public void onResume(){
        if(videoPlayer!=null)videoPlayer.onResume();
    }
    public void onDestroy(){
        if(videoPlayer!=null){
            removeView(videoPlayer);
            videoPlayer.onDestroy();
            videoPlayer=null;
        }
        if(gallery_view!=null)gallery_view=null;
    }
    public boolean onBackPressed(){
        boolean ret=false;
        if(videoPlayer!=null){
            removeView(videoPlayer);
            videoPlayer.onDestroy();
            videoPlayer=null;
            root.setVisibility(VISIBLE);
            ret=true;
        }
        if(gallery_view!=null){
            if(gallery_view.getPlaying())gallery_view.stopPlaying();
            else {
                gallery_view = null;
                root.setVisibility(VISIBLE);
            }
            ret=true;
        }
        return ret;
    }

    public void showMedia(MediaModel mediaModel) {
            root.setVisibility(GONE);
            videoPlayer=new VideoPlayer(context);
        try {
            videoPlayer.loadVideoForModel(mediaModel);
            addView(videoPlayer);
        }catch (Exception e){
            FileLog.e("VideoLyrics player",e);

            root.setVisibility(VISIBLE);
        }
    }
}
