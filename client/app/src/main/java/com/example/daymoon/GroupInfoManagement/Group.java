package com.example.daymoon.GroupInfoManagement;

import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.UserInfoManagement.User;

import java.util.List;

public class Group {
    private String groupName;
    private String description;
    private int groupID;
    private int leaderID;
    private List<User> groupMember;

    public Group(String groupName,String groupDescription)
    {
        this.groupName=groupName;
        this.description=groupDescription;
    }

    public void addGroupMember(User user){
        groupMember.add(user);
    }

    public String getGroupName(){
        return groupName;
    }

    public String getGroupDescription(){
        return description;
    }

    public int getGroupID (){
        return groupID;
    }

    public List<User> getGroupMember() {
        return groupMember;
    }
}
