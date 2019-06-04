package com.example.daymoon.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.Layout.CustomSwipeLayout;
import com.example.daymoon.R;
import com.example.daymoon.UserInterface.CalendarActivity;
import com.example.daymoon.UserInterface.EventDetailActivity;

import java.util.ArrayList;
import java.util.Collections;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class EventViewAdapter extends RecyclerView.Adapter<EventViewAdapter.ViewHolder> {
    private EventList eventList;
    private GroupEventList groupEventList;
    private Context mContext;
    private OnRecyclerItemClickListener mListener;
    private int[] numlist;
    private int[] typelist;
    public EventViewAdapter() {}
    public EventViewAdapter(EventList eventList, Context context, GroupEventList groupEventList){
        Collections.sort(eventList);
        Collections.sort(groupEventList);
        this.eventList = eventList;
        this.mContext = context;
        this.groupEventList = groupEventList;
        numlist=new int [eventList.size() + groupEventList.size()];
        typelist= new int [eventList.size() + groupEventList.size()];
        sorts();
    }
    private void sorts(){
        int i=0;
        int j=0;
        while (i<eventList.size() && j <groupEventList.size()){
            if (eventList.get(i).getBeginTime().getTime().compareTo( groupEventList.get(j).getBeginCalendar().getTime()) == 1){
                numlist[i+j]=i;
                typelist[i+j]=0;
                i+=1;
            }
            else{
                numlist[i+j]=j;
                typelist[i+j]=1;
                j+=1;
            }
        }
        while (i<eventList.size()){
            numlist[i+j]=i;
            typelist[i+j]=0;
            i+=1;
        }
        while (j<groupEventList.size()){
            numlist[i+j]=j;
            typelist[i+j]=1;
            j+=1;
        }
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(View v, int Position);
    }
    public int getEventType(int position){
        return typelist[position];
    }
    public int getEventIndex(int position){
        return numlist[position];
    }
    public void setonItemClickListener(OnRecyclerItemClickListener listener){
        this.mListener=listener;
    }

    @Override
    public int getItemCount() {
        return eventList.size()+groupEventList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_event_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.swipeLayout.setOnClickItemListener(new CustomSwipeLayout.OnClickItemListener() {
            @Override
            public void onClick(View view) {
                System.out.println(holder.getAdapterPosition());
                mListener.onItemClick(view, holder.getAdapterPosition());
            }
        });
        if (typelist[position]==0){
            int index = numlist[position];
            holder.title.setText(eventList.get(index).getTitle());
            holder.time.setText(eventList.get(index).getBeginHour_str());
            holder.des.setText(eventList.get(index).getDescription());
            holder.confirmDelete.setText("删除"+eventList.get(index).getTitle()+"？");
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClientEventControl.deleteEvent(eventList.get(holder.getAdapterPosition()).getEventID(), mContext, new Runnable() {
                        @Override
                        public void run() {
                            eventList.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            });
        }
        else{
            int index = numlist[position];
            holder.title.setText(groupEventList.get(index).getTitle());
            holder.time.setText(groupEventList.get(index).getBeginHour_str());
            holder.des.setText(groupEventList.get(index).getDescription());
            holder.eventType.setText("小组");
            holder.leftinfo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.eventlist_border_blue));
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder{ //event_list 所需数据
        TextView title;
        TextView time;
        TextView des;
        TextView confirmDelete;
        TextView eventType;
        LinearLayout leftinfo;
        Button btn;
        CustomSwipeLayout swipeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            eventType = itemView.findViewById(R.id.event_type);
            title = itemView.findViewById(R.id.event_title);
            time = itemView.findViewById(R.id.event_time);
            des = itemView.findViewById(R.id.event_descriptions);
            btn = itemView.findViewById(R.id.delete);
            confirmDelete = itemView.findViewById(R.id.confirmDelete);
            swipeLayout = itemView.findViewById(R.id.swipe);
            leftinfo = itemView.findViewById(R.id.left_info);
        }
    }
}