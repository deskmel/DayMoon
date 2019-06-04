package com.example.daymoon.UserInterface;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupInfoManagement.ClientGroupInfoControl;

import com.example.daymoon.LocalDatabase.LocalDatabaseHelper;

import com.example.daymoon.Layout.ViewPagerSlide;

import com.example.daymoon.R;
import com.example.daymoon.Tool.StatusBarUtil;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import android.view.GestureDetector;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class CalendarActivity extends DrawerActivity implements CalendarView.OnViewChangeListener{

    private Map<String, Calendar> map = new HashMap<>();
    private CalendarView calendarView;
    private CalendarLayout calendarLayout;
    private LinearLayout picker;//日期选择器
    private TextView tvMonth;
    private RecyclerView calendarRecyclerView;
    private RecyclerView timelineRecyclerView;
    private Context mainContext = null;
    private EventViewAdapter adapter = null;
    private Button btn_add;//添加事件的按钮
    private AlertDialog alert = null;//提醒框
    private ViewPagerSlide viewPager;
    private ImageButton user,calendarButton,group;
    //private TestUserInterfaceControl UIControl= TestUserInterfaceControl.getUIControl();
    private int selectYear;
    private int selectMonth;
    private int selectDay;
    private EventList todayEventList;
    private GestureDetector mDetector;
    private LayoutInflater inflater;
    private View calendarPager;
    private View timeLine;
    private View timetablePager;
    private int userId;
    private ArrayList<View> viewList;
    private Cursor cursor;
    private SQLiteDatabase db;
    private ImageButton toTimeLinePageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getIntExtra("userid",-1);
        if (userId<0) System.out.println("no userID given");
        ClientEventControl.setCurrentUserID(userId);//TODO 替换为由登录界面传递过来的ID
        ClientEventControl.getEventListFromServer(new Runnable() {
            @Override
            public void run() {

                setSchemeDate();
                flushListView();
            }
        });
        ClientGroupInfoControl.setCurrentUserID(userId);
        ClientGroupEventControl.setCurrentUserID(userId);
        setContentView(R.layout.activity_canlendar);//绑定界面
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }
        mainContext = CalendarActivity.this;
        user=findViewById(R.id.user);

        calendarButton=findViewById(R.id.calendarButton);
        toTimeLinePageButton = findViewById(R.id.timelineButton);
        group=findViewById(R.id.group);
        //ViewPager
        viewPager=(ViewPagerSlide) findViewById(R.id.viewpapers);
        inflater=getLayoutInflater();
        initCalendarPage();
        initTimeLinePage();
        initTimeTablePage();
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(calendarPager);
        viewList.add(timeLine);
        //viewList.add(timetablePager);

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
        //按钮
        initButton();
        //侧滑菜单实现
        initMenu();
    }
    private void initButton(){
        //绑定小组页面
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CalendarActivity.this,GroupActivity.class);
                startActivity(intent);
            }
        });

        toTimeLinePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
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
                        .setName(getString(R.string.profile_name))
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
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });
    }

    /**
     * 时间轴界面
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
        flushTimeViewList();
    }
    private void flushTimeViewList(){
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

    /**
     * 未完成 周视图形式界面，以事件块的形式表现事件
     */
    private void initTimeTablePage(){
        timetablePager=View.inflate(this,R.layout.timetable_layout,null);

        WeekView mWeekView = (WeekView) timetablePager.findViewById(R.id.weekView);

        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {

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
        picker = calendarPager.findViewById(R.id.picker);//时间选择器
        tvMonth = calendarPager.findViewById(R.id.tv_month);//textview
        btn_add = calendarPager.findViewById(R.id.addbutton);
        calendarRecyclerView = calendarPager.findViewById(R.id.list_one); //绑定listview
        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        calendarRecyclerView.setItemAnimator(new DefaultItemAnimator());
        calendarRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        //UIControl = new TestUserInterfaceControl();
        selectDay = calendarView.getCurDay();
        selectMonth = calendarView.getCurMonth();
        selectYear = calendarView.getCurYear();
        //初始化当前年月
        tvMonth.setText(calendarView.getCurYear() + "年" + calendarView.getCurMonth() + "月");
        //月份切换改变事件
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                tvMonth.setText(year + "年" + month + "月");
            }
        });
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
                    }
                }).setType(monthType).build();
                pvTime.show();
            }
        });

        calendarView.setOnViewChangeListener(this);
        //日期点击选择事件
        calendarView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Calendar calendar, boolean isClick) {
                selectYear = calendar.getYear();
                selectMonth = calendar.getMonth();
                selectDay = calendar.getDay();
                flushListView();
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

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        flushListView();
        setSchemeDate();
    }


    private void flushListView(){
        todayEventList = ClientEventControl.findEventListByDate(selectYear, selectMonth, selectDay);
        adapter = new EventViewAdapter(todayEventList, mainContext); //设置adapter
        calendarRecyclerView .setAdapter(adapter);
        adapter.setonItemClickListener(new EventViewAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View v, int Position) {
                Intent intent = new Intent(CalendarActivity.this,EventDetailActivity.class);
                intent.putExtra("event",ClientEventControl.findEventListByDate(selectYear, selectMonth, selectDay).get(Position));
                startActivityForResult(intent, 0);
            }
        });
    }
    public void onViewChange(boolean isMonthView) {
        Log.e("onViewChange", "  ---  " + (isMonthView ? "月视图" : "周视图"));
    }

    private void setSchemeDate(){
        map =ClientEventControl.getDatesHasEvent();
        calendarView.setSchemeDate(map);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

}
