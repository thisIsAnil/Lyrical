package com.infi.lyrical.models.fields.speech;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by INFIi on 11/26/2017.
 */

public class VideoInformation {
    @SerializedName("width")
    @Expose
    private Integer width;
    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("codec")
    @Expose
    private String codec;
    @SerializedName("pixel_aspect_ratio")
    @Expose
    private String pixelAspectRatio;

    public VideoInformation() {
    }


    public VideoInformation(Integer width, Integer height, String codec, String pixelAspectRatio) {
        super();
        this.width = width;
        this.height = height;
        this.codec = codec;
        this.pixelAspectRatio = pixelAspectRatio;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getPixelAspectRatio() {
        return pixelAspectRatio;
    }

    public void setPixelAspectRatio(String pixelAspectRatio) {
        this.pixelAspectRatio = pixelAspectRatio;
    }

}
