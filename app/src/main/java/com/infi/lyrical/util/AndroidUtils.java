package com.infi.lyrical.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.infi.lyrical.LyricalApp;
import com.infi.lyrical.R;
import com.infi.lyrical.helper.ErrorHelper;
import com.infi.lyrical.helper.FileLog;
import com.infi.lyrical.helper.RetrofitHelper;
import com.infi.lyrical.helper.RetrofitInterface;
import com.infi.lyrical.helper.SentimentListener;
import com.infi.lyrical.models.ErrorResponse;
import com.infi.lyrical.models.JobIDResponse;
import com.infi.lyrical.models.SentimentModel;
import com.infi.lyrical.models.SpeechResponse;
import com.infi.lyrical.models.fields.speech.FieldAction;
import com.infi.lyrical.models.fields.speech.SpeechData;
import com.infi.lyrical.models.ui.AudioData;
import com.infi.lyrical.models.ui.LyricModel;
import com.infi.lyrical.models.ui.MediaModel;
import com.infi.lyrical.task.DownloadTask;
import com.infi.lyrical.task.TaskCategory;
import com.infi.lyrical.task.TaskStatus;
import com.infi.lyrical.task.TaskStatusListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by INFIi on 11/27/2017.
 */

public class AndroidUtils {
    public static Point displaySize = new Point();
    public static boolean usingHardwareInput;
    private static Boolean isTablet = null;
    public static float density = 1;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static int statusBarHeight = 0;
    private static int prevOrientation = -10;
    public static Context context= LyricalApp.applicationContext;
    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        if(density!=1) {
            return (int) Math.ceil(density * value);
        }
        return (int)convertDpToPixel(value);
    }
    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            LyricalApp.applicationHandler.post(runnable);
        } else {
            LyricalApp.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        LyricalApp.applicationHandler.removeCallbacks(runnable);
    }
    public static void checkDisplaySize(Context context, Configuration newConfiguration) {
        try {
            density = context.getResources().getDisplayMetrics().density;
            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            usingHardwareInput = configuration.keyboard != Configuration.KEYBOARD_NOKEYS && configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                }
            }
            if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenWidthDp * density);
                if (Math.abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize;
                }
            }
            if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenHeightDp * density);
                if (Math.abs(displaySize.y - newSize) > 3) {
                    displaySize.y = newSize;
                }
            }
            FileLog.write("display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi);
        } catch (Exception e) {
            FileLog.write(e.getMessage());
        }
    }
    public static int convertDpToPixel(float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int)px;
    }

    public static float PxToDp(float px){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp=(px/(float)metrics.densityDpi)*160f;
        return dp;
    }

    public static boolean isTablet() {
        if (isTablet == null) {
            isTablet = LyricalApp.applicationContext.getResources().getBoolean(R.bool.isTablet);
        }
        return isTablet;
    }

    public static boolean isSmallTablet() {
        float minSide = Math.min(displaySize.x, displaySize.y) / density;
        return minSide <= 700;
    }
    public static String getMetaDuration(File f){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(f.getAbsolutePath());
        return convertTime(Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
    }
    public static String getAudioMeta(String path){
       MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(path);
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
    }
    public static String convertTime(long dur){
        dur=dur/1000;
        int hr=(int)(dur/60/60);
        int min=(int)((dur/60)%60);
        int sec=(int)(dur%60);
        String s,hrs,mins,secs;
        if(hr<1)hrs="";
        else if(hr<10)hrs="0"+hr+":";
        else hrs=hr+":";
        if(min<10)mins="0"+min+":";
        else mins=min+":";
        if(sec<10)secs="0"+sec+"";
        else secs=sec+"";
        return hrs+mins+secs;
    }
    public static int getMinTabletSide() {
        if (!isSmallTablet()) {
            int smallSide = Math.min(displaySize.x, displaySize.y);
            int leftSide = smallSide * 35 / 100;
            if (leftSide < dp(320)) {
                leftSide = dp(320);
            }
            return smallSide - leftSide;
        } else {
            int smallSide = Math.min(displaySize.x, displaySize.y);
            int maxSide = Math.max(displaySize.x, displaySize.y);
            int leftSide = maxSide * 35 / 100;
            if (leftSide < dp(320)) {
                leftSide = dp(320);
            }
            return Math.min(smallSide, maxSide - leftSide);
        }
    }
    private static String getPath(Uri uri){
        String[] proj={MediaStore.Video.Media.DATA};
        Cursor cursor=context.getContentResolver().query(uri,proj,null,null,null);
        if(null!=cursor){
            cursor.moveToFirst();
            int columnIndex=cursor.getColumnIndex(proj[0]);
            return cursor.getString(columnIndex);
        }
        return null;

    }

    public static String UriToPath(Uri selectedImageUri){

        String fileManagerString = selectedImageUri.getPath();
        String selectedImage = getPath(selectedImageUri);
        String filePath=null;
        if (fileManagerString != null) {
            filePath = fileManagerString;
        } else if (selectedImage != null) {
            filePath = selectedImage;
        }
        return filePath;
    }

    public static String getString(@StringRes int id){
        return context.getString(id);
    }

    public static int getColor(@ColorRes int id){
        return Build.VERSION.SDK_INT<23?context.getResources().getColor(id):context.getResources().getColor(id,context.getTheme());
    }

    public static List<LyricModel> convertMediaModelToLyricModel(MediaModel m){
        List<LyricModel> lyricModels=new ArrayList<>();

            List<FieldAction> actions=m.getSubtitle().getActions();
            for(FieldAction f:actions){
                List<SpeechData> result=f.getResults().getData();
                String lyric="";
                long start=0;
                long end=0;
                for(int i=0;i<result.size();i++){
                    SpeechData data=result.get(i);
                    if(i%6==0){
                        lyricModels.add(new LyricModel(lyric,start,end));
                        lyric="";
                        start=Double.valueOf(data.getStartOffset()*100.0).longValue();
                    }
                    lyric+=data.getContent()+" ";
                    end=Math.max(end,Double.valueOf(data.getEndOffset()*100.0).longValue());
                }

        }
        return lyricModels;
    }
    public static String getContentFromSpeechResponse(SpeechResponse speechResponse){
        String content="";
        List<FieldAction> fieldActions=speechResponse.getActions();
        for(FieldAction f:fieldActions){
            List<SpeechData> result=f.getResults().getData();
            for(SpeechData sd:result){
                content+=sd.getContent()+" ";
            }
        }
        return content;
    }

    public static void requestAPIForURL(final String url, final String category, final TaskStatusListener statusListener){

        RequestBody langBody=RequestBody.create(MediaType.parse("text/plain"), LanguageModel.LANGUAGE_US);
        RequestBody urlBody=RequestBody.create(MediaType.parse("text/plain"),url);
        RetrofitInterface retrofitInterface= RetrofitHelper.getInstance().create(RetrofitInterface.class);
        Call<JobIDResponse> jobCall=retrofitInterface.sendSpeechForUrl(LyricalApp.getApiKeyBody(),langBody,urlBody);
        jobCall.enqueue(new Callback<JobIDResponse>() {
            @Override
            public void onResponse(Call<JobIDResponse> call, Response<JobIDResponse> response) {
                try{
                    FileLog.write("Recieved jobCall response");
                    if(response.isSuccessful()){

                        String jobID=response.body().getJobId();
                        FileLog.w("JobID: ",jobID);
                        LyricalApp.trackThisTask(jobID,category,statusListener);
                        FileLog.registerNewTask(jobID,category,false,url, TaskStatus.STATUS_QUEUED,null,new Date().toString());
                        //getResult(jobID);
                    }else {
                        ErrorResponse errorResponse= ErrorHelper.parseError(RetrofitHelper.getInstance(),response);
                        if(errorResponse!=null)FileLog.write(errorResponse.toString());
                    }
                }catch (Exception e){
                    FileLog.write(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JobIDResponse> call, Throwable t) {
                FileLog.e(t);
            }
        });
    }

    public static File getParentForCategory(String category){
        FileLog.write("category is: "+category);
        switch (category){
            case TaskCategory.CATEGORY_VIDEO:
                return TempFolderMaker.getVideoSubtitle();
            case TaskCategory.CATEGORY_MUSIC:
                return TempFolderMaker.getAudioSubtitle();
            case TaskCategory.CATEGORY_SPEAKPAD:
                return TempFolderMaker.getTexts();
            case TaskCategory.CATEGORY_WAI:
                return TempFolderMaker.getSentiments();
            default:
                return TempFolderMaker.getVideoSubtitle();
        }
    }
    public static void requestAPIForFile(final String url, final String mimeType, String languageModel, final String category, final TaskStatusListener statusListener){
        File media=new File(url);
        RequestBody langBody=RequestBody.create(MediaType.parse("text/plain"), languageModel);
        RequestBody fileBody=RequestBody.create(MediaType.parse(mimeType),media);
        MultipartBody.Part filePart=MultipartBody.Part.createFormData("file",media.getName(),fileBody);
        RetrofitInterface retrofitInterface= RetrofitHelper.getInstance().create(RetrofitInterface.class);
        Call<JobIDResponse> jobCall=retrofitInterface.sendSpeechForFile(LyricalApp.getApiKeyBody(),langBody,filePart);
        jobCall.enqueue(new Callback<JobIDResponse>() {
            @Override
            public void onResponse(Call<JobIDResponse> call, Response<JobIDResponse> response) {
                try{
                    FileLog.write("Recieved jobCall response");
                    if(response.isSuccessful()){

                        String jobID=response.body().getJobId();
                        FileLog.w("JobID: ",jobID);
                        LyricalApp.trackThisTask(jobID,category,statusListener);
                        final DownloadTask task=FileLog.registerNewTask(jobID,category,false,url,TaskStatus.STATUS_QUEUED,null,new Date(System.currentTimeMillis()).toString());
                        runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                statusListener.onTaskStarted(task);
                            }
                        });
                        //getResult(jobID);
                    }else {
                        ErrorResponse errorResponse= ErrorHelper.parseError(RetrofitHelper.getInstance(),response);
                        if(errorResponse!=null)FileLog.write(errorResponse.toString());
                    }
                }catch (Exception e){
                    FileLog.write(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JobIDResponse> call, Throwable t) {
                FileLog.e(t);
            }
        });

    }

    public static void requestSentimentForFile(final String filePath, String languageModel, final SentimentListener sentimentListener){
        File media=new File(filePath);
        RequestBody langBody=RequestBody.create(MediaType.parse("text/plain"), languageModel);
        RequestBody fileBody=RequestBody.create(MediaType.parse("text/plain"),media);
        MultipartBody.Part filePart=MultipartBody.Part.createFormData("file",media.getName(),fileBody);
        RetrofitInterface retrofitInterface= RetrofitHelper.getInstance().create(RetrofitInterface.class);
        Call<SentimentModel> jobCall=retrofitInterface.requestSentiment(LyricalApp.getApiKeyBody(),langBody,filePart);
        jobCall.enqueue(new Callback<SentimentModel>() {
            @Override
            public void onResponse(Call<SentimentModel> call, Response<SentimentModel> response) {
                if(response.isSuccessful()){
                    SentimentModel sentimentModel=response.body();
                    sentimentListener.onRecievedSentiment(sentimentModel);
                }else {
                    FileLog.write("sentiment analysis failed");
                    sentimentListener.onFailed();
                }
            }

            @Override
            public void onFailure(Call<SentimentModel> call, Throwable t) {
                FileLog.e("SentimentAnalysis",t);
                sentimentListener.onFailed();
            }
        });
    }

    public static List<DownloadTask> getRunningTaskList(List<DownloadTask> downloadTasks){
            List<DownloadTask> queuelist = new ArrayList<>();
            for (DownloadTask downloadTask : downloadTasks) {
                if (downloadTask.getStatus().equals(TaskStatus.STATUS_QUEUED)||downloadTask.getStatus().equals(TaskStatus.STATUS_IN_PROGRESS)){
                    queuelist.add(downloadTask);
                    FileLog.write("task status is:"+downloadTask.getStatus());
                }

            }

            return queuelist;
    }
    public static void resumePreviousTask(@NonNull List<DownloadTask> taskList,List<TaskStatusListener> statusListeners){
        try{
            for(int i=0;i<taskList.size();i++) {
                DownloadTask d=taskList.get(i);
                LyricalApp.trackThisTask(d.getJobId(),d.getCategory(),statusListeners.get(i));
            }

        }catch (Exception e){
                FileLog.write(e.getMessage());
        }
    }


    public static List<AudioData> getMusicList(){
        try {
            ContentResolver cr = context.getContentResolver();
            String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";

            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM_ID
            };

            Cursor cursor = cr.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    null);
            List<AudioData> songs = new ArrayList<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    //songs.add(cursor.getString(4));
                    try {
                        String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                        Uri ArtworkUri = Uri.parse("content://media/external/audio/albumart");
                        Uri albumUri = ContentUris.withAppendedId(ArtworkUri, id);

                        int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                        songs.add(new AudioData(title, artist, path, null, duration));
                    }catch (Exception e){
                        FileLog.write(e.getMessage());
                    }

                }
            }
            return songs;
        }catch (Exception ie){
            FileLog.write(ie.getMessage());
            //  Toast.makeText(context,ie.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return new ArrayList<>();
    }


}
