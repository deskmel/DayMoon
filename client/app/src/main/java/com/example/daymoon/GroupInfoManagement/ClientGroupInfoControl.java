package com.example.daymoon.GroupInfoManagement;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.HttpUtil.CalendarSerializer;
import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.HttpUtil.HttpRequestThread;
import com.example.daymoon.UserInfoManagement.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import okhttp3.Request;

import static com.example.daymoon.Define.Constants.SERVER_IP;


public class ClientGroupInfoControl {
    private int userId;
    private GroupList groupList;
    private int currentUserID;
    private static GroupEventList groupEventList;
    private static ClientGroupInfoControl clientGroupInfoControl;

    private static ClientGroupInfoControl getInstance()
    {
        if (clientGroupInfoControl==null) {
            clientGroupInfoControl = new ClientGroupInfoControl();
            clientGroupInfoControl.groupList=new GroupList();
        }
        return clientGroupInfoControl;
    }

    public static void setCurrentUserID(int currentUserID){
        getInstance().currentUserID = currentUserID;
    }

    public static GroupList getGroupList()
    {
        return getInstance().groupList;
    }

    public static void getGroupListFromServer(Runnable success, Runnable failure){
        Map<String, String> params = new HashMap<>();
        params.put("userID", String.valueOf(getInstance().currentUserID));
        new HttpRequestThread(SERVER_IP+"getallmygroups", params, new HttpRequest.DataCallback(){
            @Override
            public void requestSuccess(String result) {
                Gson gson = new GsonBuilder().create();
                Type GroupRecordType = new TypeToken<GroupList>(){}.getType();
                getInstance().groupList = gson.fromJson(result, GroupRecordType);
                success.run();
            }
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.e("shit", "oops! Something goes wrong");
                failure.run();
            }
        }).start();
    }

    public static void createGroup(GroupInformationHolder groupInformationHolder, Runnable success, Runnable failure){
        Map<String, String> params = new HashMap<>();
        params.put("groupName", groupInformationHolder.groupName);
        params.put("description", groupInformationHolder.description);
        params.put("leaderID", String.valueOf(getInstance().currentUserID));
        final String uuid = UUID.randomUUID().toString();
        params.put("imgName", uuid);
        new HttpRequestThread(SERVER_IP+"creategroup", "image", uuid, groupInformationHolder.image, params, new HttpRequest.DataCallback(){
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

    public static String getLatestGroupEventDes(int groupId){
        groupEventList=new GroupEventList();
        ClientGroupEventControl.getGroupEventListFromServer(new HttpRequest.DataCallback() {
            @Override
            public void requestSuccess(String result) throws Exception {
                Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(GregorianCalendar.class,
                        new CalendarSerializer()).create();
                Type GroupEventRecordType = new TypeToken<GroupEventList>(){}.getType();
                groupEventList = gson.fromJson(result, GroupEventRecordType);
            }
            @Override
            public void requestFailure(Request request, IOException e) {
                groupEventList=null;
            }
        });
        if (groupEventList==null || groupEventList.size()==0){
            return "暂无事件";
        }
        else {
            Collections.sort(groupEventList);
            return String.format("%s %s %s",groupEventList.getLast().getTitle(),groupEventList.getLast().getLocation(),groupEventList.getLast().getBeginDate());
        }
    }

    public static void generateQRCode(int groupID, HttpRequest.DataCallback dataCallback){
        Map<String, String> params = new HashMap<>();
        params.put("groupID", String.valueOf(groupID));
        new HttpRequestThread(SERVER_IP+"genqrcode", params, dataCallback).start();
    }

    public static void joinGroupByQRCode(String qrCodeKey, Runnable success, Runnable failure){
        Map<String, String> params = new HashMap<>();
        params.put("qrCodeKey", qrCodeKey);
        params.put("userID", String.valueOf(getInstance().currentUserID));
        new HttpRequestThread(SERVER_IP+"joingroupbyqrcode", params, new HttpRequest.DataCallback(){
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

