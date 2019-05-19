package com.example.daymoon.HttpUtil;

import java.io.File;
import java.util.Map;


public class HttpRequestThread extends Thread{

    private boolean post, hasFile;
    private Map<String, String> params;
    private HttpRequest.DataCallback dataCallback;
    private HttpRequest.FileCallback fileCallback;
    private String url, name, fileName;
    private File file;

    public HttpRequestThread(String url, HttpRequest.FileCallback fileCallback){//下载图片
        this.post = false;
        this.url = url;
        this.fileCallback = fileCallback;
    }

    public HttpRequestThread(String url, Map<String, String> params, HttpRequest.DataCallback dataCallback){
        this.post = true;
        this.hasFile = false;
        this.url = url;
        this.params = params;
        this.dataCallback = dataCallback;
    }

    public HttpRequestThread(String url, String name, String fileName, File file, Map<String,String> params, HttpRequest.DataCallback dataCallback){
        this.post = true;
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
        if (post)
            if (hasFile)
                HttpRequest.postFile(url, name, fileName, file, params, dataCallback);
            else
                HttpRequest.post(url, params, dataCallback);
        else
            HttpRequest.getFile(url, fileCallback);
    }
}
