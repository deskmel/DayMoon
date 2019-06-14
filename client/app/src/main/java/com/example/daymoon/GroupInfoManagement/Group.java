package com.example.daymoon.GroupInfoManagement;

import android.graphics.Bitmap;

import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.UserInfoManagement.User;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Group implements Serializable {
    private String groupName;
    private String description;
    private int groupID;
    private int leaderID;
    private int[] memberIDs, eventIDs; //临时使用一下
    private List<User> groupMember;
    private String imgName;
    private GroupEventList eventList;

    public Group(String groupName,String groupDescription)
    {
        this.groupName=groupName;
        this.description=groupDescription;
    }

    /**
     * 为SQLite重载
     */
    public Group(int groupID, String groupName, String description, int[] memberIDs, int[] eventIDs, int leaderID, String imgName){
        this.groupID = groupID;
        this.groupName = groupName;
        this.description = description;
        this.memberIDs = memberIDs;
        this.eventIDs = eventIDs;
        this.leaderID = leaderID;
        this.imgName = imgName;
        this.eventList = new GroupEventList();
        GroupEventList groupEventList = ClientEventControl.getGroupEventList();
        for (int eventID:eventIDs) {
            this.eventList.add(groupEventList.get(groupEventList.findByID(eventID)));
        }
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

    public String getImgName() {
        return imgName;
    }

    public List<User> getGroupMember() {
        return groupMember;
    }

    public GroupEventList getEventList() {
        return eventList;
    }

    public int [] getMemberIDs(){ return memberIDs; }

    public int [] getEventIDs(){ return eventIDs; }

    public int getLeaderID(){return leaderID;}
}
