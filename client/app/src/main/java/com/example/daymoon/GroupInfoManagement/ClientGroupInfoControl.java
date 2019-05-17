package com.example.daymoon.GroupInfoManagement;

public class ClientGroupInfoControl {
    private int userId;
    private static GroupList groupList;
    private int currentUserID;
    private static ClientGroupInfoControl clientGroupInfoControl;
    public static ClientGroupInfoControl getInstance()
    {
        if (clientGroupInfoControl==null) {
            clientGroupInfoControl = new ClientGroupInfoControl();
            groupList=new GroupList();
        }
        return clientGroupInfoControl;
    }
    public static void setCurrentUserID(int currentUserID){
        getInstance().currentUserID = currentUserID;
    }
    public static GroupList getGroupList()
    {
        return groupList;
    }
}

