package com.example.daymoon.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.GroupEventManagement.Notification;
import com.example.daymoon.GroupEventManagement.NotificationList;
import com.example.daymoon.R;

import java.util.LinkedList;

public class NotificationViewAdapter extends RecyclerView.Adapter<NotificationViewAdapter.ViewHolder>{
    private NotificationList notificationList;
    private Context context;
    private NotificationViewAdapter.OnRecyclerItemClickListener mListener;
    private int choice=0;
    public NotificationViewAdapter(NotificationList notifications, Context context,int choice){
        this.notificationList = notifications;
        this.context=context;
        this.choice=choice;
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(View v, int Position);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
    @NonNull
    public void setonItemClickListener(NotificationViewAdapter.OnRecyclerItemClickListener listener){
        this.mListener=listener;
    }


    @Override
    public NotificationViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (choice==0) view = LayoutInflater.from(context).inflate(R.layout.notification_card,parent,false);
        else view = LayoutInflater.from(context).inflate(R.layout.notification_card_material,parent,false);
        return new NotificationViewAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull NotificationViewAdapter.ViewHolder holder, int position) {
        holder.setPosition(position);
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView message;
        private TextView publishMessage;
        private TextView groupName;
        public ViewHolder(View itemView){
            super(itemView);
            message = itemView.findViewById(R.id.message);
            publishMessage = itemView.findViewById(R.id.publishmessage);
            groupName = itemView.findViewById(R.id.groupname);
        }
        public  void setPosition(int position) {

            this.message.setText(notificationList.get(position).getMessage());
            message.setTypeface(ResourcesCompat.getFont(context,R.font.msyh));
            this.publishMessage.setText(notificationList.get(position).getPublishInfo());
            this.groupName.setText(notificationList.get(position).getGroupname());
            groupName.setTypeface(ResourcesCompat.getFont(context,R.font.msyh));
        }
    }
}
