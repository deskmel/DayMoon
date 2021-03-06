package com.example.daymoon.UserInterface;

import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.support.annotation.NonNull;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.daymoon.Adapter.NotificationViewAdapter;
import com.example.daymoon.Adapter.TimeLineAdapter;
import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.GroupEventManagement.Notification;
import com.example.daymoon.GroupEventManagement.NotificationList;
import com.example.daymoon.GroupInfoManagement.Group;
import com.example.daymoon.HttpUtil.CalendarSerializer;
import com.example.daymoon.HttpUtil.HttpRequest;

import com.example.daymoon.Layout.MyTimeTableUtils;
import com.example.daymoon.LocalDatabase.LocalDatabaseHelper;

import com.example.daymoon.Layout.ViewPagerSlide;

import com.example.daymoon.R;
import com.example.daymoon.Tool.StatusBarUtil;
import com.example.daymoon.UserInfoManagement.ClientUserInfoControl;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.haibin.calendarview.CalendarView;
import com.yalantis.phoenix.PullToRefreshView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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

    private Cursor cursor;
    private SQLiteDatabase db;

    private ViewPagerSlide viewPager;
    private RecyclerView groupEventRecyclerView;
    private ArrayList<View> viewList;
    private View timeLinePage;
    private View notificationPage;
    private RecyclerView notificationRecyclerView;
    private MaterialDialog materialDialog;
    private MaterialDialog materialDialog_creategroupevent;
    private String message;
    private TextView noneEvent;
    private View timetablePager;
    private ConstraintLayout clContent;
    private CalendarView weekView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupID = getIntent().getIntExtra("groupID",-1);
        group = (Group) getIntent().getSerializableExtra("group");
        groupEventList = group.getEventList();

        setContentView(R.layout.activity_group_schedule);
        context = this.getApplicationContext();
        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }
        TextView groupName = findViewById(R.id.group_name);
        groupName.setText(group.getGroupName());
        initButton();
        //initaddeventbutton();

        initPage();
        initData();
        initMaterialDialog();
        final SimpleDateFormat timeformat=new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Calendar c=Calendar.getInstance();
        TextView today = findViewById(R.id.today);
        today.setText(timeformat.format(c.getTime()));
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
        FloatingActionButton eventaddbutton = findViewById(R.id.add_event_button);
        eventaddbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(GroupScheduleActivity.this,AddGroupEventActivity.class);
                startActivityForResult(intent,ADD_EVENT);
            }
        });
        FloatingActionButton notificationaddbutton = findViewById(R.id.add_notification_button);
        notificationaddbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.show();
            }
        });
        ViewFlipper viewFlipper = findViewById(R.id.flipper);
        ViewFlipper viewFlipper3 = findViewById(R.id.flipper3);
        ImageButton notification = findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
                viewFlipper.showNext();
                TextView today = findViewById(R.id.today);
                today.setText("公告");
            }
        });
        ImageButton groupback = findViewById(R.id.groupback);
        groupback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                viewFlipper.showNext();
                Calendar c=Calendar.getInstance();
                final SimpleDateFormat timeformat=new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
                TextView today = findViewById(R.id.today);
                today.setText(timeformat.format(c.getTime()));
            }
        });
        ImageButton timeTableButton = findViewById(R.id.freetimetable);
        timeTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
                viewFlipper3.showNext();
                Calendar c=Calendar.getInstance();
                final SimpleDateFormat timeformat=new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
                TextView today = findViewById(R.id.today);
                today.setText(timeformat.format(c.getTime()));
            }
        });
        ImageButton backButton = findViewById(R.id.calendar);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                viewFlipper3.showNext();
                Calendar c=Calendar.getInstance();
                final SimpleDateFormat timeformat=new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
                TextView today = findViewById(R.id.today);
                today.setText(timeformat.format(c.getTime()));
            }
        });
    }

    private void initPage(){
        viewPager=(ViewPagerSlide) findViewById(R.id.viewpagers);
        initTimeLinePage();
        initNotificationPage();
        initTimeTablePage();
        refresh();
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(timeLinePage);
        viewList.add(notificationPage);
        viewList.add(timetablePager);
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

    private void initMaterialDialog(){
        materialDialog = new MaterialDialog.Builder(this)
                .title("创建公告")
                .titleGravity(GravityEnum.CENTER)
                .input("输入公告内容", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        message = input.toString();
                    }
                })
                .positiveText("确认")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Notification notification = new Notification(dialog.getInputEditText().getText().toString(), ClientUserInfoControl.getCurrentUser().getName(), new Date(), group.getGroupName(), groupID);
                        ClientGroupEventControl.createNotification(notification, new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "成功创建公告", Toast.LENGTH_LONG).show();
                                flushNotificationList();
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "something goes wrong", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .neutralText("取消")
                .build();
        materialDialog_creategroupevent = new MaterialDialog.Builder(this)
                .titleGravity(GravityEnum.CENTER)
                .title("创建小组事件")
                .input("标题", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .input("地点", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .build();
    }

    private void initTimeTablePage(){
        timetablePager = View.inflate(this,R.layout.timetable_layout,null);
        clContent = timetablePager.findViewById(R.id.cl_content);
        weekView = timetablePager.findViewById(R.id.calendarView);
        weekView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(com.haibin.calendarview.Calendar calendar, boolean isClick) {

            }
        });
        //flushTimeTableList();

    }
    private void initTimeLinePage(){
        timeLinePage =  View.inflate(this,R.layout.grouptimeline_layout,null);
        groupEventRecyclerView=timeLinePage.findViewById(R.id.group_list);
        groupEventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noneEvent = timeLinePage.findViewById(R.id.noneevent);
    }
    private void initNotificationPage(){
        notificationPage = View.inflate(this,R.layout.notification_layout,null);
        notificationRecyclerView = notificationPage.findViewById(R.id.notification_list);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void flushTimeTableList(){
        int size = 0;
        int selectYear = 2019;
        int selectWeek = 27;
        //EventList allMemberEventList = ClientGroupEventControl.findAllMemberEventListByWeek(selectYear,selectWeek);
        EventList allMemberEventList = null;
        //GroupEventList allMemberGroupEventList = ClientGroupEventControl.findAllMemberGroupEventListByWeek(selectYear,selectWeek);
        GroupEventList allMemberGroupEventList = groupEventList;
        clContent.removeViews(50,size);
        //weekGroupEventList = ClientEventControl.findGroupEventListByWeek(selectYear,selectWeek);
        clContent=timetablePager.findViewById(R.id.cl_content);
        MyTimeTableUtils myTimeTableUtils = new MyTimeTableUtils(this,clContent,allMemberEventList,allMemberGroupEventList);
        myTimeTableUtils.fill();
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
    private void flushNotificationList(){
        ClientGroupEventControl.getNotificationListFromServer(new Runnable() {
            @Override
            public void run() {
                NotificationViewAdapter notificationViewAdapter = new NotificationViewAdapter(ClientGroupEventControl.getNotificationList().filterByGroupID(groupID),context,0);
                notificationRecyclerView.setAdapter(notificationViewAdapter);
            }
        }, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "something goes wrong when getNotificationListFromServer", Toast.LENGTH_LONG).show();
            }
        });

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
                        flushNotificationList();
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 500);
            }
        });
    }
    private void initData(){
        if (groupEventList.size()==0)
            noneEvent.setText("~这个小组的事件空空如也~");
        else noneEvent.setText("");
        flushTimeLineList();
        flushNotificationList();
        flushTimeTableList();
    }
    /*
    private void initaddeventbutton(){
        LinearLayout addeventtime=findViewById(R.id.addeventtime);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_EVENT && resultCode == SUCCESS_CODE){
            int eventID;
            eventID = data.getIntExtra("eventID", -1);
            ClientGroupEventControl.getGroupEventFromServer(eventID, new HttpRequest.DataCallback() {
                @Override
                public void requestSuccess(String result) throws Exception {
                    System.out.println(result);
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
    /*
    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }*/
}
