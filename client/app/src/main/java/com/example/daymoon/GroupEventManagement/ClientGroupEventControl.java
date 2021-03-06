package com.example.daymoon.GroupEventManagement;

import android.util.Log;

import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.HttpUtil.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
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
    private NotificationList notificationList;

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
        HttpRequest.post(SERVER_IP+"getallmygroupevents", params, dataCallback);
    }

    public static NotificationList getNotificationList(){
        return getInstance().notificationList;
    }

    public static void getNotificationListFromServer(Runnable success, Runnable failure){
        Map<String, String> params = new HashMap<>();
        params.put("userID", String.valueOf(getInstance().currentUserID));
        HttpRequest.post(SERVER_IP + "getallmynotificationlists", params, new HttpRequest.DataCallback() {
            @Override
            public void requestSuccess(String result) throws Exception {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                System.out.println(result);
                Type NotificationRecordType = new TypeToken<NotificationList>(){}.getType();
                getInstance().notificationList = gson.fromJson(result, NotificationRecordType);
                success.run();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                failure.run();
            }
        });
    }

    public static void createNotification(Notification notification, Runnable success, Runnable failure){
        Map<String, String> params = new HashMap<>();
        params.put("creatorName",notification.getCreatorName());
        params.put("createTime",notification.getCreateTimeFormat());
        params.put("groupID",String.valueOf(notification.getGroupID()));
        params.put("groupName",String.valueOf(notification.getGroupname()));
        params.put("message",String.valueOf(notification.getMessage()));
        System.out.println(params);
        HttpRequest.post(SERVER_IP + "submitnotification", params, new HttpRequest.DataCallback() {
            @Override
            public void requestSuccess(String result) throws Exception {
                success.run();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Log.e("error","something wrong in creating notification");
                failure.run();
            }
        });
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
