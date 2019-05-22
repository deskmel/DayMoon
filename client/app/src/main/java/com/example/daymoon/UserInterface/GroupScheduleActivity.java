package com.example.daymoon.UserInterface;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daymoon.Adapter.TimeLineAdapter;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.GroupInfoManagement.Group;
import com.example.daymoon.HttpUtil.CalendarSerializer;
import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.R;
import com.example.daymoon.UserInfoManagement.ClientUserInfoControl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.GregorianCalendar;

import okhttp3.Request;

public class GroupScheduleActivity extends AppCompatActivity {
    private ImageButton tools;
    private ImageButton info;
    private ImageButton back;
    private Group group;
    private int groupID;
    private RecyclerView recyclerView;
    private TimeLineAdapter timeLineAdapter=null;
    private GroupEventList groupEventList;
    private static int ADD_EVENT = 100;
    private static int SUCCESS_CODE = 1;
    private static int FAILURE_CODE = 0;
    private ImageView eventaddbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupID = getIntent().getIntExtra("groupID",-1);
        group = (Group) getIntent().getSerializableExtra("group");
        setContentView(R.layout.activity_group_schedule);
        info=findViewById(R.id.info);
        back=findViewById(R.id.back);
        recyclerView=findViewById(R.id.event_list);
        eventaddbutton=findViewById(R.id.add_event_image);
        initButton();
        initData();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initaddeventbutton();



    }
    private void flushList(){
        timeLineAdapter=new TimeLineAdapter(groupEventList,this);
        recyclerView.setAdapter(timeLineAdapter);
        timeLineAdapter.setonItemClickListener(new TimeLineAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View v, int Position) {
                Intent intent=new Intent(GroupScheduleActivity.this,GroupEventDetailActivity.class);
                intent.putExtra("groupevent",groupEventList.get(Position));
                startActivityForResult(intent,0);
            }
        });
    }
    private void initButton()
    {
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupScheduleActivity.this,GroupDetailActivity.class);
                intent.putExtra("group",group);
                startActivityForResult(intent,0);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        eventaddbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupScheduleActivity.this,AddGroupEventActivity.class);
                startActivityForResult(intent,ADD_EVENT);
            }
        });
    }

    private void initData(){
        groupEventList=new GroupEventList();
        ClientGroupEventControl.getGroupEventListFromServer(groupID, new HttpRequest.DataCallback() {
            @Override
            public void requestSuccess(String result) throws Exception {
                Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(GregorianCalendar.class,
                        new CalendarSerializer()).create();
                Type GroupEventRecordType = new TypeToken<GroupEventList>(){}.getType();
                groupEventList = gson.fromJson(result, GroupEventRecordType);
                flushList();
            }
            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(getApplicationContext(),"oops something goes wrong", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void initaddeventbutton(){
        LinearLayout addeventtime=findViewById(R.id.addeventtime);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_EVENT && resultCode == SUCCESS_CODE){
            int eventID;
            eventID = data.getIntExtra("eventID", -1);
            ClientGroupEventControl.getGroupEventFromServer(eventID, new HttpRequest.DataCallback() {
                @Override
                public void requestSuccess(String result) throws Exception {
                    Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(GregorianCalendar.class,
                            new CalendarSerializer()).create();
                    GroupEvent groupEvent = gson.fromJson(result, GroupEvent.class);
                    groupEventList.add(groupEvent);
                    flushList();
                }

                @Override
                public void requestFailure(Request request, IOException e) {

                }
            });
        }
    }
}
