package com.example.daymoon.UserInterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.daymoon.GroupInfoManagement.Group;
import com.example.daymoon.R;

public class GroupDetailActivity extends BaseActivity {
    int groupID;
    RelativeLayout qrCode;
    TextView groupname;
    TextView groupMemberNumber;
    TextView groupDescription;
    ImageView back;
    TextView finish;
    private Group group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group=(Group) getIntent().getSerializableExtra("group");
        System.out.println(groupID);
        setContentView(R.layout.activity_group_detail);
        initView();
        qrCode = findViewById(R.id.qrcode);
        qrCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupDetailActivity.this,QRCodeActivity.class);
                intent.putExtra("groupID",group.getGroupID());
                startActivity(intent);
            }
        });
        initButton();
    }


    private void initView(){
        groupname=findViewById(R.id.group_name);
        groupDescription=findViewById(R.id.group_description);
        groupMemberNumber=findViewById(R.id.how_many_group_member);
        back=findViewById(R.id.back);
        finish = findViewById(R.id.finish);
        groupname.setText(group.getGroupName());
        groupDescription.setText(group.getGroupDescription());

        //groupMemberNumber.setText(group.getGroupMember().size());
    }
    private  void initButton(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
