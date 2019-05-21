package com.example.daymoon.UserInterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.daymoon.R;

public class GroupDetailActivity extends AppCompatActivity {
    int groupID;
    RelativeLayout qrCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupID = getIntent().getIntExtra("groupID", 2);
        System.out.println(groupID);
        setContentView(R.layout.activity_group_detail);


        qrCode = findViewById(R.id.qrcode);
        qrCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupDetailActivity.this,QRCodeActivity.class);
                intent.putExtra("groupID",groupID);
                startActivity(intent);
            }
        });


    }
}
