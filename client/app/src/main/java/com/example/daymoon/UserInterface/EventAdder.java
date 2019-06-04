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
import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.EventInformationHolder;
import com.example.daymoon.R;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;
import com.rengwuxian.materialedittext.MaterialEditText;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventAdder extends BaseActivity {

    //页面控件
    private TextView startDate;
    private TextView startTime;
    private TextView endTime;
    private JellyToggleButton jtb_whethercontinue;
    private JellyToggleButton jtb_whetherAllday ;
    private MaterialEditText TitleView;
    private MaterialEditText DescriptionView;
    private EventInformationHolder eventInformationHolder;
    private Intent intent;
    private Bundle bundle;
    private TestUserInterfaceControl UIControl= TestUserInterfaceControl.getUIControl();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_event_adder);
        startDate=findViewById(R.id.startdate);
        startTime=findViewById(R.id.starttime);
        endTime = findViewById(R.id.endtime);
        jtb_whethercontinue=findViewById(R.id.whethercontinue);
        jtb_whetherAllday = findViewById(R.id.whetherAllday);
        TitleView =findViewById(R.id.titleEditView);
        DescriptionView = findViewById(R.id.DesEditView);
        //为表单绑定日期选择工具
        intent=this.getIntent();
        bundle=intent.getExtras();

        //初始化事件的时间
        eventInformationHolder = new EventInformationHolder(bundle.getInt("selectYear"),bundle.getInt("selectMonth"),bundle.getInt("selectDay"));
        UIControl=new TestUserInterfaceControl();

        final SimpleDateFormat dateformat=new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        final SimpleDateFormat Hourformat=new SimpleDateFormat("HH:mm", Locale.CHINA);
        final boolean[] dateType = {true, true, true, false, false, false};
        final boolean[] timeType = {false, false, false, true, true, false};
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
                }).setType(dateType).build();
                pvTime.show();
            }
        });
        //为表单绑定时间选择工具
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerView pvTime = new TimePickerBuilder(EventAdder.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        int hour = c.get(java.util.Calendar.HOUR);
                        int minute = c.get(java.util.Calendar.MINUTE);
                        //滚动到指定日期
                        eventInformationHolder.startHour_=hour;
                        eventInformationHolder.startMinute_=minute;
                        startTime.setText(Hourformat.format(c.getTime()));
                    }
                }).setType(timeType).build();
                pvTime.show();
            }
        });
        //结束时间
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerView pvTime = new TimePickerBuilder(EventAdder.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        int hour = c.get(java.util.Calendar.HOUR);
                        int minute = c.get(java.util.Calendar.MINUTE);
                        //滚动到指定日期
                        eventInformationHolder.endHour_=hour;
                        eventInformationHolder.endMinute_=minute;
                        endTime.setText(Hourformat.format(c.getTime()));
                    }
                }).setType(timeType).build();
                pvTime.show();
            }
        });
        //绑定全天选择按钮
        jtb_whetherAllday.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state.equals(State.LEFT)) {
                    eventInformationHolder.allday=false;
                }
                if (state.equals(State.RIGHT)) {
                    eventInformationHolder.allday=true;
                }
            }
        });
        //绑定是否process按钮
        jtb_whethercontinue.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
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
        //
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
                Log.d("begintime",String.format("%d %d %d %d %d\n",eventInformationHolder.Year_,eventInformationHolder.Month_,eventInformationHolder.Date_,eventInformationHolder.startHour_,eventInformationHolder.startMinute_));
                Log.d("endtime",String.format("%d %d %d %d %d\n",eventInformationHolder.Year_,eventInformationHolder.Month_,eventInformationHolder.Date_,eventInformationHolder.endHour_,eventInformationHolder.endMinute_));

                ClientEventControl.addEvent(eventInformationHolder, getApplicationContext(), new Runnable() {
                    @Override
                    public void run() { finish(); }
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
