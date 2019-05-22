package com.example.daymoon.Adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.GroupInfoManagement.ClientGroupInfoControl;
import com.example.daymoon.GroupInfoManagement.GroupList;
import com.example.daymoon.HttpUtil.CalendarSerializer;
import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.HttpUtil.HttpRequestThread;
import com.example.daymoon.Layout.CustomSwipeLayout;
import com.example.daymoon.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.GregorianCalendar;

import okhttp3.Request;

import static com.example.daymoon.Define.Constants.SERVER_IP;

public class GroupViewAdapter extends RecyclerView.Adapter<GroupViewAdapter.ViewHolder>  {
    private GroupList groupList;
    private Context mContext;
    private OnRecyclerItemClickListener mListener;
    private GroupEventList groupEventList;
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
        return groupList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_group_list,parent,false);
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
        Collections.sort(groupList.get(position).getEventList());
        if (groupList.get(position).getEventList().size()==0){
            holder.last_event_info.setText("暂无事件");
        }
        else holder.last_event_info.setText(String.format("%s %s %s",groupList.get(position).getEventList().getLast().getTitle(),groupList.get(position).getEventList().getLast().getLocation(),groupList.get(position).getEventList().getLast().getBeginDate()));





        new HttpRequestThread(SERVER_IP + "image/" + groupList.get(position).getImgName(), new HttpRequest.FileCallback() {
            @Override
            public void requestSuccess(Bitmap bitmap) throws Exception {
                holder.groupImage.setImageBitmap(bitmap);
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        }).run();
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
        ImageView groupImage;
        CustomSwipeLayout swipeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            groupname=itemView.findViewById(R.id.groupname);
            groupImage=itemView.findViewById(R.id.groupimage);
            last_event_info=itemView.findViewById(R.id.last_event_info);
            swipeLayout=itemView.findViewById(R.id.swipe);
        }
    }
}
