package com.infi.lyrical.helper;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

/**
 * Created by INFIi on 11/21/2017.
 */

public class RetrofitHelper {
    public enum REQ_MODE {SYNC, ASYNC };

    private static String apiKey = "38e6f10e-8a0c-4664-a834-13eb80942c49";
    private static String hodBase = "https://api.havenondemand.com/1/api/";
    private static String hodJobResult = "https://api.havenondemand.com/1/job/result/";
    private static String hodJobStatus = "https://api.havenondemand.com/1/job/status/";
    private static String version = "v1";
    private static Retrofit retrofit = null;

    public static MultipartBody.Part getForFile(File file,String mimeType){
        return MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(mimeType), file));

    }
    public static Retrofit getInstance(){
        if(retrofit==null){
            retrofit= new Retrofit.Builder().
                    baseUrl(hodBase).
                    addConverterFactory(GsonConverterFactory.create()).
                    addCallAdapterFactory(RxJava2CallAdapterFactory.create()).
                    build();
        }
        return retrofit;
    }

    public static Retrofit getRawInstance(){
        return new Retrofit.Builder().
                baseUrl(hodBase).
                build();

    }

}
