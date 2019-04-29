package com.example.daymoon;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import java.util.Calendar

import com.haibin.calendarview.CalendarView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;


public class CalendarActivity extends AppCompatActivity{
    private CalendarView calendarView;
    private TextView selectDate;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = (CalendarView)findViewById(R.id.calendarView);
        //
        Calendar currentdate = Calendar.getInstance();
        calendarView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(com.haibin.calendarview.Calendar calendar, boolean isClick) {

            }
        });

    }




}
