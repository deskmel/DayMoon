package com.example.daymoon.EventManagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class EventList {
    LinkedList<Event> eventRecord;

    public EventList(){
        eventRecord = new LinkedList<Event>();
    }


    public void add(Event event){
        eventRecord.add(event);

    }



    public void delete(int eventID){
        eventRecord.remove(eventID);
    }



    public int length(){
        return eventRecord.size();
    }

    public LinkedList<Event> getAllEventRecord() {
        return eventRecord;
    }

    public Event get(int index){
        return eventRecord.get(index);
    }



    // 找对应eventID的索引
    public int find(int eventID){
        for (int i = 0; i < eventRecord.size(); i++){
            if (eventRecord.get(i).getEventID() == eventID){

                return i;
            }
        }
        return -1;
    }



    // 找对应eventID的索引，二分查找，要先排序
    public int findBinary(int eventID){

        int start = 0, end = eventRecord.size() - 1;
        int middle;

        while (start <= end){
            middle = (start + end) / 2;
            if (eventRecord.get(middle).getEventID() > eventID){
                end = middle - 1;
            }
            else if (eventRecord.get(middle).getEventID() < eventID){
                start = middle + 1;
            }
            else {
                return middle;
            }
        }
        return -1;
    }



    // 按eventID排序
    public void sortByEventID(){
        Collections.sort(eventRecord);
    }



    // 测试
    public static void main(String[] args){

    }
}

