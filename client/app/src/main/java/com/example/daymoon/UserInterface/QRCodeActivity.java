package com.example.daymoon.UserInterface;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.daymoon.GroupInfoManagement.ClientGroupInfoControl;
import com.example.daymoon.HttpUtil.HttpRequest;
import com.example.daymoon.HttpUtil.HttpRequestThread;
import com.example.daymoon.R;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.IOException;

import okhttp3.Request;

import static com.example.daymoon.Define.Constants.SERVER_IP;

public class QRCodeActivity extends AppCompatActivity {

    private int groupID;
    private ImageView qrCodeImageView;
    private Button saveBtn;
    private Bitmap qrCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode);
        ZXingLibrary.initDisplayOpinion(this);
        saveBtn = findViewById(R.id.saveimage);
        qrCodeImageView = findViewById(R.id.qrcodeimg);

        groupID = getIntent().getIntExtra("groupID", 2);

        ClientGroupInfoControl.generateQRCode(groupID, new HttpRequest.DataCallback() {
            @Override
            public void requestSuccess(String result) throws Exception {
                System.out.print(result);
                qrCode = CodeUtils.createImage(result, 400, 400, null);
                qrCodeImageView.setImageBitmap(qrCode);

            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaStore.Images.Media.insertImage(getContentResolver(), qrCode, "title", "description");
            }
        });


    }
}
