package com.example.daymoon.UserInterface;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventInfomationHolder;
import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.R;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.goodiebag.horizontalpicker.HorizontalPicker;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;

public class AddGroupEventActivity extends AppCompatActivity {
    private int selector= GroupEvent.EVENTTYPE.Default;
    private MaterialEditText title;
    private MaterialEditText location;
    private MaterialEditText description;
    private JellyToggleButton whetherAllDay;
    private TextView startTime;
    private TextView endTime;
    private TextView cancel;
    private TextView complete;
    private Calendar currentTime;
    private ImageView iconEvent;
    private static int SUCCESS_CODE=1;
    private static int FAILURE_CODE=0;
    private GroupEventInfomationHolder groupEventInfomationHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_event);
        title=findViewById(R.id.titleEditView);
        location=findViewById(R.id.location);
        description=findViewById(R.id.description);
        cancel=findViewById(R.id.cancel);
        complete=findViewById(R.id.complete);
        startTime=findViewById(R.id.startdate);
        endTime=findViewById(R.id.enddate);
        iconEvent=findViewById(R.id.icon_event);
        currentTime=Calendar.getInstance();
        groupEventInfomationHolder=new GroupEventInfomationHolder(currentTime);
        whetherAllDay=findViewById(R.id.whetherAllday);
        initView();
        initIconSelector();
    }
    private void initView(){
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //完成新建任务
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupEventInfomationHolder.descriptions=description.getText().toString();
                groupEventInfomationHolder.title=title.getText().toString();
                groupEventInfomationHolder.location=location.getText().toString();
                groupEventInfomationHolder.allMember=true;
                ClientGroupEventControl.createGroupEvent(groupEventInfomationHolder, new HttpRequest.DataCallback() {
                    @Override
                    public void requestSuccess(String result) throws Exception {
                        Intent data=new Intent();
                        if (result.isEmpty()) {
                            setResult(FAILURE_CODE, data);
                        }
                        else {
                            System.out.println(result);
                            data.putExtra("eventID",Integer.parseInt(result));
                            setResult(SUCCESS_CODE, data);
                        }
                        finish();
                    }

                    @Override
                    public void requestFailure(Request request, IOException e) {
                        setResult(FAILURE_CODE);
                        finish();
                    }
                });
            }
        });
        final boolean[] timeType = {true, true, true, true, true, false};
        final SimpleDateFormat BeginTime=new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
        currentTime.set(Calendar.MINUTE,0);
        startTime.setText(BeginTime.format(currentTime.getTime()));
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(AddGroupEventActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        //滚动到指定日期
                        groupEventInfomationHolder.setBeginTime(c);
                        startTime.setText(BeginTime.format(c.getTime()));
                    }
                }).setType(timeType).setDate(currentTime).build();
                pvTime.show();
            }
        });
        currentTime.set(Calendar.MINUTE,59);
        endTime.setText(BeginTime.format(currentTime.getTime()));
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(AddGroupEventActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        groupEventInfomationHolder.setEndTime(c);
                        //滚动到指定日期
                        endTime.setText(BeginTime.format(c.getTime()));
                    }
                }).setType(timeType).setDate(currentTime).build();
                pvTime.show();
            }
        });
        whetherAllDay.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state.equals(State.LEFT)) {
                    groupEventInfomationHolder.allday=false;
                }
                if (state.equals(State.RIGHT)) {
                    groupEventInfomationHolder.allday=true;
                }
            }
        });

    }
    private void setIconEvent(int selector){
        switch (selector){
            case GroupEvent.EVENTTYPE.Default:
                iconEvent.setImageResource(R.mipmap.event_default);
                break;
            case GroupEvent.EVENTTYPE.discussion:
                iconEvent.setImageResource(R.mipmap.discuss);
                break;
            case GroupEvent.EVENTTYPE.game:
                iconEvent.setImageResource(R.mipmap.game);
                break;
            case GroupEvent.EVENTTYPE.eating:
                iconEvent.setImageResource(R.mipmap.eating);
                break;
            case GroupEvent.EVENTTYPE.sports:
                iconEvent.setImageResource(R.mipmap.sports);
                break;
            case GroupEvent.EVENTTYPE.travel:
                iconEvent.setImageResource(R.mipmap.travel);
                break;
            case GroupEvent.EVENTTYPE.lesson:
                iconEvent.setImageResource(R.mipmap.lession);
                break;
        }
    }
    private void initIconSelector(){

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.icon_select_diag, null);
        HorizontalPicker hpImage= (HorizontalPicker) customView.findViewById(R.id.hpicker);
        List<HorizontalPicker.PickerItem> imageItems = new ArrayList<>();
        TextView selectText=customView.findViewById(R.id.selectortext);
        MaterialStyledDialog dialog  = new MaterialStyledDialog.Builder(this)
                .setCustomView(customView)
                .setCancelable(true)
                .setPositiveText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }})
                .setNeutralText("确定")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d("MaterialStyledDialogs", "Do something!");
                        groupEventInfomationHolder.eventType=selector;
                        setIconEvent(selector);
                    }})
                .build();
        imageItems.add(new HorizontalPicker.DrawableItem(R.mipmap.game_white));
        imageItems.add(new HorizontalPicker.DrawableItem(R.mipmap.discuss_white));
        imageItems.add(new HorizontalPicker.DrawableItem(R.mipmap.travel_white));
        imageItems.add(new HorizontalPicker.DrawableItem(R.mipmap.sports_white));
        imageItems.add(new HorizontalPicker.DrawableItem(R.mipmap.eating_white));
        imageItems.add(new HorizontalPicker.DrawableItem(R.mipmap.lession_white));

        hpImage.setItems(imageItems);
        hpImage.setChangeListener(new HorizontalPicker.OnSelectionChangeListener() {
            @Override
            public void onItemSelect(HorizontalPicker horizontalPicker, int i) {
                switch (i){
                    case 0:
                        selector= GroupEvent.EVENTTYPE.game;
                        selectText.setText("游戏");
                        break;
                    case 1:
                        selector= GroupEvent.EVENTTYPE.discussion;
                        selectText.setText("讨论");
                        break;
                    case 2:
                        selector= GroupEvent.EVENTTYPE.travel;
                        selectText.setText("旅游");
                        break;
                    case 3:
                        selector= GroupEvent.EVENTTYPE.sports;
                        selectText.setText("运动");
                        break;
                    case 4:
                        selector= GroupEvent.EVENTTYPE.eating;
                        selectText.setText("聚餐");
                        break;
                    case 5:
                        selector= GroupEvent.EVENTTYPE.lesson;
                        selectText.setText("课程");
                        break;
                }
            }
        });


        iconEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }
}
