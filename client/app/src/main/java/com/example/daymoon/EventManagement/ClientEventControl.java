package com.example.daymoon.EventManagement;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.HttpUtil.CalendarSerializer;
import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.HttpUtil.HttpRequestThread;
import static com.example.daymoon.Define.Constants.SERVER_IP;

import okhttp3.Request;


import com.example.daymoon.LocalDatabase.LocalDatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.haibin.calendarview.Calendar;

public class ClientEventControl {//施工

    private int currentUserID, currentEventID; // 下一个event的ID，即当前event总数+1，这样可以保证eventID不重复

    private EventList eventList;
    private GroupEventList groupEventList;
    private static ClientEventControl clientEventControl;

    public static void setCurrentUserID(int currentUserID){
        getInstance().currentUserID = currentUserID;
    }

    private static ClientEventControl getInstance(){
        if (clientEventControl == null){
            clientEventControl = new ClientEventControl();
            clientEventControl.eventList = new EventList();
        }
        return clientEventControl;
    }

    public static EventList getEventList(){
        return getInstance().eventList;
    }

    // 通过currentUserID向服务器找到当前user的eventList
    public static void getEventListFromServer(Runnable callback){
        Map<String,String> params = new HashMap<>();
        params.put("userID",String.valueOf(getInstance().currentUserID));
        HttpRequest.post(SERVER_IP+"getallmyevents",params, new HttpRequest.DataCallback(){
            @Override
            public void requestSuccess(String result) {
                Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(GregorianCalendar.class,
                        new CalendarSerializer()).create();
                System.out.println(result);
                Type EventRecordType = new TypeToken<EventList>(){}.getType();
                getInstance().eventList = gson.fromJson(result, EventRecordType);

                callback.run();
            }
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.e("shit", "oops! Something goes wrong");
            }
        });
    }

    public static void getGroupEventListFromServer(Runnable callback){
        Map<String,String> params = new HashMap<>();
        params.put("userID",String.valueOf(getInstance().currentUserID));
        HttpRequest.post(SERVER_IP+"getallmygroupeventlists",params, new HttpRequest.DataCallback(){
            @Override
            public void requestSuccess(String result) {
                Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(GregorianCalendar.class,
                        new CalendarSerializer()).create();
                System.out.println(result);
                Type GroupEventRecordType = new TypeToken<GroupEventList>(){}.getType();
                getInstance().groupEventList = gson.fromJson(result, GroupEventRecordType);
                callback.run();
            }
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.e("shit", "oops! Something goes wrong");
            }
        });
    }


    // 增加一个event
    public static void addEvent(EventInformationHolder eventInformationHolder, Context context, Runnable success, Runnable failure){
        Event event;
        try {
            event = new Event(eventInformationHolder.title, eventInformationHolder.descriptions, 0, eventInformationHolder.Year_, eventInformationHolder.Month_, eventInformationHolder.Date_, eventInformationHolder.startHour_, eventInformationHolder.startMinute_, eventInformationHolder.Year_, eventInformationHolder.Month_, eventInformationHolder.Date_, eventInformationHolder.endHour_, eventInformationHolder.endMinute_, eventInformationHolder.process);
        }catch(Exception e){
            return;
        }

        Map<String,String> params = new HashMap<>();

        params.put("userID", String.valueOf(getInstance().currentUserID));
        params.put("eventName", event.getTitle());
        params.put("whetherProcess", event.whetherProcess?"1":"0");
        params.put("beginTime", event.getBeginTimeFormat());
        params.put("endTime", event.getEndTimeFormat());
        params.put("description", event.getDescription());

        HttpRequest.post(SERVER_IP+"submitevent", params, new HttpRequest.DataCallback(){
            @Override
            public void requestSuccess(String result) {
                Log.i("success","add the event successfully");

                Toast.makeText(context, "成功添加事件并上传", Toast.LENGTH_SHORT).show();
                event.setEventID(Integer.valueOf(result));
                getInstance().eventList.add(event);
                success.run();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(context, "出了点问题", Toast.LENGTH_SHORT).show();
                Log.e("shit","oops! Something goes wrong");
                failure.run();
            }
        });
    }



    // 删除一个event
    public static void deleteEvent(int eventID, Context context, Runnable success, Runnable failure){
        Map<String,String> params = new HashMap<>();

        params.put("userID", String.valueOf(getInstance().currentUserID));
        params.put("eventID", String.valueOf(eventID));

        HttpRequest.post(SERVER_IP+"deleteevent", params, new HttpRequest.DataCallback(){
            @Override
            public void requestSuccess(String result) {
                Log.i("success","delete the event successfully");

                Toast.makeText(context, "成功删除事件并上传", Toast.LENGTH_SHORT).show();
                getInstance().eventList.removeByID(eventID);
                success.run();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(context, "出了点问题", Toast.LENGTH_SHORT).show();
                Log.e("shit","oops! Something goes wrong");
                failure.run();
            }
        });

    }

    private static Calendar getSchemeCalendar(int year, int month, int day, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setScheme(text);
        return calendar;
    }

    // 修改一个event
    public static void editEvent(int eventID, EventInformationHolder eventInformationHolder,  Context context, Runnable success, Runnable failure){
        Event event;
        try {
            event = new Event(eventInformationHolder.title, eventInformationHolder.descriptions, eventID, eventInformationHolder.Year_, eventInformationHolder.Month_, eventInformationHolder.Date_, eventInformationHolder.startHour_, eventInformationHolder.startMinute_, eventInformationHolder.Year_, eventInformationHolder.Month_, eventInformationHolder.Date_, eventInformationHolder.endHour_, eventInformationHolder.endMinute_, eventInformationHolder.process);
        }catch(Exception e){
            return;
        }

        int index = getInstance().eventList.findByID(eventID);
        Map<String,String> params = new HashMap<>();

        params.put("userID", String.valueOf(getInstance().currentUserID));
        params.put("eventID", String.valueOf(eventID));
        params.put("eventName", event.getTitle());
        //params.put("whetherProcess", event.whetherProcess?"1":"0");
        params.put("beginTime", event.getBeginTimeFormat());
        params.put("endTime", event.getEndTimeFormat());
        params.put("description", event.getDescription());

        if (index == -1) return;
        HttpRequest.post(SERVER_IP+"editevent", params, new HttpRequest.DataCallback(){
            @Override
            public void requestSuccess(String result) {
                Log.i("success","add the event successfully");
                Toast.makeText(context, "成功修改事件并上传", Toast.LENGTH_SHORT).show();
                getInstance().eventList.set(index, event);
                success.run();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(context, "出了点问题", Toast.LENGTH_SHORT).show();
                Log.e("shit","oops! Something goes wrong");
                failure.run();
            }
        });

    }



    // 将现在的eventList同步到服务器
    public void updateEvent(){

        // ！！！！！！！！！！！

    }



    // 输入日期，得到当天的eventList
    public static EventList findEventListByDate(int year, int month, int day){
        EventList resultList = new EventList();
        Log.d("hahaevent",String.valueOf(getInstance().eventList.size()));
        for (Event event:getInstance().eventList){
            if (event.getBeginTime().get(GregorianCalendar.YEAR) == year
                    && event.getBeginTime().get(GregorianCalendar.MONTH ) + 1 == month
                    && event.getBeginTime().get(GregorianCalendar.DATE) == day){
                resultList.add(event);
            }
        }
        return resultList;
    }

    public static EventList findEventListByWeek(int year,int weekOfYear){
        EventList resultList = new EventList();
        if (getInstance().eventList!=null){
            for (Event event:getInstance().eventList ){
                Log.d("events_WeekOfYEAR",String.valueOf(event.getBeginTime().get(java.util.Calendar.WEEK_OF_YEAR)));
                if (event.getBeginTime().get(java.util.Calendar.WEEK_OF_YEAR) == weekOfYear
                        && year == event.getBeginTime().get(java.util.Calendar.YEAR) ){
                    resultList.add(event);
                }
            }
        }

        return resultList;
    }

    public static GroupEventList findGroupEventListByDate(int year,int month,int day){
        GroupEventList resultList = new GroupEventList();
        //
        if (getInstance().groupEventList!=null)
        {
            Log.d("hahagroupevent",String.valueOf(getInstance().groupEventList.size()));
            for (GroupEvent event:getInstance().groupEventList){
                if (event.getBeginTime().get(GregorianCalendar.YEAR) == year
                        && event.getBeginTime().get(GregorianCalendar.MONTH ) + 1 == month
                        && event.getBeginTime().get(GregorianCalendar.DATE) == day){
                    resultList.add(event);
                }
            }
        }
        return resultList;
    }

    public  static  GroupEventList findGroupEventListByWeek(int year,int weekOfYear){
        GroupEventList resultList = new GroupEventList();
        for (GroupEvent event:getInstance().groupEventList){
            if (event.getBeginTime().get(java.util.Calendar.WEEK_OF_YEAR) == weekOfYear
                    && year == event.getBeginTime().get(java.util.Calendar.YEAR) ){
                resultList.add(event);
            }
        }
        return resultList;
    }
    public static Map<String, Calendar> getDatesHasEvent()
    {
        Map<String,Calendar> map=new HashMap<>();
        System.out.print(getInstance().eventList.size());
        for (Event event:getInstance().eventList) {
            GregorianCalendar c = event.getBeginTime();
            Calendar calendar = getSchemeCalendar(c.get(java.util.Calendar.YEAR),c.get(java.util.Calendar.MONTH)+1,c.get(java.util.Calendar.DATE),"1");
            map.put(calendar.toString(),calendar);
        }
        /*
        for (GroupEvent event:getInstance().groupEventList){
            GregorianCalendar c = event.getBeginTime();
            Calendar calendar = getSchemeCalendar(c.get(java.util.Calendar.YEAR),c.get(java.util.Calendar.MONTH)+1,c.get(java.util.Calendar.DATE),"2");
            map.put(calendar.toString(),calendar);
        }*/
        return map;
    }


    // 测试
    public static void main(String[] args){
        /*
        ClientEventControl crtl = new ClientEventControl(25);
        EventList eventList = new EventList();
        crtl.eventList = eventList;
        // 使用时要保证eventID不在list中，建议使用currentEventID创建新的event
        try {
            for (int i = 10; i > 0; i -- ){
                crtl.addEvent(new Event(String.format("%d-%d-%d",i,i,i), i, i, i, i + 1, i + 1, true));
            }

            for (int i = 0; i < crtl.eventList.length(); i ++){
                System.out.println("eventID: " + crtl.eventList.get(i).getEventID());
                System.out.println("eventDescription: " + crtl.eventList.get(i).getDescription());
                System.out.println("eventBeginTime: " +crtl.eventList.get(i).getBeginTime().getTime().toString());
                System.out.println("eventEndTime: " +crtl.eventList.get(i).getEndTime().getTime().toString());
                System.out.println();
            }

            crtl.deleteEvent(5);
            crtl.eventList.sortByEventID();
            crtl.editEvent("yyy",6, 2019,8,19,0,32,2019,9,12,4,2,true);

            for (int i = 0; i < crtl.eventList.length(); i ++){
                System.out.println("eventID: " + crtl.eventList.get(i).getEventID());
                System.out.println("eventDescription: " + crtl.eventList.get(i).getDescription());
                System.out.println("eventBeginTime: " +crtl.eventList.get(i).getBeginTime().getTime().toString());
                System.out.println("eventEndTime: " +crtl.eventList.get(i).getEndTime().getTime().toString());
                System.out.println();
            }

            EventList el = crtl.findEventListByDate(2019, 4, 29);
            for (int i = 0; i < el.length(); i++){
                System.out.println(el.get(i).getEventID());
            }

        }catch(Exception ex){
            //System.out.println(ex.getMessage());
        }*/
    }
}
