package com.rt.rtsl.net;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hello-world on 2018/5/4.
 */

public final class JSONObjectConvertFactory extends Converter.Factory
{
    static final GsonConverterFactory gsonConverterFactory= GsonConverterFactory.create();
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit)
    {
        return new ResponseBodyConverter();
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit)
    {
        return gsonConverterFactory.requestBodyConverter(type,parameterAnnotations,methodAnnotations,retrofit);
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit)
    {
        return gsonConverterFactory.stringConverter(type, annotations, retrofit);
    }


    class ResponseBodyConverter implements Converter<ResponseBody,JSONObject>
    {

        @Override
        public JSONObject convert(ResponseBody value) throws IOException {
            String body=value.string();
            try {
                return new JSONObject(body);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
