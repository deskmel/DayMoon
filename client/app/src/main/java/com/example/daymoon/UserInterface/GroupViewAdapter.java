package com.example.daymoon.UserInterface;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupEventManagement.GroupEventControl;
import com.example.daymoon.GroupInfoManagement.ClientGroupInfoControl;
import com.example.daymoon.GroupInfoManagement.Group;
import com.example.daymoon.GroupInfoManagement.GroupList;
import com.example.daymoon.Layout.CustomSwipeLayout;
import com.example.daymoon.R;

public class GroupViewAdapter extends RecyclerView.Adapter<GroupViewAdapter.ViewHolder>  {
    private GroupList groupList;
    private EventList eventList;
    private Context mContext;
    private OnRecyclerItemClickListener mListener;
    private ClientGroupEventControl clientGroupEventControl;
    public GroupViewAdapter() {}
    public GroupViewAdapter(GroupList groupList, Context context){
        this.groupList = groupList;
        this.mContext = context;
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(View v, int Position);
    }

    public void setonItemClickListener(GroupViewAdapter.OnRecyclerItemClickListener listener){
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

        holder.groupname.setText(groupList.get(position).getGroupName());
        clientGroupEventControl = new ClientGroupEventControl();
        holder.last_event_info.setText(clientGroupEventControl.getLatestGroupEventDes(groupList.get(position).getGroupId()));
        //holder.des.setText(eventList.get(position).getDescription());
        //holder.confirmDelete.setText("删除"+eventList.get(position).getTitle()+"？");
        /*
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
        });*/
    }

    public class ViewHolder extends RecyclerView.ViewHolder{ //event_list 所需数据
        TextView groupname;
        TextView last_event_info;
        TextView des;
        TextView confirmDelete;
        Button btn;
        CustomSwipeLayout swipeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            groupname=itemView.findViewById(R.id.groupname);
            last_event_info=itemView.findViewById(R.id.last_event_info);
        }
    }
}
