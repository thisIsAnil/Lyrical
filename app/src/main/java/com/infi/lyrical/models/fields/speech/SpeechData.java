package com.infi.lyrical.models.fields.speech;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by INFIi on 11/26/2017.
 */

public class SpeechData {
    @SerializedName("start_time_offset")
    @Expose
    private Double startOffset;

    @SerializedName("end_time_offset")
    @Expose
    private Double endOffset;

    @SerializedName("text")
    @Expose
    private String content;

    @SerializedName("confidence")
    @Expose
    private Double confidence;

    public SpeechData() {
    }

    public SpeechData(Double startOffset, Double endOffset, String content, Double confidence) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.content = content;
        this.confidence = confidence;
    }

    public Double getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Double startOffset) {
        this.startOffset = startOffset;
    }

    public Double getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(Double endOffset) {
        this.endOffset = endOffset;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "SpeechResponse:{"+"start:"+startOffset+" end: "+endOffset+" text: "+content+" confidence: "+confidence+" }";
    }
}
