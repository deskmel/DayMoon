package com.example.daymoon.UserInterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.daymoon.GroupEventManagement.GroupEventInfomationHolder;
import com.example.daymoon.R;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddGroupEventActivity extends AppCompatActivity {
    private MaterialEditText title;
    private MaterialEditText location;
    private MaterialEditText description;
    private JellyToggleButton whetherAllDay;
    private TextView startTime;
    private TextView endTime;
    private TextView cancel;
    private TextView complete;
    private Calendar currentTime;
    private GroupEventInfomationHolder groupEventInfomationHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_event);
        title=findViewById(R.id.titleEditView);
        location=findViewById(R.id.location);
        description=findViewById(R.id.description);
        cancel=findViewById(R.id.cancel);
        complete=findViewById(R.id.complete);
        startTime=findViewById(R.id.startdate);
        endTime=findViewById(R.id.enddate);
        currentTime=Calendar.getInstance();
        groupEventInfomationHolder=new GroupEventInfomationHolder(currentTime);
        whetherAllDay=findViewById(R.id.whetherAllday);
        initView();
    }
    private void initView(){
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //完成新建任务
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupEventInfomationHolder.descriptions=description.getText().toString();
                groupEventInfomationHolder.title=title.getText().toString();
                groupEventInfomationHolder.location=location.getText().toString();
                groupEventInfomationHolder.allMember=true;
                finish();
            }
        });
        final boolean[] timeType = {true, true, true, true, true, false};
        final SimpleDateFormat BeginTime=new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
        currentTime.set(Calendar.MINUTE,0);
        startTime.setText(BeginTime.format(currentTime.getTime()));
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(AddGroupEventActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        //滚动到指定日期
                        groupEventInfomationHolder.setBeginTime(c);
                        startTime.setText(BeginTime.format(c.getTime()));
                    }
                }).setType(timeType).setDate(currentTime).build();
                pvTime.show();
            }
        });
        currentTime.set(Calendar.MINUTE,59);
        endTime.setText(BeginTime.format(currentTime.getTime()));
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(AddGroupEventActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        groupEventInfomationHolder.setEndTime(c);
                        //滚动到指定日期
                        endTime.setText(BeginTime.format(c.getTime()));
                    }
                }).setType(timeType).setDate(currentTime).build();
                pvTime.show();
            }
        });
        whetherAllDay.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state.equals(State.LEFT)) {
                    groupEventInfomationHolder.allday=false;
                }
                if (state.equals(State.RIGHT)) {
                    groupEventInfomationHolder.allday=true;
                }
            }
        });


    }
}
