package com.infi.lyrical.adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.infi.lyrical.R;
import com.infi.lyrical.adapters.holders.MusicHolder;
import com.infi.lyrical.helper.FileLog;
import com.infi.lyrical.models.SpeechResponse;
import com.infi.lyrical.models.ui.AudioData;
import com.infi.lyrical.models.ui.MediaModel;
import com.infi.lyrical.util.AndroidUtils;
import com.infi.lyrical.util.TempFolderMaker;

import java.util.List;

/**
 * Created by INFIi on 12/2/2017.
 */

public class MusicAdapter extends BaseAdapter {

    public interface MusicActionListener{
        void onMusicSelected(String musicPath,long musicDuration);
    }
    private List<AudioData> songsList;
    private Context context;
    private int playingPosition;
    private int finalPlayingPos;
    private MediaPlayer mediaPlayer;
    private MusicActionListener musicActionListener;
    private int colorText;
    public MusicAdapter(Context context, List<AudioData> songsList, int playingPosition,MusicActionListener musicActionListener) {
        this.context = context;
        this.songsList = songsList;
        this.playingPosition = playingPosition;
        this.musicActionListener=musicActionListener;
        colorText=AndroidUtils.getColor(R.color.primary_text);
        finalPlayingPos=-1;
    }

    @Override
    public int getCount() {
        return songsList.size();
    }

    @Override
    public Object getItem(int i) {
        return songsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setPlayingPosition(int playingPosition) {
        this.playingPosition = playingPosition;
    }

    public void onPause(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying())mediaPlayer.pause();
    }
    public void onResume(){
        try {
            if (mediaPlayer != null) mediaPlayer.start();
        }catch (Exception e){

        }

    }
    public void onDestroy(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer=null;
        }
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_list_row, viewGroup, false);

        final MusicHolder viewHolder = new MusicHolder(view);

        try {

            viewHolder.title.setTextColor(colorText);
            viewHolder.title.setText(songsList.get(i).getTitle());
            viewHolder.artist.setText(songsList.get(i).getArtist());
            viewHolder.add.setVisibility(View.GONE);

            final int pos = i;
            if (i == playingPosition || i == finalPlayingPos) {
                viewHolder.title.setTextColor(AndroidUtils.getColor(R.color.music_text_color));
                viewHolder.add.setVisibility(View.VISIBLE);
            }
            viewHolder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AudioData data=songsList.get(pos);
                    playMusic(data);
                    playingPosition=pos;
                    finalPlayingPos=pos;
                    String musicPath=data.getPath();
                    long musicDuration=data.getDuration();
                    try {
                        musicActionListener.onMusicSelected(musicPath, musicDuration);
                    }catch (Exception e){
                        FileLog.e("error while adding music task",e);
                        Toast.makeText(context,"Check Your Internet",Toast.LENGTH_LONG).show();
                    }
                }
            });

            viewHolder.frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.add.setVisibility(View.VISIBLE);
                    FileLog.write("musicAdapter#frame clicked");
                    AudioData data=songsList.get(pos);
                    playMusic(data);
                    playingPosition=pos;
                }
            });


        } catch (Exception np) {
            FileLog.e("musicadapter",np);
            // Toast.makeText(context, np.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return view;
    }
    private void playMusic(AudioData data){
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.setDataSource(data.getPath());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.prepare();
        }catch (Exception e){
            FileLog.e("music player#add",e);
        }
    }
}



