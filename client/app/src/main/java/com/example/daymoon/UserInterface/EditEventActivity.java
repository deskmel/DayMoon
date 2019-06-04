
package com.example.daymoon.UserInterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.EventManagement.EventInformationHolder;
import com.example.daymoon.R;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class EditEventActivity extends BaseActivity {
    private JellyToggleButton jtb_whethercontinue;
    private TextView startTimeView,endTimeView,delete,back,complete;
    private MaterialEditText title,description;
    private Event event;
    final SimpleDateFormat BeginTime=new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
    final SimpleDateFormat EndTime=new SimpleDateFormat("HH:mm", Locale.CHINA);
    final boolean[] startTimeType = {true, true, true, true, true, false};
    final boolean[] endTimeType = {false, false, false, true, true, false};
    Intent intent;

    private EventInformationHolder eventInformationHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        event=(Event) getIntent().getSerializableExtra("event");

        //先前遗留问题
        eventInformationHolder=new EventInformationHolder(event);
        eventInformationHolder.Month_ += 1;

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
                        eventInformationHolder.Year_ = c.get(java.util.Calendar.YEAR);
                        eventInformationHolder.Month_ = c.get(java.util.Calendar.MONTH)+1;
                        eventInformationHolder.Date_ = c.get(Calendar.DATE);
                        eventInformationHolder.startHour_ = c.get(Calendar.HOUR);
                        eventInformationHolder.startMinute_ = c.get(Calendar.MINUTE);
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
                        eventInformationHolder.startHour_=c.get(Calendar.HOUR);
                        eventInformationHolder.startMinute_=c.get(Calendar.MINUTE);
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
                    eventInformationHolder.process=false;
                }
                if (state.equals(State.RIGHT)) {
                    eventInformationHolder.process=true;
                }
            }
        });

        //绑定完成界面
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventInformationHolder.title=title.getText().toString();
                eventInformationHolder.descriptions=description.getText().toString();
                ClientEventControl.editEvent(event.getEventID(), eventInformationHolder, getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            event = new Event(eventInformationHolder.title, eventInformationHolder.descriptions, event.getEventID(), eventInformationHolder.Year_, eventInformationHolder.Month_, eventInformationHolder.Date_, eventInformationHolder.startHour_, eventInformationHolder.startMinute_, eventInformationHolder.Year_, eventInformationHolder.Month_, eventInformationHolder.Date_, eventInformationHolder.endHour_, eventInformationHolder.endMinute_, eventInformationHolder.process);
                        } catch (Exception e) {
                            finish();
                        }
                        Intent intent = new Intent(EditEventActivity.this, EventDetailActivity.class);
                        intent.putExtra("event", event);
                        setResult(1, intent);
                        finish();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
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