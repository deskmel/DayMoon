package com.example.daymoon.UserInterface;

import com.example.daymoon.EventManagement.Event;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class EventInformationHolder {
    public int Year_;
    public int Month_;
    public int Date_;
    public int startHour_;
    public int endHour_;
    public int startMinute_;
    public int endMinute_;
    public boolean allday;
    public boolean process;
    public String descriptions;
    public String title;

    EventInformationHolder(Event event)
    {
        GregorianCalendar beginTime=event.getBeginTime();
        GregorianCalendar endTime=event.getEndTime();
        Year_=beginTime.get(Calendar.YEAR);
        Month_=beginTime.get(Calendar.MONTH);
        Date_=beginTime.get(Calendar.DATE);
        startHour_=beginTime.get(Calendar.HOUR);
        startMinute_=beginTime.get(Calendar.MINUTE);
        endHour_=endTime.get(Calendar.HOUR);
        endMinute_=endTime.get(Calendar.MINUTE);
        allday=event.getWhetherProcess();
        process=event.getWhetherProcess();
        descriptions=event.getDescription();
        title=event.getTitle();
    }

    EventInformationHolder(int y,int m,int d){
        Year_=y;
        Month_=m;
        Date_=d;
        startHour_= java.util.Calendar.HOUR;
        startMinute_=0;
        endHour_=startHour_+1;
        endMinute_ = 0;
    };
}
