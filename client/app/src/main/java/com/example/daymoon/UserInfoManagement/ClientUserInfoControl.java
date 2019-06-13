package com.example.daymoon.UserInfoManagement;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.HttpUtil.HttpRequestThread;
import com.example.daymoon.R;
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

    private User currentUser;

    private static ClientUserInfoControl clientUserInfoControl;

    private static ClientUserInfoControl getInstance()
    {
        if (clientUserInfoControl==null) {
            clientUserInfoControl = new ClientUserInfoControl();
            clientUserInfoControl.currentUser=new User();
        }
        return clientUserInfoControl;
    }
    
    public static void setCurrentUser(int userID, Runnable success, Runnable failure) {
        getUserInfoFromServer(getInstance().currentUser, userID, success, failure);
    }

    public static User getCurrentUser(){
        return getInstance().currentUser;
    }

    public static void getProfilePhoto(User user, Runnable success, Runnable failure){
        HttpRequest.getFile(SERVER_IP + "image/" + user.getProfilePhotoName(), new HttpRequest.FileCallback() {
            @Override
            public void requestSuccess(Bitmap bitmap) throws Exception {
                user.setProfilePhoto(bitmap);
                //System.out.println(bitmap.getHeight());
                success.run();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                failure.run();
            }
        });
    }

    public static void getUserInfoFromServer(User user, int userID, Runnable success, Runnable failure){
        Map<String,String> params = new HashMap<>();
        params.put("userID", String.valueOf(userID));
        HttpRequest.post(SERVER_IP+"getuserinfo",params, new HttpRequest.DataCallback(){
            @Override
            public void requestSuccess(String result) {
                Gson gson = new GsonBuilder().create();
                Type UserRecordType = new TypeToken<User>(){}.getType();
                user.CopyFrom(gson.fromJson(result, UserRecordType));
                getProfilePhoto(user,success,failure);
            }
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.e("shit", "oops! Something goes wrong");
                failure.run();
            }
        });
    }

    public static void addUser(UserInformationHolder userInformationHolder, HttpRequest.DataCallback dataCallback){
        Map<String,String> params = new HashMap<>();
        params.put("name", userInformationHolder.name);
        //params.put("email", String.valueOf(userInformationHolder.email));
        //params.put("description", String.valueOf(userInformationHolder.description));
        //params.put("phoneNumber", String.valueOf(userInformationHolder.phoneNumber));
        try {
            params.put("password", String.valueOf(MD5Tool.encode(userInformationHolder.password)));
        }catch (RuntimeException e){
            return;
        }
        params.put("mail", userInformationHolder.name);
        params.put("phoneNumber",userInformationHolder.name);
        System.out.println(userInformationHolder.name);
        System.out.println(String.valueOf(MD5Tool.encode(userInformationHolder.password)));
        HttpRequest.post(SERVER_IP+"signup",params, dataCallback);
    }

    public static void login(UserInformationHolder userInformationHolder, HttpRequest.DataCallback dataCallback){
        Map<String,String> params = new HashMap<>();
        params.put("logstr", userInformationHolder.name);
        try {
            params.put("password", String.valueOf(MD5Tool.encode(userInformationHolder.password)));
        }catch (RuntimeException e){
            return;
        }
        HttpRequest.post(SERVER_IP+"login", params, dataCallback);
    }
}
