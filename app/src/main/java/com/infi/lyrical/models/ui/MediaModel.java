package com.infi.lyrical.models.ui;

import com.infi.lyrical.models.SpeechResponse;

/**
 * Created by INFIi on 12/1/2017.
 */

public class MediaModel {
    private String url;
    private SpeechResponse subtitle;

    public MediaModel(String url, SpeechResponse subtitle) {
        this.url = url;
        this.subtitle = subtitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SpeechResponse getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(SpeechResponse subtitle) {
        this.subtitle = subtitle;
    }
}
