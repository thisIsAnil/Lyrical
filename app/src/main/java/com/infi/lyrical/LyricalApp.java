package com.infi.lyrical;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Handler;

import com.infi.lyrical.helper.FileLog;
import com.infi.lyrical.helper.RetrofitHelper;
import com.infi.lyrical.helper.RetrofitInterface;
import com.infi.lyrical.models.SpeechResponse;
import com.infi.lyrical.models.StatusResponse;
import com.infi.lyrical.task.TaskStatusListener;
import com.infi.lyrical.util.AndroidUtils;
import com.infi.lyrical.task.TaskStatus;
import com.infi.lyrical.util.TempFolderMaker;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by INFIi on 11/27/2017.
 */

public class LyricalApp extends Application {
    public static volatile Handler applicationHandler;
    public static volatile Context applicationContext;
    private static CompositeDisposable mCompositeDisposable;
    private static int TASK_CALLBACK_DELAY=15000;
    private static int numTasks=0;
    private static RequestBody apiKeyBody;

    public static RetrofitInterface retrofitInterface;

    private static String apiKey = "";//add api key here
    private static String sample_url="https://www.havenondemand.com/sample-content/videos/hpnext.mp4";
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            applicationContext = getApplicationContext();
            applicationHandler = new Handler(applicationContext.getMainLooper());
            mCompositeDisposable = new CompositeDisposable();
            retrofitInterface= RetrofitHelper.getInstance().create(RetrofitInterface.class);
            apiKeyBody= RequestBody.create(MediaType.parse("text/plain"),apiKey);
            TempFolderMaker.createFolders();
            AndroidUtils.checkDisplaySize(applicationContext, null);
        } catch (Exception e) {
            FileLog.w("from application ", e.getMessage());
        }
    }
        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            try{
                AndroidUtils.checkDisplaySize(applicationContext,newConfig);
            }catch (Exception e){

            }
            super.onConfigurationChanged(newConfig);
        }

    @Override
    public void onTerminate() {
        mCompositeDisposable.clear();
        super.onTerminate();
    }

    public static RequestBody getApiKeyBody(){
        if(apiKeyBody==null)apiKeyBody= RequestBody.create(MediaType.parse("text/plain"),apiKey);
        return apiKeyBody;
    }
    private static void fetchResult(final String jobId, final String category,final TaskStatusListener statusListener){
        mCompositeDisposable.add(retrofitInterface.getResult(jobId,apiKey)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnError(new Consumer<Throwable>() {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception {
                                        FileLog.e(throwable);
                                        FileLog.updateStatusOf(jobId,TaskStatus.STATUS_FAILED,statusListener);
                                    }
                                })
                                .subscribe(new Consumer<SpeechResponse>() {
                                    @Override
                                    public void accept(@NonNull final SpeechResponse speechResponse) throws Exception {
                                        FileLog.write("Fetched results");
                                        new AsyncTask<Void,Void,Void>(){
                                            @Override
                                            protected Void doInBackground(Void... voids) {
                                                FileLog.createSubtitleJson(speechResponse,category,jobId,statusListener);
                                                return null;
                                            }

                                            @Override
                                            protected void onPostExecute(Void aVoid) {

                                            }
                                        }.execute();

                                    }
                                }));
    }
    private static void retry(final String jobId,final String category,final TaskStatusListener statusListener){
        AndroidUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                trackThisTask(jobId,category,statusListener);
            }
        },TASK_CALLBACK_DELAY);

    }
    private static void resolveStatus(final String jobId,final String category, StatusResponse statusResponse,TaskStatusListener statusListener){
        String status=statusResponse.getStatus();
        if(status!=null){
            if(status.equals(TaskStatus.STATUS_FINISHED)){
                FileLog.write("Status finished");
                fetchResult(jobId,category,statusListener);
            }else if(status.equals(TaskStatus.STATUS_FAILED)){
                try {
                    FileLog.updateStatusOf(jobId, TaskStatus.STATUS_FAILED,statusListener);
                }catch (IOException io){
                    FileLog.e(io);
                }
            }else {
                retry(jobId,category,statusListener);
                FileLog.write("status is: "+status);
            }
        }else{
            retry(jobId,category,statusListener);
            FileLog.write("status is null");
        }
    }
    public static void trackThisTask(final String jobId,final String category, final TaskStatusListener statusListener){
        mCompositeDisposable.add(retrofitInterface.getStatus(jobId,apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        FileLog.e(throwable);
                        FileLog.updateStatusOf(jobId,TaskStatus.STATUS_FAILED,statusListener);
                    }
                })
                .subscribe(new Consumer<StatusResponse>() {
                    @Override
                    public void accept(@NonNull StatusResponse statusResponse) throws Exception {
                        FileLog.write("Recieved response object");
                            resolveStatus(jobId,category,statusResponse,statusListener);
                    }
                }));
    }
}
