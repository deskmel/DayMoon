package com.example.daymoon.UserInterface;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.R;

import java.util.LinkedList;

public class EventViewAdapter extends BaseAdapter {
    private EventList eventList;
    private Context mContext;

    public EventViewAdapter() {}
    public EventViewAdapter(EventList eventList, Context context){
        this.eventList = eventList;
        this.mContext = context;
    }
    @Override
    public int getCount(){
        return eventList.size();
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
            holder.btn = (Button) convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(eventList.get(position).getTitle());
        holder.time.setText(eventList.get(position).getBeginTime_str());
        holder.des.setText(eventList.get(position).getDescription());
        holder.btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ClientEventControl.deleteEvent(eventList.get(position).getEventID(), mContext, new Runnable() {
                    @Override
                    public void run() {
                        eventList.remove(position);
                        notifyDataSetChanged();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });
        return convertView;
    }

    private class ViewHolder{ //event_list 所需数据
        TextView title;
        TextView time;
        TextView des;
        Button btn;
    }
}
