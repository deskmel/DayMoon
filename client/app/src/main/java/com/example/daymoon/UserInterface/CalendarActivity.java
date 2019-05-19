package com.example.daymoon.UserInterface;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.app.AlertDialog;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.Event;
import static com.example.daymoon.Define.Constants.SERVER_IP;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.GroupInfoManagement.ClientGroupInfoControl;
import com.example.daymoon.R;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.heinrichreimersoftware.materialdrawer.DrawerActivity;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import com.heinrichreimersoftware.materialdrawer.theme.DrawerTheme;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

public class CalendarActivity extends DrawerActivity implements CalendarView.OnViewChangeListener{

    private Map<String, Calendar> map = new HashMap<>();
    private CalendarView calendarView;
    private CalendarLayout calendarLayout;
    private LinearLayout picker;//日期选择器
    private TextView tvMonth;
    private RecyclerView recyclerView;
    private Context mainContext = null;
    private EventViewAdapter adapter = null;
    private Button btn_add;//添加事件的按钮
    private AlertDialog alert = null;//提醒框
    private ImageButton user,more,group;
    //private TestUserInterfaceControl UIControl= TestUserInterfaceControl.getUIControl();
    private int selectYear;
    private int selectMonth;
    private int selectDay;
    private EventList todayEventList;
    private GestureDetector mDetector;
    private int userId;
    //String[] dis={"哈皮","sb戴哥邀请跑步","贯神之狼","李晓肝甲甲","超哥归寝"};
    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,dis);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ClientEventControl.setCurrentUserID(1);//TODO 替换为由登录界面传递过来的ID
        ClientEventControl.getEventListFromServer(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
        ClientGroupInfoControl.setCurrentUserID(1);
        setContentView(R.layout.activity_canlendar);//绑定界面
        calendarView = findViewById(R.id.calendarView);//绑定calendar
        calendarLayout=findViewById(R.id.calendarLayout);
        picker = findViewById(R.id.picker);//时间选择器
        tvMonth = findViewById(R.id.tv_month);//textview
        btn_add = findViewById(R.id.addbutton);
        mainContext = CalendarActivity.this;
        user=findViewById(R.id.user);
        more=findViewById(R.id.more);
        group=findViewById(R.id.group);

        recyclerView = findViewById(R.id.list_one); //绑定listview
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


        //UIControl = new TestUserInterfaceControl();
        selectDay = calendarView.getCurDay();
        selectMonth = calendarView.getCurMonth();
        selectYear = calendarView.getCurYear();
        initData();
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
        //周视图和月视图转换
        /*
        calendarLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });*/

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
        //绑定小组页面
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CalendarActivity.this,GroupActivity.class);
                startActivity(intent);
            }
        });




        //绑定more操作
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //侧滑菜单实现
        initMenu();
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
                    .setImage(ContextCompat.getDrawable(this,R.drawable.group))
                    .setTextPrimary(getString(R.string.menu_item_group))
        );
        addDivider();
        addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this,R.drawable.setting))
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
    private void flushListView(){
        todayEventList = ClientEventControl.findEventListByDate(selectYear, selectMonth, selectDay);
        adapter = new EventViewAdapter(todayEventList, mainContext); //设置adapter
        recyclerView.setAdapter(adapter);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        flushListView();
    }

    private void initData() {
        map =ClientEventControl.getDatesHasEvent();
        calendarView.setSchemeDate(map);
    }

    private Calendar getSchemeCalendar(int year, int month, int day, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setScheme(text);
        return calendar;
    }
}
