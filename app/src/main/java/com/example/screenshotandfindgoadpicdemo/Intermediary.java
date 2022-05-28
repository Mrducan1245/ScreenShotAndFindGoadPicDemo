package com.example.screenshotandfindgoadpicdemo;

public class Intermediary {
    public ScreenShotService getScreenShotService() {
        return screenShotService;
    }

    public void setScreenShotService(ScreenShotService screenShotService) {
        this.screenShotService = screenShotService;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private ScreenShotService screenShotService;
    private MainActivity mainActivity;
}
