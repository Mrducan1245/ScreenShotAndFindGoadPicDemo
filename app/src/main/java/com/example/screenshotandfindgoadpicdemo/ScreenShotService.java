package com.example.screenshotandfindgoadpicdemo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ScreenShotService extends Service {
    private int resultCode;
    private MediaProjection mediaProjection;
    private MediaProjectionManager mediaProjectionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Log.e("service","调用了oncreat函数");
    }

    public ScreenShotService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("service","调用了onStartCommand函数");
        //从intet里获取相关数据
        int resultCode = intent.getIntExtra("code",1);
        int width = intent.getIntExtra("width",1080);
        int height = intent.getIntExtra("height",1920);
        int dpi = intent.getIntExtra("dpi",1080);
        Intent data = intent.getParcelableExtra("date");

        @SuppressLint("WrongConstant") ImageReader imageReader = ImageReader.newInstance(width,height,
                PixelFormat.RGBA_8888,1);
        Log.e("onActivityResult"," imageReader对象是"+imageReader);
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode,data);
        mediaProjection.createVirtualDisplay("screenshot",width,height,dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,imageReader.getSurface(),null,null);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder1 = new Notification.Builder(this);
        builder1.setSmallIcon(R.drawable.ic_launcher_background);
        builder1.setContentTitle("截图服务");
        Notification notificantion =builder1.build();
        startForeground(1,notificantion);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}