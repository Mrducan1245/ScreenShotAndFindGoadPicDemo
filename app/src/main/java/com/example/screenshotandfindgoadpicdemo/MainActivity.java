package com.example.screenshotandfindgoadpicdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    private  Button btnConfirm;
    private TextView tvShowPoint;
    public ImageView ivShootScreenShot;
    private Button btnStartServer;
    //与截图相关
    private MediaProjectionManager mediaProjectionManager;
    private final int REQUEST_CODE = 1;
    private MyApplication myApplication;
    private Intent serverIntent;

    private ImageReader imageReader;
    private ScreenShotService screenShotService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        myApplication = (MyApplication) getApplication();

        //创建中介类，并把自己传给该类
        myApplication.getIntermediaryl().setMainActivity(this);

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent,REQUEST_CODE);
    }

    private void bindView() {
        btnConfirm = findViewById(R.id.btn_confirm);
        tvShowPoint = findViewById(R.id.tv_show_point);
        btnConfirm.setOnClickListener(new Onclick());
        btnStartServer = findViewById(R.id.btn_startserver);
        btnStartServer.setOnClickListener(new Onclick());
        tvShowPoint.setOnClickListener(new Onclick());
        ivShootScreenShot = findViewById(R.id.iv_show_screenshot);
    }


    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==  REQUEST_CODE && resultCode == Activity.RESULT_OK){
            serverIntent = new Intent(this,ScreenShotService.class);
            serverIntent.putExtra("resultCode",Activity.RESULT_OK);
            serverIntent.putExtra("data",data);
            mediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            myApplication.setMediaProjectionManager(mediaProjectionManager);
        }
    }


    private class Onclick implements View.OnClickListener{
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                //按下确认后就开始截图
                case R.id.btn_confirm:
                    if (myApplication.getIntermediaryl().getScreenShotService() == null){
                        Toast.makeText(view.getContext(),"服务没有启动，请先启动服务",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    screenShotService =  myApplication.getIntermediaryl().getScreenShotService();
                    screenShotService.startScreenShot();
                    ivShootScreenShot.setImageBitmap(screenShotService.bitmap);
                    Toast.makeText(view.getContext(),"截图成功",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_show_point:
                    break;

                case R.id.btn_startserver:
                    //启动服务并且把有关mediprojection的东西发出去
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(serverIntent);
                    }else {
                        startService(serverIntent);
                    }
                    break;
            }
        }
    }
}