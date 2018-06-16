package com.infi.lyrical.models.ui;

/**
 * Created by INFIi on 12/2/2017.
 */

public class LyricModel {
    private String lineData;
    private long start;
    private long stop;

    public LyricModel(String lineData, long start, long stop) {
        this.lineData = lineData;
        this.start = start;
        this.stop = stop;
    }

    public String getLineData() {
        return lineData;
    }

    public void setLineData(String lineData) {
        this.lineData = lineData;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStop() {
        return stop;
    }

    public void setStop(long stop) {
        this.stop = stop;
    }
}
