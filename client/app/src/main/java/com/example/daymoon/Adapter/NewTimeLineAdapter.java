package com.example.daymoon.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.EventManagement.EventList;
import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.R;

import java.util.Collections;
public class NewTimeLineAdapter extends RecyclerView.Adapter<NewTimeLineAdapter.ViewHolder>{
    private EventList eventList;
    private Context mContext;
    private NewTimeLineAdapter.OnRecyclerItemClickListener mListener;
    private ClientGroupEventControl clientGroupEventControl;
    public NewTimeLineAdapter(EventList eventList,Context context){
        this.eventList=eventList;
        this.mContext=context;
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(View v, int Position);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
    @NonNull
    public void setonItemClickListener(NewTimeLineAdapter.OnRecyclerItemClickListener listener){
        this.mListener=listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.timeline,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.eventimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(holder.getAdapterPosition());
                mListener.onItemClick(v, holder.getAdapterPosition());
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder { //event_list 所需数据
        TextView groupeventdate;
        TextView groupeventtime;
        TextView title;
        ImageView eventimage;
        ImageView groupcreatorimage;
        Button btn;
        View vLine;
        View horiLine;
        RelativeLayout relativeLayout;
        LinearLayout time;

        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.rl_title);
            groupeventdate = itemView.findViewById(R.id.event_date);
            groupeventtime = itemView.findViewById(R.id.event_time);
            title = itemView.findViewById(R.id.event_title);
            eventimage = itemView.findViewById(R.id.event_image);
            groupcreatorimage = itemView.findViewById(R.id.creatorimage);
            vLine = itemView.findViewById(R.id.v_line);
            horiLine = itemView.findViewById(R.id.line4);
            time = itemView.findViewById(R.id.time);
        }

        public void setPosition(int position) {
            Event event=eventList.get(position);
            title.setText(event.getTitle());
            eventimage.setImageResource(R.mipmap.sports);
            if(position%2==0){
                RelativeLayout.LayoutParams ll =(RelativeLayout.LayoutParams) time.getLayoutParams();
                ll.addRule(RelativeLayout.LEFT_OF,R.id.event_image);
                time.setLayoutParams(ll);
                ll = (RelativeLayout.LayoutParams) horiLine.getLayoutParams();
                ll.addRule(RelativeLayout.RIGHT_OF,R.id.event_image);
                horiLine.setLayoutParams(ll);
                ll = (RelativeLayout.LayoutParams) title.getLayoutParams();
                ll.addRule(RelativeLayout.RIGHT_OF,R.id.line4);
                title.setLayoutParams(ll);
                ll = (RelativeLayout.LayoutParams) groupcreatorimage.getLayoutParams();
                ll.addRule(RelativeLayout.RIGHT_OF,R.id.event_title);
                groupcreatorimage.setLayoutParams(ll);
            }
            else{
                RelativeLayout.LayoutParams ll =(RelativeLayout.LayoutParams) time.getLayoutParams();
                ll.addRule(RelativeLayout.RIGHT_OF,R.id.event_image);
                time.setLayoutParams(ll);
                ll = (RelativeLayout.LayoutParams) horiLine.getLayoutParams();
                ll.addRule(RelativeLayout.LEFT_OF,R.id.event_image);
                horiLine.setLayoutParams(ll);
                ll = (RelativeLayout.LayoutParams) title.getLayoutParams();
                ll.addRule(RelativeLayout.LEFT_OF,R.id.line4);
                title.setLayoutParams(ll);
                ll = (RelativeLayout.LayoutParams) groupcreatorimage.getLayoutParams();
                ll.addRule(RelativeLayout.LEFT_OF,R.id.event_title);
                groupcreatorimage.setLayoutParams(ll);
            }
        }
    }
}
