package EventManagement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class Event implements Comparable<Event> {
    String description;
    int eventID;
    GregorianCalendar beginTime;
    GregorianCalendar endTime;
    boolean whetherProcess;
    // Reminder reminder;

    public Event(String str, int eID, int beginYear, int beginMonth, int beginDate, int beginHour, int beginMin,
          int endYear, int endMonth, int endDate, int endHour, int endMin, boolean wProcess) throws Exception{

        if (validEventInfo(str, beginYear, beginMonth, beginDate, beginHour, beginMin, endYear, endMonth, endDate, endHour, endMin)) {
            description = str;
            eventID = eID;
            beginTime = new GregorianCalendar(beginYear, beginMonth - 1, beginDate, beginHour, beginMin);
            endTime = new GregorianCalendar(endYear, endMonth - 1, endDate, endHour, endMin);
            whetherProcess = wProcess;
        }
        else{
            throw new Exception("Fail to construct event");
        }
    }



    // 默认当天的年月日初始化
    public Event(String str, int eID, int beginHour, int beginMin,
          int endHour, int endMin, boolean wProcess) throws Exception{

        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int date = today.get(Calendar.DATE);
        if (validEventInfo(str, year, month + 1, date, beginHour, beginMin, year, month + 1, date, endHour, endMin)) {
            description = str;
            eventID = eID;
            beginTime = new GregorianCalendar(year, month, date, beginHour, beginMin);
            endTime = new GregorianCalendar(year, month, date, endHour, endMin);
            whetherProcess = wProcess;
        }
        else{
            throw new Exception("Fail to construct event");
        }

    }



    // 检查event信息合法性: 日期和时间的格式，字符串<=100个字
    public boolean validEventInfo(String description, int beginYear, int beginMonth, int beginDate, int beginHour, int beginMin,
                                  int endYear, int endMonth, int endDate, int endHour, int endMin){
        try {
            String date1Str = String.valueOf(beginYear) + '-' + String.valueOf(beginMonth) + '-' + String.valueOf(beginDate);
            String date2Str = String.valueOf(endYear) + '-' + String.valueOf(endMonth) + '-' + String.valueOf(endDate);
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            format.parse(date1Str);
            format.parse(date2Str);

            if ( (beginHour >=0 && beginHour <= 23 ) && (endHour >=0 && endHour <= 23 )
                    && (beginMin >=0 && beginMin <= 59) && (endMin >= 0 && endMin <= 59)
                    && description.length() <= 100
                    && endAfterBegin(beginYear, beginMonth, beginDate, beginHour, beginMin, endYear, endMonth, endDate, endHour, endMin)){

                return true;
            }
            else{
                System.out.println("Invalid time format");
            }

        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Invalid date format");
        }

        return false;
    }



    // 辅助函数，判断beginTime是否先于endTime
    public boolean endAfterBegin(int beginYear, int beginMonth, int beginDate, int beginHour, int beginMin,
                                 int endYear, int endMonth, int endDate, int endHour, int endMin){

        if (endYear > beginYear){
            return true;
        }
        else if(endYear == beginYear){
            if (endMonth > beginMonth){
                return true;
            }
            else if (endMonth == beginMonth){
                if (endDate > beginDate){
                    return true;
                }
                else if (endDate == beginDate){
                    if (endHour > beginHour){
                        return true;
                    }
                    else if (endHour == beginHour){
                        if (endMin >= beginMin){
                            return true;
                        }
                    }
                }
            }
        }

        return false;

    }



    public String getDescription(){
        return description;
    }



    public GregorianCalendar getBeginTime(){
        return beginTime;
    }



    public GregorianCalendar getEndTime(){
        return endTime;
    }



    public int getEventID(){
        return eventID;
    }



    public boolean getWhetherProcess(){
        return whetherProcess;
    }



    public void setDescription(String description) {
        this.description = description;
    }



    // 最好不更改
    public void setEventID(int eventID){
        this.eventID = eventID;
    }



    public void setBeginTime(GregorianCalendar beginTime){
        this.beginTime = beginTime;
    }



    public void setEndTime(GregorianCalendar endTime){
        this.endTime = endTime;
    }



    public void setWhetherProcess(boolean whetherProcess){
        this.whetherProcess = whetherProcess;
    }



    // 重载比较函数
    @Override
    public int compareTo(Event otherEvent) {
        return new Integer(this.getEventID()).compareTo(otherEvent.getEventID());
    }



    // 测试
    public static void main(String[] args){

        try {
            Event ev1 = new Event("xx", 0, 2019, 2, 30, 17, 61, 2019, 4, 32, 0, 0, true);
            Event ev2 = new Event("xx", 1, 17, 61, 0, 0, true);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}





