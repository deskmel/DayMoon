package com.example.daymoon.UserInterface;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.R;

import java.util.LinkedList;

public class EventAdapter extends BaseAdapter {
    private LinkedList<Event> Events;
    private Context mContext;

    public EventAdapter() {}
    public EventAdapter(LinkedList<Event> Events,Context context){
        this.Events=Events;
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
            holder.txt_content = (TextView) convertView.findViewById(R.id.txt_content);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_content.setText(Events.get(position).getContent());
        return convertView;
    }

    private class ViewHolder{ //event_list 所需数据
        TextView txt_content;
    }
}
