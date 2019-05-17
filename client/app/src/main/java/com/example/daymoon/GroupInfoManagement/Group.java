package com.example.daymoon.GroupInfoManagement;

import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;

public class Group {
    private String groupName;
    private String groupDescription;
    private int groupId;


    public Group(String groupName,String groupDescription)
    {
        this.groupName=groupName;
        this.groupDescription=groupDescription;
    }



    public String getGroupName()
    {
        return groupName;
    }
    public String getGroupDescription()
    {
        return groupDescription;
    }
    public int getGroupId ()
    {
        return groupId;
    }
}
