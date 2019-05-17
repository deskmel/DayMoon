package com.example.daymoon.UserInterface;
import android.util.Log;

import com.example.daymoon.Define.Constants;
import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.EventManagement.EventList;

public class TestUserInterfaceControl implements UserInterfaceControl{
    private int UserId;
    /**
     * 单例模式
     */
    private static TestUserInterfaceControl UIControl;
    private ClientEventControl eventControl;

    public TestUserInterfaceControl(){}


    public TestUserInterfaceControl(int UserID){
        UIControl = new TestUserInterfaceControl();
        UIControl.UserId=UserID;
    }

    /**
     *
     * @return 获取UIControl 实例
     */
    public static TestUserInterfaceControl getUIControl()
    {
        if (UIControl==null)
        {
            UIControl=new TestUserInterfaceControl(0
            );
        }
        return UIControl;
    }
    /**
     * @description
     * @param year 年
     * @param month 月
     * @param date 日
     * @return 事件列表
     */
    public EventList getEventlist(int year,int month,int date) {

        //test
        EventList eventList=new EventList();
        try {
            Event today = new Event("today", "shit", 0, year, month, date, 0, 0, year, month, date, 1, 1, true);
            eventList.add(today);

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        return  eventList;
        //
        //return  eventControl.findEventListByDate(year,month,date);
    }


    /**
     *
     * @param event_info 事件信息
     * @return 返回状态 具体定义见 Define.Constants
     */
    public int addEvent(EventInformationHolder event_info){
        try{
            Event today;
            return Constants.NORMAL;
        }
        catch (Exception ex)
        {
            Log.d("?","error");
            return Constants.ERROR;
        }

    }
    public int deleteEvent(int EventID){
        return  1;
    }
    public int editEvent(EventInformationHolder event_info)
    {
        return 1;
    }


}
