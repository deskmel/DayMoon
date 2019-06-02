package com.example.daymoon.GroupEventManagement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notification {
    private String groupname;
    private int creatorID;
    private String creatorName;
    private String message;
    private Date createTime;
    public Notification(String message,String creatorName,Date date,String groupname){
        this.message =message;
        this.creatorName=creatorName;
        this.createTime=date;
        this.groupname= groupname;
    }


    public String getMessage() {
        return message;
    }
    public  String getPublishInfo(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 hh/mm ", Locale.CHINA);
        return String.format("%s %s",creatorName,dateFormat.format(createTime));
    }
    public  String getGroupname(){
        return groupname;
    }
}
