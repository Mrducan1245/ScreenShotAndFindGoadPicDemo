package com.example.screenshotandfindgoadpicdemo;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;

public class FindGoalImage {
    //统计每个颜色值数量的数组可以用hashMap
    public static final HashMap<Integer,Integer> hashMap = new HashMap<>();

    /**
     * 获得每个像素的android颜色值
     * @param bitmap bitmap对象
     * @param width 图片的宽度
     * @param height 图片的高度
     * @return
     */
    private static int[][] getImageRgb(Bitmap bitmap,int width,int height){
        int[][] colorList = new int[width][height];
        //纵向所有颜色值
        for (int h = 0;h<height;h++){
            int color = bitmap.getPixel((width-1)/2,h);
            setColorHasMap(color);
            colorList[(width-1)/2][h] = color;
        }
        //横向所有颜色值
        for (int w = 0;w<width;w++){
            int color = bitmap.getPixel(w,(height-1)/2);
            setColorHasMap(color);
            colorList[w][(height-1)/2] = color;
        }
        getMostColor();
        return colorList;
    }

    /**
     * 把颜色值添加到hashMap里操作它
     * @param color
     * @return
     */
    private static HashMap<Integer, Integer> setColorHasMap( int color){
        if (hashMap.containsKey(color)){
            int count = hashMap.get(color);
            count+=1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                hashMap.replace(color,count);
            }
        }else {
            hashMap.put(color,1);
        }
        return hashMap;
    }

    /**
     * 从hasnMap里找到数量最多和第二多的颜色值
     * @return
     */
    private static int[] getMostColor(){
        int goalCount = Collections.max(hashMap.values());
        int goalCount0 = Collections.min(hashMap.values());
        int goalColor = 0;
        int goalColor0 = 0;
        Log.e("数量最多颜色值的数量是：",""+goalCount);
        Log.e("数量最少颜色值的数量是：",""+goalCount0);
        for (Integer j:hashMap.keySet()){
            if (hashMap.get(j) == goalCount){
                goalColor = j;
            }else if (hashMap.get(j) == goalCount0){
                goalColor0 = j;
            }
        }
        Log.e("数量最多颜色值是：",""+goalColor);
        Log.e("数量最多颜色值是：",""+goalColor0);
        hashMap.clear();
        return new int[]{goalColor,goalColor0};
    }

    /**
     * 调用函数时传入要查找的图片和范围图片
     * @param screenShotImage
     * @param goalImage
     * @return
     */
    public static int[] findImage(Bitmap screenShotImage, Bitmap goalImage){
        //存储坐标值
        int[] pointLocation = new int[2];
        //获取Bitmap的宽度和长度
        int screenWidth = screenShotImage.getWidth();
        int screenHeight = screenShotImage.getHeight();
        int goalWidth = goalImage.getWidth();
        int goalHeight =goalImage.getHeight();

        Log.e("width","目标图片的宽度是："+goalWidth+";目标图片的高度是："+goalHeight);

        int[][] screenList = getImageRgb( screenShotImage, screenWidth, screenHeight);
        int[][] goalList = getImageRgb(goalImage, goalWidth, goalHeight);

        //当目标图片与截图上存在上、中、下、左、右几个位置颜色值基本对应时就说明基本找到了
        for (int x=0;x<=screenWidth-goalWidth;x++){
//            for (int y = 0;y<=screenHeight-goalHeight;y++){
//                if((goalList[0][0]^screenList[x][y])==0
//                  && (goalList[0][goalHeight-1]^screenList[x][y+goalHeight-1])==0
//                  && (goalList[goalWidth-1][goalHeight-1]^screenList[x+goalWidth-1][y+goalHeight-1])==0
//                  && (goalList[goalWidth-1][0]^screenList[x+goalWidth-1][y])==0) {
//                    //返回坐标值
//                    pointLocation[0] = x+goalWidth/2;
//                    pointLocation[1] = y+goalWidth/2;
//                    Log.e("坐标值：","x坐标"+pointLocation[0]+"y坐标"+pointLocation[1]);
//                }
//            }
            for (int y = 0;y<=screenHeight-goalHeight;y++){
                if((goalList[goalWidth/2-1][0]^screenList[x+goalWidth/2-1][y])==0
                        && (goalList[(goalWidth-1)/2][(goalHeight-1)/2]^screenList[x+(goalWidth-1)/2][y+(goalHeight-1)/2])==0
                        && (goalList[(goalWidth-1)/2][goalHeight-1]^screenList[x+(goalWidth-1)/2][y+goalHeight-1])==0
                        && (goalList[0][goalHeight/2-1]^screenList[x][y+(goalHeight-1)/2])==0
                        && (goalList[goalWidth-1][(goalHeight-1)/2]^screenList[x+goalWidth-1][y+(goalHeight-1)/2])==0) {
                    //返回坐标值
                    pointLocation[0] = x+goalWidth/2;
                    pointLocation[1] = y+goalHeight/2;
                    Log.e("坐标值：","x坐标"+pointLocation[0]+"y坐标"+pointLocation[1]);
                }
            }
        }
        return pointLocation;
    }
}
