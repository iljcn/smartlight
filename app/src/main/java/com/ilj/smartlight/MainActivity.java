package com.ilj.smartlight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.api.IRegisterCallback;
import com.tuya.smart.android.user.api.IValidateCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.RoomBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.home.sdk.callback.ITuyaRoomResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btn_login;//登录按钮
    private String uerName,password;//获取的用户名，密码，加密密码
    private EditText user_input,password_input;//编辑框



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_login=findViewById(R.id.btn_login);
        user_input=findViewById(R.id.user_input);
        password_input=findViewById(R.id.password_input);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();

/*        //获取手机验证码
        TuyaHomeSdk.getUserInstance().getValidateCode("86","***", new IValidateCallback(){
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "获取验证码成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String code, String error) {
                Toast.makeText(MainActivity.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
            }
        });*/

/*        //注册手机密码账户
        TuyaHomeSdk.getUserInstance().registerAccountWithPhone("86","***","***","541773", new IRegisterCallback() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(MainActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(String code, String error) {
                Toast.makeText(MainActivity.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
            }
        });*/

/*
        List<String> rooms = new ArrayList();
        rooms.add("myhome1");
       TuyaHomeSdk.getHomeManagerInstance().createHome("myhome", 0, 0, "sh", rooms, new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {
                // do something
                Toast.makeText(MainActivity.this, "Home success" + bean.getName(), Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Home success" + bean.getHomeId(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(String errorCode, String errorMsg) {
                // do something
                Toast.makeText(MainActivity.this, "创建Home failed: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });*/

    }





    private void init() {


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始登录，获取用户名和密码 getText().toString().trim();
                uerName=user_input.getText().toString().trim();
                password=password_input.getText().toString().trim();


                // TextUtils.isEmpty
                if(TextUtils.isEmpty(uerName)){
                    Toast.makeText(MainActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                    // md5Psw.equals(); 判断，输入的密码加密后，是否与保存在SharedPreferences中一致
                }


                TuyaHomeSdk.getUserInstance().loginWithPhonePassword("86", uerName, password, new ILoginCallback() {
                    @Override
                    public void onSuccess(User user) {
                        Toast.makeText(MainActivity.this, "登录成功" , Toast.LENGTH_SHORT).show();
                        //Toast.makeText(MainActivity.this, "登录成功，用户名：" +TuyaHomeSdk.getUserInstance().getUser().getUsername(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this,MenuActivity.class);
                        startActivity(intent);

                    }

                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(MainActivity.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });
    }





}
