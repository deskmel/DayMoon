package com.example.daymoon.UserInterface;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.R;

import java.util.LinkedList;

public class EventViewAdapter extends BaseAdapter {
    private LinkedList<Event> Events;
    private Context mContext;

    public EventViewAdapter() {}
    public EventViewAdapter(EventList Events, Context context){
        this.Events=Events.getAllEventRecord();
        this.mContext=context;
    }
    @Override
    public int getCount(){
        return Events.size();
    }
    @Override
    public Object getItem(int position)
    {return null;}
    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.custom_event_list,parent,false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.event_title);
            holder.time = (TextView) convertView.findViewById(R.id.event_time);
            holder.des = (TextView) convertView.findViewById(R.id.event_descriptions);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(Events.get(position).getTitle());
        holder.time.setText(Events.get(position).getBeginTime_str());
        holder.des.setText(Events.get(position).getDescription());
        return convertView;
    }

    private class ViewHolder{ //event_list 所需数据
        TextView title;
        TextView time;
        TextView des;
    }
}
