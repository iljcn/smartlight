package com.ilj.smartlight;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bifan.detectlib.FaceDetectTextureView;
import com.bifan.detectlib.FaceDetectView;

public class DeviceActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{
    private String tag = "DeviceActivity";
    private String TAG = "DeviceActivity";
    private boolean isSavingPic = false;
    private FaceDetectView faceDetectView;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";


    private String devId;
    ITuyaDevice mDevice;

    private Button btn_open;
    private Button btn_close;

    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private SeekBar seekBar3;
    private SeekBar seekBar4;
    private SeekBar seekBar5;

    private int mRed = 128;
    private int mGreen = 128;
    private int mBlue = 128;

    private long mSystem = 0;
    /**
     * @param n
     * @Title: intTohex
     * @Description: int型转换成16进制
     * @return: String
     */
    public static String intTohex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (n != 0) {
            s = s.append(b[n % 16]);
            n = n / 16;
        }
        a = s.reverse().toString();
        if ("".equals(a)) {
            a = "00";
        }
        if (a.length() == 1) {
            a = "0" + a;
        }
        return a;
    }

    public static class ConfirmationDialogFragment extends DialogFragment {
        private static final String ARG_MESSAGE = "message";
        private static final String ARG_PERMISSIONS = "permissions";
        private static final String ARG_REQUEST_CODE = "request_code";
        private static final String ARG_NOT_GRANTED_MESSAGE = "not_granted_message";

        public static ConfirmationDialogFragment newInstance(@StringRes int message,
                                                             String[] permissions, int requestCode, @StringRes int notGrantedMessage) {
            ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_MESSAGE, message);
            args.putStringArray(ARG_PERMISSIONS, permissions);
            args.putInt(ARG_REQUEST_CODE, requestCode);
            args.putInt(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage);
            fragment.setArguments(args);
            return fragment;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ConfirmationDialogFragment.newInstance(R.string.camera_permission_confirmation,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION,
                    R.string.camera_permission_not_granted)
                    .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (faceDetectView != null) {
            faceDetectView.release();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        seekBar1 = findViewById(R.id.seekBar1);
        seekBar2 = findViewById(R.id.seekBar2);
        seekBar3 = findViewById(R.id.seekBar3);
        seekBar4 = findViewById(R.id.seekBar4);
        seekBar5 = findViewById(R.id.seekBar5);

        seekBar1.setProgress(100);
        seekBar2.setProgress(100);
        seekBar3.setProgress(100);
        seekBar4.setProgress(100);
        seekBar5.setProgress(100);

        seekBar1.setOnSeekBarChangeListener(this);
        seekBar2.setOnSeekBarChangeListener(this);
        seekBar3.setOnSeekBarChangeListener(this);
        seekBar4.setOnSeekBarChangeListener(this);
        seekBar5.setOnSeekBarChangeListener(this);


        btn_open=findViewById(R.id.button);
        btn_close=findViewById(R.id.button2);

        devId = getIntent().getStringExtra("devid");
        mDevice = TuyaHomeSdk.newDeviceInstance(devId);
        //ITuyaLightDevice lightDevice = new TuyaLightDevice(devId);

        mDevice.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                //mView.updateView();
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {

            }

            @Override
            public void onNetworkStatusChanged(String devId, boolean status) {

            }

            @Override
            public void onDevInfoUpdate(String devId) {

            }
        });



        faceDetectView = findViewById(R.id.faceDetectView);
        faceDetectView.setFramePreViewListener(new FaceDetectTextureView.IFramePreViewListener() {
            @Override
            public boolean onFrame(Bitmap preFrame) {
                //每一帧回调
                //这个这帧preFrame处理了就是进行了回收，返回true
                //否则返回false，内部进行回收处理
                //return false;

                if (isSavingPic == false) {
                    isSavingPic = true;
                    executorService.submit(new SavePicRunnable(preFrame,0));
                }

                return true;

            }

            @Override
            public boolean onFaceFrame(Bitmap preFrame, FaceDetector.Face[] faces) {
                //faces是检测出来的人脸参数
                //检测到人脸的回调,保存人脸图片到本地
                if (isSavingPic == false) {
                    isSavingPic = true;
                    executorService.submit(new SavePicRunnable(preFrame,faces.length));
                }

                // Log.i(tag, "当前图片人脸个数：" + faces.length);
                //这个这帧preFrame处理了就是进行了回收，返回true
                //否则返回false，内部进行回收处理
                return true;

            }



        });



        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String dps;
