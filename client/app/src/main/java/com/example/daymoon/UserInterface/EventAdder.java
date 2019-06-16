package com.example.daymoon.UserInterface;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.daymoon.Adapter.UMExpandLayout;
import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.EventInformationHolder;
import com.example.daymoon.R;
import com.example.daymoon.Tool.NotificationServiceUtil;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;
import com.rengwuxian.materialedittext.MaterialEditText;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class EventAdder extends BaseActivity {

    //页面控件
    private TextView startDate;
    private TextView startTime;
    private TextView endTime;
    private JellyToggleButton jtb_whethercontinue;
    private JellyToggleButton jtb_whetherAllday ;
    private JellyToggleButton jtb_whetherRemind;
    private MaterialEditText LocationView;
    private MaterialEditText TitleView;
    private MaterialEditText DescriptionView;
    private EventInformationHolder eventInformationHolder;
    private UMExpandLayout remindTimeLayout;
    private Intent intent;
    private Bundle bundle;
    private TestUserInterfaceControl UIControl= TestUserInterfaceControl.getUIControl();
    private java.util.Calendar current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_event_adder);
        startDate=findViewById(R.id.startdate);
        startTime=findViewById(R.id.starttime);
        endTime = findViewById(R.id.endtime);
        jtb_whethercontinue=findViewById(R.id.whethercontinue);
        jtb_whetherAllday = findViewById(R.id.whetherAllday);
        jtb_whetherRemind = findViewById(R.id.whetherRemind);
        TitleView =findViewById(R.id.titleEditView);
        DescriptionView = findViewById(R.id.DesEditView);
        LocationView = findViewById(R.id.location);
        remindTimeLayout = findViewById(R.id.remindTime_layout);
        //为表单绑定日期选择工具
        intent=this.getIntent();
        bundle=intent.getExtras();

        //初始化事件的时间
        eventInformationHolder = new EventInformationHolder(bundle.getInt("selectYear"),bundle.getInt("selectMonth"),bundle.getInt("selectDay"));
        UIControl=new TestUserInterfaceControl();
        final SimpleDateFormat dateformat=new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        final SimpleDateFormat Hourformat=new SimpleDateFormat("HH:mm", Locale.CHINA);
        final SimpleDateFormat remindTimeFormat = new SimpleDateFormat("MM/dd HH:mm",Locale.CHINA);
        final boolean[] dateType = {true, true, true, false, false, false};
        final boolean[] timeType = {false, false, false, true, true, false};
        final boolean[] remindTimeType = {false,true,true,true,true,false};
        current = Calendar.getInstance();
        current.set(Calendar.YEAR,bundle.getInt("selectYear"));
        current.set(Calendar.MONTH,bundle.getInt("selectMonth")-1);
        current.set(Calendar.DATE,bundle.getInt("selectDay"));
        startDate.setText(dateformat.format(current.getTime()));
        //开始日期选择工具
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerView pvTime = new TimePickerBuilder(EventAdder.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        int year = c.get(java.util.Calendar.YEAR);
                        int month = c.get(java.util.Calendar.MONTH)+1;
                        int datee = c.get(java.util.Calendar.DATE);
                        //滚动到指定日期

                        startDate.setText(dateformat.format(c.getTime()));
                        eventInformationHolder.Year_ = year;
                        eventInformationHolder.Month_ = month;
                        eventInformationHolder.Date_ = datee;
                    }
                }).setType(dateType).setDate(current).build();
                pvTime.show();
            }
        });
        //为表单绑定时间选择工具
        Calendar beginHour = Calendar.getInstance();
        beginHour.set(Calendar.HOUR_OF_DAY,bundle.get("beginHour")==null?8:bundle.getInt("beginHour"));
        beginHour.set(Calendar.MINUTE,bundle.get("beginMinute")==null?0:bundle.getInt("beginMinute"));
        eventInformationHolder.startHour_=beginHour.get(Calendar.HOUR_OF_DAY);
        eventInformationHolder.startMinute_=beginHour.get(Calendar.MINUTE);
        startTime.setText(Hourformat.format(beginHour.getTime()));
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerView pvTime = new TimePickerBuilder(EventAdder.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(java.util.Calendar.MINUTE);
                        //滚动到指定日期
                        eventInformationHolder.startHour_=hour;
                        eventInformationHolder.startMinute_=minute;
                        startTime.setText(Hourformat.format(c.getTime()));
                    }
                }).setType(timeType).setDate(beginHour).build();
                pvTime.show();
            }
        });
        //结束时间
        Calendar endHour = Calendar.getInstance();
        endHour.set(Calendar.HOUR_OF_DAY,bundle.get("endHour")==null?12:bundle.getInt("endHour"));
        endHour.set(Calendar.MINUTE,bundle.get("endMinute")==null?59:bundle.getInt("endMinute"));
        eventInformationHolder.endHour_=endHour.get(Calendar.HOUR_OF_DAY);
        eventInformationHolder.endMinute_=endHour.get(Calendar.MINUTE);
        endTime.setText(Hourformat.format(endHour.getTime()));
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerView pvTime = new TimePickerBuilder(EventAdder.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(java.util.Calendar.MINUTE);
                        //滚动到指定日期
                        eventInformationHolder.endHour_=hour;
                        eventInformationHolder.endMinute_=minute;
                        endTime.setText(Hourformat.format(c.getTime()));
                    }
                }).setType(timeType).setDate(endHour).build();
                pvTime.show();
            }
        });
        //绑定全天选择按钮
        jtb_whetherAllday.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state.equals(State.LEFT)) {
                    eventInformationHolder.process=false;

                }
                if (state.equals(State.RIGHT)) {
                    eventInformationHolder.process=true;

                }
            }
        });
        //绑定是否提醒按钮
        jtb_whetherRemind.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state.equals(State.LEFT)) {
                    eventInformationHolder.whetherRemind=false;
                    remindTimeLayout.collapse();
                }
                if (state.equals(State.RIGHT)) {
                    eventInformationHolder.whetherRemind=true;
                    remindTimeLayout.expand();
                }
            }
        });
        //绑定提醒时间按钮
        TextView remindTime= findViewById(R.id.remindTime);
        remindTime.setText(remindTimeFormat.format(beginHour.getTime()));
        remindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(EventAdder.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH);
                        int date_ = c.get(Calendar.DATE);
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(java.util.Calendar.MINUTE);
                        //滚动到指定日期
                        GregorianCalendar gregorianCalendar=new GregorianCalendar(year,month,date_,hour,minute);
                        eventInformationHolder.remindTime=gregorianCalendar;
                        remindTime.setText(remindTimeFormat.format(c.getTime()));
                    }
                }).setType(remindTimeType).setDate(beginHour).build();
                pvTime.show();
            }
        });

        //描述content
        LocationView.setText(bundle.get("location")==null?"":bundle.getString("location"));
        TitleView.setText(bundle.get("title")==null?"":bundle.getString("title"));
        DescriptionView.setText(bundle.get("description")==null?"":bundle.getString("description"));
        //绑定取消按钮
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //绑定完成按钮
        findViewById(R.id.complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventInformationHolder.title=TitleView.getText().toString();
                eventInformationHolder.descriptions = DescriptionView.getText().toString();
                eventInformationHolder.location = LocationView.getText().toString();
                if (!eventInformationHolder.whetherRemind) eventInformationHolder.remindTime=null;
                //Log.d("begintime",String.format("%d %d %d %d %d\n",eventInformationHolder.Year_,eventInformationHolder.Month_,eventInformationHolder.Date_,eventInformationHolder.startHour_,eventInformationHolder.startMinute_));
                //Log.d("endtime",String.format("%d %d %d %d %d\n",eventInformationHolder.Year_,eventInformationHolder.Month_,eventInformationHolder.Date_,eventInformationHolder.endHour_,eventInformationHolder.endMinute_));
                ClientEventControl.addEvent(eventInformationHolder, getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        if (eventInformationHolder.whetherRemind)
                        {
                            long time =eventInformationHolder.remindTime.getTimeInMillis()-System.currentTimeMillis();
                            //Log.d("SB", String.valueOf(time));
                            NotificationServiceUtil.invokeTimerNotification(EventAdder.this,time,eventInformationHolder.title,0);
                        }
                        finish(); }
                }, new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        });
    }

}
