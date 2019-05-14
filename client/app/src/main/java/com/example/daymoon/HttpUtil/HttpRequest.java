package com.example.daymoon.HttpUtil;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class HttpRequest {
    private static HttpRequest httpRequest;
    private static OkHttpClient okHttpClient;
    private Handler mHandler;

    private HttpRequest() {
        okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder();
        mHandler = new Handler(Looper.getMainLooper());
    }

    private static HttpRequest getInstance() {
        if (httpRequest == null) {
            httpRequest = new HttpRequest();
        }
        return httpRequest;
    }

    static void post(String url, Map<String, String> params, DataCallback dataCallbackack) {
        getInstance().inner_post(url, params, dataCallbackack);
    }

    private void inner_post(String url, Map<String,String> params,final DataCallback dataCallback){
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String,String> pair : params.entrySet()){
            builder.add(pair.getKey(),pair.getValue());
            Log.i(pair.getKey(),pair.getValue());
        }
        final Request request = new okhttp3.Request.Builder()
                .post(builder.build())
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, dataCallback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    deliverDataSuccess(response.body().string(), dataCallback);
                } else {
                    throw new IOException(response + "");
                }
            }
        });

    }

    private void deliverDataFailure(final Request request, final IOException e, final DataCallback dataCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (dataCallback != null) {
                    dataCallback.requestFailure(request, e);
                }
            }
        });
    }

    private void deliverDataSuccess(final String result, final DataCallback dataCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (dataCallback != null) {
                    try {
                        dataCallback.requestSuccess(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public interface DataCallback{
        void requestSuccess(String result) throws Exception;
        void requestFailure(Request request, IOException e);
    }
}