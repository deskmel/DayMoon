package com.example.daymoon.GroupEventManagement;

import java.util.LinkedList;

public class  GroupEventList extends LinkedList<GroupEvent> {
    public int findByID(int eventID){//A naive implementation
        int index = -1;
        for (int i = 0; i < size(); i += 1){
            if (get(i).getEventID() == eventID){
                index = i;
                break;
            }
        }
        return index;
    }
}
