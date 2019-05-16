package com.example.daymoon.UserInterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.R;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity {
    private JellyToggleButton jtb_whethercontinue;
    private TextView startTimeView,endTimeView,delete,back,complete;
    private MaterialEditText title,description;
    private Event event;
    final SimpleDateFormat BeginTime=new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
    final SimpleDateFormat EndTime=new SimpleDateFormat("HH:mm", Locale.CHINA);
    final boolean[] startTimeType = {true, true, true, true, true, false};
    final boolean[] endTimeType = {false, false, false, true, true, false};
    private Event_information_holder event_information_holder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        event=(Event) getIntent().getSerializableExtra("event");
        event_information_holder=new Event_information_holder(event);
        jtb_whethercontinue=findViewById(R.id.whethercontinue);
        startTimeView=findViewById(R.id.start_time);
        endTimeView=findViewById(R.id.end_time);
        delete=findViewById(R.id.delete_event);
        back=findViewById(R.id.back);
        complete=findViewById(R.id.complete);
        title=findViewById(R.id.title);
        description=findViewById(R.id.DesEditView);
        //初始化页面
        GregorianCalendar beginTime=event.getBeginTime();
        GregorianCalendar endTime=event.getEndTime();
        startTimeView.setText(BeginTime.format(beginTime.getTime()));
        endTimeView.setText(EndTime.format(endTime.getTime()));
        title.setText(event.getTitle());
        description.setText(event.getDescription());


        //绑定时间选择
        startTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(EditEventActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        startTimeView.setText(BeginTime.format(c.getTime()));
                        event_information_holder.Year_=c.get(java.util.Calendar.YEAR);;
                        event_information_holder.Month_=c.get(java.util.Calendar.MONTH)+1;
                        event_information_holder.Date_=c.get(Calendar.DATE);
                        event_information_holder.startHour_=c.get(Calendar.HOUR_OF_DAY);
                        event_information_holder.startMinute_=c.get(Calendar.MINUTE);
                    }
                }).setType(startTimeType).setDate(beginTime).build();
                pvTime.show();
            }
        });
        endTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(EditEventActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        endTimeView.setText(EndTime.format(c.getTime()));
                        event_information_holder.startHour_=c.get(Calendar.HOUR_OF_DAY);
                        event_information_holder.startMinute_=c.get(Calendar.MINUTE);
                    }
                }).setType(endTimeType).setDate(endTime).build();

                pvTime.show();
            }
        });

        //绑定whetherProcess
        jtb_whethercontinue.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state.equals(State.LEFT)) {
                    event_information_holder.process=false;
                }
                if (state.equals(State.RIGHT)) {
                    event_information_holder.process=true;
                }
            }
        });

        //绑定完成界面
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event_information_holder.title=title.getText().toString();
                event_information_holder.descriptions=description.getText().toString();
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
