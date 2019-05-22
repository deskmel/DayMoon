package com.example.daymoon.HttpUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.daymoon.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MultipartBody;

import static android.content.Context.MODE_PRIVATE;


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

    public static void post(String url, Map<String, String> params, DataCallback dataCallbackack) {
        getInstance().innerPost(url, params, dataCallbackack);
    }
    static void postFile(String url, String name, String fileName, File file, Map<String,String> params, DataCallback dataCallback){
        getInstance().innerPostFile(url, name, fileName, file, params, dataCallback);
    }

    static void getFile(String url, FileCallback fileCallback){
        getInstance().innerGetFile(url, fileCallback);
    }

    private void innerGetFile(String url, FileCallback fileCallback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                deliverFileFailure(request, e, fileCallback);
            }
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    deliverFileSuccess(bitmap, fileCallback);
                } else {
                    throw new IOException(response + "");
                }
            }
        });
    }

    private void innerPostFile(String url, String name, String fileName, File file, Map<String,String> params, final DataCallback dataCallback){
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("file", fileName, fileBody);
        for (Map.Entry<String,String> pair : params.entrySet()){
            builder.addFormDataPart(pair.getKey(), pair.getValue());
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

    private void innerPost(String url, Map<String,String> params,final DataCallback dataCallback){
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

    private void deliverFileFailure(final Request request, final IOException e, final FileCallback fileCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (fileCallback != null) {
                    fileCallback.requestFailure(request, e);
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

    private void deliverFileSuccess(final Bitmap result, final FileCallback fileCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (fileCallback != null) {
                    try {
                        fileCallback.requestSuccess(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    public interface FileCallback{
        void requestSuccess(Bitmap result) throws Exception;
        void requestFailure(Request request, IOException e);
    }
}
