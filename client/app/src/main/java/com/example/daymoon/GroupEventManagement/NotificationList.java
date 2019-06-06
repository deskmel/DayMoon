package com.example.daymoon.GroupEventManagement;

import java.util.LinkedList;

public class  NotificationList  extends LinkedList<Notification> {
    public NotificationList filterByGroupID(int GroupID){
        NotificationList result=new NotificationList();
        for (Notification notification:this){
            if (notification.getGroupID() == GroupID) result.add(notification);
        }
        return result;
    }
}
