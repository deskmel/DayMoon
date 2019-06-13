package com.example.daymoon.UserInterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.daymoon.R;
import com.rengwuxian.materialedittext.MaterialEditText;

public class CreateEventSemanticAnalyze extends AppCompatActivity {
    private MaterialEditText materialEditText;
    String content;
    private Intent intent;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_semantic_analyze);
        intent=this.getIntent();
        bundle=intent.getExtras();
        initView();
        initButton();
    }
    private void initView(){
        materialEditText = findViewById(R.id.contentEdit);
    }
    private void initButton(){
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageButton ok = findViewById(R.id.right);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventSemanticAnalyze.this, EventAdder.class);
                bundle.putInt("beginHour",8);
                bundle.putInt("beginMinute",0);
                bundle.putInt("endHour",12);
                bundle.putInt("endMinute",20);
                bundle.putString("description","吃喝嫖赌样样精通");
                bundle.putString("title","玩吧");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }



}
