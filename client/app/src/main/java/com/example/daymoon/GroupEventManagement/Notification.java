package com.example.daymoon.GroupEventManagement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notification {
    private String groupName;
    private int groupID;
    private String creatorName;
    private String message;
    private Date createTime;
    public Notification(String message,String creatorName,Date date,String groupName,int groupID){
        this.message =message;
        this.creatorName=creatorName;
        this.createTime=date;
        this.groupName= groupName;
        this.groupID = groupID;
    }

    public String getCreatorName() {
        return creatorName;
    }

    String getCreateTimeFormat(){
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
        return dateFormat.format(createTime);
    }

    public int getGroupID() {
        return groupID;
    }

    public String getMessage() {
        return message;
    }

    public  String getPublishInfo(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 hh/mm ", Locale.CHINA);
        return String.format("%s %s",creatorName,dateFormat.format(createTime));
    }

    public  String getGroupname(){
        return groupName;
    }
}
