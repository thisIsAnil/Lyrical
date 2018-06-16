package com.infi.lyrical.models.fields.speech;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by INFIi on 11/26/2017.
 */

public class FieldAction {
    @SerializedName("result")
    @Expose
    private FieldResult results;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("action")
    @Expose
    private String action;

    public FieldAction() {
    }

    public FieldAction(FieldResult results, String status, String action) {
        this.results = results;
        this.status = status;
        this.action = action;
    }

    public FieldResult getResults() {
        return results;
    }

    public void setResults(FieldResult results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "FieldAction is: { "+" result: "+results.toString()+" Status: "+status+" action: "+action+" } ";
    }


}
