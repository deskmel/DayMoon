package com.example.daymoon.GroupEventManagement;

import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.example.daymoon.GroupInfoManagement.Group;
import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.HttpUtil.HttpRequestThread;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

import static com.example.daymoon.Define.Constants.SERVER_IP;

public class ClientGroupEventControl {
    private int GroupID;
    private int GroupEventID;
    private int currentUserID;
    private static ClientGroupEventControl clientGroupEventControl;
    private static ClientGroupEventControl getInstance(){
        if (clientGroupEventControl==null){
            clientGroupEventControl=new ClientGroupEventControl();
            return clientGroupEventControl;
        }
        else return clientGroupEventControl;
    }
    public static void setCurrentGroupID(int GroupID){
        getInstance().GroupID=GroupID;
    }

    public static void setCurrentUserID(int userID){
        getInstance().currentUserID = userID;
    }

    public static void getGroupEventListFromServer(HttpRequest.DataCallback dataCallback){
        Map<String, String> params = new HashMap<>();
        params.put("userID", String.valueOf(getInstance().currentUserID));
        params.put("groupID", String.valueOf(getInstance().GroupID));
        new HttpRequestThread(SERVER_IP+"getallmygroupevents", params, dataCallback).start();
    }

    public static void createGroupEvent(GroupEventInfomationHolder groupEventInfomationHolder, HttpRequest.DataCallback dataCallback){
        Map<String, String> params = new HashMap<>();
        params.put("userID", String.valueOf(getInstance().currentUserID));
        params.put("groupID", String.valueOf(getInstance().GroupID));
        params.put("eventName", groupEventInfomationHolder.title);
        params.put("description", groupEventInfomationHolder.descriptions);
        params.put("location", groupEventInfomationHolder.location);
        params.put("beginTime", groupEventInfomationHolder.getBeginTimeFormat());
        params.put("endTime", groupEventInfomationHolder.getEndTimeFormat());
        params.put("eventType", String.valueOf(groupEventInfomationHolder.eventType));
        params.put("whetherProcess", groupEventInfomationHolder.allday?"1":"0");
        System.out.println(params);
        new HttpRequestThread(SERVER_IP+"submitgroupevent", params, dataCallback).start();

    }

    public static void getGroupEventFromServer(int eventID, HttpRequest.DataCallback dataCallback){
        Map<String, String> params = new HashMap<>();
        params.put("userID", String.valueOf(getInstance().currentUserID));
        params.put("eventID", String.valueOf(eventID));
        new HttpRequestThread(SERVER_IP+"getgroupevent", params, dataCallback).start();
    }

    public String getLatestGroupEventDes(int groupId){
        return "组会 东上院 9:00-10:00";
    }
}
