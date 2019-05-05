package com.example.daymoon.UserInterface;

public class Event_information_holder {
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

    public Event_information_holder(int y,int m,int d){
        Year_=y;
        Month_=m;
        Date_=d;
        java.util.Calendar c = java.util.Calendar.getInstance();
        startHour_=c.HOUR;
        startMinute_=0;
        endHour_=startHour_+1;
        endMinute_ = 0;
    };
}
