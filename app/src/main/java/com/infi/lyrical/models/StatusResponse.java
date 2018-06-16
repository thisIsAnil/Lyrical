package com.infi.lyrical.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by INFIi on 11/26/2017.
 */

public class StatusResponse {

    @SerializedName("jobID")
    @Expose
    String jobID;

    @SerializedName("status")
    @Expose
    String status;

    public StatusResponse(String jobID, String status) {
        this.jobID = jobID;
        this.status = status;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
