package com.example.screenshotandfindgoadpicdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveTask extends AsyncTask<Image, Void, int[]> {
    public String fileURL;
    public String fileName;
    public int TAG = 0;//用这个标签确定是否执行过发送图片task
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public boolean ifSaveImage;//是否保存截图为本地文件

    public SaveTask(Context context, Boolean ifSaveImage){
        super();
        this.context = context;
        this.ifSaveImage = ifSaveImage;
    }

    /**
     * 不能在这里更新界面UI
     * @param args
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected int[] doInBackground(Image... args) {
        int[] pointLocation = new int[2];
        if (null == args || 1 > args.length || null == args[0]) {
            return null;
        }

        Image image = args[0];
        Log.e("SaveTask","image:"+image);

        int width;
        int height;
        try {
            width = image.getWidth();
            height = image.getHeight();
        } catch (IllegalStateException e) {
            return null;
        }
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();

        // 每个像素的间距
        int pixelStride = planes[0].getPixelStride();
        // 总的间距
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        image.close();
        if (null != bitmap) {
            //在截图里找到小图片从而返回目标按钮坐标
            Bitmap goalBitmap = getBitmap(context,R.drawable.goal_image);
            pointLocation = FindGoalImage.findImage(bitmap,goalBitmap);
        }
        return pointLocation;
    }

    /**
     *  1.将执行结果返回到UI
     *  2.接收线程任务执行结果
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onPostExecute(int[] location) {
        super.onPostExecute(location);
        MyApplication myApplication = (MyApplication) context.getApplicationContext();
        //x，y分别为坐标值
        int x = location[0];
        int y = location[1];
        myApplication.getIntermediaryl().getMainActivity().tvShowPoint.setText("坐标是："+"["+x+","+y+"]");
        if (ScreenShot.surface.isValid()) {
            ScreenShot.surface.release();
        }
    }

    // 输出目录
    public String createFile() {
        String outDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String date = simpleDateFormat.format(new Date());
        fileName = date + ".png";
        return outDir + fileName;
    }

    /**
     * 从资源图片获取bitmap
     * @param context
     * @param resId
     * @return
     */
    public  Bitmap getBitmap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        context.getResources().openRawResource(resId, value);
        options.inTargetDensity = value.density;
        options.inScaled=false;//不缩放
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }

}
