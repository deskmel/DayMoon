package com.example.daymoon.UserInterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.daymoon.R;

public class GroupScheduleActivity extends AppCompatActivity {
    private ImageButton tools;
    private ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_schedule);
        tools=findViewById(R.id.tools);
        back=findViewById(R.id.back);

    }

    private void initButton()
    {
        tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupScheduleActivity.this,GroupDetailActivity.class);
                startActivityForResult(intent,0);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
