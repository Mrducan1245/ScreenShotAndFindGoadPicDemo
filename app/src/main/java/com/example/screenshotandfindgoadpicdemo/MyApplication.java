package com.example.screenshotandfindgoadpicdemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        intermediaryl = new Intermediary();
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public MediaProjectionManager getMediaProjectionManager() {
        return mediaProjectionManager;
    }

    public void setMediaProjectionManager(MediaProjectionManager mediaProjectionManager) {
        this.mediaProjectionManager = mediaProjectionManager;
    }

    private MediaProjectionManager mediaProjectionManager;
    private boolean ifSaveImage = true;
    private Intermediary intermediaryl;

    public boolean isIfSaveImage() {
        return ifSaveImage;
    }

    public void setIfSaveImage(boolean ifSaveImage) {
        this.ifSaveImage = ifSaveImage;
    }

    public Intermediary getIntermediaryl() {
        return intermediaryl;
    }

    public void setIntermediaryl(Intermediary intermediaryl) {
        this.intermediaryl = intermediaryl;
    }
}
