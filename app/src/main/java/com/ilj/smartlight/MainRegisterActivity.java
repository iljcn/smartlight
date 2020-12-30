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

public class MainRegisterActivity extends AppCompatActivity {

    private Button btn_validatecode_get,btn_validatecode_register;//登录按钮
    private String uerName,password,validatecode;//获取的用户名，密码，加密密码
    private EditText user_input,password_input,validatecode_input;//编辑框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);

        btn_validatecode_get=findViewById(R.id.btn_validatecode_get);
        btn_validatecode_register=findViewById(R.id.btn_validatecode_register);

        user_input=findViewById(R.id.user_input);
        password_input=findViewById(R.id.password_input);
        validatecode_input=findViewById(R.id.password_input);


        btn_validatecode_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始登录，获取用户名和密码 getText().toString().trim();
                uerName=user_input.getText().toString().trim();

                // TextUtils.isEmpty
                if(TextUtils.isEmpty(uerName)){
                    Toast.makeText(MainRegisterActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }


                TuyaHomeSdk.getUserInstance().getValidateCode("86",uerName, new IValidateCallback(){
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainRegisterActivity.this, "获取验证码成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(MainRegisterActivity.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });




        btn_validatecode_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始登录，获取用户名和密码 getText().toString().trim();
                uerName=user_input.getText().toString().trim();
                password=password_input.getText().toString().trim();
                validatecode=validatecode_input.getText().toString().trim();
                // TextUtils.isEmpty
                if(TextUtils.isEmpty(uerName)){
                    Toast.makeText(MainRegisterActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(MainRegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(validatecode)){
                    Toast.makeText(MainRegisterActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }

                TuyaHomeSdk.getUserInstance().registerAccountWithPhone("86",uerName,password,validatecode, new IRegisterCallback() {
                    @Override
                    public void onSuccess(User user) {
                        Toast.makeText(MainRegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(MainRegisterActivity.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });





        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);












    }
}
