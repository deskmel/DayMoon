package com.example.daymoon.GroupInfoManagement;

import android.graphics.Bitmap;

import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.UserInfoManagement.User;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {
    private String groupName;
    private String description;
    private int groupID;
    private int leaderID;
    private int[] memberIDs, eventIDs; //临时使用一下
    private List<User> groupMember;
    private String imgName;

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

    public String getImgName() {
        return imgName;
    }

    public List<User> getGroupMember() {
        return groupMember;
    }

}
