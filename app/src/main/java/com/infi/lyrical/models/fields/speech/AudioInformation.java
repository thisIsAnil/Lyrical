package com.infi.lyrical.models.fields.speech;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by INFIi on 11/26/2017.
 */

public class AudioInformation {
    @SerializedName("codec")
    @Expose
    private String codec;

    @SerializedName("sample_rate")
    @Expose
    private Integer sampleRate;

    @SerializedName("channels")
    @Expose
    private Integer channels;

    public AudioInformation() {
    }

    public AudioInformation(String codec, Integer sampleRate, Integer channels) {
        this.codec = codec;
        this.sampleRate = sampleRate;
        this.channels = channels;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public Integer getChannels() {
        return channels;
    }

    public void setChannels(Integer channels) {
        this.channels = channels;
    }
}
