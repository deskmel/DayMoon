package com.example.daymoon.LocalDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.GroupInfoManagement.ClientGroupInfoControl;
import com.example.daymoon.GroupInfoManagement.Group;
import com.example.daymoon.GroupInfoManagement.GroupList;
import com.example.daymoon.HttpUtil.CalendarSerializer;
import com.example.daymoon.HttpUtil.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import okhttp3.Request;

import static com.example.daymoon.Define.Constants.SERVER_IP;

public class LocalDatabaseHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "DayMoonLocal"; // the name of our database
    private static final int DB_VERSION = 2; // the version of the database
    private static int currentUserID;

    public static void setCurrentUserID(int currentUserID) {
        LocalDatabaseHelper.currentUserID = currentUserID;
    }

    public LocalDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       ;
    }
    /**
     * 判断某张表是否存在
     * @param db
     * @param tableName
     * @return
     */
    public boolean tableExist(SQLiteDatabase db, String tableName){
        boolean result = false;
        if(tableName == null){
            return false;
        }
        Cursor cursor;
        try {
            String sql = "select count(*) as c from sqlite_master  where type ='table' and name ='"+tableName.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count > 0){
                    result = true;
                }
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Error occurs in tableExist");
        }
        return result;
    }

    /**
     * 插入一行event
     * @param db
     * @param event
     */
    private void insertEvent(SQLiteDatabase db, Event event) {
        ContentValues eventValues = new ContentValues();

        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        eventValues.put("eventID", event.getEventID());
        eventValues.put("eventName", event.getTitle());
        eventValues.put("description", event.getDescription());
        eventValues.put("beginTime", formatter.format(event.getBeginTime().getTime()));
        eventValues.put("endTime", formatter.format(event.getEndTime().getTime()));
        eventValues.put("whetherProcess", event.getWhetherProcess());
        eventValues.put("remind",0); //存疑
        db.insert("events" + String.valueOf(currentUserID), null, eventValues);
    }

    /**
     * 修改一行已有eventID的event
     * @param db
     * @param event
     */
    private void updateEvent(SQLiteDatabase db, Event event) {
        ContentValues eventValues = new ContentValues();

        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        eventValues.put("eventID", event.getEventID());
        eventValues.put("eventName", event.getTitle());
        eventValues.put("description", event.getDescription());
        eventValues.put("beginTime", formatter.format(event.getBeginTime().getTime()));
        eventValues.put("endTime", formatter.format(event.getEndTime().getTime()));
        eventValues.put("whetherProcess", event.getWhetherProcess());
        eventValues.put("remind",0); //存疑
        db.update("events" + String.valueOf(currentUserID),eventValues,"eventID=?",new String[]{String.valueOf(event.getEventID())});
    }

    /**
     * 同步events（前端调用）
     */
    public void syncEvents(EventList eventList){
        SQLiteDatabase db = getWritableDatabase();
        //判断表events是否存在
        if (!tableExist(db, "events" + String.valueOf(currentUserID))) {
            String SQL_CREATE_EVENTS = "create table events"+ String.valueOf(currentUserID) +
                    "(eventID INTEGER PRIMARY KEY, " +
                    "eventName CHAR(50), " +
                    "description VARCHAR(500), " +
                    "beginTime CHAR(50), " +
                    "endTime CHAR(50)," +
                    "whetherProcess INTEGER," +
                    "remind INTEGER)";

            db.execSQL(SQL_CREATE_EVENTS);
            for (Event event : eventList){
                insertEvent(db, event);
            }
        }
        else{//如果不存在考虑修改
            if (eventList == null){
                System.out.print("eventList is empty!");
            }
            else {
                Cursor cursor = null;
                for (Event event : eventList) {
                    cursor = db.query("events" + String.valueOf(currentUserID),
                            new String[]{"eventName"},
                            "eventID=?",
                            new String[]{String.valueOf(event.getEventID())},
                            null, null, null);
                    if (cursor.moveToFirst()) {
                        updateEvent(db, event);
                    } else {
                        insertEvent(db, event);
                    }
                }
                cursor.close();
            }
        }
    }

    /**
     * 查询当前user的个人event，返回一个eventList（前端调用）
     * @return
     */
    public EventList queryEventList(){
        SQLiteDatabase db = getWritableDatabase();
        EventList eventList = new EventList();
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.CHINA);

        Cursor cursor = null;
        if (tableExist(db, "events"+ String.valueOf(currentUserID) )) {
            cursor = db.rawQuery("SELECT * FROM events" + String.valueOf(currentUserID), null);
            while (cursor.moveToNext()) {
                try {
                    //整理取到的表格信息。出错可能性较大
                    int eventID = cursor.getInt(0);
                    String eventName = cursor.getString(1);
                    String description = cursor.getString(2);
                    Date date1 = formatter.parse(cursor.getString(3));
                    date1 = new Date(date1.getTime());
                    GregorianCalendar beginTime = new GregorianCalendar();
                    beginTime.setTime(date1);
                    Date date2 = formatter.parse(cursor.getString(4));
                    date2 = new Date(date2.getTime());
                    GregorianCalendar endTime = new GregorianCalendar();
                    endTime.setTime(date2);
                    int whetherProcess = cursor.getInt(5);

                    eventList.add( new Event(eventID, eventName, description, beginTime, endTime, whetherProcess));

                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Invalid date format in queryEventList");
                }
            }
            cursor.close();
        }
        else{
            System.out.println("There is no table called events" + String.valueOf(currentUserID));
        }

        return eventList;

    }

    /**
     * 插入一行group
     * @param db
     * @param group
     */
    private void insertGroup(SQLiteDatabase db, Group group){
        ContentValues groupValues = new ContentValues();

        Gson gson = new Gson();
        String memberIDs = gson.toJson(group.getMemberIDs());
        String eventIDS = gson.toJson(group.getEventIDs());
        groupValues.put("groupID", group.getGroupID());
        groupValues.put("groupName", group.getGroupName());
        groupValues.put("description", group.getGroupDescription());
        groupValues.put("memberIDs", memberIDs);
        groupValues.put("eventIDs", eventIDS);
        groupValues.put("leaderID", group.getLeaderID());
        groupValues.put("imgName", group.getImgName());
        db.insert("groups" + String.valueOf(currentUserID), null, groupValues);

    }

    /**
     * 修改一行已有groupID的group
     * @param db
     * @param group
     */
    private void updateGroup(SQLiteDatabase db, Group group) {
        ContentValues groupValues = new ContentValues();

        Gson gson = new Gson();
        String memberIDs = gson.toJson(group.getMemberIDs());
        String eventIDS = gson.toJson(group.getEventIDs());
        groupValues.put("groupID", group.getGroupID());
        groupValues.put("groupName", group.getGroupName());
        groupValues.put("description", group.getGroupDescription());
        groupValues.put("memberIDs", memberIDs);
        groupValues.put("eventIDs", eventIDS);
        groupValues.put("leaderID", group.getLeaderID());
        groupValues.put("imgName", group.getImgName());
        db.update("groups" + String.valueOf(currentUserID),groupValues,"groupID=?",new String[]{String.valueOf(group.getGroupID())});
    }

    /**
     * 同步groups（前端调用）
     */
    public void syncGroups(GroupList groupList){
        SQLiteDatabase db = getWritableDatabase();
        //判断表groups是否存在
        if (!tableExist(db, "groups" + String.valueOf(currentUserID))) {
            String SQL_CREATE_GROUPS = "create table groups" + String.valueOf(currentUserID) +
                    "(groupID INTEGER PRIMARY KEY," +
                    "groupName CHAR(50)," +
                    "description  VARCHAR(500) ," +
                    "memberIDs VARCHAR(1000) ," +
                    "eventIDs VARCHAR(1000) ," +
                    "leaderID INTEGER," +
                    "imgName VARCHAR(50))";
            db.execSQL(SQL_CREATE_GROUPS);

            for (Group group : groupList){
                insertGroup(db,group);
            }
        }
        else{
            if (groupList == null){
                System.out.print("groupList is empty!");
            }
            else {
                Cursor cursor = null;
                for (Group group : groupList) {
                    cursor = db.query("groups" + String.valueOf(currentUserID),
                            new String[]{"groupName"},
                            "groupID=?",
                            new String[]{String.valueOf(group.getGroupID())},
                            null, null, null);
                    if (cursor.moveToFirst()) {
                        updateGroup(db, group);
                    } else {
                        insertGroup(db, group);
                    }
                }
                cursor.close();
            }
        }
    }

    /**
     * 查询当前user的个人所有group，返回一个groupList（前端调用）
     * @return
     */
    public GroupList queryGroupList(){
        SQLiteDatabase db = getWritableDatabase();
        GroupList groupList = new GroupList();
        Gson gson = new Gson();

        Cursor cursor = null;
        if (tableExist(db, "groups"+ String.valueOf(currentUserID) )) {
            cursor = db.rawQuery("SELECT * FROM groups" + String.valueOf(currentUserID), null);
            while (cursor.moveToNext()) {
                try {
                    //整理取到的表格信息。出错可能性较大
                    int groupID = cursor.getInt(0);
                    String groupName = cursor.getString(1);
                    String description = cursor.getString(2);
                    int [] memberIDs = gson.fromJson(cursor.getString(3), int[].class);
                    int [] eventIDs = gson.fromJson(cursor.getString(4), int[].class);
                    int leaderID = cursor.getInt(5);
                    String imgName = cursor.getString(6);
                    groupList.add(new Group(groupID, groupName, description, memberIDs, eventIDs, leaderID, imgName));

                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Error in queryGroupList");
                }
            }
        }
        else{
            System.out.println("There is no table called groups" + String.valueOf(currentUserID));
        }

        cursor.close();
        return groupList;
    }

    /**
     * 插入一行groupEvent
     * @param db
     * @param groupEvent
     */
    private void insertGroupEvent(SQLiteDatabase db, GroupEvent groupEvent){
        ContentValues groupEventValues = new ContentValues();

        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Gson gson = new Gson();
        groupEventValues.put("eventID", groupEvent.getEventID());
        groupEventValues.put("groupID", groupEvent.getGroupID());
        groupEventValues.put("dutyUserIDs", gson.toJson(groupEvent.getMemberID()));
        groupEventValues.put("eventName", groupEvent.getTitle());
        groupEventValues.put("description", groupEvent.getDescription());
        groupEventValues.put("beginTime", formatter.format(groupEvent.getBeginTime().getTime()));
        groupEventValues.put("endTime", formatter.format(groupEvent.getEndTime().getTime()));
        groupEventValues.put("whetherProcess", groupEvent.getWhetherProcess());
        groupEventValues.put("remind",0);//存疑
        groupEventValues.put("eventType", groupEvent.getEventType());
        db.insert("groupevents" + String.valueOf(currentUserID), null, groupEventValues);

    }

    /**
     * 修改一条已有groupID和eventID的groupEvent
     * @param db
     * @param groupEvent
     */
    private void updateGroupEvent(SQLiteDatabase db, GroupEvent groupEvent) {
        ContentValues groupEventValues = new ContentValues();

        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Gson gson = new Gson();
        groupEventValues.put("eventID", groupEvent.getEventID());
        groupEventValues.put("groupID", groupEvent.getGroupID());
        groupEventValues.put("dutyUserIDs", gson.toJson(groupEvent.getMemberID()));
        groupEventValues.put("eventName", groupEvent.getTitle());
        groupEventValues.put("description", groupEvent.getDescription());
        groupEventValues.put("beginTime", formatter.format(groupEvent.getBeginTime().getTime()));
        groupEventValues.put("endTime", formatter.format(groupEvent.getEndTime().getTime()));
        groupEventValues.put("whetherProcess", groupEvent.getWhetherProcess());
        groupEventValues.put("remind",0); //存疑
        groupEventValues.put("eventType", groupEvent.getEventType());
        db.update("groupevents"+ String.valueOf(currentUserID),groupEventValues,"groupID=? and eventID=?",new String[]{String.valueOf(groupEvent.getGroupID()), String.valueOf(groupEvent.getEventID())});
    }

    /**
     * 同步groupevents
     */
    public void syncGroupEvents(GroupEventList groupEventList){
        SQLiteDatabase db = getWritableDatabase();
        //判断表groupevents是否存在
        if (!tableExist(db, "groupevents" + String.valueOf(currentUserID))) {
            String SQL_CREATE_GROUPEVENTS = "create table groupevents" + String.valueOf(currentUserID) +
                    "(eventID INTEGER PRIMARY KEY," +
                    "groupID INTEGER," +
                    "dutyUserIDs VARCHAR(500)," +
                    "eventName CHAR(50)," +
                    "description VARCHAR(500)," +
                    "beginTime CHAR(50)," +
                    "endTime CHAR(50)," +
                    "whetherProcess INTEGER," +
                    "remind INTEGER," +
                    "eventType INTEGER )";
            db.execSQL(SQL_CREATE_GROUPEVENTS);

            for (GroupEvent groupEvent : groupEventList){
                insertGroupEvent(db, groupEvent);
            }
        }
        else{ //判断当前groupEventList的groupID是否在table中存在,如果在，就改写；如果不在，就直接insert
            if (groupEventList == null){
                System.out.print("groupEventList is empty!");
            }
            else{
                //查询groupID
                Cursor cursor = null;
                for (GroupEvent groupEvent : groupEventList){
                    cursor = db.query("groupevents" + String.valueOf(currentUserID),
                            new String[] {"eventName"},
                            "groupID=? and eventID=?",
                            new String[] {String.valueOf(groupEvent.getGroupID()), String.valueOf(groupEvent.getEventID())},
                            null, null, null);
                    if (cursor.moveToFirst()){
                        updateGroupEvent(db, groupEvent);
                    }
                    else{
                        insertGroupEvent(db, groupEvent);
                    }
                }
                cursor.close();
            }
        }
    }
    public GroupEventList queryGroupEventList(){
        SQLiteDatabase db = getWritableDatabase();
        GroupEventList groupEventList = new GroupEventList();
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Gson gson = new Gson();

        Cursor cursor = null;
        if (tableExist(db, "groupevents"+ String.valueOf(currentUserID) )) {
            cursor = db.rawQuery("SELECT * FROM groupevents" + String.valueOf(currentUserID),null);
            while (cursor.moveToNext()) {
                try {
                    //整理取到的表格信息。出错可能性较大
                    int eventID = cursor.getInt(0);
                    int groupID = cursor.getInt(1);
                    int [] dutyUserIDs = gson.fromJson(cursor.getString(2), int[].class);
                    String eventName = cursor.getString(3);
                    String description = cursor.getString(4);
                    Date date1 = formatter.parse(cursor.getString(5));
                    date1 = new Date(date1.getTime());
                    GregorianCalendar beginTime = new GregorianCalendar();
                    beginTime.setTime(date1);
                    Date date2 = formatter.parse(cursor.getString(6));
                    date2 = new Date(date2.getTime());
                    GregorianCalendar endTime = new GregorianCalendar();
                    endTime.setTime(date2);
                    int whetherProcess = cursor.getInt(7);
                    int eventType = cursor.getInt(8);
                    groupEventList.add(new GroupEvent(eventID, groupID, dutyUserIDs, eventName, description, beginTime, endTime, whetherProcess, eventType));

                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Error in queryGroupEventList");
                }
            }
        }
        else{
            System.out.println("There is no table called groupevents" + String.valueOf(currentUserID));
        }

        cursor.close();
        return groupEventList;
    }
    /**
     * 查询当前user给定targetGroupID组的所有groupevents，返回一个groupEventList（前端调用）
     * @return
     */
    public GroupEventList queryGroupEventList(int targetGroupID ){
        SQLiteDatabase db = getWritableDatabase();
        GroupEventList groupEventList = new GroupEventList();
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Gson gson = new Gson();

        Cursor cursor = null;
        if (tableExist(db, "groupevents"+ String.valueOf(currentUserID) )) {
            cursor = db.rawQuery("SELECT * FROM groupevents" + String.valueOf(currentUserID) + " WHERE groupID=?",
                    new String[]{String.valueOf(targetGroupID)});
            while (cursor.moveToNext()) {
                try {
                    //整理取到的表格信息。出错可能性较大
                    int eventID = cursor.getInt(0);
                    int groupID = cursor.getInt(1);
                    int [] dutyUserIDs = gson.fromJson(cursor.getString(2), int[].class);
                    String eventName = cursor.getString(3);
                    String description = cursor.getString(4);
                    Date date1 = formatter.parse(cursor.getString(5));
                    date1 = new Date(date1.getTime());
                    GregorianCalendar beginTime = new GregorianCalendar();
                    beginTime.setTime(date1);
                    Date date2 = formatter.parse(cursor.getString(6));
                    date2 = new Date(date2.getTime());
                    GregorianCalendar endTime = new GregorianCalendar();
                    endTime.setTime(date2);
                    int whetherProcess = cursor.getInt(7);
                    int eventType = cursor.getInt(8);
                    groupEventList.add(new GroupEvent(eventID, groupID, dutyUserIDs, eventName, description, beginTime, endTime, whetherProcess, eventType));

                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Error in queryGroupEventList");
                }
            }
        }
        else{
            System.out.println("There is no table called groupevents" + String.valueOf(currentUserID));
        }

        cursor.close();
        return groupEventList;
    }

    /**
     * 测试用，查看表内数据。
     */
    public void seeTable(String tableName){
        SQLiteDatabase db = getWritableDatabase();
        String SEE_TABLE = "SELECT * FROM " + tableName;
        Cursor cursor = db.rawQuery(SEE_TABLE, null);

        if (tableName.equals("events" + String.valueOf(currentUserID))) {
            while (cursor.moveToNext()) {
                System.out.println("eventID:" + cursor.getString(0)
                        + "  eventName:" + cursor.getString(1)
                        + "  description:" + cursor.getString(2)
                        + "  beginTime:" + cursor.getString(3)
                        + "  endTime:" + cursor.getString(4)
                        + "  whetherProcess:" + cursor.getString(5)
                        + "  remind:" + cursor.getString(6));
            }
        }
        else if (tableName.equals("groups"+ String.valueOf(currentUserID))){
            while (cursor.moveToNext()) {
                System.out.println("groupID:" + cursor.getString(0)
                        + "  groupName:" + cursor.getString(1)
                        + "  description:" + cursor.getString(2)
                        + " memberIDs:" + cursor.getString(3)
                        + "  eventIDs:" + cursor.getString(4)
                        + " leaderID:" + cursor.getString(5)
                        + "  imgName:" + cursor.getString(6));
            }
        }
        else if (tableName.equals("groupevents" + String.valueOf(currentUserID))){
            while (cursor.moveToNext()){
                System.out.println("eventID:" + cursor.getString(0)
                        + "  groupID:" + cursor.getString(1)
                        + "  dutyUserID:" + cursor.getString(2)
                        + " eventName:" + cursor.getString(3)
                        + "  description:" + cursor.getString(4)
                        + " beginTime:" + cursor.getString(5)
                        + "  endTime:" + cursor.getString(6)
                        + "  whetherProcess:" + cursor.getString(7)
                        + "  remind:" + cursor.getString(8));
            }

        }
        cursor.close();
    }

    // 所有代码都没有测试过orz
}
