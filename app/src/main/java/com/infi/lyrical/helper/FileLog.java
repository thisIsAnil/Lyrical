package com.infi.lyrical.helper;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infi.lyrical.models.SpeechResponse;
import com.infi.lyrical.task.DownloadTask;
import com.infi.lyrical.task.TaskStatus;
import com.infi.lyrical.task.TaskStatusListener;
import com.infi.lyrical.util.AndroidUtils;
import com.infi.lyrical.util.TempFolderMaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by INFIi on 11/22/2017.
 */

public class FileLog {

    public static final boolean DEBUG=true;
    public static void write(String msg) {
        if (DEBUG) {
            try {
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sampleLog.txt");
                if (!f.exists()) f.createNewFile();
                FileInputStream fis = new FileInputStream(f);
                String old;
                byte[] bytes = new byte[(int) f.length()];
                if (bytes.length > 0) fis.read(bytes, 0, bytes.length);
                old = new String(bytes, Charset.defaultCharset());
                msg = old + "\n" + msg;
                fis.close();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(msg.getBytes(), 0, msg.length());
                fos.close();


            } catch (Exception ffe) {
            }
        }
    }
    public static void writeToFile(String data,String path) throws IOException{
        File f=new File(path);
        if (!f.exists()) f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        byte[] bytes=data.getBytes();
        fos.write(bytes, 0, bytes.length);
        fos.close();

    }
    public static void e(String tag,Exception e){
        write(e.getMessage());

    }
    public static void e(Throwable e){
        write(e.getMessage());

    }
    public static void e(String tag,Throwable e){
        write(e.getMessage());

    }

    public static void w(String tag,String msg) {
        write(tag + msg);
    }

    public List<SpeechResponse> getVideoSubtitlesList(){
        return getSpeechResponseForFolder(TempFolderMaker.getVideoSubtitle().getAbsolutePath());
    }
    public List<SpeechResponse> getAudioSubtitlesList(){
        return getSpeechResponseForFolder(TempFolderMaker.getAudioSubtitle().getAbsolutePath());
    }
    public List<SpeechResponse> getSpeakPadList(){
        return getSpeechResponseForFolder(TempFolderMaker.getTexts().getAbsolutePath());
    }

    public static List<DownloadTask> getTaskStatusList() throws IOException {
        String json=null;
        try {
            json = getTaskStatusJSON();
        }catch (Exception e){
            json=null;
        }

        FileLog.write("Json string is: "+json);
        Gson gson=new Gson();
        Type type=new TypeToken<List<DownloadTask>>(){}.getType();
        List<DownloadTask> downloadTasks;
        if(json==null||json.isEmpty()||json.length()==1){
            downloadTasks=new ArrayList<>();
        }else {
            downloadTasks = gson.fromJson(json, type);
            if(downloadTasks==null)downloadTasks=new ArrayList<>();
        }
        return downloadTasks;
    }
    public static synchronized void updateStatusOf(String jobId, String taskStatus,final TaskStatusListener statusListener) throws IOException{
        String json=getTaskStatusJSON();
        Gson gson=new Gson();
        Type type=new TypeToken<List<DownloadTask>>(){}.getType();
        List<DownloadTask> downloadTasks=gson.fromJson(json,type);
        DownloadTask remove=null;
        int id=-1;
        for(int i=0;i<downloadTasks.size();i++){
            DownloadTask task=downloadTasks.get(i);
            if(task.getJobId().equals(jobId)){
                remove=task;
                id=i;
                break;
            }
        }
        FileLog.write("Remove task not found");
        if(remove==null)return;
        if(remove.getStatus().equals(TaskStatus.STATUS_FINISHED))return;
        final DownloadTask update=remove;
        update.setStatus(taskStatus);
        downloadTasks.remove(remove);
        downloadTasks.add(id,update);

        String newJson=gson.toJson(downloadTasks,type);
        saveJSON(newJson.getBytes(),TempFolderMaker.getStatus());
        if(statusListener!=null){
            AndroidUtils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    statusListener.onTaskStatusUpdate(update);
                }
            });
        }
    }
    public static synchronized void createSubtitleJson(SpeechResponse speechResponse,String jobId,String category,TaskStatusListener statusListener){
        FileLog.write("Creating subtitle JSON");
        Gson gson=new Gson();
        Type type=new TypeToken<SpeechResponse>(){}.getType();
        try {
            FileLog.write(speechResponse.toString());
            String json=gson.toJson(speechResponse,type);
            FileLog.write("Subtitle JSON is:\n"+json);

            saveJSON(json.getBytes(), new File(AndroidUtils.getParentForCategory(category),System.currentTimeMillis() + ".json"));
            updateStatusOf(jobId,TaskStatus.STATUS_FINISHED,statusListener);
        }catch (Exception io){
            //should not happen
            FileLog.write(io.getMessage());
        }
    }
    //TODO synchronize reads and writes
    public static synchronized DownloadTask registerNewTask(String jobId,String category, Boolean isFile, String url, String status, String subTitlePath, String timeStamp) throws IOException{
        String json=null;
        try {
            json = getTaskStatusJSON();
        }catch (Exception e){
                json=null;
        }

        FileLog.write("Json string is: "+json);
        Gson gson=new Gson();
        Type type=new TypeToken<List<DownloadTask>>(){}.getType();
        List<DownloadTask> downloadTasks;
        if(json==null||json.isEmpty()||json.length()==1){
            downloadTasks=new ArrayList<>();
        }else {
            downloadTasks = gson.fromJson(json, type);
            if(downloadTasks==null)downloadTasks=new ArrayList<>();
        }
        DownloadTask downloadTask=new DownloadTask(downloadTasks.size(),jobId,category,isFile,url,status,subTitlePath,timeStamp);
        downloadTasks.add(downloadTask);
        String newJson=gson.toJson(downloadTasks,type);
        saveJSON(newJson.getBytes(),TempFolderMaker.getStatus());
        return downloadTask;
    }
    private static String getJSONStringFromFile(File file) throws IOException{
        FileInputStream jsonStream= new FileInputStream(file);
        byte[] data=new byte[(int)file.length()];
        jsonStream.read(data,0,data.length);
        jsonStream.close();
        return new String(data,Charset.defaultCharset());
    }
    public static SpeechResponse getSpeechResponseForFile(String path) throws IOException{
        String json=null;
        try {
            json = getJSONStringFromFile(new File(path));
        }catch (Exception e){
            json=null;
        }

        FileLog.write("Json string is: "+json);
        Gson gson=new Gson();
        Type type=new TypeToken<SpeechResponse>(){}.getType();
        SpeechResponse speechResponse;
        if(json==null||json.isEmpty()||json.length()==1){
            return null;
        }else {
            speechResponse=gson.fromJson(json,type);
        }
        return speechResponse;

    }
    private static String getTaskStatusJSON() throws IOException{
        File status=TempFolderMaker.getStatus();
        return getJSONStringFromFile(status);
    }
    private static void saveJSON(byte[] jsonString,File json) throws IOException{
        json.createNewFile();
        FileOutputStream fos=new FileOutputStream(json);
        fos.write(jsonString,0,jsonString.length);
        fos.close();
    }
    private static List<SpeechResponse> getSpeechResponseForFolder(String folder){
        File dir=new File(folder);
        List<SpeechResponse> speechResponses=new ArrayList<>();
        for(String s:dir.list()){
            try {
                SpeechResponse response = getSpeechResponseForFile(s);
                if(response!=null)speechResponses.add(response);
            }catch (IOException io){
                FileLog.e(io);
            }
        }
        return speechResponses;
    }

}
