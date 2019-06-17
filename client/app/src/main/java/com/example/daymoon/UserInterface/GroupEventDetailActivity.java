package com.example.daymoon.UserInterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.Layout.CircularImage;
import com.example.daymoon.R;
import com.example.daymoon.Tool.StatusBarUtil;
import com.example.daymoon.UserInfoManagement.ClientUserInfoControl;
import com.example.daymoon.UserInfoManagement.User;

public class GroupEventDetailActivity extends AppCompatActivity {
    private TextView starttime;
    private TextView endtime;
    private TextView back;
    private TextView edit;
    private TextView title;
    private TextView description;
    private GroupEvent event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        setContentView(R.layout.activity_group_event_detail);
        event =(GroupEvent) getIntent().getSerializableExtra("groupevent");
        initView();
        initButton();

    }
    private void initView(){
        title=findViewById(R.id.title);
        back=findViewById(R.id.back);
        edit=findViewById(R.id.edit);
        description=findViewById(R.id.description);
        starttime=findViewById(R.id.starttime);
        endtime=findViewById(R.id.endtime);
        title.setText(event.getTitle());
        description.setText(event.getDescription());
        description.setText(event.getLocation());
        starttime.setText(String.format("%s %s %s","",event.getBeginDate(),event.getBeginHour()));
        if (event.getBeginDate().equals(event.getEndDate()))endtime.setText(String.format("%s %s %s","-","",event.getEndHour()));
        else endtime.setText(String.format("%s %s %s","-",event.getEndDate(),event.getEndHour()));

        CircularImage groupcreatorimage= (CircularImage)findViewById(R.id.creatorimage);
        User user = new User();
        ClientUserInfoControl.getUserInfoFromServer(user, event.getCreatorID(), new Runnable() {
            @Override
            public void run() {
                groupcreatorimage.setImageBitmap(user.getProfilePhoto());
            }
        }, new Runnable() {
            @Override
            public void run() {

            }
        });
    }
    private void initButton(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
