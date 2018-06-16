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
import com.infi.lyrical.util.VideoRequestHandler;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by INFIi on 12/2/2017.
 */

public class VideoLyricsAdapter extends RecyclerView.Adapter<MediaHolder> {
    private Context context;
    List<MediaModel> mediaModels;
    private View root;
    private Picasso picasso;
    private VideoRequestHandler handler;
    private LyricsActionListener actionListener;
    public VideoLyricsAdapter(Context context,List<MediaModel> modelList,LyricsActionListener actionListener){
        this.context=context;
        mediaModels=modelList;
        this.actionListener=actionListener;
        handler=new VideoRequestHandler();
        picasso=new Picasso.Builder(context)
                .addRequestHandler(handler).build();

    }
    @Override
    public MediaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        root= LayoutInflater.from(context).inflate(R.layout.holder_media,parent,false);
        return new MediaHolder(root);
    }

    @Override
    public void onBindViewHolder(MediaHolder holder, int position) {
        if(holder==null)holder=new MediaHolder(root);
        final MediaModel mediaModel=mediaModels.get(position);
        File mediaFile=new File(mediaModel.getUrl());
        holder.mediaIcon.setBackgroundResource(R.drawable.gradient_video);
        picasso.load(handler.SCHEME_VIDEO+":"+mediaFile.getAbsolutePath()).error(R.drawable.ic_video).into(holder.mediaIcon);
        holder.mediaName.setText(mediaFile.getName());
        holder.mediaDetails.setText(AndroidUtils.getMetaDuration(mediaFile));
        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.showMedia(mediaModel, MainActivity.SHOW_VIDEO_REQUEST);
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
