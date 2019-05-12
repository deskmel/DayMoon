package com.example.daymoon.UserInterface;
import com.example.daymoon.EventManagement.EventList;

/**
 * UserInterface接口，用于和EventManagement子系统交互
 *
 */

public interface UserInterfaceControl {
    /**
     * 获取某一天的事件列表
     * @param year Year
     * @param month Month
     * @param date Date
     * @return EventList or null
     */
    EventList getEventlist(int year, int month, int date);

    /**
     * 添加事件
     * @param event_info
     * class event_info{
     *     public int Year_;
     *     public int Month_;
     *     public int Date_;
     *     public int startHour_;
     *     public int endHour_;
     *     public int startMinute_;
     *     public int endMinute_;
     *     public boolean allday;
     *     public boolean process;
     *     public String descriptions;
     *     public String title;
     * }
     * @return
     *  Constants.NORMAL or Constants.ERROR
     */
    int addEvent(Event_information_holder event_info);

    /**
     *
     * @param eventID 事件ID
     * @return
     * Constant.ERROR or Constant.NORMAL
     */
    int deleteEvent(int eventID);

    /**
     *
     * @param event_info 事件信息
     * @return
     * Constant.ERROR or Constant.NORMAL
     */
    int editEvent(Event_information_holder event_info);







}
