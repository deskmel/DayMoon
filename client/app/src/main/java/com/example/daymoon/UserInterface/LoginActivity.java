package com.example.daymoon.UserInterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.R;
import com.example.daymoon.Tool.StatusBarUtil;
import com.example.daymoon.UserInfoManagement.ClientUserInfoControl;
import com.example.daymoon.UserInfoManagement.UserInformationHolder;

import java.io.IOException;

import okhttp3.Request;

//import cn.edu.gdmec.android.boxuegu.R;
//import cn.edu.gdmec.android.boxuegu.utils.MD5Utils;
// 加密算法？？？？
public class LoginActivity extends AppCompatActivity {
    //标题
    private TextView tv_main_title;
    //返回键,显示的注册，找回密码
    private TextView tv_back,tv_register,tv_find_psw;
    //登录按钮
    private Button btn_login;
    //获取的用户名，密码，加密密码
    private String userName,psw,spPsw;
    //用户名和密码的输入框
    private EditText et_user_name,et_psw;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }
    //获取界面控件
    private void init() {
        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //从main_title_bar中获取的id
        //tv_main_title=findViewById(R.id.tv_main_title);
        //tv_main_title.setText("登录");
        tv_back=findViewById(R.id.tv_back);
        //从activity_login.xml中获取的
        tv_register=findViewById(R.id.tv_register);
        tv_find_psw=findViewById(R.id.tv_find_psw);
        btn_login=findViewById(R.id.btn_login);
        et_user_name=findViewById(R.id.et_user_name);
        et_psw=findViewById(R.id.et_psw);
        //返回键的点击事件
//        tv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //登录界面销毁
//                LoginActivity.this.finish();
//            }
//        });
        //立即注册控件的点击事件
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //为了跳转到注册界面，并实现注册功能
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        //找回密码控件的点击事件
        tv_find_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到找回密码界面（此页面暂未创建）
                /*Intent intent=new Intent(LoginActivity.this,FindPwdActivity.class);
                startActivity(intent);*/
            }
        });
        //登录按钮的点击事件
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始登录，获取用户名和密码 getText().toString().trim();
                if((spPsw!=null&&!TextUtils.isEmpty(spPsw))){
                    Toast.makeText(LoginActivity.this, "输入的用户名和密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserInformationHolder userInformationHolder = new UserInformationHolder();
                userInformationHolder.name = et_user_name.getText().toString().trim();
                userInformationHolder.password = et_psw.getText().toString().trim();
                ClientUserInfoControl.login(userInformationHolder, new HttpRequest.DataCallback() {
                    @Override
                    public void requestSuccess(String result) throws Exception {
                        if (result.isEmpty()){
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            saveLoginStatus(true, userName);
                            Intent data=new Intent();
                            data.putExtra("isLogin",true);
                            data.putExtra("userName",userName);
                            setResult(RESULT_OK,data);
                            LoginActivity.this.finish();
                            Intent intent = new Intent();
                            intent.putExtra("userid", Integer.parseInt(result));
                            intent.setClass(LoginActivity.this,CalendarActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void requestFailure(Request request, IOException e) {
                        Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    /**
     *从SharedPreferences中根据用户名读取密码
     */
    private String readPsw(String userName){
        //getSharedPreferences("loginInfo",MODE_PRIVATE);
        //"loginInfo",mode_private; MODE_PRIVATE表示可以继续写入
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        //sp.getString() userName, "";
        return sp.getString(userName , "");
    }
    /**
     *保存登录状态和登录用户名到SharedPreferences中
     */
    private void saveLoginStatus(boolean status,String userName){
        //saveLoginStatus(true, userName);
        //loginInfo表示文件名  SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        //获取编辑器
        SharedPreferences.Editor editor=sp.edit();
        //存入boolean类型的登录状态
        editor.putBoolean("isLogin", status);
        //存入登录状态时的用户名
        editor.putString("loginUserName", userName);
        //提交修改
        editor.commit();
    }
    /**
     * 注册成功的数据返回至此
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 数据
     */
    @Override
    //显示数据， onActivityResult
    //startActivityForResult(intent, 1); 从注册界面中获取数据
    //int requestCode , int resultCode , Intent data
    // LoginActivity -> startActivityForResult -> onActivityResult();
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            //是获取注册界面回传过来的用户名
            // getExtra().getString("***");
            String userName=data.getStringExtra("userName");
            if(!TextUtils.isEmpty(userName)){
                //设置用户名到 et_user_name 控件
                et_user_name.setText(userName);
                //et_user_name控件的setSelection()方法来设置光标位置
                et_user_name.setSelection(userName.length());
            }
        }
    }
}
