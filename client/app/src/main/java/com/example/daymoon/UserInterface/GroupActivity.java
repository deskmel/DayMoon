package com.example.daymoon.UserInterface;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.example.daymoon.Adapter.GroupViewAdapter;
import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupInfoManagement.ClientGroupInfoControl;
import com.example.daymoon.GroupInfoManagement.GroupList;
import com.example.daymoon.R;
import com.example.daymoon.Tool.PermissionUtil;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.yalantis.phoenix.PullToRefreshView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GroupActivity extends AppCompatActivity {
    private final int REQUEST_QRCODE = 1;
    private final int REQUEST_CREATEGROUP = 2;
    private int userId;
    private RecyclerView recyclerView;
    private GroupViewAdapter adapter =null;
    private GroupList groupList;
    private ImageButton more;
    private Context mainContext = null;
    private ClientGroupInfoControl clientGroupInfoControl;
    private PopupWindow popupWindow;
    private ImageButton calenderButton;
    private TextView today;
    private PullToRefreshView mPullToRefreshView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtil.verifyStoragePermissions(GroupActivity.this);
        ZXingLibrary.initDisplayOpinion(this);
        setContentView(R.layout.activity_group);
        mainContext = GroupActivity.this;
        recyclerView=findViewById(R.id.group_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        flushlist();
        calenderButton=findViewById(R.id.calendar);
        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        more = findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpMenu();
            }
        });
        final SimpleDateFormat timeformat=new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Calendar c=Calendar.getInstance();
        today=findViewById(R.id.today);
        today.setText(timeformat.format(c.getTime()));
        refresh();

    }
    private void flushlist(){
        ClientGroupInfoControl.getGroupListFromServer(new Runnable() {
            @Override
            public void run() {
                groupList = ClientGroupInfoControl.getGroupList();
                flushGroupList();
            }
        }, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "something goes wrong", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void flushGroupList()
    {
        adapter = new GroupViewAdapter(groupList, mainContext);
        recyclerView.setAdapter(adapter);
        adapter.setonItemClickListener(new GroupViewAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View v, int Position) {
                Intent intent = new Intent(GroupActivity.this,GroupScheduleActivity.class);
                int groupID = groupList.get(Position).getGroupID();
                intent.putExtra("groupID", groupID);
                intent.putExtra("group",groupList.get(Position));
                ClientGroupEventControl.setCurrentGroupID(groupID);
                startActivityForResult(intent, 0);
            }
        });
    }
    private void refresh(){
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flushlist();
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 500);
            }
        });
    }
    private void showPopUpMenu(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopupWindow = inflater.inflate(R.layout.popupmenu, null, false);
        popupWindow = new PopupWindow(vPopupWindow, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        int width = more.getMeasuredWidth();//按钮宽度
        popupWindow.setWidth(width*4);
        //popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        View parentView = LayoutInflater.from(GroupActivity.this).inflate(R.layout.layout_popupwindow, null);
        //popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        popupWindow.showAsDropDown(more,29,0,Gravity.RIGHT);
        addBackground(GroupActivity.this,0.8f);
        vPopupWindow.findViewById(R.id.creategroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupActivity.this,CreateGroupActivity.class);
                startActivityForResult(intent,REQUEST_CREATEGROUP);
            }
        });
        vPopupWindow.findViewById(R.id.joingroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_QRCODE);
            }
        });


    }
    private void addBackground(Activity activity,float bgAlpha) {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
        //dismiss时恢复原样
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_QRCODE) {
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    ClientGroupInfoControl.joinGroupByQRCode(result, new Runnable() {
                        @Override
                        public void run() {
                            flushGroupList();
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
        else if (requestCode==REQUEST_CREATEGROUP){
            flushGroupList();
        }
    }
}
