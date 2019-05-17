package com.example.daymoon.UserInterface;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.GroupInfoManagement.ClientGroupInfoControl;
import com.example.daymoon.GroupInfoManagement.Group;
import com.example.daymoon.GroupInfoManagement.GroupList;
import com.example.daymoon.R;

public class GroupActivity extends AppCompatActivity {
    private int userId;
    private RecyclerView recyclerView;
    private GroupViewAdapter adapter =null;
    private GroupList groupList;
    private Context mainContext = null;
    private ClientGroupInfoControl clientGroupInfoControl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        mainContext = GroupActivity.this;
    }
    private void flushGroupList()
    {
        groupList =ClientGroupInfoControl.getGroupList() ;
        Group group1 = new Group("哈哈","hehe");
        Group group2 = new Group("东15","基佬群");
        groupList.add(group1);
        groupList.add(group2);
        adapter = new GroupViewAdapter(groupList, mainContext);
    }
}
