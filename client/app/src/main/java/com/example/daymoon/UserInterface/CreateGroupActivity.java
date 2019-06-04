package com.example.daymoon.UserInterface;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.daymoon.GroupInfoManagement.ClientGroupInfoControl;
import com.example.daymoon.GroupInfoManagement.GroupInformationHolder;
import com.example.daymoon.R;
import com.example.daymoon.Tool.PermissionUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateGroupActivity extends BaseActivity {
    private ImageView profilephoto;
    private MaterialEditText groupname;
    private Button submit;
    private TextView dismiss;
    private PopupWindow popupWindow;
    private GroupInformationHolder groupInformationHolder;

    private static final int ALBUM_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 3;


    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtil.verifyCameraPermission(CreateGroupActivity.this);
        setContentView(R.layout.activity_create_group);

        profilephoto=findViewById(R.id.profilephote);
        groupname=findViewById(R.id.groupname);
        submit=findViewById(R.id.createbutton);
        dismiss=findViewById(R.id.dismiss);

        groupInformationHolder = new GroupInformationHolder();

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupInformationHolder.groupName = groupname.getText().toString();
                groupInformationHolder.image = tempFile;
                groupInformationHolder.description = "placeholder";
                ClientGroupInfoControl.createGroup(groupInformationHolder, new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        });
        profilephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpWindow();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            // 调用相机后返回
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //用相机返回的照片去调用剪裁也需要对Uri进行处理
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(CreateGroupActivity.this, "com.example.daymoon.fileprovider", tempFile);
                        cropPhoto(contentUri);//裁剪图片
                    } else {
                        cropPhoto(Uri.fromFile(tempFile));//裁剪图片
                    }
                }
                break;
            //调用相册后返回
            case ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    cropPhoto(uri);//裁剪图片
                }
                break;
            //调用剪裁后返回
            case CROP_REQUEST_CODE:
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Bitmap image = bundle.getParcelable("data");
                    profilephoto.setImageBitmap(image);
                    String path = saveImage("userHeader", image);
                    tempFile = new File(path);
                }
                break;
        }
    }

    private void getPicFromCamera() {
        //用于保存调用相机拍照后所生成的文件
        tempFile = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".jpg");
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(CreateGroupActivity.this, "com.example.daymoon.fileprovider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {    //否则使用Uri.fromFile(file)方法获取Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
    private void getPicFromAlbum()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent,ALBUM_REQUEST_CODE);
    }

    private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    public String saveImage(String name, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory().getPath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void showPopUpWindow(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopupWindow = inflater.inflate(R.layout.popupmenu_profilephoto, null, false);
        popupWindow = new PopupWindow(vPopupWindow, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        addBackground();

        //popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        View parentView = LayoutInflater.from(CreateGroupActivity.this).inflate(R.layout.layout_popupwindow, null);
        //popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        popupWindow.showAtLocation(parentView,Gravity.BOTTOM,0,0);
        vPopupWindow.findViewById(R.id.takepotato).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicFromCamera();
            }
        });
        vPopupWindow.findViewById(R.id.album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicFromAlbum();
            }
        });
        vPopupWindow.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }
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
