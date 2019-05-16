package com.example.daymoon.UserInterface;

import android.content.Intent;
import android.support.annotation.Nullable;
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
    TextView eventTitle, eventTime, eventLastTime, eventDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        event =(Event) getIntent().getSerializableExtra("event");
        eventTitle=(TextView) findViewById(R.id.event_name);
        eventTime= (TextView) findViewById(R.id.event_time);
        eventLastTime= (TextView) findViewById(R.id.event_lasting_time);
        eventDescription = (TextView) findViewById(R.id.event_descriptions);
        flushEventDetail();

        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EventDetailActivity.this,EditEventActivity.class);;
                intent.putExtra("event",event);
                startActivityForResult(intent,0);
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
                ClientEventControl.deleteEvent(event.getEventID(), getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        setResult(0);
                        finish();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        setResult(0);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            event = (Event) data.getSerializableExtra("event");
            flushEventDetail();
        }
    }

    private void flushEventDetail(){
        eventTitle.setText(event.getTitle());
        eventDescription.setText(event.getDescription());
        eventTime.setText(event.getBeginTime_str());
        eventLastTime.setText(event.getLastingTime_str());
    }
}
