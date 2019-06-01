package com.example.daymoon.UserInterface;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import com.example.daymoon.Adapter.GroupViewAdapter;
import com.example.daymoon.Adapter.NotificationViewAdapter;
import com.example.daymoon.GroupEventManagement.ClientGroupEventControl;
import com.example.daymoon.GroupEventManagement.Notification;
import com.example.daymoon.GroupEventManagement.NotificationList;
import com.example.daymoon.GroupInfoManagement.ClientGroupInfoControl;
import com.example.daymoon.GroupInfoManagement.GroupList;
import com.example.daymoon.Layout.ViewPagerSlide;
import com.example.daymoon.R;
import com.example.daymoon.Tool.PermissionUtil;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.yalantis.phoenix.PullToRefreshView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.zip.Inflater;

public class GroupActivity extends AppCompatActivity {
    private final int REQUEST_QRCODE = 1;
    private final int REQUEST_CREATEGROUP = 2;
    private int userId;
    private RecyclerView groupListRecyclerView;
    private RecyclerView notificationRecyclerView;
    private GroupViewAdapter adapter =null;
    private GroupList groupList;
    private ViewPagerSlide viewPager;
    private LayoutInflater inflater;
    private View groupListPage;
    private View notificationPage;
    private ImageButton more;
    private Context mainContext = null;
    private ClientGroupInfoControl clientGroupInfoControl;
    private PopupWindow popupWindow;
    private ImageButton calenderButton;
    private TextView today;
    private PullToRefreshView mPullToRefreshView;
    private ArrayList<View> viewList;
    private ImageButton notification;
    private ImageButton groupback;
    private ViewFlipper viewFlipper;
    private ViewFlipper viewFlipper1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtil.verifyStoragePermissions(GroupActivity.this);
        ZXingLibrary.initDisplayOpinion(this);
        setContentView(R.layout.activity_group);
        mainContext = GroupActivity.this;
        initPage();
        initButton();
        final SimpleDateFormat timeformat=new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Calendar c=Calendar.getInstance();
        today=findViewById(R.id.today);
        today.setText(timeformat.format(c.getTime()));
        refresh();
    }
    private void initButton(){
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
        viewFlipper1 = findViewById(R.id.flipper1);
        viewFlipper = findViewById(R.id.flipper);
        notification = findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
                viewFlipper.showNext();
                viewFlipper1.showNext();
            }
        });
        groupback = findViewById(R.id.groupback);
        groupback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                viewFlipper.showNext();
                viewFlipper1.showNext();
            }
        });

    }
    private void initPage(){
        viewPager=(ViewPagerSlide) findViewById(R.id.viewpage);
        initGroupListPager();
        initNotificationPager();
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(groupListPage);
        viewList.add(notificationPage);
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);
    }
    private void initGroupListPager(){
        groupListPage = View.inflate(this,R.layout.group_layout,null);
        groupListRecyclerView=groupListPage.findViewById(R.id.group_list);
        groupListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        groupListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        groupListRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        flushlist();
    }
    private void initNotificationPager(){
        notificationPage = View.inflate(this,R.layout.notification_layout,null);
        notificationRecyclerView = notificationPage.findViewById(R.id.notification_list);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        notificationRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

    }
    private void flushlist(){
        ClientGroupInfoControl.getGroupListFromServer(new Runnable() {
            @Override
            public void run() {
                groupList = ClientGroupInfoControl.getGroupList();
                flushGroupList();
                flushNotificationList();
            }
        }, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "something goes wrong", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void flushNotificationList(){
        Notification notification = new Notification("瞎几把搞","sg",Calendar.getInstance().getTime(),"游山玩水");
        NotificationList notificationList= new NotificationList();
        notificationList.add(notification);
        NotificationViewAdapter notificationViewAdapter = new NotificationViewAdapter(notificationList,mainContext,0);
        notificationRecyclerView.setAdapter(notificationViewAdapter);

    }
    private void flushGroupList()
    {
        adapter = new GroupViewAdapter(groupList, mainContext);
        groupListRecyclerView.setAdapter(adapter);
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
