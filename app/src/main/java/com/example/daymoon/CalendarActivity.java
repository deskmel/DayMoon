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
import com.haibin.calendarview.TrunkBranchAnnals;


public class CalendarActivity extends AppCompatActivity implements CalendarView.OnMon {
    private CalendarView calendarView;
    private TextView selectDate;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = (CalendarView)findViewById(R.id.calendarView);
        //
        Calendar currentdate = Calendar.getInstance();

        selectDate.setText(String.valueOf(currentdate.get(Calendar.YEAR))+' '+
                String.valueOf(currentdate.get(Calendar.MONTH))+" "+
                String.valueOf((currentdate.get(Calendar.DATE))));

        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener());
    }




}
