package com.example.administrator.screenrecordtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.cpp.opencv_core;

import java.io.File;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

/**
 * Created by Jason.yu on 2015/11/9.
 */
public class VideoCapture {

    /**
     * 获取SDCard的目录路径功能
     *
     * @return
     */
    private static String getSDCardPath() {
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }

    private static String savePath="";

    private static void makeDir() {
        savePath = getSDCardPath() + "/AndyDemo/ScreenImage";
        File path = new File(savePath);
        if (!path.exists()) {
            path.mkdirs();
        }
    }
    public static void fun(Context mContext){
        try {
            makeDir();
            Bitmap testBitmap = getImageByPath(savePath+"/Screen_" + 1 + ".png");
            File dir=new File(savePath);

            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
                    savePath+"/test2.mp4", testBitmap.getWidth(),
                    testBitmap.getHeight());
            recorder.setFormat("mp4");
            recorder.setFrameRate(6);// 录像帧率
            recorder.start();
            int index = 1;
            while (index < dir.listFiles().length-2) {
                opencv_core.IplImage image = cvLoadImage(savePath+"/Screen_" + index + ".png");
                recorder.record(image);
                index++;
            }
            Log.d("test", "录制完成....");
            recorder.stop();
            new AlertDialog.Builder(mContext).setTitle("info").setMessage("录制完成....").setPositiveButton("确定",null).show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static Bitmap getImageByPath(String path) {
        return BitmapFactory.decodeFile(path);
    }
}

