package com.example.daymoon.GroupEventManagement;

import android.util.EventLogTags;
import android.util.Log;

import com.example.daymoon.EventManagement.Event;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class GroupEvent extends Event implements Serializable {
    private String location;
    private int creatorID;
    private int groupID;
    private boolean AllDay;
    private boolean AllMember;
    private int[] MemberID;

    private int eventType=EVENTTYPE.Default;


    /**
     * 未完善，需要修改
     * @param creatorID;
     * @param groupID;
     * @param title;
     * @param description;
     */
    /**
     * 测试用
     * @param creatorID
     * @param groupID
     * @param title
     * @param description
     * @param beginYear
     * @param beginMonth
     * @param beginDate
     * @param beginHour
     * @param beginMin
     */
    public GroupEvent(int creatorID,int groupID,String title,String description,int beginYear,int beginMonth,int beginDate,int beginHour,int beginMin){
        super(title, description,beginYear, beginMonth,beginDate, beginHour, beginMin,
                beginYear, beginMonth, beginDate, beginHour, beginMin, false);
        this.creatorID=creatorID;
        this.groupID=groupID;
        this.description=description;
        beginTime = new GregorianCalendar(beginYear, beginMonth, beginDate, beginHour, beginMin);
        endTime = new GregorianCalendar(beginYear, beginMonth, beginDate, beginHour, 59);
        this.location="东川路";
    }

    public GroupEvent(int creatorID,int groupID,String title,String description,String location,int beginYear,int beginMonth,int beginDate,int beginHour,int beginMin,int endYear,int endMonth,int endDate,int endHour,int endMin,boolean whetherAllDay,boolean whetherAllMember,int [] MemberID){
        super(title, description,beginYear, beginMonth,beginDate, beginHour, beginMin,
                endYear, endMonth, endDate, endHour, endMin, false);
        this.creatorID=creatorID;
        this.groupID=groupID;
        this.description=description;
        this.AllDay=whetherAllDay;
        this.AllMember=whetherAllMember;
        this.MemberID=MemberID;
        this.location=location;
        this.beginTime = new GregorianCalendar(beginYear, beginMonth, beginDate, beginHour, beginMin);
        this.endTime = new GregorianCalendar(endYear, endMonth, endDate, endHour, endMin);


    }

    /**
     * 为SQLite重载
     */
    public GroupEvent(int eventID, int groupID, int[] MemberID, String eventName, String description, GregorianCalendar beginTime, GregorianCalendar endTime, int whetherProcess, int eventType, int creatorID){
        super(eventID, eventName,description, beginTime, endTime, whetherProcess);
        this.groupID = groupID;
        this.MemberID = MemberID;
        this.eventType = eventType;
        this.creatorID = creatorID;
    }

    public String getLocation(){
        return location;
    }

    public GregorianCalendar getBeginCalendar(){
        return this.beginTime;
    }

    public GregorianCalendar getEndCalendar(){
        return this.endTime;
    }
    public int compareTo(GroupEvent groupEvent)
    {
        return this.beginTime.compareTo(groupEvent.getBeginCalendar());
    }
    public String getBeginDate(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Log.d("?",dateFormat.format(beginTime.getTime()));
        return dateFormat.format(beginTime.getTime());
    }

    public String getBeginHour(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Log.d("?",dateFormat.format(beginTime.getTime()));
        return dateFormat.format(beginTime.getTime());
    }
    public String getEndDate() {
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Log.d("?",dateFormat.format(endTime.getTime()));
        return dateFormat.format(endTime.getTime());
    }
    public String getEndHour(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Log.d("?",dateFormat.format(endTime.getTime()));
        return dateFormat.format(endTime.getTime());
    }
    public int getEventType() {return this.eventType;}
    public String getTitle(){
        return this.eventName;
    }
    public String getDescription(){
        return this.description;
    }
    public int getCreatorID(){
        return this.creatorID;
    }

    String getBeginTimeFormat(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
        Log.d("?",dateFormat.format(beginTime.getTime()));
        return dateFormat.format(beginTime.getTime());
    }

    String getEndTimeFormat(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
        Log.d("?",dateFormat.format(endTime.getTime()));
        return dateFormat.format(endTime.getTime());
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getGroupID(){return groupID;}

    public int [] getMemberID(){ return  MemberID;}

    public class EVENTTYPE{
        public static final int Default = 1, game = 2, discussion = 3, travel = 4, sports = 5, eating = 6, lesson =7;
    }
}

