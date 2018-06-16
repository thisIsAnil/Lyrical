package com.infi.lyrical.adapters;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infi.lyrical.R;
import com.infi.lyrical.adapters.holders.MenuHolder;
import com.infi.lyrical.helper.FileLog;
import com.infi.lyrical.util.AndroidUtils;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by INFIi on 11/30/2017.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuHolder>{

    public interface onActionDelegate{
        void openVideoLyrics();
        void openSpeakPad();
        void openAudioLyrics();
        void openWAI();
    }
    private final  int[]bg_colors={R.drawable.gradient_video,R.drawable.gradient_notepad,R.drawable.gradient_music,R.drawable.gradient_wai};
    private final int[]icons={R.drawable.ic_video,R.drawable.ic_notepad,R.drawable.ic_music,R.drawable.ic_who_am_i};
    private final  int[]titles={R.string.lyrical_video,R.string.lyrical_notepad,R.string.lyrical_audio,R.string.lyrical_how_am_i};
    private onActionDelegate actionDelegate;

    public MenuAdapter(onActionDelegate delegate){
        actionDelegate=delegate;
    }
    @Override
    public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root= LayoutInflater.from(AndroidUtils.context).inflate(R.layout.holder_menu,parent,false);

        return new MenuHolder(root);
    }

    @Override
    public void onBindViewHolder(MenuHolder holder, int position) {
        try {
            holder.gradientFrame.setBackgroundResource(bg_colors[position]);
            holder.menuIcon.setImageResource(icons[position]);
            holder.menuTitle.setText(titles[position]);
            final int pos=holder.getAdapterPosition();
            holder.gradientFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendClickEvent(pos);
                }
            });
        }catch (Exception e){
            FileLog.e("menuadapter#",e);
        }
    }

    private void sendClickEvent(int position){
        if(position==0)actionDelegate.openVideoLyrics();
        else if(position==1)actionDelegate.openSpeakPad();
        else if(position==2)actionDelegate.openAudioLyrics();
        else actionDelegate.openWAI();
    }
    @Override
    public int getItemCount() {
        return 4;
    }
}
