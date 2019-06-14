package com.example.daymoon.Tool;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

//import com.coder80.timer.MainActivity;
import com.example.daymoon.R;
import com.example.daymoon.UserInterface.CalendarActivity;

public class TimerNotificationService extends Service implements Runnable {
    private String TAG = TimerNotificationService.class.getSimpleName();
    private String content = "";
    private int id = 1;

    @Override
    public void onCreate(){
        super.onCreate();
        run();
    }

    @Override
    public  int onStartCommand(Intent intent, int flags, int startId){
        try{
            content = intent.getExtras().getString("content");
            id = intent.getExtras().getInt("id");
            uploadPOIInfo();
        } catch (Exception e){
            Log.e("content接受","Error!!!!!!1");
        }
        Log.i("content接受","11111111111111111not running");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private  void uploadPOIInfo(){
        new Thread(this).start();

    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    private  boolean isRunningForeGround (Context context){
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if ( !TextUtils.isEmpty(currentPackageName)
                && currentPackageName.equals(getPackageName()))
            return true;
        return false;
    }

    @Override
    public void run(){
        Log.i("AWSL","好像被调用了");

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nm  =(NotificationManager) getSystemService(ns);
        long when = System.currentTimeMillis();
        Notification.Builder builder = new Notification.Builder(this);
        //标题
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,
                new Intent(this,CalendarActivity.class),0);
        Notification notification;
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
        nm.notify(id,notification);
    }

}

