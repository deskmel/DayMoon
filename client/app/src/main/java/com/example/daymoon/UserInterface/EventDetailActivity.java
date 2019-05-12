package com.example.daymoon.UserInterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.R;

public class EventDetailActivity extends AppCompatActivity {
    Event event;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        event =(Event) getIntent().getSerializableExtra("Event");
        TextView eventTitle=(TextView) findViewById(R.id.event_title);
        eventTitle.setText(event.getTitle());

        TextView eventTime= (TextView) findViewById(R.id.event_time);
        eventTime.setText(event.getBeginTime_str());

        TextView eventLastTime= (TextView) findViewById(R.id.event_lasting_time);
        eventLastTime.setText(event.getLastingTime_str());

        TextView eventDescription = (TextView) findViewById(R.id.event_descriptions);
        eventDescription.setText(event.getDescription());


        findViewById(R.id.getback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
