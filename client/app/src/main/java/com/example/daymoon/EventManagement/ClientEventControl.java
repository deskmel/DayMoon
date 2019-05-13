package com.example.daymoon.EventManagement;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.example.daymoon.HttpUtil.CalendarSerializer;
import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.HttpUtil.HttpRequestThread;
import static com.example.daymoon.Define.Constants.SERVER_IP;

import okhttp3.Request;

import com.example.daymoon.UserInterface.Event_information_holder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ClientEventControl {//施工

    private int currentUserID, currentEventID; // 下一个event的ID，即当前event总数+1，这样可以保证eventID不重复

    private EventList eventList;

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
    public static void getEventListFromServer(){
        Map<String,String> params = new HashMap<>();
        params.put("userID",String.valueOf(getInstance().currentUserID));
        new HttpRequestThread(SERVER_IP+"getallmyevents",params, new HttpRequest.DataCallback(){
            @Override
            public void requestSuccess(String result) {
                Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(GregorianCalendar.class,
                        new CalendarSerializer()).create();
                System.out.println(result);
                Type EventRecordType = new TypeToken<EventList>(){}.getType();
                getInstance().eventList = gson.fromJson(result, EventRecordType);
            }
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.e("shit", "oops! Something goes wrong");
            }
        }).start();
    }


    // 增加一个event
    public static void addEvent(Event_information_holder event_info, Runnable callback){
        Event event;
        try {
            event = new Event(event_info.title, event_info.descriptions, 0, event_info.Year_, event_info.Month_, event_info.Date_, event_info.startHour_, event_info.startMinute_, event_info.Year_, event_info.Month_, event_info.Date_, event_info.endHour_, event_info.endMinute_, event_info.process);
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

        new HttpRequestThread(SERVER_IP+"submitevent", params, new HttpRequest.DataCallback(){
            @Override
            public void requestSuccess(String result) {
                Log.i("success","add the event successfully");
                event.setEventID(Integer.valueOf(result));
                getInstance().eventList.add(event);
                callback.run();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Log.e("shit","oops! Something goes wrong");
                callback.run();
            }
        }).start();
    }



    // 删除一个event
    public int deleteEvent(int eventID){
        eventList.sortByEventID();
        int index = 0;//TODO 此处因为二分查找被删了 暂时没有

        if (index != -1){
            eventList.remove(index);
            return 0;
        }
        else{
            System.out.println("EventID not found");
            return 1;
        }

    }



    // 修改一个event
    public int editEvent(String description, int eventID, int beginYear, int beginMonth, int beginDate, int beginHour, int beginMin,
                         int endYear, int endMonth, int endDate, int endHour, int endMin, boolean wProcess){
        eventList.sortByEventID();
        int index = 0;//TODO 此处因为二分查找被删了 暂时没有

        if (index != -1){

            eventList.get(index).setDescription(description);
            eventList.get(index).setBeginTime(new GregorianCalendar(beginYear, beginMonth - 1, beginDate, beginHour, beginMin));
            eventList.get(index).setEndTime(new GregorianCalendar(endYear, endMonth - 1, endDate, endHour, endMin));
            eventList.get(index).whetherProcess = wProcess;

            return 0;
        }
        else{

            System.out.println("EventID not found.");
            return 1;

        }
    }



    // 将现在的eventList同步到服务器
    public void updateEvent(){

        // ！！！！！！！！！！！

    }



    // 输入日期，得到当天的eventList
    public static EventList findEventListByDate(int year, int month, int day){
        EventList resultList = new EventList();
        for (Event event:getInstance().eventList){
            if (event.getBeginTime().get(GregorianCalendar.YEAR) == year
                    && event.getBeginTime().get(GregorianCalendar.MONTH ) + 1 == month
                    && event.getBeginTime().get(GregorianCalendar.DATE) == day){

                resultList.add(event);
            }
        }
        return resultList;
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
