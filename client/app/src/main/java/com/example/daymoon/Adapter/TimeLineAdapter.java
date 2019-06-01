package com.example.daymoon.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupEventManagement.GroupEvent;
import com.example.daymoon.GroupEventManagement.GroupEventList;
import com.example.daymoon.R;

import java.util.Collections;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {
    private GroupEventList groupEventList;
    private Context mContext;
    private OnRecyclerItemClickListener mListener;
    private ClientGroupEventControl clientGroupEventControl;
    public  TimeLineAdapter(){}
    public  TimeLineAdapter(GroupEventList groupEventList,Context mContext){
        Collections.sort(groupEventList);
        this.groupEventList=groupEventList;
        this.mContext=mContext;
    }
    public interface OnRecyclerItemClickListener {
        void onItemClick(View v, int Position);
    }
    public void setonItemClickListener(TimeLineAdapter.OnRecyclerItemClickListener listener){
        this.mListener=listener;
    }
    @Override
    public int getItemCount() {
        return groupEventList.size();
    }
    @NonNull
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

        holder.setPosition(position);
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
        View  bottomline;
        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout=itemView.findViewById(R.id.rl_title);
            groupeventdate=itemView.findViewById(R.id.event_date);
            groupeventtime=itemView.findViewById(R.id.event_time);
            title=itemView.findViewById(R.id.event_title);
            eventimage=itemView.findViewById(R.id.event_image);
            groupcreatorimage=itemView.findViewById(R.id.creatorimage);
            vLine=itemView.findViewById(R.id.v_line);
            horiLine=itemView.findViewById(R.id.line4);
            time=itemView.findViewById(R.id.time);
            bottomline = itemView.findViewById(R.id.v_line_bottom);

        }
        public void setPosition(int position){
            GroupEvent event=groupEventList.get(position);
            groupeventtime.setText(event.getBeginHour());
            groupeventdate.setText(event.getBeginDate());
            title.setText(event.getTitle());
            switch ( event.getEventType())
            {
                case GroupEvent.EVENTTYPE.game:
                    eventimage.setImageResource(R.mipmap.game);
                    break;
                case GroupEvent.EVENTTYPE.eating:
                    eventimage.setImageResource(R.mipmap.eating);
                    break;
                case GroupEvent.EVENTTYPE.discussion:
                    eventimage.setImageResource(R.mipmap.discuss);
                    break;
                case GroupEvent.EVENTTYPE.lesson:
                    eventimage.setImageResource(R.mipmap.lession);
                    break;
                case GroupEvent.EVENTTYPE.sports:
                    eventimage.setImageResource(R.mipmap.sports);
                    break;
                case GroupEvent.EVENTTYPE.travel:
                    eventimage.setImageResource(R.mipmap.travel);
                    break;
                case GroupEvent.EVENTTYPE.Default:
                    eventimage.setImageResource(R.mipmap.event_default_white);
                    break;

            }


            if(position%2==0){
                RelativeLayout.LayoutParams ll =(RelativeLayout.LayoutParams) time.getLayoutParams();
                ll.addRule(RelativeLayout.LEFT_OF,R.id.center_image);
                time.setLayoutParams(ll);
                ll = (RelativeLayout.LayoutParams) horiLine.getLayoutParams();
                ll.addRule(RelativeLayout.RIGHT_OF,R.id.center_image);
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
                ll.addRule(RelativeLayout.RIGHT_OF,R.id.center_image);
                time.setLayoutParams(ll);
                ll = (RelativeLayout.LayoutParams) horiLine.getLayoutParams();
                ll.addRule(RelativeLayout.LEFT_OF,R.id.center_image);
                horiLine.setLayoutParams(ll);
                ll = (RelativeLayout.LayoutParams) title.getLayoutParams();
                ll.addRule(RelativeLayout.LEFT_OF,R.id.line4);
                title.setLayoutParams(ll);
                ll = (RelativeLayout.LayoutParams) groupcreatorimage.getLayoutParams();
                ll.addRule(RelativeLayout.LEFT_OF,R.id.event_title);
                groupcreatorimage.setLayoutParams(ll);

            }
            if (position==groupEventList.size()-1){
                bottomline.setBackgroundColor(Color.parseColor("#FFFFFF"));
                bottomline.setBackground(ContextCompat.getDrawable(mContext,R.drawable.dashline));

            }

        }
        public int dip2px(Context context, float dpValue) {
            float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

    }
}
