package com.example.daymoon.UserInterface;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.app.AlertDialog;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.daymoon.Adapter.EventViewAdapter;
import com.example.daymoon.Adapter.NewTimeLineAdapter;
import com.example.daymoon.Adapter.TimeLineAdapter;
import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.GroupInfoManagement.ClientGroupInfoControl;

import com.example.daymoon.Layout.MyTimeTableUtils;
import com.example.daymoon.LocalDatabase.LocalDatabaseHelper;

import com.example.daymoon.Layout.ViewPagerSlide;

import com.example.daymoon.R;
import com.example.daymoon.Tool.StatusBarUtil;
import com.example.daymoon.Tool.pxUtils;
import com.example.daymoon.UserInfoManagement.ClientUserInfoControl;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.heinrichreimersoftware.materialdrawer.DrawerActivity;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import com.heinrichreimersoftware.materialdrawer.theme.DrawerTheme;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import android.view.GestureDetector;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;

public class CalendarActivity extends DrawerActivity implements CalendarView.OnViewChangeListener{

    private CalendarView calendarView;
    private CalendarView weekView;
    private CalendarLayout calendarLayout;
    private TextView tvMonth;
    private RecyclerView calendarRecyclerView;
    private RecyclerView timelineRecyclerView;
    private Context mainContext = null;
    private EventViewAdapter adapter = null;
    private AlertDialog alert = null;//提醒框
    private ViewPagerSlide viewPager;
    //private TestUserInterfaceControl UIControl= TestUserInterfaceControl.getUIControl();
    private int selectYear;
    private int selectMonth;
    private int selectDay;
    private int selectWeek;
    private EventList todayEventList;
    private EventList weekEventList;
    private GroupEventList todayGroupEventList;
    private GroupEventList weekGroupEventList;
    private GestureDetector mDetector;
    private LayoutInflater inflater;
    private View calendarPager;
    private View timeLine;
    private View timetablePager;
    private ArrayList<View> viewList;
    private Cursor cursor;
    private SQLiteDatabase db;
    private ConstraintLayout clContent;
    private TextView picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int userId = getIntent().getIntExtra("userid", -1);
        if (userId <0) System.out.println("no userID given");
        ClientUserInfoControl.setCurrentUser(userId, new Runnable(){
            @Override
            public void run() {

            }
        }, new Runnable(){
            @Override
            public void run() {

            }
        });
        ClientEventControl.setCurrentUserID(userId);//TODO 替换为由登录界面传递过来的ID
        ClientEventControl.getEventListFromServer(new Runnable() {
            @Override
            public void run() {
                setSchemeDate();
                flushCalendarListView();
            }
        });
        ClientEventControl.getGroupEventListFromServer(new Runnable() {
            @Override
            public void run() {
                setSchemeDate();
                flushCalendarListView();
            }
        });
        ClientGroupInfoControl.setCurrentUserID(userId);
        ClientGroupEventControl.setCurrentUserID(userId);
        setContentView(R.layout.activity_canlendar);//绑定界面
        //设置沉浸式界面 - 狗屎方法 - 但是不想改了
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }
        mainContext = CalendarActivity.this;
        //初始化多个页面
        //日历 月视图 周视图 时间轴
        initPage();
        //按钮
        initButton();


        //侧滑菜单实现
        initMenu();
    }

    private  void initPage(){
        viewPager=(ViewPagerSlide) findViewById(R.id.viewpapers);
        inflater=getLayoutInflater();
        initCalendarPage();
        initTimeLinePage();
        initTimeTablePage();
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(calendarPager);
        viewList.add(timeLine);
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
        //日历
    }

    /**
     * 按钮绑定
     */
    private void initButton() {
        //绑定小组页面
        ImageButton group = findViewById(R.id.group);
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, GroupActivity.class);

                startActivity(intent);
            }
        });
        ViewFlipper viewFlipper = findViewById(R.id.flipper);
        ImageButton toTimeLinePageButton = findViewById(R.id.timetableButton);
        toTimeLinePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
                viewFlipper.showNext();
            }
        });
        ImageButton backCalendar = findViewById(R.id.backcalendar);
        backCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                viewFlipper.showNext();
            }
        });
        picker = findViewById(R.id.date);
        final boolean[] monthType = {true, true, false, false, false, false};
        //时间选择器选择年月，对应的日历切换到指定日期
        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("wtf","???");
                TimePickerView pvTime = new TimePickerBuilder(CalendarActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        int year = c.get(java.util.Calendar.YEAR);
                        int month = c.get(java.util.Calendar.MONTH);
                        //滚动到指定日期
                        calendarView.scrollToCalendar(year, month + 1, 1);
                        weekView.scrollToCalendar(year,month+1,1);
                    }
                }).setType(monthType).build();
                pvTime.show();
            }
        });
    }

    /**
     * 个人界面 后期修改
     */
    private void initMenu(){

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setDrawerTheme(
                new DrawerTheme(this)
        );
        addProfile(
                new DrawerProfile()
                        .setRoundedAvatar((BitmapDrawable)getResources().getDrawable(R.mipmap.user))
                        .setBackground(getResources().getDrawable(R.drawable.cv_bg_material))
                        .setName(ClientUserInfoControl.getCurrentUser().getName())
                        .setDescription(getString((R.string.profile_description)))
                        .setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
                            @Override
                            public void onClick(DrawerProfile drawerProfile, long l) {
                                Toast.makeText(CalendarActivity.this,"clicked profile",Toast.LENGTH_SHORT).show();
                            }
                        })
        );
        addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this,R.drawable.setting_new))
                .setTextPrimary(getString(R.string.menu_item_setting))
        );
        addDivider();
        /*
        ImageButton user = findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });*/
    }

    /**
     * 时间轴界面
     * 暂时未加入
     */
    private void initTimeLinePage(){
        timeLine = View.inflate(this,R.layout.timeline_layout,null);
        ImageView eventaddbutton=timeLine.findViewById(R.id.add_event_image);
        timelineRecyclerView=timeLine.findViewById(R.id.event_list);
        timelineRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final SimpleDateFormat timeformat=new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        java.util.Calendar c= java.util.Calendar.getInstance();
        TextView today=timeLine.findViewById(R.id.today);
        today.setText(timeformat.format(c.getTime()));
        eventaddbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CalendarActivity.this,EventAdder.class);
                startActivityForResult(intent,0);
            }
        });
        flushTimeLineListView();
    }
    /**
     * 周视图形式界面，以事件块的形式表现事件
     * ToDo 周切换时 下发时间块随之滑动
     */
    private void initTimeTablePage(){
        timetablePager=View.inflate(this,R.layout.timetable_layout,null);
        clContent = timetablePager.findViewById(R.id.cl_content);
        weekView = timetablePager.findViewById(R.id.calendarView);
        weekView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Calendar calendar, boolean isClick) {

                GregorianCalendar c = new GregorianCalendar(calendar.getYear(),calendar.getMonth()-1,calendar.getDay());
                //Log.d("week",String.valueOf(c.get(java.util.Calendar.WEEK_OF_YEAR)));
               // Log.d("year",String.valueOf(c.get(java.util.Calendar.YEAR)));
                selectYear = calendar.getYear();
                selectMonth = calendar.getMonth();
                selectDay = calendar.getDay();
                selectWeek = c.get(java.util.Calendar.WEEK_OF_YEAR);
                TextView date_text = findViewById(R.id.date);
                date_text.setText(String.format("%d月%d日",selectMonth,selectDay));
                TextView year_text = findViewById(R.id.year);
                year_text.setText(String.valueOf(selectYear));
                TextView des_text=findViewById(R.id.des_date);
                des_text.setText(calendar.getLunar());
                flushTimeTableListView();
            }
        });
    }


    /**
     * 日历界面 主界面
     *
     */
    private void initCalendarPage(){
        calendarPager= View.inflate(this,R.layout.calendar_layout,null);
        calendarView = calendarPager.findViewById(R.id.calendarView);//绑定calendar
        calendarLayout=calendarPager.findViewById(R.id.calendarLayout);
        Log.d("s",calendarLayout.toString());
        //日期选择器

        //添加事件的按钮
        ImageButton btn_add = findViewById(R.id.addbutton);
        calendarRecyclerView = calendarPager.findViewById(R.id.list_one); //绑定listview
        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        calendarRecyclerView.setItemAnimator(new DefaultItemAnimator());
        calendarRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        //UIControl = new TestUserInterfaceControl();
        selectDay = calendarView.getCurDay();
        selectMonth = calendarView.getCurMonth();
        selectYear = calendarView.getCurYear();
        //初始化当前年月
        //月份切换改变事件
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
            }
        });


        calendarView.setOnViewChangeListener(this);
        //日期点击选择事件
        calendarView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Calendar calendar, boolean isClick) {
                GregorianCalendar c = new GregorianCalendar(calendar.getYear(),calendar.getMonth()-1,calendar.getDay());
                selectYear = calendar.getYear();
                selectMonth = calendar.getMonth();
                selectDay = calendar.getDay();
                selectWeek = c.get(java.util.Calendar.WEEK_OF_YEAR);
                TextView date_text = findViewById(R.id.date);
                date_text.setText(String.format("%d月%d日",selectMonth,selectDay));
                TextView year_text = findViewById(R.id.year);
                year_text.setText(String.valueOf(selectYear));
                TextView des_text=findViewById(R.id.des_date);
                des_text.setText(calendar.getLunar());
                flushCalendarListView();
            }
        });
        //为加号绑定此弹出表单
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("adder","touched");
                Intent intent = new Intent(CalendarActivity.this,EventAdder.class);
                Bundle bundle=new Bundle();
                bundle.putInt("selectYear",selectYear);
                bundle.putInt("selectMonth",selectMonth);
                bundle.putInt("selectDay",selectDay);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });
        ImageButton btn_semantice_add=findViewById(R.id.semantic_adder);
        btn_semantice_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this,CreateEventSemanticAnalyze.class);
                Bundle bundle=new Bundle();
                bundle.putInt("selectYear",selectYear);
                bundle.putInt("selectMonth",selectMonth);
                bundle.putInt("selectDay",selectDay);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

    }

    /**
     * 事件列表更新 
     * 进入页面时刷新
     * 返回页面时刷新
     * 日期更换时刷新
     * 
     */
    private void flushView(){
        flushCalendarListView();
        flushTimeTableListView();
        flushTimeLineListView();
    }

    private void flushTimeLineListView(){
        NewTimeLineAdapter timeLineAdapter=new NewTimeLineAdapter(ClientEventControl.getEventList(),this);
        timelineRecyclerView = timeLine.findViewById(R.id.event_list);; //绑定listview
        timelineRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        timelineRecyclerView.setAdapter(timeLineAdapter);
        timeLineAdapter.setonItemClickListener(new NewTimeLineAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View v, int Position) {
                Intent intent=new Intent(CalendarActivity.this,EventDetailActivity.class);
                intent.putExtra("groupevent",ClientEventControl.getEventList().get(Position));
                startActivityForResult(intent,0);
            }
        });
    }
    
    
    private void flushTimeTableListView(){
        int size = 0;
        size += weekEventList==null? 0:weekEventList.size();
        size += weekGroupEventList==null?0:weekGroupEventList.size();
        clContent.removeViews(50,size);
        weekEventList = ClientEventControl.findEventListByWeek(selectYear,selectWeek);
        //weekGroupEventList = ClientEventControl.findGroupEventListByWeek(selectYear,selectWeek);
        weekGroupEventList = ClientEventControl.findGroupEventListByWeek(selectYear,selectWeek);
        clContent=timetablePager.findViewById(R.id.cl_content);
        MyTimeTableUtils myTimeTableUtils = new MyTimeTableUtils(this,clContent,weekEventList,weekGroupEventList);
        myTimeTableUtils.flush();
    }
    private void flushCalendarListView(){
        todayEventList = ClientEventControl.findEventListByDate(selectYear, selectMonth, selectDay);
        todayGroupEventList = ClientEventControl.findGroupEventListByDate(selectYear, selectMonth, selectDay);
        Log.d("num",String.valueOf(todayGroupEventList.size()));
        //todayGroupEventList.add(new GroupEvent(0,1,"白痴","真的傻逼",2019,5,31,9,30));
        adapter = new EventViewAdapter(todayEventList, mainContext,todayGroupEventList); //设置adapter
        calendarRecyclerView .setAdapter(adapter);
        adapter.setonItemClickListener(new EventViewAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View v, int Position) {
                if (adapter.getEventType(Position)==0) {
                    Intent intent = new Intent(CalendarActivity.this, EventDetailActivity.class);
                    intent.putExtra("event", ClientEventControl.findEventListByDate(selectYear, selectMonth, selectDay).get(adapter.getEventIndex(Position)));
                    startActivityForResult(intent, 0);
                }
                else {
                    Intent intent = new Intent(CalendarActivity.this, GroupEventDetailActivity.class);
                    intent.putExtra("groupevent", todayGroupEventList.get(adapter.getEventIndex(Position)));
                    startActivityForResult(intent, 0);
                }
                }
        });
    }
    public void onViewChange(boolean isMonthView) {
        Log.e("onViewChange", "  ---  " + (isMonthView ? "月视图" : "周视图"));
    }

    private void setSchemeDate(){
        Map<String, Calendar> map = ClientEventControl.getDatesHasEvent();
        calendarView.setSchemeDate(map);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        flushView();
        setSchemeDate();
    }
}
