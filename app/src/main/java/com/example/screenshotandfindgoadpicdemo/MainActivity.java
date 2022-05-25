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
    //与截图相关
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private WindowManager windowManager;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;

    private final int REQUEST_CODE = 1;

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==  REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode,data);
                //获取屏幕宽度等数据
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();

                Intent intent = new Intent(this,ScreenShotService.class);
                intent.putExtra("code",resultCode);
                intent.putExtra("date",data);

                imageReader = ImageReader.newInstance(display.getWidth(),display.getHeight(),
                        PixelFormat.RGBA_8888,1);
                Log.e("onActivityResult"," imageReader对象是"+imageReader);

                virtualDisplay = mediaProjection.createVirtualDisplay("screenshot",display.getWidth(),display.getHeight(),displayMetrics.densityDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,imageReader.getSurface(),null,null);
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
        tvShowPoint.setOnClickListener(new Onclick());
        ivShootScreenShot = findViewById(R.id.iv_show_screenshot);

        //生成截图MediaProject
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, REQUEST_CODE);
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
            }
        }
    }
}