package com.infi.lyrical.views.Gallery;

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;


import com.infi.lyrical.R;
import com.infi.lyrical.helper.FileLog;
import com.infi.lyrical.util.AndroidUtils;

import java.io.File;

/**
 * Created by INFIi on 8/6/2017.
 */

public class GalleryPlayer extends BlurRelativeLayout {
    public interface onHideListener{
        void onHide();
    }
    onHideListener hideListener;

    public void setHideListener(onHideListener hideListener) {
        this.hideListener = hideListener;
    }
    private VideoView videoView;
    private FrameLayout frame;
    public GalleryPlayer(Context context){
        super(context,null);
        videoView=new VideoView(context);
        videoView.setTag("video");
        frame=new FrameLayout(context);
        frame.setBackgroundResource(R.drawable.player_bg);
        frame.setTag("frame");
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(v.getTag()!=null&&v.getTag().equals("frame"));
                    if(hideListener!=null)hideListener.onHide();
                }catch (Exception e){
                    FileLog.write(e.getMessage());
                }
            }
        });
        videoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public void play(String path) throws Exception{
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(path);
        int vw=Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int vh=Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        float rt=(float)vh/vw;
        int width=Math.min(AndroidUtils.displaySize.x,AndroidUtils.displaySize.y);
        int height=(int)(rt*width)+AndroidUtils.dp(60);
        RelativeLayout.LayoutParams params1=new RelativeLayout.LayoutParams(width-AndroidUtils.dp(40),height);
        params1.addRule(CENTER_IN_PARENT);
        params1.setMargins(AndroidUtils.dp(0),AndroidUtils.dp(30),AndroidUtils.dp(0),AndroidUtils.dp(30));
        addView(frame,params1);

        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(width-AndroidUtils.dp(40),height);
        params.setMargins(AndroidUtils.dp(0),AndroidUtils.dp(30),AndroidUtils.dp(0),AndroidUtils.dp(30));
        params.gravity= Gravity.CENTER;
        frame.addView(videoView,params);

        TextView title=new TextView(getContext());
        File f=new File(path);
        title.setText(f.getName());
        title.setTextColor(0x00000000);
        title.setTextSize(18);
        title.setTypeface(Typeface.SANS_SERIF);
        FrameLayout.LayoutParams tP=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tP.gravity=Gravity.TOP;
        tP.setMargins(AndroidUtils.dp(10),AndroidUtils.dp(20),AndroidUtils.dp(10),0);
        frame.addView(title,tP);

        videoView.setVideoPath(path);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });
        AndroidUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                setShouldRender(true);
            }
        },200);
    }
    public void stop(){
        if(videoView!=null&&videoView.isPlaying())videoView.stopPlayback();
    }
}
