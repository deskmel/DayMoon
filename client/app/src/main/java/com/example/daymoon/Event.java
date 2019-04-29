package com.example.daymoon;

public class Event {
    private String description;
    public Event(){}
    public  Event(String des)
    { this.description=des; }


    public String getContent()
    {
        return description;
    }
}
