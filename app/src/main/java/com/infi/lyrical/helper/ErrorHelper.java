package com.infi.lyrical.helper;

import com.infi.lyrical.models.ErrorResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by INFIi on 11/26/2017.
 */

public class ErrorHelper {
    public static ErrorResponse parseError(Retrofit retrofit, Response<?> response) {

        Converter<ResponseBody, ErrorResponse> converter = retrofit
                .responseBodyConverter(ErrorResponse.class, new Annotation[0]);

        ErrorResponse error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ErrorResponse();
        }

        return error;
    }


}
