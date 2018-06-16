package com.infi.lyrical.helper;

import com.infi.lyrical.models.SentimentModel;

/**
 * Created by INFIi on 12/2/2017.
 */

public interface SentimentListener {
    void onRecievedSentiment(SentimentModel model);
    void onFailed();
}
