package com.infi.lyrical.task;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by INFIi on 11/27/2017.
 */

public class DownloadTask {

    @SerializedName("uid")
    @Expose
    private Integer uid;
    @SerializedName("jobId")
    @Expose
    private String jobId;

    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("isFile")
    @Expose
    private Boolean isFile;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("subtitlePath")
    @Expose
    private String subTitlePath;

    @SerializedName("timeStamp")
    @Expose
    private String timeStamp;

    public DownloadTask(Integer uid, String jobId, String category,Boolean isFile, String url, String status, String subTitlePath, String timeStamp) {
        this.uid = uid;
        this.jobId = jobId;
        this.category=category;
        this.isFile = isFile;
        this.url = url;
        this.status = status;
        this.subTitlePath = subTitlePath;
        this.timeStamp = timeStamp;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getFile() {
        return isFile;
    }

    public void setFile(Boolean file) {
        isFile = file;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubTitlePath() {
        return subTitlePath;
    }

    public void setSubTitlePath(String subTitlePath) {
        this.subTitlePath = subTitlePath;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