// , "102": "00ffff"
                dps = "{\"1\": true}";
                //dps = "{\"102\": \"00ffff\"}";
                mDevice.publishDps(dps, new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(DeviceActivity.this, "开灯失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(DeviceActivity.this, "开灯成功", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dps;
//, "102": "0"
                dps = "{\"1\": false}";
                mDevice.publishDps(dps, new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(DeviceActivity.this, "关灯失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(DeviceActivity.this, "关灯成功", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });






    }

    /*
     * SeekBar停止滚动的回调函数
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //Log.d(TAG, "seekid:"+seekBar.getId()+", progess"+progress);
        String dps = "";

        switch(seekBar.getId()) {
            case R.id.seekBar1:{
                dps = "{\"3\": " + String.valueOf(seekBar.getProgress()) + " }";
                break;
            }
            case R.id.seekBar2: {
                dps = "{\"4\": " + String.valueOf(seekBar.getProgress()) + " }";
                break;
            }
            case R.id.seekBar3:
            {

                dps = "{\"5\": \"" + intTohex(seekBar3.getProgress()) + intTohex(seekBar4.getProgress()) + intTohex(seekBar5.getProgress()) + "0000ffff\" }";
                break;
            }
            case R.id.seekBar4:
            {

                dps = "{\"5\": \"" + intTohex(seekBar3.getProgress()) + intTohex(seekBar4.getProgress()) + intTohex(seekBar5.getProgress()) + "0000ffff\" }";
                break;
            }
            case R.id.seekBar5:
            {

                dps = "{\"5\": \"" + intTohex(seekBar3.getProgress()) + intTohex(seekBar4.getProgress()) + intTohex(seekBar5.getProgress()) + "0000ffff\" }";
                break;
            }
            default:
                break;
        }

        Log.i(TAG,dps);

        mDevice.publishDps(dps, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Toast.makeText(DeviceActivity.this, "operate failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(DeviceActivity.this, "operate success", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*
     * SeekBar开始滚动的回调函数
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {


    }


    public void startDetect(View view) {
        if (!faceDetectView.isHasInit()) {
            //必须是在view可见后进行初始化
            faceDetectView.initView();
            faceDetectView.initCamera();
            faceDetectView.getDetectConfig().CameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
            faceDetectView.getDetectConfig().EnableFaceDetect = true;
            faceDetectView.getDetectConfig().MinDetectTime = 1000;
            faceDetectView.getDetectConfig().Simple = 0.6f;//图片检测时的压缩取样率，0~1，越小检测越流畅
            faceDetectView.getDetectConfig().MaxDetectTime =2000;//进入智能休眠检测，以2秒一次的这个速度检测
            faceDetectView.getDetectConfig().EnableIdleSleepOption=true;//启用智能休眠检测机制
            faceDetectView.getDetectConfig().IdleSleepOptionJudgeTime=1000*10;//1分钟内没有检测到人脸，进入智能休眠检测
        }
        faceDetectView.startCameraPreview();
    }

    public void endDetect(View view) {
        faceDetectView.stopCameraPreview();
        faceDetectView.getFaceRectView().clearBorder();
    }


    private class SavePicRunnable implements Runnable {
        Bitmap bitmap;
        int facenum;
        SavePicRunnable(Bitmap bitmap, int facenum) {
            this.bitmap = bitmap;
            this.facenum = facenum;
        }

        @Override
        public void run() {
            //saveFacePicToLocal(bitmap);
            String dps = "";
            int red;
            int green;
            int blue;



            if(facenum > 0)
            {
                mSystem = System.currentTimeMillis();
                red = 255;
                green = 255;
                blue = 255;
            }else {

                if(System.currentTimeMillis() - mSystem < 3000)
                {
                    red = mRed;
                    green = mGreen;
                    blue = mBlue;
                }
                else {
                    float redpixs = 0;
                    float greenpixs = 0;
                    float bluepixs = 0;

                    int width = bitmap.getWidth();

                    int height = bitmap.getHeight(); // 保存所有的像素的数组，图片宽×高

                    int[] pixels = new int[width * height];

                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

                    for (int i = 0; i < pixels.length; i++) {
                        int clr = pixels[i];

                        int redpix = (clr & 0x00ff0000) >> 16; // 取高两位

                        int greenpix = (clr & 0x0000ff00) >> 8; // 取中两位

                        int bluepix = clr & 0x000000ff; // 取低两位 Log.d("tag", "r=" + red + ",g=" + green + ",b=" + blue);

                        redpixs += redpix;
                        greenpixs += greenpix;
                        bluepixs += bluepix;
                    }

                    red = (int) (redpixs / (width * height));
                    green = (int) (greenpixs / (width * height));
                    blue = (int) (bluepixs / (width * height));
                }

            }

            if (mRed != red || mGreen != green || mBlue != blue) {
                mRed = red;
                mGreen = green;
                mBlue = blue;

                dps = "{\"5\": \"" + intTohex(mRed) + intTohex(mGreen) + intTohex(mBlue) + "0000ffff\" }";

                mDevice.publishDps(dps, new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        //Toast.makeText(DeviceActivity.this, "operate failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        //Toast.makeText(DeviceActivity.this, "operate success", Toast.LENGTH_SHORT).show();
                    }
                });

            }


            bitmap.recycle();

            isSavingPic = false;
        }
    }




    private void saveFacePicToLocal(Bitmap bitmap) {
        String picPath = Environment.getExternalStorageDirectory() + "/face.jpg";
        FileOutputStream fileOutputStream = null;
        File facePicFile = new File(picPath);
        try {
            facePicFile.createNewFile();
        } catch (IOException e) {
            Log.e(tag, "保存失败" + e.toString() + "," + picPath);
            e.printStackTrace();
        }
        try {
            fileOutputStream = new FileOutputStream(facePicFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (fileOutputStream != null) {
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(tag, e.toString());
            }
        }
        bitmap.recycle();
    }





}
