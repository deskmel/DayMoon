package com.example.daymoon.HttpUtil;

import java.util.Map;


public class HttpRequestThread extends Thread{

    private Map<String, String> params;
    private HttpRequest.DataCallback dataCallback;
    private String url;

    public HttpRequestThread(String url, Map<String, String> params, HttpRequest.DataCallback dataCallback){
        this.url = url;
        this.params = params;
        this.dataCallback = dataCallback;
    }

    @Override
    public void run(){
        HttpRequest.post(url, params, dataCallback);
    }
}
