package com.infi.lyrical.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infi.lyrical.MainActivity;
import com.infi.lyrical.R;
import com.infi.lyrical.adapters.holders.MediaHolder;
import com.infi.lyrical.models.ui.MediaModel;
import com.infi.lyrical.util.AndroidUtils;

import java.io.File;
import java.util.List;

/**
 * Created by INFIi on 12/2/2017.
 */

public class MusicLyricAdapter extends RecyclerView.Adapter<MediaHolder> {
    private LyricsActionListener lyricsActionListener;
    private Context context;
    private List<MediaModel> mediaModels;
    private View root;
    public MusicLyricAdapter(Context context,List<MediaModel> mediaModels,LyricsActionListener lyricsActionListener){
        this.context=context;
        this.mediaModels=mediaModels;
        this.lyricsActionListener=lyricsActionListener;
    }
    @Override
    public MediaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        root= LayoutInflater.from(context).inflate(R.layout.holder_media,parent,false);
        return new MediaHolder(root);
    }

    @Override
    public void onBindViewHolder(MediaHolder holder, int position) {
        final MediaModel mediaModel=mediaModels.get(position);
        File mediaFile =new File(mediaModel.getUrl());
        holder.mediaName.setText(mediaFile.getName().substring(0,25));
        holder.mediaDetails.setText(AndroidUtils.getAudioMeta(mediaFile.getAbsolutePath()));
        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lyricsActionListener.showMedia(mediaModel, MainActivity.SHOW_MUSIC_REQUEST);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mediaModels.size();
    }

    public void addItem(MediaModel mediaModel){
        mediaModels.add(mediaModel);
        notifyItemInserted(mediaModels.size()-1);
        notifyDataSetChanged();
    }
}
