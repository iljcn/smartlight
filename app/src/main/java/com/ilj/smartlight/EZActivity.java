package com.ilj.smartlight;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.builder.ActivatorBuilder;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.ActivatorModelEnum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EZActivity extends AppCompatActivity {

    private Button btn_ez;//登录按钮
    private String ssidName,password;//获取的用户名，密码，加密密码
    private EditText ssid_input,password_input_ez;//编辑框

    private long homeId;
    //private long roomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ez);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        homeId = getIntent().getLongExtra("homeid", 0);
        //roomId = getIntent().getLongExtra("roomid", 0);

        btn_ez=findViewById(R.id.btn_ez);

        ssid_input=findViewById(R.id.ssid_input);
        password_input_ez=findViewById(R.id.password_input_ez);

        btn_ez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ssidName=ssid_input.getText().toString().trim();
                password=password_input_ez.getText().toString().trim();

                // TextUtils.isEmpty
                if(TextUtils.isEmpty(ssidName)){
                    Toast.makeText(EZActivity.this, "请输入SSID", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(EZActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                    // md5Psw.equals(); 判断，输入的密码加密后，是否与保存在SharedPreferences中一致
                }


                TuyaHomeSdk.getActivatorInstance().getActivatorToken(homeId, new ITuyaActivatorGetToken() {

                    @Override
                    public void onSuccess(String token) {

                        ActivatorBuilder builder = new ActivatorBuilder()
                            .setSsid(ssidName)
                            .setContext(EZActivity.this)
                            .setPassword(password)
                            .setActivatorModel(ActivatorModelEnum.TY_EZ)
                            .setToken(token)
                            .setListener(new ITuyaSmartActivatorListener() {

                                @Override
                                public void onError(String errorCode, String errorMsg) {
                                 Toast.makeText(EZActivity.this, "配网失败", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onActiveSuccess(DeviceBean devResp) {
                                 //多个设备同时配网，将多次回调

                                 Toast.makeText(EZActivity.this, "配网成功", Toast.LENGTH_SHORT).show();

                                 /*TuyaHomeSdk.newRoomInstance(roomId).addDevice(devResp.devId, new IResultCallback() {
                                     @Override
                                     public void onSuccess() {
                                         // do something
                                         Toast.makeText(EZActivity.this, "addDevice成功", Toast.LENGTH_SHORT).show();
                                     }
                                     @Override
                                     public void onError(String code, String error) {
                                         // do something
                                         Toast.makeText(EZActivity.this, "addDevice失败", Toast.LENGTH_SHORT).show();
                                     }
                                 });*/

                                 finish();


                                }

                                @Override
                                public void onStep(String step, Object data) {

                                 Toast.makeText(EZActivity.this, "配网Step:" + step, Toast.LENGTH_SHORT).show();

                                 finish();
                                }
                             });

                        ITuyaActivator mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newMultiActivator(builder);
                        mTuyaActivator.start();

                    }

                    @Override
                    public void onFailure(String s, String s1) {

                    }
                });



            }



        });

    }

}
