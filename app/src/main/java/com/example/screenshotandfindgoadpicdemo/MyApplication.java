package com.example.screenshotandfindgoadpicdemo;

import android.app.Application;
import android.media.projection.MediaProjectionManager;

public class MyApplication extends Application {
    public MediaProjectionManager getMediaProjectionManager() {
        return mediaProjectionManager;
    }

    public void setMediaProjectionManager(MediaProjectionManager mediaProjectionManager) {
        this.mediaProjectionManager = mediaProjectionManager;
    }

    private MediaProjectionManager mediaProjectionManager;
}
