package com.example.daymoon.GroupEventManagement;

import android.util.EventLogTags;
import android.util.Log;

import com.example.daymoon.EventManagement.Event;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class GroupEvent implements Serializable {
    private String title;
    private String description;
    private String location;
    private int creatorID;
    private int groupID;
    private boolean AllDay;
    private boolean AllMember;
    private int[] MemberID;
    public enum EVENTTYPE{
        Default,game,discussion,travel,sports,eating,lession
    };
    private EVENTTYPE eventType=EVENTTYPE.Default;
    private GregorianCalendar beginTime, endTime;

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
        this.title=title;
        this.creatorID=creatorID;
        this.groupID=groupID;
        this.description=description;
        beginTime = new GregorianCalendar(beginYear, beginMonth, beginDate, beginHour, beginMin);
        endTime = new GregorianCalendar(beginYear, beginMonth, beginDate, beginHour, 59);
        this.location="东川路";
    }

    public GroupEvent(int creatorID,int groupID,String title,String description,String location,int beginYear,int beginMonth,int beginDate,int beginHour,int beginMin,int endYear,int endMonth,int endDate,int endHour,int endMin,boolean whetherAllDay,boolean whetherAllMember,int [] MemberID){
        this.title=title;
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

    public String getLocation(){
        return location;
    }

    public GregorianCalendar getBeginCalendar(){
        return this.beginTime;
    }

    public GregorianCalendar getEndCalendar(){
        return this.endTime;
    }

    public String getBeginDate(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
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
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        Log.d("?",dateFormat.format(endTime.getTime()));
        return dateFormat.format(endTime.getTime());
    }
    public String getEndHour(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Log.d("?",dateFormat.format(endTime.getTime()));
        return dateFormat.format(endTime.getTime());
    }
    public EVENTTYPE getEventType() {return this.eventType;}
    public String getTitle(){
        return this.title;
    }
    public String getDescription(){
        return this.description;
    }
    public int getCreatorID(){
        return this.creatorID;
    }

}
