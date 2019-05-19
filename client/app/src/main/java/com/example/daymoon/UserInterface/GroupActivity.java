package com.example.daymoon.UserInterface;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;


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
    private ImageButton more;
    private Context mainContext = null;
    private ClientGroupInfoControl clientGroupInfoControl;
    private PopupWindow popupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        mainContext = GroupActivity.this;
        recyclerView=findViewById(R.id.group_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        ClientGroupInfoControl.getGroupListFromServer(new Runnable() {
            @Override
            public void run() {
                groupList = ClientGroupInfoControl.getGroupList();
                flushGroupList();
            }
        }, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "something goes wrong", Toast.LENGTH_LONG).show();
            }
        });

        more = findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoneEffect();
            }
        });
    }
    private void flushGroupList()
    {
        adapter = new GroupViewAdapter(groupList, mainContext);
        recyclerView.setAdapter(adapter);
        adapter.setonItemClickListener(new GroupViewAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View v, int Position) {
                Intent intent = new Intent(GroupActivity.this,GroupScheduleActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }
    private void showNoneEffect(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopupWindow = inflater.inflate(R.layout.popupmenu, null, false);
        popupWindow = new PopupWindow(vPopupWindow, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        addBackground();
        int width = more.getMeasuredWidth();//按钮宽度
        popupWindow.setWidth(width*4);

        //popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        View parentView = LayoutInflater.from(GroupActivity.this).inflate(R.layout.layout_popupwindow, null);
        //popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);

        popupWindow.showAsDropDown(more,29,0,Gravity.RIGHT);
        vPopupWindow.findViewById(R.id.creategroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupActivity.this,CreateGroupActivity.class);
                startActivityForResult(intent,0);
            }
        });
        vPopupWindow.findViewById(R.id.joingroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });


    }
    private void addBackground() {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;//调节透明度
        getWindow().setAttributes(lp);
        //dismiss时恢复原样
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }
}
