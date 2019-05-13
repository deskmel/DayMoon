package com.example.daymoon.HttpUtil;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import okhttp3.Request;

import static com.example.daymoon.Define.Constants.SERVER_IP;

public class HttpRequestThread extends Thread{

    private Map<String, String> params;
    private HttpRequest.DataCallback dataCallback;

    public HttpRequestThread(Map<String, String> params, HttpRequest.DataCallback dataCallback){
        this.params = params;
        this.dataCallback = dataCallback;
    }

    @Override
    public void run(){
        HttpRequest.post(SERVER_IP+"signup", params, dataCallback);
    }
}
