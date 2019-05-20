package com.example.daymoon.GroupEventManagement;

import com.example.daymoon.GroupInfoManagement.Group;

public class ClientGroupEventControl {
    private int GroupID;
    private int GroupEventID;
    private int currentUserID;
    private static ClientGroupEventControl clientGroupEventControl;
    private static ClientGroupEventControl getInstance(){
        if (clientGroupEventControl==null){
            clientGroupEventControl=new ClientGroupEventControl();
            return clientGroupEventControl;
        }
        else return clientGroupEventControl;
    }
    public static void setCurrentID(int GroupID,int UserID){
        getInstance().GroupID=GroupID;
        getInstance().currentUserID=UserID;
    }

    public static GroupEventList getGroupEventListFromServer(Runnable success,Runnable failure){
        return null;
    }

    public static void createGroupEvent(Runnable success,Runnable faliure){
    }


    public String getLatestGroupEventDes(int groupId){
        return "组会 东上院 9:00-10:00";
    }
}
