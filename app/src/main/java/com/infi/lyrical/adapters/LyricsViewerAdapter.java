package com.infi.lyrical.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infi.lyrical.R;
import com.infi.lyrical.adapters.holders.LyricHolder;
import com.infi.lyrical.helper.FileLog;
import com.infi.lyrical.models.ui.LyricModel;
import com.infi.lyrical.util.AndroidUtils;

import java.util.List;

/**
 * Created by INFIi on 12/2/2017.
 */

public class LyricsViewerAdapter extends RecyclerView.Adapter<LyricHolder> {

    public interface onLyricsChangedListener{
        void onLyricsChanged(int position);
    }
    private onLyricsChangedListener lyricsChangedListener;

    private List<LyricModel> lyricModels;
    private Context context;
    private View root;
    private long currentPosition;
    private long currentMin;
    private long callbackAt;
    private int lastPosition;
    public LyricsViewerAdapter(Context context,List<LyricModel> lyricModels,onLyricsChangedListener lyricsChangedListener){
        this.lyricModels=lyricModels;
        this.context=context;
        this.lyricsChangedListener=lyricsChangedListener;
        currentPosition=0;
    }

    @Override
    public LyricHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        root= LayoutInflater.from(context).inflate(R.layout.holder_lyric,parent,false);
        return new LyricHolder(root);
    }

    @Override
    public void onBindViewHolder(LyricHolder holder, int position) {
        try {
            if (holder == null) holder = new LyricHolder(root);
            LyricModel model = lyricModels.get(position);
            holder.lyricTv.setText(model.getLineData());
            if (currentPosition >= model.getStart() && currentPosition <= model.getStop()) {
                callbackAt = model.getStop();
                currentMin = model.getStart();
                lastPosition = holder.getAdapterPosition();
                lyricsChangedListener.onLyricsChanged(lastPosition);
                holder.lyricTv.setTextColor(AndroidUtils.getColor(R.color.notepad_text_color));
                notifyItemRangeChanged(position - 1, position + 1);
            } else holder.lyricTv.setTextColor(AndroidUtils.getColor(R.color.white));
        }catch (Exception e){
            FileLog.e("MusicPlayer#lyricsadapter ",e);
        }
    }

    public void setCurrentPosition(long currentPosition) {
        try {
            currentPosition=currentPosition/10;
            if(lastPosition==lyricModels.size()-1)return;
            this.currentPosition = currentPosition;
            if (currentPosition > callbackAt) {
                notifyItemRangeChanged(lastPosition - 1, lyricModels.size()-1);
            } else if (currentPosition < currentMin) {
                notifyItemRangeChanged(0, lastPosition);
            }
        }catch (Exception e){
            FileLog.write("currentPostion is: "+currentPosition);
            FileLog.e("lyricsadapter#setCurrentPosition ",e);
        }
    }

    @Override
    public int getItemCount() {
        return lyricModels.size();
    }
}
