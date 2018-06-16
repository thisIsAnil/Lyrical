package com.infi.lyrical.models.fields.sentiment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by INFIi on 12/2/2017.
 */

public class Aggregate {
    @SerializedName("sentiment")
    @Expose
    private Aggregate.Sentiment sentiment;

    @SerializedName("score")
    private Double score;

    public Aggregate() {
    }

    public Aggregate(Sentiment sentiment, Double score) {
        this.sentiment = sentiment;
        this.score = score;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public enum Sentiment {

        @SerializedName("negative")
        NEGATIVE("negative"),
        @SerializedName("slightly negative")
        SLIGHTLY_NEGATIVE("slightly negative"),
        @SerializedName("mixed")
        MIXED("mixed"),
        @SerializedName("slightly positive")
        SLIGHTLY_POSITIVE("slightly positive"),
        @SerializedName("positive")
        POSITIVE("positive"),
        @SerializedName("neutral")
        NEUTRAL("neutral");
        private final String value;
        private final static Map<String, Sentiment> CONSTANTS = new HashMap<String, Sentiment>();

        static {
            for (Aggregate.Sentiment c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Sentiment(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static Aggregate.Sentiment fromValue(String value) {
            Aggregate.Sentiment constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }


}
