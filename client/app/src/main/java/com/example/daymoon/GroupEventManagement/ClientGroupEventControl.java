package com.example.daymoon.GroupEventManagement;

import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.GroupInfoManagement.Group;
import com.example.daymoon.HttpUtil.CalendarSerializer;
import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.HttpUtil.HttpRequestThread;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

import static com.example.daymoon.Define.Constants.SERVER_IP;

public class ClientGroupEventControl {
    private int GroupID;
    private int GroupEventID;
    private int currentUserID;
    private GroupEventList groupEventList;

    private GroupEventList allMemberGroupEventList;
    private EventList allMemberEventList;
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

    public static int getCurrentUseID(){return getInstance().currentUserID;}

    public static void getGroupEventListFromServer(int groupID, HttpRequest.DataCallback dataCallback){
        Map<String, String> params = new HashMap<>();
        params.put("userID", String.valueOf(getInstance().currentUserID));
        params.put("groupID", String.valueOf(groupID));
        System.out.println(getInstance().currentUserID);
        System.out.println(groupID);
        HttpRequest.post(SERVER_IP+"getallmygroupevents", params, dataCallback);
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
        HttpRequest.post(SERVER_IP+"submitgroupevent", params, dataCallback);

    }
    public static void getAllMemberEventFromServer(){
        return;
    }
    public static void getGroupEventFromServer(int eventID, HttpRequest.DataCallback dataCallback){
        Map<String, String> params = new HashMap<>();
        params.put("userID", String.valueOf(getInstance().currentUserID));
        params.put("eventID", String.valueOf(eventID));
        HttpRequest.post(SERVER_IP+"getgroupevent", params, dataCallback);
    }
    public static EventList findAllMemberEventListByWeek(int year,int weekOfYear){
        EventList resultList = new EventList();
        if (getInstance().allMemberEventList!=null){
            for (Event event:getInstance().allMemberEventList ){
                Log.d("events_WeekOfYEAR",String.valueOf(event.getBeginTime().get(java.util.Calendar.WEEK_OF_YEAR)));
                if (event.getBeginTime().get(java.util.Calendar.WEEK_OF_YEAR) == weekOfYear
                        && year == event.getBeginTime().get(java.util.Calendar.YEAR) ){
                    resultList.add(event);
                }
            }
        }

        return resultList;
    }
    public static GroupEventList findAllMemberGroupEventListByWeek(int year,int weekOfYear){
        GroupEventList resultList = new GroupEventList();
        if (getInstance().allMemberGroupEventList!=null){
            for (GroupEvent event:getInstance().allMemberGroupEventList ){
                Log.d("events_WeekOfYEAR",String.valueOf(event.getBeginTime().get(java.util.Calendar.WEEK_OF_YEAR)));
                if (event.getBeginTime().get(java.util.Calendar.WEEK_OF_YEAR) == weekOfYear
                        && year == event.getBeginTime().get(java.util.Calendar.YEAR) ){
                    resultList.add(event);
                }
            }
        }

        return resultList;
    }


    /*public String getLatestGroupEventDes(int groupId){
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
    }*/
}
