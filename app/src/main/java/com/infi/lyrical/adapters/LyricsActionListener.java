package com.infi.lyrical.adapters;

import com.infi.lyrical.models.ui.MediaModel;

public interface LyricsActionListener{
        void showMedia(MediaModel mediaModel,int requestCode);
        void onAddTask(String url,String mimeType,String languageModel,String category);
}
    