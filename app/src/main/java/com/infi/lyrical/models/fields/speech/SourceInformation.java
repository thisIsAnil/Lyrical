package com.infi.lyrical.models.fields.speech;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by INFIi on 11/26/2017.
 */

public class SourceInformation {
    @SerializedName("mime_type")
    @Expose
    private String mimeType;
    @SerializedName("video_information")
    @Expose
    private VideoInformation videoInformation;
    @SerializedName("audio_information")
    @Expose
    private AudioInformation audioInformation;

    public SourceInformation() {
    }

    public SourceInformation(String mimeType, VideoInformation videoInformation, AudioInformation audioInformation) {
        this.mimeType = mimeType;
        this.videoInformation = videoInformation;
        this.audioInformation = audioInformation;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public VideoInformation getVideoInformation() {
        return videoInformation;
    }

    public void setVideoInformation(VideoInformation videoInformation) {
        this.videoInformation = videoInformation;
    }

    public AudioInformation getAudioInformation() {
        return audioInformation;
    }

    public void setAudioInformation(AudioInformation audioInformation) {
        this.audioInformation = audioInformation;
    }
}
