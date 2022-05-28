package com.example.screenshotandfindgoadpicdemo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

public class ScreenShotService extends Service {

    private WindowManager mWindowManager;
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private MyApplication application;

    private int resultCode;
    private Intent resultData;
    public Bitmap bitmap;

    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        application = (MyApplication) getApplication();
        application.getIntermediaryl().setScreenShotService(ScreenShotService.this);
        mediaProjectionManager = application.getMediaProjectionManager();
    }

    public ScreenShotService() {
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            resultCode = intent.getIntExtra("code",-1);
            resultData = intent.getParcelableExtra("data");
            //开启前台服务
            startForeground(1,setNotification());
            //获取medioProjection必须放在startForeground后面
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode,resultData);
        }
        return super.onStartCommand(intent, flags, startId);
//        return 1;
    }

    /**前台服务必须设置好相关通知
     *
     */
    private Notification setNotification(){
        Notification notification= null;

        String CHANNEL_ONE_NAME = "Channel One";
        String CHANNEL_ONE_ID = "com.primedu.cn";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        registerNotifyChannel(notificationManager,CHANNEL_ONE_ID,CHANNEL_ONE_NAME);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this,CHANNEL_ONE_ID);
            builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("截图服务")
                    .setContentText("开启此服务将会实时截图");
            notification = builder.build();

        }
        return notification;
    }

    /**
     * 注册通知通道（8.0以上必须要设置的，不然通知没法用）
     * @param notificationManager
     */
    private void registerNotifyChannel(NotificationManager notificationManager,String id,String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id,name,NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("开启实时截屏服务");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 调用此函数来全局截图
     */
    public void startScreenShot(){
        ScreenShot.getWH(mWindowManager);
        ScreenShot.createImageReader();
        ScreenShot.beginScreenShot(mediaProjection,ScreenShotService.this,application.isIfSaveImage());
        bitmap = ScreenShot.bitmap;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}