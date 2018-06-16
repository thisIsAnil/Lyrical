package com.infi.lyrical.views;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.infi.lyrical.R;
import com.infi.lyrical.adapters.LyricsActionListener;
import com.infi.lyrical.adapters.MusicAdapter;
import com.infi.lyrical.adapters.MusicLyricAdapter;
import com.infi.lyrical.helper.FileLog;
import com.infi.lyrical.models.SpeechResponse;
import com.infi.lyrical.models.ui.MediaModel;
import com.infi.lyrical.task.DownloadTask;
import com.infi.lyrical.task.TaskCategory;
import com.infi.lyrical.task.TaskStatus;
import com.infi.lyrical.util.AndroidUtils;
import com.infi.lyrical.util.LanguageModel;
import com.infi.lyrical.util.TempFolderMaker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by INFIi on 12/2/2017.
 */

public class MusicLyriView extends LyricsView{

    private MusicPlayer musicPlayer;
    private MusicLyricAdapter lyricAdapter;
    private MusicGalleryView musicGalleryView;

    public MusicLyriView(Context context,LyricsActionListener lyricsActionListener){
        super(context,lyricsActionListener);
        titlebar.setText(AndroidUtils.getString(R.string.lyrical_audio));
            generateMediaModel();
    }

    @Override
    protected void onAddClicked() {
        root.setVisibility(GONE);
        musicGalleryView=new MusicGalleryView(context);
        musicGalleryView.showList(context, new MusicAdapter.MusicActionListener() {
            @Override
            public void onMusicSelected(String musicPath, long musicDuration) {
                musicGalleryView.onDestroy();
                removeView(musicGalleryView);
                root.setVisibility(VISIBLE);
                Toast.makeText(context,"Task will be added shortly",Toast.LENGTH_LONG).show();
                //if(FileLog.DEBUG)debugPlayerTest();
                lyricsActionListener.onAddTask(musicPath,"audio/*", LanguageModel.LANGUAGE_TELEPHONE,TaskCategory.CATEGORY_MUSIC);
            }
        });
        addView(musicGalleryView);
    }

    private void debugPlayerTest(){
        try{
            SpeechResponse speechResponse=FileLog.getSpeechResponseForFile(TempFolderMaker.getCache().getAbsolutePath()+"/texts/sample.json");
            MediaModel mediaModel=new MediaModel("/sdcard/sampleAudio.mp3",speechResponse);
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
                        if (d.getCategory().equals(TaskCategory.CATEGORY_MUSIC) && d.getStatus().equals(TaskStatus.STATUS_FINISHED)) {
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
    private void setupRV(){
        lyricAdapter=new MusicLyricAdapter(context,mediaModelList,lyricsActionListener);
        lyricsRv.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        lyricsRv.setAdapter(lyricAdapter);
    }
    public void addItem(MediaModel mediaModel){
        if(mediaModelList==null){
            mediaModelList=new ArrayList<>();
        }
        if(lyricAdapter==null){
            mediaModelList.add(mediaModel);
            hideNotFound();
            setupRV();
        }else {
            lyricAdapter.addItem(mediaModel);
        }
    }
    public void showMedia(MediaModel mediaModel) {
            root.setVisibility(GONE);
            musicPlayer=new MusicPlayer(context);
            try{
                musicPlayer.loadMusicForModel(mediaModel);
                addView(musicPlayer);
            }catch (Exception e){
                Toast.makeText(context,"Failed to load",Toast.LENGTH_LONG).show();
                root.setVisibility(VISIBLE);
            }
    }

    public void onPause(){
        if(musicPlayer!=null){
            musicPlayer.onPause();
        }
        if(musicGalleryView!=null)
            musicGalleryView.onPause();
    }
    public void onResume(){
        if(musicPlayer!=null){
            musicPlayer.onResume();
        }
        if(musicGalleryView!=null)
            musicGalleryView.onResume();
    }
    public boolean onBackPressed(){
        boolean ret=false;
        if(musicPlayer!=null){
            removeView(musicPlayer);
            musicPlayer.destroy();
            musicPlayer=null;
            root.setVisibility(VISIBLE);
            ret=true;
        }
        if(musicGalleryView!=null){
            musicGalleryView.onDestroy();
            removeView(musicGalleryView);
            musicGalleryView=null;
            root.setVisibility(VISIBLE);
            ret=true;
        }
        return ret;
    }

    public void onDestroy(){
        if(musicPlayer!=null){
            removeView(musicPlayer);
            musicPlayer.destroy();
            musicPlayer=null;
        }
        if(musicGalleryView!=null){
            musicGalleryView.onDestroy();
            removeView(musicGalleryView);
            musicGalleryView=null;
        }
    }
}
