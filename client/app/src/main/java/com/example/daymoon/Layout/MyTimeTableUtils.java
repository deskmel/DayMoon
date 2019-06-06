package com.example.daymoon.Layout;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.alamkanak.weekview.WeekView;
import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.GroupInfoManagement.Group;
import com.example.daymoon.R;
import com.example.daymoon.Tool.pxUtils;
import com.example.daymoon.UserInterface.CalendarActivity;
import com.example.daymoon.UserInterface.EventDetailActivity;
import com.example.daymoon.UserInterface.GroupEventDetailActivity;

import java.util.Calendar;

public class MyTimeTableUtils {
    private Context context;
    private ConstraintLayout clContent;
    private EventList weekEventList;
    private GroupEventList weekGroupEventList;
    private int oneHourPx30;//一个小时高度是60dp，转换成像素
    private int oneMimutePx1;//一分钟高度是1dp，转化成像素
    private int oneDayPx;
    private int offset;
    private int width;
    private int padding;


    public MyTimeTableUtils(Context context, ConstraintLayout clContent, EventList weekEventList,GroupEventList weekGroupEventList){
        this.context=context;
        this.clContent = clContent;
        this.weekEventList = weekEventList;
        this.weekGroupEventList = weekGroupEventList;
        initData();
    }

    public void flush(){
        if (weekEventList!=null){
            for (Event event:weekEventList){
                setNewEvent(event);
            }
        }
        if (weekGroupEventList != null){
            for (GroupEvent event:weekGroupEventList){
                setNewGroupEvent(event);
            }
        }
    }

