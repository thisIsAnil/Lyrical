package com.infi.lyrical.adapters.holders;

import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infi.lyrical.R;

/**
 * Created by INFIi on 12/2/2017.
 */

public class MusicHolder {
    public TextView title,artist;
    public AppCompatImageView icon;
    public Button add;
    public LinearLayout frame;
        public MusicHolder(View view){
            icon=(AppCompatImageView) view.findViewById(R.id.album_art);
            title=(TextView)view.findViewById(R.id.music_title);
            add=(Button)view.findViewById(R.id.add_btn);
            artist=(TextView)view.findViewById(R.id.artistName);
            frame=(LinearLayout)view.findViewById(R.id.music_row_frame);
        }
}
