package com.example.daymoon.EventManagement;

import android.util.Log;

import com.example.daymoon.GroupEventManagement.GroupEvent;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Event implements Comparable<Event>, Serializable {
    protected String eventName;
    protected String description;
    protected String location;
    protected GregorianCalendar remindTime;
    protected boolean whetherRemind;
    protected int eventID;
    protected GregorianCalendar beginTime, endTime;
    boolean whetherProcess;

    // Reminder reminder;
    public Event(String name, String des,int beginYear, int beginMonth, int beginDate, int beginHour, int beginMin,
                 int endYear, int endMonth, int endDate, int endHour, int endMin, boolean wProcess) {
            description = des;
            beginTime = new GregorianCalendar(beginYear, beginMonth - 1, beginDate, beginHour, beginMin);
            endTime = new GregorianCalendar(endYear, endMonth - 1, endDate, endHour, endMin);
            whetherProcess = wProcess;
            //需要修改
            eventName = name;
    }

    //加 location的初始化
    /*
    TODO 全部修改完成，替换旧的初始化函数
     */
    public Event(String name, String des,int eID,int beginYear, int beginMonth, int beginDate, int beginHour, int beginMin,
                 int endYear, int endMonth, int endDate, int endHour, int endMin, boolean wProcess,String location,boolean whetherRemind,GregorianCalendar remindTime) throws Exception{

        if (validEventInfo(des, beginYear, beginMonth, beginDate, beginHour, beginMin, endYear, endMonth, endDate, endHour, endMin)) {
            this.description = des;
            this.beginTime = new GregorianCalendar(beginYear, beginMonth - 1, beginDate, beginHour, beginMin);
            this.endTime = new GregorianCalendar(endYear, endMonth - 1, endDate, endHour, endMin);
            this.whetherProcess = wProcess;
            this.eventName = name;
            //添加的内容
            this.whetherRemind= whetherRemind;
            this.remindTime = remindTime;
            this.location = location;
        }
        else{
            throw new Exception("Fail to construct event");
        }
    }


    public Event(String name, String des, int eID, int beginYear, int beginMonth, int beginDate, int beginHour, int beginMin,
                 int endYear, int endMonth, int endDate, int endHour, int endMin, boolean wProcess) throws Exception{

        if (validEventInfo(des, beginYear, beginMonth, beginDate, beginHour, beginMin, endYear, endMonth, endDate, endHour, endMin)) {
            description = des;
            eventID = eID;
            beginTime = new GregorianCalendar(beginYear, beginMonth - 1, beginDate, beginHour, beginMin);
            endTime = new GregorianCalendar(endYear, endMonth - 1, endDate, endHour, endMin);
            whetherProcess = wProcess;
            //需要修改
            eventName = name;
        }
        else{
            throw new Exception("Fail to construct event");
        }
    }



    // 默认当天的年月日初始化
    public Event(String str, int eID, int beginHour, int beginMin,
                 int endHour, int endMin, boolean wProcess) throws Exception{

        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int date = today.get(Calendar.DATE);
        if (validEventInfo(str, year, month + 1, date, beginHour, beginMin, year, month + 1, date, endHour, endMin)) {
            description = str;
            eventID = eID;
            beginTime = new GregorianCalendar(year, month, date, beginHour, beginMin);
            endTime = new GregorianCalendar(year, month, date, endHour, endMin);
            whetherProcess = wProcess;
        }
        else{
            throw new Exception("Fail to construct event");
        }

    }

    /**
     * 为SQLite重载一下
     */
    public Event(int eventID, String eventName, String description, GregorianCalendar beginTime, GregorianCalendar endTime, int whetherProcess){
        this.eventID = eventID;
        this.eventName = eventName;
        this.description = description;
        this.beginTime = beginTime;
        this.endTime = endTime;
        if (whetherProcess == 0){this.whetherProcess = false;}
        else{this.whetherProcess = true;}
    }


    public int compareTo(Event event)
    {
        return this.beginTime.compareTo(event.getBeginTime());
    }


    // 检查event信息合法性: 日期和时间的格式，字符串<=100个字
    private boolean validEventInfo(String description, int beginYear, int beginMonth, int beginDate, int beginHour, int beginMin,
                                  int endYear, int endMonth, int endDate, int endHour, int endMin){
        try {
            String date1Str = String.valueOf(beginYear) + '-' + String.valueOf(beginMonth) + '-' + String.valueOf(beginDate);
            String date2Str = String.valueOf(endYear) + '-' + String.valueOf(endMonth) + '-' + String.valueOf(endDate);
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
            format.setLenient(false);
            format.parse(date1Str);
            format.parse(date2Str);

            if ( (beginHour >=0 && beginHour <= 23 ) && (endHour >=0 && endHour <= 23 )
                    && (beginMin >=0 && beginMin <= 59) && (endMin >= 0 && endMin <= 59)
                    && description.length() <= 100
                    && endAfterBegin(beginYear, beginMonth, beginDate, beginHour, beginMin, endYear, endMonth, endDate, endHour, endMin)){

                return true;
            }
            else{
                System.out.println("Invalid time format");
            }

        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Invalid date format");
        }

        return false;
    }



    // 辅助函数，判断beginTime是否先于endTime
    private boolean endAfterBegin(int beginYear, int beginMonth, int beginDate, int beginHour, int beginMin,
                                 int endYear, int endMonth, int endDate, int endHour, int endMin){

        if (endYear > beginYear){
            return true;
        }
        else if(endYear == beginYear){
            if (endMonth > beginMonth){
                return true;
            }
            else if (endMonth == beginMonth){
                if (endDate > beginDate){
                    return true;
                }
                else if (endDate == beginDate){
                    if (endHour > beginHour){
                        return true;
                    }
                    else if (endHour == beginHour){
                        if (endMin >= beginMin){
                            return true;
                        }
                    }
                }
            }
        }

        return false;

    }

    public String getLocation(){return location;}

    public String getDescription(){
        return description;
    }

    public String getTitle(){
        return eventName;
    }

    public GregorianCalendar getBeginTime(){
        return beginTime;
    }

    /**
     *
     * @return @return 返回事件的字符串形式 如2016年8月18日 22时35分
     */

    public String getBeginTime_str(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分",Locale.CHINA);
        Log.d("?",dateFormat.format(beginTime.getTime()));
        return dateFormat.format(beginTime.getTime());
    }

    String getBeginTimeFormat(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
        Log.d("?",dateFormat.format(beginTime.getTime()));
        return dateFormat.format(beginTime.getTime());
    }

    public GregorianCalendar getEndTime(){
        return endTime;
    }


    /**
     *
     * @return 返回事件的字符串形式 如2016年8月18日 22时35分
     */
    public String getEndTime_str(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("y年M月d日 H时m分",Locale.CHINA);
        return dateFormat.format(endTime);

    }

    public String getEndHour(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("HH:mm",Locale.CHINA);
        return dateFormat.format(endTime.getTime());
    }

    String getEndTimeFormat(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
        Log.d("?",dateFormat.format(endTime.getTime()));
        return dateFormat.format(endTime.getTime());
    }

    /**
     *
     * @return 返回事件的持续事件
     * if (allday) result = 全天
     * else result = BeginTime.
     */
    public String getLastingTime_str(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        String BeginMinute = dateFormat.format(beginTime.getTime());
        String EndMinute = dateFormat.format(endTime.getTime());
        Log.d("BeginMinute",BeginMinute);
        Log.d("EndMinute",EndMinute);
        return String.format("%s - %s",BeginMinute,EndMinute);
    }
    public String getBeginHour_str(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        String BeginMinute = dateFormat.format(beginTime.getTime());
        return String.format("%s",BeginMinute);
    }
    public String getEndHour_str(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        String EndMinute = dateFormat.format(endTime.getTime());
        Log.d("EndMinute",EndMinute);
        return String.format("%s",EndMinute);
    }
    public int getEventID(){
        return eventID;
    }



    public boolean getWhetherProcess(){
        return whetherProcess;
    }



    void setDescription(String description) {
        this.description = description;
    }



    // 最好不更改
    void setEventID(int eventID){
        this.eventID = eventID;
    }



    void setBeginTime(GregorianCalendar beginTime){
        this.beginTime = beginTime;
    }



    void setEndTime(GregorianCalendar endTime){
        this.endTime = endTime;
    }



    public void setWhetherProcess(boolean whetherProcess){
        this.whetherProcess = whetherProcess;
    }
    // 测试
    public static void main(String[] args){

    }
}