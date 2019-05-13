package com.example.daymoon.EventManagement;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.HttpUtil.HttpRequestThread;
import com.example.daymoon.Define.Constants.*;

import okhttp3.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ClientEventControl {//施工

    private int currentUserID, currentEventID; // 下一个event的ID，即当前event总数+1，这样可以保证eventID不重复

    private EventList eventList;

    private ClientEventControl(int currentEventID){
        currentUserID = currentEventID;
    }

    // 通过currentUserID向服务器找到当前user的eventList
    public void getEventList(){
        Map<String,String> params = new HashMap<>();
        params.put("userid",String.valueOf(currentUserID));
        try{
            new HttpRequestThread(params, new HttpRequest.DataCallback(){
                @Override
                public void requestSuccess(String result) {
                    Gson gson = new GsonBuilder().create();
                    Type EventRecordType = new TypeToken<LinkedList<Event>>(){}.getType();
                    eventList.eventRecord = gson.fromJson(result, EventRecordType);
                }

                @Override
                public void requestFailure(Request request, IOException e) {
                    Log.e("shit", "oops! Something goes wrong");
                }
            }).join();
        }catch (InterruptedException e){
            Log.e("shit", "InterruptedException");
            e.printStackTrace();
        }
    }



    // 增加一个event
    public void addEvent(Event event){
        eventList.add(event);
        Map<String,String> params = new HashMap<>();

        //TO DO params.put();

        new HttpRequestThread(params, new HttpRequest.DataCallback(){
            @Override
            public void requestSuccess(String result) {
                Log.i("success","add the event successfully");
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Log.e("shit","oops! Something goes wrong");
            }
        }).start();
    }



    // 删除一个event
    public int deleteEvent(int eventID){
        eventList.sortByEventID();
        int index = eventList.findBinary(eventID);

        if (index != -1){
            eventList.delete(index);
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
        int index = eventList.findBinary(eventID);

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
    public EventList findEventListByDate(int beginYear, int beginMonth, int beginDate){
        EventList resultList = new EventList();

        for (int i = 0; i < eventList.length(); i ++){
            Event currentEvent = eventList.get(i);
            if (currentEvent.getBeginTime().get(GregorianCalendar.YEAR) == beginYear
                    && currentEvent.getBeginTime().get(GregorianCalendar.MONTH ) + 1 == beginMonth
                    && currentEvent.getBeginTime().get(GregorianCalendar.DATE) == beginDate){

                resultList.add(currentEvent);
            }

        }
        return resultList;
    }



    // 测试
    public static void main(String[] args){
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
        }
    }
}
