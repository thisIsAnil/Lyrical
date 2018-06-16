package com.infi.lyrical.helper;

/**
 * Created by INFIi on 11/23/2017.
 */

public interface RetryListener {

    void requestCompletedWithContent(String response);
    void requestCompletedWithJobID(String response);
    void onErrorOccurred(String errorMessage);
}
