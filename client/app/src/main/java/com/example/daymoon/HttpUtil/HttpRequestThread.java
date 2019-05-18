package com.example.daymoon.HttpUtil;

import java.io.File;
import java.util.Map;


public class HttpRequestThread extends Thread{

    private boolean hasFile;
    private Map<String, String> params;
    private HttpRequest.DataCallback dataCallback;
    private String url, name, fileName;
    private File file;

    public HttpRequestThread(String url, Map<String, String> params, HttpRequest.DataCallback dataCallback){
        this.hasFile = false;
        this.url = url;
        this.params = params;
        this.dataCallback = dataCallback;
    }

    public HttpRequestThread(String url, String name, String fileName, File file, Map<String,String> params, HttpRequest.DataCallback dataCallback){
        this.hasFile = true;
        this.url = url;
        this.name = name;
        this.fileName = fileName;
        this.file = file;
        this.params = params;
        this.dataCallback = dataCallback;
    }

    @Override
    public void run(){
        if (hasFile)
            HttpRequest.postFile(url, name, fileName, file, params, dataCallback);
        else
            HttpRequest.post(url, params, dataCallback);
    }
}
