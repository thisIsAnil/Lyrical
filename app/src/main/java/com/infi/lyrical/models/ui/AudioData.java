package com.infi.lyrical.models.ui;

/**
 * Created by INFIi on 12/2/2017.
 */

public class AudioData{
    private String title,artist,path;
    private int duration;
    private String albumart;
    public AudioData(String title,String artist,String path,String albumart,int duration){
        this.title=title;
        this.albumart=albumart;
        this.title=title;
        this.path=path;
        this.duration=duration;
        this.artist=artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAlbumart() {
        return albumart;
    }

    public void setAlbumart(String albumart) {
        this.albumart = albumart;
    }
}

