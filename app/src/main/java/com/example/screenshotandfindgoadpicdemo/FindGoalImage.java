package com.example.screenshotandfindgoadpicdemo;

import android.graphics.Bitmap;
import android.util.Log;

public class FindGoalImage {

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
            colorList[(width-1)/2][h] = color;
        }
        //横向所有颜色值
        for (int w = 0;w<width;w++){
            int color = bitmap.getPixel(w,(height-1)/2);
            colorList[w][(height-1)/2] = color;
        }
//        for (int w = 0;w<width;w++){
//            for (int h = 0;h<height;h++){
//                int color  = bitmap.getPixel(w,h);
//                colorList[w][h] = color;
//            }

        return colorList;
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
