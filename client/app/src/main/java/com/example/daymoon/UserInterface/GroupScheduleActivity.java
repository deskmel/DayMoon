package com.example.daymoon.UserInterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.R;

public class GroupScheduleActivity extends AppCompatActivity {
    private ImageButton tools;
    private ImageButton back;
    private RecyclerView recyclerView;
    private TimeLineAdapter timeLineAdapter=null;
    private GroupEventList groupEventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_schedule);
        tools=findViewById(R.id.tools);
        back=findViewById(R.id.back);
        recyclerView=findViewById(R.id.event_list);

        initButton();
        initData();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        timeLineAdapter=new TimeLineAdapter(groupEventList,this);
        recyclerView.setAdapter(timeLineAdapter);

        initaddevent();



    }

    private void initButton()
    {
        tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupScheduleActivity.this,GroupDetailActivity.class);
                startActivityForResult(intent,0);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initData(){
        groupEventList=new GroupEventList();
        GroupEvent event1=new GroupEvent(1,0,"东上院组会","",2019,5,6,12,30);
        GroupEvent event2=new GroupEvent(1,0,"东上院跳舞","",2019,5,6,16,30);
        GroupEvent event3=new GroupEvent(1,0,"寝室乱嗨","",2019,5,6,19,30);
        groupEventList.add(event1);
        groupEventList.add(event2);
        groupEventList.add(event3);
    }
    private void initaddevent(){
        LinearLayout addeventtime=findViewById(R.id.addeventtime);
        TextView addeventtext=findViewById(R.id.addeventtext);
        if (groupEventList.size()%2==0) {
            RelativeLayout.LayoutParams ll =(RelativeLayout.LayoutParams) addeventtime.getLayoutParams();
            ll.addRule(RelativeLayout.LEFT_OF,R.id.add_event_image);
            addeventtime.setLayoutParams(ll);
            ll =(RelativeLayout.LayoutParams) addeventtext.getLayoutParams();
            ll.addRule(RelativeLayout.RIGHT_OF,R.id.add_event_image);
            addeventtext.setLayoutParams(ll);
        }
        else{
            RelativeLayout.LayoutParams ll =(RelativeLayout.LayoutParams) addeventtime.getLayoutParams();
            ll.addRule(RelativeLayout.RIGHT_OF,R.id.add_event_image);
            addeventtime.setLayoutParams(ll);
            ll =(RelativeLayout.LayoutParams) addeventtext.getLayoutParams();
            ll.addRule(RelativeLayout.LEFT_OF,R.id.add_event_image);
            addeventtext.setLayoutParams(ll);
        }
    }
}
