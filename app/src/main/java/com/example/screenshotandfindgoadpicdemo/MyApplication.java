package com.example.screenshotandfindgoadpicdemo;

import android.app.Application;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

public class MyApplication extends Application {

    public MediaProjectionManager getMediaProjectionManager() {
        return mediaProjectionManager;
    }

    public void setMediaProjectionManager(MediaProjectionManager mediaProjectionManager) {
        this.mediaProjectionManager = mediaProjectionManager;
    }

    private MediaProjectionManager mediaProjectionManager;
    private Intent intent;
    private boolean ifSaveImage = false;

    public boolean isIfSaveImage() {
        return ifSaveImage;
    }

    public void setIfSaveImage(boolean ifSaveImage) {
        this.ifSaveImage = ifSaveImage;
    }
}
