package com.infi.lyrical.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by INFIi on 11/26/2017.
 */

public class ErrorResponse {

    @SerializedName("error")
    private Integer error;

    @SerializedName("reason")
    private String reason;

    public ErrorResponse(){}
    public ErrorResponse(Integer error, String reason) {
        this.error = error;
        this.reason = reason;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ErrorResponse: { "+" error: "+error+" reason: "+reason+" } ";
    }
}
