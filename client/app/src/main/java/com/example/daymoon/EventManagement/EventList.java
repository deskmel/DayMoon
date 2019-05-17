package com.example.daymoon.EventManagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class EventList extends LinkedList<Event>{
    //原来的实现似乎没有太大必要 后续需要在补充方法

    void removeByID(int eventID){//A naive implementation
        int index = findByID(eventID);
        if (index >= 0) remove(index);
    }

    int findByID(int eventID){//A naive implementation
        int index = -1;
        for (int i = 0; i < size(); i += 1){
            if (get(i).getEventID() == eventID){
                index = i;
                break;
            }
        }
        return index;
    }

    // 找对应eventID的索引，二分查找，要先排序
    /*public int findBinary(int eventID){

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
    }*/



    // 按eventID排序
    void sortByEventID(){
        Collections.sort(this);
    }



    // 测试
    public static void main(String[] args){

    }


}

