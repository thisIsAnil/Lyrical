package com.infi.lyrical.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.infi.lyrical.models.fields.sentiment.Aggregate;

import java.util.List;

/**
 * Created by INFIi on 12/2/2017.
 */

public class SentimentModel {

    @SerializedName("aggregate")
    @Expose
    private Aggregate aggregate;

    @SerializedName("negative")
    @Expose
    private List<SentimentModel> negative;

    @SerializedName("positive")
    @Expose
    private List<SentimentModel> positive;

    public SentimentModel() {
    }

    public SentimentModel(Aggregate aggregate, List<SentimentModel> negative, List<SentimentModel> positive) {
        this.aggregate = aggregate;
        this.negative = negative;
        this.positive = positive;
    }

    public Aggregate getAggregate() {
        return aggregate;
    }

    public void setAggregate(Aggregate aggregate) {
        this.aggregate = aggregate;
    }

    public List<SentimentModel> getNegative() {
        return negative;
    }

    public void setNegative(List<SentimentModel> negative) {
        this.negative = negative;
    }

    public List<SentimentModel> getPositive() {
        return positive;
    }

    public void setPositive(List<SentimentModel> positive) {
        this.positive = positive;
    }


}
