package com.example.daymoon.GroupInfoManagement;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.HttpUtil.HttpRequestThread;

import okhttp3.Request;

import static com.example.daymoon.Define.Constants.SERVER_IP;


public class ClientGroupInfoControl {
    private int userId;
    private static GroupList groupList;
    private int currentUserID;
    private static ClientGroupInfoControl clientGroupInfoControl;

    public static ClientGroupInfoControl getInstance()
    {
        if (clientGroupInfoControl==null) {
            clientGroupInfoControl = new ClientGroupInfoControl();
            groupList=new GroupList();
        }
        return clientGroupInfoControl;
    }

    public static void setCurrentUserID(int currentUserID){
        getInstance().currentUserID = currentUserID;
    }

    public static GroupList getGroupList()
    {
        return groupList;
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


}

