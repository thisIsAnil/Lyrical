package com.infi.lyrical.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.infi.lyrical.models.fields.speech.FieldAction;

import java.util.List;

/**
 * Created by INFIi on 11/26/2017.
 */

public class SpeechResponse {
    @SerializedName("actions")
    @Expose
    private List<FieldAction> actions;

    @SerializedName("jobID")
    @Expose
    private String jobId;

    @SerializedName("status")
    @Expose
    private String status;

    public SpeechResponse() {
    }

    public SpeechResponse(List<FieldAction> actions, String jobId, String status) {
        this.actions = actions;
        this.jobId = jobId;
        this.status = status;
    }

    public List<FieldAction> getActions() {
        return actions;
    }

    public void setActions(List<FieldAction> actions) {
        this.actions = actions;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String res="actions: [";
        if(actions!=null){
            for(FieldAction fa:actions){
                res+=fa.toString();
            }
        }else res+="list is null ";
        res+=" ] "+" jobID: "+jobId+" status: "+status+" }";
        return res;
    }
}
