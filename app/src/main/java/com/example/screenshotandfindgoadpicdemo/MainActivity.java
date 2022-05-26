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

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    private  Button btnConfirm;
    private TextView tvShowPoint;
    private ImageView ivShootScreenShot;
    private Button btnStartServer;
    //与截图相关
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private WindowManager windowManager;
    private ImageReader imageReader;
    private final int REQUEST_CODE = 1;

    private Intent serverIntent;

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==  REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){

                //获取屏幕宽度等数据
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);
                int width = display.getWidth();
                int height = display.getHeight();
                int dpi = displayMetrics.densityDpi;
                Log.e("onActivityResult","相关屏幕参数如下："+width+","+height+","+dpi);
                //参数传给intent,让他带给服务
                serverIntent = new Intent(this,ScreenShotService.class);
                serverIntent.putExtra("code",resultCode);
                serverIntent.putExtra("data",data);
                serverIntent.putExtra("width",width);
                serverIntent.putExtra("height",height);
                serverIntent.putExtra("dpi",dpi);

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConfirm = findViewById(R.id.btn_confirm);
        tvShowPoint = findViewById(R.id.tv_show_point);
        btnConfirm.setOnClickListener(new Onclick());
        btnStartServer = findViewById(R.id.btn_startserver);
        btnStartServer.setOnClickListener(new Onclick());
        tvShowPoint.setOnClickListener(new Onclick());
        ivShootScreenShot = findViewById(R.id.iv_show_screenshot);

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent,REQUEST_CODE);
    }


    private class Onclick implements View.OnClickListener{
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                //按下确认后就开始截图
                case R.id.btn_confirm:
                    Image image = imageReader.acquireLatestImage();
                    int width = image.getWidth();
                    int height = image.getHeight();

                    final Image.Plane[] planes = image.getPlanes();
                    final ByteBuffer byteBuffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * width;

                    Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(byteBuffer);
                    ivShootScreenShot.setImageBitmap(bitmap);
                    image.close();
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