package com.example.daymoon.GroupEventManagement;

import android.util.EventLogTags;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class GroupEvent{
    private String title;
    private String description;
    private int creatorID;
    private int groupID;
    private GregorianCalendar beginTime, endTime;

    /**
     * 未完善，需要修改
     * @param creatorID;
     * @param groupID;
     * @param title;
     * @param description;
     */
    public GroupEvent(int creatorID,int groupID,String title,String description,int beginYear,int beginMonth,int beginDate,int beginHour,int beginMin){
        this.title=title;
        this.creatorID=creatorID;
        this.groupID=groupID;
        this.description=description;
        beginTime = new GregorianCalendar(beginYear, beginMonth, beginDate, beginHour, beginMin);
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
