package com.example.screenshotandfindgoadpicdemo;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class FindGoalImage {
    //统计每个颜色值数量的数组可以用hashMap
    public static final HashMap<Integer,Integer> hashMap = new HashMap<>();
    public static  int[] mostColor = new int[2];

    private static int[] lastLocation;//目标图像最后的点坐标
    private static int[] startLocation;//目标图像最开始的点坐标

    /**
     * 转化为二进制图片
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    private static char[][] image2charImage(Bitmap bitmap, int width, int height){
        char[][] charList = new char[width][height];//转化为二进制图像

        for (int w =0;w<width;w++){
            for (int h = 0;h<height;h++){
                int colorInt = bitmap.getPixel(w,h);
                if (colorInt == mostColor[0]){
                    charList[w][h] = 1;
                }
            }
        }
        return charList;
    }

    /**
     * 获得目标图片每个像素的android颜色值
     * @param bitmap bitmap对象
     * @param width 图片的宽度
     * @param height 图片的高度
     * @return
     */
    private static char[][] getGoalImageRgb(Bitmap bitmap, int width, int height){
        char[][] colorList ;
        //纵向所有颜色值
        for (int h = 0;h<height;h++){
            int color = bitmap.getPixel((width-1)/2,h);
            setColorHasMap(color);
        }

        //横向所有颜色值
        for (int w = 0;w<width;w++){
            int color = bitmap.getPixel(w,(height-1)/2);
            setColorHasMap(color);
        }

        getMostColor();
        colorList = image2charImage(bitmap, width, height);

        //然后再获取图像边界范围坐标
        Stack<int[]> chars = new Stack<>();

        boolean isFirstPoint = true;

        for (int x =0;x<width;x++){
            for (int y =0;y<height;y++){
                if (colorList[x][y] == 1){
                    //当第一次得到这个坐标
                    if (isFirstPoint) {
                        startLocation = new int[]{x,y};
                        isFirstPoint = false;
                    }
                    chars.push(new int[]{x,y});
                    if (!chars.isEmpty()){
                        lastLocation = chars.pop();
                    }else {//当stack为空说明弹出的就是组后一个坐标点了
                        break;
                    }
                }
            }
        }
        Log.e("getGoalImageRgb","第一次坐标值是："+"["+startLocation[0]+","+startLocation[1]+"]");
        Log.e("getGoalImageRgb","最后一次坐标值是："+"["+lastLocation[0]+","+lastLocation[1]+"]");
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
    private static void getMostColor(){
        List<Integer> countList = new ArrayList<>(hashMap.values());
        Collections.sort(countList);

        int goalCount = countList.get(countList.size()-1);
        int goalCount0 = countList.get(countList.size()-2);

        int goalColor = 0,goalColor0 = 0;
        Log.e("数量最多颜色值的数量是：",""+goalCount);
        Log.e("数量第二多颜色值的数量是：",""+goalCount0);
        for (Integer j:hashMap.keySet()){
            if (hashMap.get(j) == goalCount){
                goalColor = j;
            }else if (hashMap.get(j) == goalCount0){
                goalColor0 = j;
            }
        }
        Log.e("数量最多颜色值是：",""+goalColor);
        Log.e("数量第二多颜色值是：",""+goalColor0);

        hashMap.clear();

        mostColor[0] = goalColor;
        mostColor[1] = goalColor0;
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

        int priGoalWidth = goalImage.getWidth();
        int priGoalHeight = goalImage.getHeight();


        char[][] screenList = image2charImage(screenShotImage,screenWidth,screenHeight);
        dideImageAreRrturPoint(screenList,screenWidth,screenHeight);

        char[][] goalList = getGoalImageRgb(goalImage, priGoalWidth, priGoalHeight);

        int startX = startLocation[0], startY =startLocation[1];
        int lastX = lastLocation[0],lastY =lastLocation[1];

        int goalWidth = lastX-startX;
        int goalHeight =lastY-startY;
        Log.e("width","目标图片的宽度是："+goalWidth+";目标图片的高度是："+goalHeight);
        Log.e("width","截图图片的宽度是："+screenWidth+";截图图片的高度是："+screenHeight);

        //遍历每个二维数组值，为0那么跳过，当
        for (int x=0;x<=screenWidth-goalWidth;x++){
            for (int y = 0;y<=screenHeight-goalHeight;y++){
                if (screenList[x][y] == 1) {
                    if((goalList[startX-1][startY-1]==screenList[x][y])
                     && (goalList[lastX-1][lastY-1]==screenList[x+goalWidth-1][y+goalHeight-1])) {
                        //返回坐标值
                    pointLocation[0] = (lastLocation[0]-startLocation[0])/2;
                    pointLocation[1] = (lastLocation[1]-startLocation[1])/2;
                    Log.e("坐标值：","x坐标"+pointLocation[0]+"y坐标"+pointLocation[1]);
                    }
                }//如果不为1则直接跳过
            }
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
//            for (int y = 0;y<=screenHeight-goalHeight;y++){
//                if((goalList[goalWidth/2-1][0]^screenList[x+goalWidth/2-1][y])==0
//                        && (goalList[(goalWidth-1)/2][(goalHeight-1)/2]^screenList[x+(goalWidth-1)/2][y+(goalHeight-1)/2])==0
//                        && (goalList[(goalWidth-1)/2][goalHeight-1]^screenList[x+(goalWidth-1)/2][y+goalHeight-1])==0
//                        && (goalList[0][goalHeight/2-1]^screenList[x][y+(goalHeight-1)/2])==0
//                        && (goalList[goalWidth-1][(goalHeight-1)/2]^screenList[x+goalWidth-1][y+(goalHeight-1)/2])==0) {
//                    //返回坐标值
//                    pointLocation[0] = x+goalWidth/2;
//                    pointLocation[1] = y+goalHeight/2;
//                    Log.e("坐标值：","x坐标"+pointLocation[0]+"y坐标"+pointLocation[1]);
//                }
//            }
        }
        return pointLocation;
    }

    /**
     * 把图上的各个块区分出来并返回对应坐标值
     * @param screenList
     * @param screenWidth
     * @param screenHeight
     */
    private static void dideImageAreRrturPoint(char[][] screenList, int screenWidth, int screenHeight) {
        //获取每个片段的二维图形
        ArrayList<int[]> arrayList = new ArrayList<>();
        int tempW = 0,tempH = 0;
        for (int w=0;w<screenWidth;w++){
            for (int h=0;h<screenHeight;h++){
                if (screenList[w][h] == 1){
                    if (w-tempW < 50 && h-tempH < 50){
                        arrayList.add(new int[]{w,h});
                        tempH = h;
                        tempW = w;
                    }
                }
            }
        }
        int[] firstPoint = arrayList.get(0);
        int[] lastPoint = arrayList.get(arrayList.size()-1);
        Log.e("dideImageAreRrturPoint","截图的第一个坐标值是："+firstPoint[0]+","+firstPoint[1]);
        Log.e("dideImageAreRrturPoint","截图的最后一个坐标值是："+lastPoint[0]+","+lastPoint[1]);
    }
}
