package com.example.daymoon.UserInterface;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
// set your own notification sound normally not used
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.example.daymoon.R;

public class NotificationActivity extends BaseActivity{
    private Context context;
    private NotificationManager notificationManager;
    private Notification notification;
    Bitmap bitmap = null;
    private static final int NOTIFICATION_1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context = this;
        //创建图片的Bitmap
        bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.calendar_blue);
        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(context,CalendarActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 ,intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        //标题
        builder.setContentTitle("状态栏通知")
                .setContentText("状态栏会显示一个通知栏的图标")
                .setSubText("丰富你的程序，运行手机多媒体")
                .setTicker("收到Notification信息")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        notification = builder.build();
        notificationManager.notify(NOTIFICATION_1,notification);
    }

}