    //放在onCreate里
    private void initData() {
        oneHourPx30 = pxUtils.dip2px(context, 30);
        oneMimutePx1 = pxUtils.dip2px(context,(float) 30./60 );
        //显示控件宽度，不是必须得，假如你的宽度是动态的，然后距离两边多少也可以
        offset = pxUtils.dip2px(context,40);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int dmwidth = dm.widthPixels;
        oneDayPx = (dmwidth-offset)/7;
        padding = pxUtils.dip2px(context,4);
        width = oneDayPx-padding*2;}
    private void setNewGroupEvent(GroupEvent event){
        //生成一个放入的控件
        TextView textView = new TextView(context);
        //给控件赋予一个id
        textView.setId(View.generateViewId());
        //设置控件属性，这个不用照抄，自己喜欢什么弄什么就行，显示什么背景什么都自己定
        textView.setBackground(context.getResources().getDrawable(R.drawable.green_corner_bg));
        textView.setPadding(5,5,5,5);
        textView.setTextColor(context.getResources().getColor(R.color.black));
        textView.setText(event.getTitle());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupEventDetailActivity.class);
                intent.putExtra("groupevent",event);
                context.startActivity(intent);
            }
        });
        //底下获取Calendar，为了拿到一天的时间
        //获取开始时间的Calendar，这个东西很强大，能单独设置年月日时分秒为多少
        java.util.Calendar calendar1 = java.util.Calendar.getInstance();
        calendar1.setTime(event.getBeginTime().getTime());
        //获取结束时间的Calendar
        java.util.Calendar calendar2 = java.util.Calendar.getInstance();
        calendar2.setTime(event.getEndTime().getTime());
        //设置高度，算出他们直接差了多少分钟，然后乘以分钟所占的像素
        int height = (int) ((calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) /
                60000 * oneMimutePx1);
        if (calendar2.get(Calendar.DATE)!= calendar1.get(Calendar.DATE) || calendar1.get(Calendar.MONTH) != calendar1.get(Calendar.MONTH) || calendar1.get(Calendar.MONTH) != calendar2.get(Calendar.MONTH)){
            height = 60 * oneMimutePx1;
            textView.setText(String.format("%s\n%s",event.getTitle(),"跨天"));
        }
        //动态在ConstraintLayout里面加入控件，需要ConstraintSet，使用这个，就必须所有控件都有id
        //这就是上面为什么把ConstraintLayout里所有控件都加了id的原因
        ConstraintSet constraintSet = new ConstraintSet();
        //把显示内容添加进去
        textView.setTextSize(11);
        textView.setTypeface(ResourcesCompat.getFont(context,R.font.msyh));
        clContent.addView(textView);
        //计算距离顶部的高度，很好理解，比如说13点20分，那就是距离0点有13个小时，再加上20分钟的高度
        int marginTop = calendar1.get(java.util.Calendar.HOUR_OF_DAY) * oneHourPx30 +
                calendar1.get(java.util.Calendar.MINUTE) * oneMimutePx1;
        int marginStart = (calendar1.get(java.util.Calendar.DAY_OF_WEEK)-1) * oneDayPx + offset+padding;
        //设置显示内容宽度
        constraintSet.clone(clContent);
        constraintSet.constrainWidth(textView.getId(), width);
        constraintSet.constrainHeight(textView.getId(), height);
        //设置显示位置，这个是水平居中，假如需要偏左偏右，就设置下margin，参考最后一个connect
        constraintSet.connect(textView.getId(),ConstraintSet.START,
                ConstraintSet.PARENT_ID,ConstraintSet.START,marginStart);
        constraintSet.connect(textView.getId(),ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,ConstraintSet.TOP,marginTop);
        //应用
        constraintSet.applyTo(clContent);
    }


    private void setNewEvent(Event event) {
        //生成一个放入的控件
        TextView textView = new TextView(context);
        //给控件赋予一个id
        textView.setId(View.generateViewId());
        //设置控件属性，这个不用照抄，自己喜欢什么弄什么就行，显示什么背景什么都自己定
        textView.setBackground(context.getResources().getDrawable(R.drawable.blue_corner_bg));
        textView.setPadding(5,5,5,5);
        textView.setTextColor(context.getResources().getColor(R.color.black));
        textView.setText(event.getTitle());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("event",event);
                context.startActivity(intent);
            }
        });
        //底下获取Calendar，为了拿到一天的时间
        //获取开始时间的Calendar，这个东西很强大，能单独设置年月日时分秒为多少
        java.util.Calendar calendar1 = java.util.Calendar.getInstance();
        calendar1.setTime(event.getBeginTime().getTime());
        //获取结束时间的Calendar
        java.util.Calendar calendar2 = java.util.Calendar.getInstance();
        calendar2.setTime(event.getEndTime().getTime());
        //设置高度，算出他们直接差了多少分钟，然后乘以分钟所占的像素
        int height = (int) ((calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) /
                60000 * oneMimutePx1);
         if (calendar2.get(Calendar.DATE)!= calendar1.get(Calendar.DATE) || calendar1.get(Calendar.MONTH) != calendar1.get(Calendar.MONTH) || calendar1.get(Calendar.MONTH) != calendar2.get(Calendar.MONTH)){
            height = 60 * oneMimutePx1;
             textView.setText(String.format("%s\n%s",event.getTitle(),"跨天"));
        }
        //动态在ConstraintLayout里面加入控件，需要ConstraintSet，使用这个，就必须所有控件都有id
        //这就是上面为什么把ConstraintLayout里所有控件都加了id的原因
        ConstraintSet constraintSet = new ConstraintSet();
        //把显示内容添加进去
        textView.setTextSize(11);
        textView.setTypeface(ResourcesCompat.getFont(context,R.font.msyh));
        clContent.addView(textView);
        //计算距离顶部的高度，很好理解，比如说13点20分，那就是距离0点有13个小时，再加上20分钟的高度
        int marginTop = calendar1.get(java.util.Calendar.HOUR_OF_DAY) * oneHourPx30 +
                calendar1.get(java.util.Calendar.MINUTE) * oneMimutePx1;
        int marginStart = (calendar1.get(java.util.Calendar.DAY_OF_WEEK)-1) * oneDayPx + offset+padding;
        //设置显示内容宽度
        constraintSet.clone(clContent);
        constraintSet.constrainWidth(textView.getId(), width);
        constraintSet.constrainHeight(textView.getId(), height);
        //设置显示位置，这个是水平居中，假如需要偏左偏右，就设置下margin，参考最后一个connect
        constraintSet.connect(textView.getId(),ConstraintSet.START,
                ConstraintSet.PARENT_ID,ConstraintSet.START,marginStart);
        constraintSet.connect(textView.getId(),ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,ConstraintSet.TOP,marginTop);
        //应用
        constraintSet.applyTo(clContent);
    }

}
