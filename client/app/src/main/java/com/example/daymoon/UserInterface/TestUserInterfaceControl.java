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
            Event today = new Event("today", 0, year, month, date, 0, 0, year, month, date, 1, 1, true);
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
    public int addEvent(Event_information_holder event_info){
        try{
            Event today = new Event(event_info.title, 0, event_info.Year_, event_info.Month_, event_info.Date_, event_info.startHour_, event_info.startMinute_, event_info.Year_, event_info.Month_, event_info.Date_, event_info.endHour_, event_info.endMinute_, event_info.process);
            eventControl.addEvent(today);
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
    public int editEvent(Event_information_holder event_info)
    {
        return 1;
    }


}
