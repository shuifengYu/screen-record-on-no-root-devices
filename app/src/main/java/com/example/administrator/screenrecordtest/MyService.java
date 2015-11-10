package com.example.administrator.screenrecordtest;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Jason.yu on 2015/11/6.
 */
public class MyService extends Service {

    String savePath = "";
    boolean flag=false;
    int index=0;
    Bitmap bmp;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra("flag", false)) {
            flag=true;
            savePath = intent.getStringExtra("savePaht");
            start();
        }else{
            flag=false;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void start() {
        while (flag) {
            index++;
            long d1 = SystemClock.elapsedRealtime();
            GetandSaveCurrentImage();
            long d2 = SystemClock.elapsedRealtime();
            Log.d("MainActivity", "time" + index + ":" + (d2 - d1));
        }
    }

    /**
     * 获取和保存当前屏幕的截图
     */
    private void GetandSaveCurrentImage() {
        try {
            //文件
            String filepath = savePath + "/Screen_" + index + ".png";
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
