package com.example.daymoon.UserInterface;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.daymoon.Adapter.NotificationViewAdapter;
import com.example.daymoon.Adapter.TimeLineAdapter;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.GroupEventManagement.Notification;
import com.example.daymoon.GroupEventManagement.NotificationList;
import com.example.daymoon.GroupInfoManagement.Group;
import com.example.daymoon.HttpUtil.CalendarSerializer;
import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.Layout.ViewPagerSlide;
import com.example.daymoon.R;
import com.example.daymoon.UserInfoManagement.ClientUserInfoControl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yalantis.phoenix.PullToRefreshView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Locale;

import okhttp3.Request;

public class GroupScheduleActivity extends AppCompatActivity {
    private ImageButton tools;
    private ImageButton info;
    private ImageButton back;
    private Group group;
    private int groupID;

    private TimeLineAdapter timeLineAdapter=null;
    private GroupEventList groupEventList;
    private static int ADD_EVENT = 100;
    private static int SUCCESS_CODE = 1;
    private static int FAILURE_CODE = 0;
    private PullToRefreshView mPullToRefreshView;
    private ViewPagerSlide viewPager;
    private RecyclerView groupEventRecyclerView;
    private ArrayList<View> viewList;
    private View timeLinePage;
    private View notificationPage;
    private RecyclerView notificationRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupID = getIntent().getIntExtra("groupID",-1);
        group = (Group) getIntent().getSerializableExtra("group");
        setContentView(R.layout.activity_group_schedule);
        TextView groupName = findViewById(R.id.group_name);
        groupName.setText(group.getGroupName());
        initButton();
        initData();
        initaddeventbutton();
        initPage();
        final SimpleDateFormat timeformat=new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Calendar c=Calendar.getInstance();
        TextView today = findViewById(R.id.today);
        today.setText(timeformat.format(c.getTime()));
    }
    private void initPage(){
        viewPager=(ViewPagerSlide) findViewById(R.id.viewpagers);
        initTimeLinePage();
        initNotificationPage();
        refresh();
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(timeLinePage);
        viewList.add(notificationPage);
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
            }
            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(viewList.get(position));
            }
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);
        viewPager.setSlide(false);
    }
    private void initTimeLinePage(){
        timeLinePage =  View.inflate(this,R.layout.grouptimeline_layout,null);
        groupEventRecyclerView=timeLinePage.findViewById(R.id.group_list);
        groupEventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void initNotificationPage(){
        notificationPage = View.inflate(this,R.layout.notification_layout,null);
        notificationRecyclerView = notificationPage.findViewById(R.id.notification_list);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void flushTimeLineList(){
        timeLineAdapter=new TimeLineAdapter(groupEventList,this);
        groupEventRecyclerView.setAdapter(timeLineAdapter);
        timeLineAdapter.setonItemClickListener(new TimeLineAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View v, int Position) {
                Intent intent=new Intent(GroupScheduleActivity.this,GroupEventDetailActivity.class);
                intent.putExtra("groupevent",groupEventList.get(Position));
                startActivityForResult(intent,0);
            }
        });
    }
    private void flushNoificationList(){
        Notification notification = new Notification("瞎几把搞","sg",Calendar.getInstance().getTime(),"游山玩水");
        NotificationList notificationList= new NotificationList();
        notificationList.add(notification);
        NotificationViewAdapter notificationViewAdapter = new NotificationViewAdapter(notificationList,this,1);
        notificationRecyclerView.setAdapter(notificationViewAdapter);
    }
    private void refresh(){
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flushTimeLineList();
                        flushNoificationList();
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 500);
            }
        });
    }
    private void initButton()
    {
        info=findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupScheduleActivity.this,GroupDetailActivity.class);
                intent.putExtra("group",group);
                startActivityForResult(intent,0);
            }
        });
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView eventaddbutton = findViewById(R.id.add_event_button);
        eventaddbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupScheduleActivity.this,AddGroupEventActivity.class);
                startActivityForResult(intent,ADD_EVENT);
            }
        });
        ImageButton notificationaddbutton = findViewById(R.id.add_notification_button);
        notificationaddbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ViewFlipper viewFlipper = findViewById(R.id.flipper);
        ViewFlipper viewFlipper1 = findViewById(R.id.flipper1);
        ImageButton notification = findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
                viewFlipper.showNext();
                viewFlipper1.showNext();
            }
        });
        ImageButton groupback = findViewById(R.id.groupback);
        groupback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                viewFlipper.showNext();
                viewFlipper1.showNext();
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
                TextView noneEvent = findViewById(R.id.noneevent);
                flushTimeLineList();
                flushNoificationList();
                if (groupEventList.size()==0)
                    noneEvent.setText("~这个小组的事件空空如也~");
                else noneEvent.setText("");
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
                    flushTimeLineList();
                }

                @Override
                public void requestFailure(Request request, IOException e) {

                }
            });
        }
    }
}
