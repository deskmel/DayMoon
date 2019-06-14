package com.example.daymoon.UserInterface;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.daymoon.R;
import com.example.daymoon.Tool.AudioUtils;
import com.example.daymoon.Tool.PermissionUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

public class CreateEventSemanticAnalyze extends BaseActivity {
    private MaterialEditText materialEditText;
    String content;
    private Intent intent;
    private Bundle bundle;
    private AudioUtils audioUtils;
    private PopupWindow popupWindow;
    private Drawable drawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtil.verifyAudioPermission(CreateEventSemanticAnalyze.this);
        setContentView(R.layout.activity_create_event_semantic_analyze);
        intent=this.getIntent();
        bundle=intent.getExtras();
        initView();
        initButton();

    }
    private void initView(){
        materialEditText = findViewById(R.id.contentEdit);
    }
    private void initButton(){
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageButton Audio = findViewById(R.id.audio_input);
        Audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpWindow();
            }
        });
        ImageButton ok = findViewById(R.id.right);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventSemanticAnalyze.this, EventAdder.class);
                bundle.putInt("beginHour",8);
                bundle.putInt("beginMinute",0);
                bundle.putInt("endHour",12);
                bundle.putInt("endMinute",20);
                bundle.putString("description","吃喝嫖赌样样精通");
                bundle.putString("title","玩吧");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void showPopUpWindow(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopupWindow = inflater.inflate(R.layout.popupmenu_audiorecord, null, false);
        popupWindow = new PopupWindow(vPopupWindow, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        addBackground();
        ViewFlipper viewFlipper = vPopupWindow.findViewById(R.id.flipper);
        ImageButton record = vPopupWindow.findViewById(R.id.audio_record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioUtils=new AudioUtils(CreateEventSemanticAnalyze.this);
                audioUtils.startRecord(handler);
                Toast.makeText(CreateEventSemanticAnalyze.this,"开始录音",Toast.LENGTH_SHORT).show();

                viewFlipper.showNext();
            }
        });
        ImageButton stop = vPopupWindow.findViewById(R.id.audio_stop);
        drawable=stop.getDrawable();
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioUtils.stopRecord();
                byte[] data = audioUtils.getRecord();
                Toast.makeText(CreateEventSemanticAnalyze.this,"录音完成",Toast.LENGTH_SHORT).show();
                //Log.d("wtf", String.valueOf(data));
                popupWindow.dismiss();
            }
        });
        //popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        View parentView = LayoutInflater.from(CreateEventSemanticAnalyze.this).inflate(R.layout.layout_popupwindow, null);
        //popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        popupWindow.showAtLocation(parentView, Gravity.BOTTOM,0,0);

    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case AudioUtils.VOICE_VOLUME:
                    drawable.setLevel(msg.arg1);
                    break;
                case AudioUtils.RECORD_STOP:
                    drawable.setLevel(0);
                    break;
                default:
                    break;
            }
            return true;
        }
    });
    private void addBackground() {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;//调节透明度
        getWindow().setAttributes(lp);
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



}
