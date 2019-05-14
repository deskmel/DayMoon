package com.example.daymoon.UserInterface;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.R;

import java.util.LinkedList;

import static android.view.FrameMetrics.ANIMATION_DURATION;

public class EventViewAdapter extends RecyclerView.Adapter<EventViewAdapter.ViewHolder> {
    private EventList eventList;
    private Context mContext;
    private OnRecyclerItemClickListener mListener;

    public EventViewAdapter() {}
    public EventViewAdapter(EventList eventList, Context context){
        this.eventList = eventList;
        this.mContext = context;
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(View v, int Position);
    }

    public void setonItemClickListener(OnRecyclerItemClickListener listener){
        this.mListener=listener;
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_event_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                System.out.println(holder.getAdapterPosition());
                mListener.onItemClick(view, holder.getAdapterPosition());
                return false;
            }
        });

        holder.title.setText(eventList.get(position).getTitle());
        holder.time.setText(eventList.get(position).getBeginTime_str());
        holder.des.setText(eventList.get(position).getDescription());
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

    public class ViewHolder extends RecyclerView.ViewHolder{ //event_list 所需数据
        TextView title;
        TextView time;
        TextView des;
        Button btn;
        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            time = itemView.findViewById(R.id.event_time);
            des = itemView.findViewById(R.id.event_descriptions);
            btn = itemView.findViewById(R.id.delete);
        }
    }
}