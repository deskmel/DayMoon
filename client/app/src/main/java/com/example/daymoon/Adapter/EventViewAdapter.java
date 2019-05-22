package com.example.daymoon.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;

import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.Layout.CustomSwipeLayout;
import com.example.daymoon.R;

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
        holder.swipeLayout.setOnClickItemListener(new CustomSwipeLayout.OnClickItemListener() {
            @Override
            public void onClick(View view) {
                System.out.println(holder.getAdapterPosition());
                mListener.onItemClick(view, holder.getAdapterPosition());
            }
        });

        holder.title.setText(eventList.get(position).getTitle());
        holder.time.setText(eventList.get(position).getBeginHour_str());
        holder.des.setText(eventList.get(position).getDescription());
        holder.confirmDelete.setText("删除"+eventList.get(position).getTitle()+"？");
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
        TextView confirmDelete;
        Button btn;
        CustomSwipeLayout swipeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            time = itemView.findViewById(R.id.event_time);
            des = itemView.findViewById(R.id.event_descriptions);
            btn = itemView.findViewById(R.id.delete);
            confirmDelete = itemView.findViewById(R.id.confirmDelete);
            swipeLayout = itemView.findViewById(R.id.swipe);
        }
    }
}