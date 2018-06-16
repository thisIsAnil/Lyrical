package com.infi.lyrical.models.fields.sentiment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SentimentObject {

    @SerializedName("normalized_length")
    @Expose
    private Double normalizedLength;

    @SerializedName("normalized_text")
    @Expose
    private String normalizedText;

    @SerializedName("original_length")
    @Expose
    private Double originalLength;

    @SerializedName("original_text")
    @Expose
    private String originalText;

    @SerializedName("score")
    @Expose
    private Double score;

    @SerializedName("sentiment")
    @Expose
    private String sentiment;

    @SerializedName("topic")
    @Expose
    private String topic;

    @SerializedName("offset")
    @Expose
    private Integer offset;

    public SentimentObject() {
    }

    public SentimentObject(Double normalizedLength, String normalizedText, Double originalLength, String originalText, Double score, String sentiment, String topic, Integer offset) {
        this.normalizedLength = normalizedLength;
        this.normalizedText = normalizedText;
        this.originalLength = originalLength;
        this.originalText = originalText;
        this.score = score;
        this.sentiment = sentiment;
        this.topic = topic;
        this.offset = offset;
    }

    public Double getNormalizedLength() {
        return normalizedLength;
    }

    public void setNormalizedLength(Double normalizedLength) {
        this.normalizedLength = normalizedLength;
    }

    public String getNormalizedText() {
        return normalizedText;
    }

    public void setNormalizedText(String normalizedText) {
        this.normalizedText = normalizedText;
    }

    public Double getOriginalLength() {
        return originalLength;
    }
    public void setOriginalLength(Double originalLength) {
        this.originalLength = originalLength;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

}
