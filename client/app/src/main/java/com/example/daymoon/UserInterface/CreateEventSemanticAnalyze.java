package com.example.daymoon.UserInterface;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.daymoon.EventManagement.ClientEventControl;
import com.example.daymoon.EventManagement.Event;
import com.example.daymoon.R;
import com.example.daymoon.Tool.AudioUtils;
import com.example.daymoon.Tool.PermissionUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;


import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.time.temporal.ValueRange;


public class CreateEventSemanticAnalyze extends BaseActivity {
    private MaterialEditText materialEditText;
    String content;
    private Intent intent;
    private Bundle bundle;
    private AudioUtils audioUtils;
    private PopupWindow popupWindow;
    private Drawable drawable;
    private RecognizerListener mRecoListener;
    private SpeechRecognizer mIat;
    private StringBuffer buffer = new StringBuffer();
    private Event event;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = new Event();
        SpeechUtility.createUtility(CreateEventSemanticAnalyze.this, "appid=5d04f06b");
        initRecognizer();
        context = this;
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
                ClientEventControl.getSemantic(materialEditText.getText().toString(), event, new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(CreateEventSemanticAnalyze.this, EventAdder.class);
                        Bundle bundle2 = new Bundle();
                        bundle2.putInt("selectYear", bundle.getInt("selectYear"));
                        bundle2.putInt("selectMonth", event.getBeginTime().getTime().getMonth()+1);
                        bundle2.putInt("selectDay", event.getBeginTime().getTime().getDate());
                        bundle2.putInt("beginHour",event.getBeginTime().getTime().getHours());
                        bundle2.putInt("beginMinute",event.getBeginTime().getTime().getMinutes());
                        bundle2.putInt("endHour",event.getEndTime().getTime().getHours());
                        bundle2.putInt("endMinute",event.getEndTime().getTime().getMinutes());
                        bundle2.putString("description",event.getDescription());
                        bundle2.putString("title",event.getTitle());
                        bundle2.putString("location",event.getEventLocation());
                        intent.putExtras(bundle2);
                        startActivity(intent);
                        finish();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"Fail to get semantic from server", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }
    private void initRecognizer(){
        //1.创建SpeechRecognizer对象
        //初始化识别无UI识别对象
        //使用SpeechRecognizer对象，可根据回调消息自定义界面；

        mIat = SpeechRecognizer.createRecognizer(CreateEventSemanticAnalyze.this, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    Log.d("tag","error");
                    Toast.makeText(CreateEventSemanticAnalyze.this, "出现奇怪的错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

    //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        mIat.setParameter( SpeechConstant.CLOUD_GRAMMAR, null );
        mIat.setParameter( SpeechConstant.SUBJECT, null );
    //设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "plain");
        //此处engineType为“cloud”
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
    //设置语音输入语言，zh_cn为简体中文
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
    //设置结果返回语言
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
        //取值范围{1000～10000}
        mIat.setParameter(SpeechConstant.VAD_BOS, "2000");
        //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
        //自动停止录音，范围{0~10000}
        mIat.setParameter(SpeechConstant.VAD_EOS, "4000");
    //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT,"1");
    mRecoListener=new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            Log.d("volumn", String.valueOf(i));
            drawable.setLevel(5000+(i-1)*2000);
        }

        @Override
        public void onBeginOfSpeech() {
            Toast.makeText(CreateEventSemanticAnalyze.this,"开始说话",Toast.LENGTH_LONG);

        }

        @Override
        public void onEndOfSpeech() {
            drawable.setLevel(0);
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.d("result",recognizerResult.getResultString());
            Log.d("str",recognizerResult.toString());
            buffer.append(recognizerResult.getResultString());
            materialEditText.setText(buffer);
        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };
    }

    private void showPopUpWindow(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopupWindow = inflater.inflate(R.layout.popupmenu_audiorecord, null, false);
        popupWindow = new PopupWindow(vPopupWindow, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        ViewFlipper viewFlipper = vPopupWindow.findViewById(R.id.flipper);
        ImageButton record = vPopupWindow.findViewById(R.id.audio_record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //audioUtils=new AudioUtils(CreateEventSemanticAnalyze.this);
                //audioUtils.startRecord(handler);/
                Toast.makeText(CreateEventSemanticAnalyze.this,"开始录音",Toast.LENGTH_SHORT).show();
                buffer.setLength(0);
                mIat.startListening(mRecoListener);
                viewFlipper.showNext();
            }
        });
        ImageButton stop = vPopupWindow.findViewById(R.id.audio_stop);
        drawable=stop.getDrawable();
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //audioUtils.stopRecord();
                //byte[] data = audioUtils.getRecord();
                Toast.makeText(CreateEventSemanticAnalyze.this,"录音完成",Toast.LENGTH_SHORT).show();
                mIat.stopListening();
                //Log.d("wtf", String.valueOf(data));
                popupWindow.dismiss();
            }
        });
        //popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        View parentView = LayoutInflater.from(CreateEventSemanticAnalyze.this).inflate(R.layout.layout_popupwindow, null);
        //popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        popupWindow.showAtLocation(parentView, Gravity.BOTTOM,0,0);

    }

}
