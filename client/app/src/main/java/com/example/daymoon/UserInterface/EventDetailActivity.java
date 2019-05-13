package com.example.daymoon.UserInterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.R;

public class EventDetailActivity extends AppCompatActivity {
    Event event;
    private TestUserInterfaceControl UIControl= TestUserInterfaceControl.getUIControl();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        event =(Event) getIntent().getSerializableExtra("event");
        TextView eventTitle=(TextView) findViewById(R.id.event_name);
        eventTitle.setText(event.getTitle());

        TextView eventTime= (TextView) findViewById(R.id.event_time);
        eventTime.setText(event.getBeginTime_str());

        TextView eventLastTime= (TextView) findViewById(R.id.event_lasting_time);
        eventLastTime.setText(event.getLastingTime_str());

        TextView eventDescription = (TextView) findViewById(R.id.event_descriptions);
        eventDescription.setText(event.getDescription());

        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.getback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.delete_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientEventControl.deleteEvent(event.getEventID(), getApplicationContext(), new Runnable(){
                    @Override
                    public void run() {
                        setResult(0);
                        finish();
                    }
                });
            }
        });
    }
}
