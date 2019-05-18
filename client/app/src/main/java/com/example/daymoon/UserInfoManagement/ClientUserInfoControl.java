package com.example.daymoon.UserInfoManagement;

import android.util.Log;

import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.HttpUtil.HttpRequestThread;
import com.example.daymoon.Tool.MD5Tool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

import static com.example.daymoon.Define.Constants.SERVER_IP;


public class ClientUserInfoControl {

    public static void getUserInfoFromServer(User user, int userID, Runnable success, Runnable failure){
        Map<String,String> params = new HashMap<>();
        params.put("userID", String.valueOf(userID));
        new HttpRequestThread(SERVER_IP+"getuserinfo",params, new HttpRequest.DataCallback(){
            @Override
            public void requestSuccess(String result) {
                Gson gson = new GsonBuilder().create();
                Type UserRecordType = new TypeToken<User>(){}.getType();
                user.CopyFrom(gson.fromJson(result, UserRecordType));
                success.run();
            }
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.e("shit", "oops! Something goes wrong");
                failure.run();
            }
        }).start();
    }

    public static void addUser(UserInformationHolder userInformationHolder, Runnable success, Runnable failure){
        Map<String,String> params = new HashMap<>();
        params.put("name", String.valueOf(userInformationHolder.name));
        params.put("email", String.valueOf(userInformationHolder.email));
        params.put("description", String.valueOf(userInformationHolder.description));
        params.put("phoneNumber", String.valueOf(userInformationHolder.phoneNumber));
        try {
            params.put("password", String.valueOf(MD5Tool.encode(userInformationHolder.password)));
        }catch (RuntimeException e){
            return;
        }
        new HttpRequestThread(SERVER_IP+"adduser",params, new HttpRequest.DataCallback(){
            @Override
            public void requestSuccess(String result) {
                //TODO 服务器不同返回值进行不同操作
                success.run();
            }
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.e("shit", "oops! Something goes wrong");
                failure.run();
            }
        }).start();
    }
}
