package com.example.daymoon.GroupEventManagement;

import android.util.Log;

import com.example.daymoon.EventManagement.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class GroupEventInfomationHolder {
    public int startYear_;
    public int startMonth_;
    public int startDate_;
    public int startHour_;
    public int startMinute_;

    public int endYear_;
    public int endMonth_;
    public int endDate_;
    public int endHour_;
    public int endMinute_;

    private GregorianCalendar beginTime, endTime;
    public int eventType;
    public boolean allday;

    public String location;
    public String descriptions;
    public String title;

    public boolean allMember;
    public int[] memberID;
    public GroupEventInfomationHolder(Calendar c)
    {
        startYear_=c.get(Calendar.YEAR);
        startMonth_=c.get(Calendar.MONTH);
        startDate_=c.get(Calendar.DATE);
        startHour_=c.get(Calendar.HOUR);
        startMinute_=0;

        endYear_=c.get(Calendar.YEAR);
        endMonth_=c.get(Calendar.MONTH);
        endDate_=c.get(Calendar.DATE);
        endHour_=c.get(Calendar.HOUR);
        endMinute_=59;
        beginTime = new GregorianCalendar(startYear_, startMonth_, startDate_, startHour_, startMinute_);
        endTime = new GregorianCalendar(endYear_, endMonth_, endDate_, endHour_, endMinute_);
    }
    public void setBeginTime(Calendar c)
    {
        startYear_=c.get(Calendar.YEAR);
        startMonth_=c.get(Calendar.MONTH);
        startDate_=c.get(Calendar.DATE);
        startHour_=c.get(Calendar.HOUR);
        startMinute_=c.get(Calendar.MINUTE);
        beginTime = new GregorianCalendar(startYear_, startMonth_, startDate_, startHour_, startMinute_);
    }
    public void setEndTime(Calendar c)
    {
        endYear_=c.get(Calendar.YEAR);
        endMonth_=c.get(Calendar.MONTH);
        endDate_=c.get(Calendar.DATE);
        endHour_=c.get(Calendar.HOUR);
        endMinute_=c.get(Calendar.MINUTE);
        endTime = new GregorianCalendar(endYear_, endMonth_, endDate_, endHour_, endMinute_);
    }
    public GroupEventInfomationHolder(Event event)
    {
        GregorianCalendar beginTime=event.getBeginTime();
        GregorianCalendar endTime=event.getEndTime();
        startYear_=beginTime.get(Calendar.YEAR);
        startMonth_=beginTime.get(Calendar.MONTH);
        startDate_=beginTime.get(Calendar.DATE);
        startHour_=beginTime.get(Calendar.HOUR);
        startMinute_=beginTime.get(Calendar.MINUTE);

        endYear_=beginTime.get(Calendar.YEAR);
        endMonth_=beginTime.get(Calendar.MONTH);
        endDate_=beginTime.get(Calendar.DATE);
        endHour_=endTime.get(Calendar.HOUR);
        endMinute_=endTime.get(Calendar.MINUTE);
        allday=event.getWhetherProcess();


        descriptions=event.getDescription();
        title=event.getTitle();
    }

    GroupEventInfomationHolder(int y,int m,int d){
        startYear_=y;
        startMonth_=m;
        startDate_=d;
        startHour_= java.util.Calendar.HOUR;
        startMinute_=0;

        endYear_=y;
        endMonth_=m;
        endDate_=d;
        endHour_=startHour_;
        endMinute_ = 59;
    };

    String getEndTimeFormat(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
        Log.d("?",dateFormat.format(endTime.getTime()));
        return dateFormat.format(endTime.getTime());
    }

    String getBeginTimeFormat(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
        Log.d("?",dateFormat.format(beginTime.getTime()));
        return dateFormat.format(beginTime.getTime());
    }
}
