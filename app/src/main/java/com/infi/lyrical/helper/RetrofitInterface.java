package com.infi.lyrical.helper;

import com.infi.lyrical.models.JobIDResponse;
import com.infi.lyrical.models.SentimentModel;
import com.infi.lyrical.models.SpeechResponse;
import com.infi.lyrical.models.StatusResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by INFIi on 11/21/2017.
 */

public interface RetrofitInterface {

    @Multipart
    @POST("https://api.havenondemand.com/1/api/{sync}/{APIdentifier}/{APIVersion}")
    Observable<List<SpeechResponse>> postRequest(@Path("sync") String sync, @Path("APIdentifier") String apiIdentifier, @Path("APIVersion") String apiVersion);

    @Multipart
    @POST("https://api.havenondemand.com/1/api/async/recognizespeech/v2")
    Call<JobIDResponse> sendSpeechForFile(@Part("apikey") RequestBody apiKey, @Part("language_model") RequestBody lang, @Part MultipartBody.Part file);


    @Multipart
    @POST("https://api.havenondemand.com/1/api/async/recognizespeech/v2")
    Call<JobIDResponse> sendSpeechForUrl(@Part("apikey") RequestBody apiKey,@Part("language_model") RequestBody lang,@Part("url") RequestBody url);

    @Multipart
    @POST("https://api.havenondemand.com/1/api/sync/analyzesentiment/v2")
    Call<SentimentModel> requestSentiment(@Part("apikey") RequestBody apiKey,@Part("language_model") RequestBody lang,@Part MultipartBody.Part file);

    @POST("https://api.havenondemand.com/1/job/status/{jobId}")
    Single<StatusResponse> getStatus(@Path("jobId") String jobId, @Query("apikey") String apikey);

    @POST("https://api.havenondemand.com/1/job/result/{jobId}")
    Single<SpeechResponse> getResult(@Path("jobId") String jobId,@Query("apikey") String apikey);

    @POST("https://api.havenondemand.com/1/job/result/{jobId}")
    Observable<Object> getRawResult(@Path("jobId") String jobId,@Query("apikey") String apikey);



}
