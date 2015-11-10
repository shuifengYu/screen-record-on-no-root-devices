package com.example.administrator.screenrecordtest;

import android.animation.AnimatorSet;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.model.Picture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getName();
    Button btn_img, btn_video, btn_stop,btn_compound;
    int index = 0;
    int img_index = 0;
    Bitmap bmp;
    String savePath;
    int w, h;
    boolean flag = false;
    Display display;
    View decorview;
    ImageView iv_rotate;
    Thread thread;
    Animation animator;
    private Handler mHandler;
     Bitmap temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeDir();
        initHandler();
        initView();
        initThread();
        addActionListener();
    }

    private void initHandler() {
        mHandler = new Handler();
    }

    private void initThread() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    index++;
//                    final Bitmap temp = decorview.getDrawingCache();
                    temp = decorview.getDrawingCache();
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                bmp = Bitmap.createBitmap(temp, 0, 0, w, h);
                                decorview.destroyDrawingCache();
                                GetandSaveCurrentImage();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            super.run();
                        }
                    }.start();
                    mHandler.postDelayed(thread, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    public static Picture fromBitmap(Bitmap src) {
        Picture dst = Picture.create(src.getWidth(), src.getHeight(), org.jcodec.common.model.ColorSpace.RGB);
//        long d1=SystemClock.elapsedRealtime();
        fromBitmap(src, dst);
//        long d2=SystemClock.elapsedRealtime();
//        Log.d(TAG, d2-d1+"");
        return dst;
    }

    private Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(savePath ,pathString);
            Log.d(TAG,"file Path："+file.getAbsolutePath());
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(savePath+"/"+pathString);
            }else{
                Log.d(TAG,"file not exist");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }


        return bitmap;
    }

    public static void fromBitmap(Bitmap src, Picture dst) {
        int[] dstData = dst.getPlaneData(0);
        int[] packed = new int[src.getWidth() * src.getHeight()];

        src.getPixels(packed, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());

        for (int i = 0, srcOff = 0, dstOff = 0; i < src.getHeight(); i++) {
            for (int j = 0; j < src.getWidth(); j++, srcOff++, dstOff += 3) {
                int rgb = packed[srcOff];
                dstData[dstOff] = (rgb >> 16) & 0xff;
                dstData[dstOff + 1] = (rgb >> 8) & 0xff;
                dstData[dstOff + 2] = rgb & 0xff;
            }
        }
    }


    /**
     * 获取和保存当前屏幕的截图
     */
    private void GetandSaveCurrentImage() throws Exception {

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
    }


    private void addActionListener() {
        btn_img.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_video.setOnClickListener(this);
        btn_compound.setOnClickListener(this);

    }

    private void initView() {
        btn_img = (Button) findViewById(R.id.btn_begin_by_img);
        btn_video = (Button) findViewById(R.id.btn_begin_by_video);
        btn_stop = (Button) findViewById(R.id.btn_begin_by_stop);
        btn_compound= (Button) findViewById(R.id.btn_compound);
        iv_rotate = (ImageView) findViewById(R.id.iv_rotate);
        AnimatorSet as = new AnimatorSet();
        display = getWindowManager().getDefaultDisplay();
        w = display.getWidth();
        h = display.getHeight();
        bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        decorview = this.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        animator = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animator.setInterpolator(new LinearInterpolator());
    }

    private void makeDir() {
        savePath = getSDCardPath() + "/AndyDemo/ScreenImage";
        File path = new File(savePath);
        if (!path.exists()) {
            path.mkdirs();
        }
    }

    /**
     * 获取SDCard的目录路径功能
     *
     * @return
     */
    private String getSDCardPath() {
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_img) {
            startTranscribe();

        } else if (v == btn_video) {
            new AlertDialog.Builder(MainActivity.this).setTitle("title").setMessage("test dialog").setPositiveButton("submit",null).show();
//            try {
//                execCommand("screenrecord " + index + ".mp4");
//                index++;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        } else if (v == btn_stop) {
            stop();
        }else if(v==btn_compound){
            compoundToVideo();
        }
    }

    private void compoundToVideo(){
        new Thread(){
            public void run(){
                VideoCapture.fun(MainActivity.this);
            }
        }.start();
    }

    private void startTranscribe() {
        iv_rotate.startAnimation(animator);
        flag = true;
        mHandler.postDelayed(thread, 200);
    }


    private void stop() {
        Log.d("MainActivity", "stop()");
        iv_rotate.clearAnimation();
        mHandler.removeCallbacks(thread);
    }

    public void execCommand(String command) throws IOException {
        // start the ls command running
        //String[] args =  new String[]{"sh", "-c", command};
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);        //这句话就是shell与高级语言间的调用
        //如果有参数的话可以用另外一个被重载的exec方法
        //实际上这样执行时启动了一个子进程,它没有父进程的控制台
        //也就看不到输出,所以我们需要用输出流来得到shell执行后的输出
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        // read the ls output
        String line = "";
        StringBuilder sb = new StringBuilder(line);
        while ((line = bufferedreader.readLine()) != null) {
            //System.out.println(line);
            sb.append(line);
            sb.append('\n');
        }
        //tv.setText(sb.toString());
        //使用exec执行不会等执行成功以后才返回,它会立即返回
        //所以在某些情况下是很要命的(比如复制文件的时候)
        //使用wairFor()可以等待命令执行完成以后才返回
        try {
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }

}
