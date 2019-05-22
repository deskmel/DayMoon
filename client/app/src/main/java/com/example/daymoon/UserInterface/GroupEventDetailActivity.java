package com.example.daymoon.UserInterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.R;

public class GroupEventDetailActivity extends AppCompatActivity {
    private TextView starttime;
    private TextView endtime;
    private TextView back;
    private TextView edit;
    private TextView title;
    private TextView description;
    private GroupEvent event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_event_detail);
        event =(GroupEvent) getIntent().getSerializableExtra("groupevent");
        initView();

    }
    private void initView(){
        title=findViewById(R.id.title);
        back=findViewById(R.id.back);
        edit=findViewById(R.id.edit);
        description=findViewById(R.id.description);
        starttime=findViewById(R.id.starttime);
        endtime=findViewById(R.id.endtime);
        title.setText(event.getTitle());
        description.setText(event.getDescription());
        description.setText(event.getLocation());
        starttime.setText(String.format("%s %s %s","开始",event.getBeginDate(),event.getBeginHour()));
        endtime.setText(String.format("%s %s %s","开始",event.getEndDate(),event.getEndHour()));
    }
}
