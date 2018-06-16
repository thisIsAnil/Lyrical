package com.infi.lyrical.task;

/**
 * Created by INFIi on 12/1/2017.
 */

public interface TaskStatusListener  {

    void onTaskStatusUpdate(DownloadTask downloadTask);
    void onTaskStarted(DownloadTask downloadTask);
}
