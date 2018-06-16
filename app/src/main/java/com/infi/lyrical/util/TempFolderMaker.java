package com.infi.lyrical.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by INFIi on 11/27/2017.
 */

public class TempFolderMaker {
    private static final String ROOT= Environment.getExternalStorageDirectory().getPath();
    private static final String SLASH="/";
    private static final String PARENT="Lyrical";
    private static final String CACHE="Cache";
    private static final String VIDEO_SUBTITLES="Video";
    private static final String AUDIO_SUBTITLES="Audio";
    private static final String CONVERTED_TEXT="SpeakPad";
    private static final String CONVERTED_SENTIMENT="WAI";

    private static final String TASK_STATUS="taskstatus.json";

    private static File parent;
    private static File cache;
    private static File status;
    private static File texts;
    private static File videoSubtitle;
    private static File audioSubtitle;
    private static File sentiments;
    public static void createFolders() {


        try {

            parent = new File(ROOT, SLASH + PARENT);
            if (!parent.exists()) parent.mkdirs();

            cache=new File(parent,CACHE);
            if(!cache.exists())cache.mkdirs();

            status = new File(parent, TASK_STATUS);
            if (!status.exists()) status.createNewFile();

            texts=new File(parent,CONVERTED_TEXT);
            if(!texts.exists())texts.mkdirs();

            videoSubtitle=new File(parent,VIDEO_SUBTITLES);
            if(!videoSubtitle.exists())videoSubtitle.mkdirs();

            audioSubtitle=new File(parent,AUDIO_SUBTITLES);
            if(!audioSubtitle.exists())audioSubtitle.mkdirs();

            sentiments=new File(cache,CONVERTED_SENTIMENT);
            if(!sentiments.exists())sentiments.mkdirs();

        }catch (IOException io){

        }

    }

    public static File getVideoSubtitle() {
        return videoSubtitle;
    }

    public static File getAudioSubtitle() {
        return audioSubtitle;
    }

    public static File getSentiments() {
        return sentiments;
    }

    public static File getParent() {
        return parent;
    }

    public static File getCache() {
        return cache;
    }

    public static File getStatus() {
        return status;
    }

    public static File getTexts() {
        return texts;
    }
}
