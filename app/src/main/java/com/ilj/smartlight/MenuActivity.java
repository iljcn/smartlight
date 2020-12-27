package com.ilj.smartlight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.RoomBean;
import com.tuya.smart.home.sdk.builder.ActivatorBuilder;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.home.sdk.callback.ITuyaRoomResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.ActivatorModelEnum;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private Button btn_ListHome;
    private Button btn_NewHome;
    private Button btn_DeleteHome;

    private Button btn_HomeNetEz;
    private Button btn_HomeDevice;

    private TextView text_view;

    List<HomeBean> mHomeBeans = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        btn_ListHome=findViewById(R.id.ListHome);
        btn_NewHome=findViewById(R.id.NewHome);
        btn_DeleteHome=findViewById(R.id.DeleteHome);
        btn_HomeNetEz=findViewById(R.id.HomeNetEz);
        btn_HomeDevice=findViewById(R.id.HomeDevice);

        text_view =findViewById(R.id.HomeTextView);


        btn_ListHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
                    @Override
                    public void onSuccess(List<HomeBean> homeBeans) {
                        // do something
                        //text_view.sette
                        String stMsg = "ListHome Success,homesize="+homeBeans.size()+"\n";

                        for(int i=0;i<homeBeans.size();i++)
                        {
                            stMsg += "Home Id:" + homeBeans.get(i).getHomeId() + "\n";
                        }

                        text_view.setText(stMsg);

                        mHomeBeans = homeBeans;

                    }
                    @Override
                    public void onError(String errorCode, String error) {
                        // do something
                        text_view.setText("ListHome Failed\n");
                    }
                });

            }

        });



        btn_NewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> rooms = new ArrayList();
                rooms.add("myhome1");
                TuyaHomeSdk.getHomeManagerInstance().createHome("myhome", 0, 0, "sh", rooms, new ITuyaHomeResultCallback() {
                    @Override
                    public void onSuccess(HomeBean bean) {
                        // do something
                        text_view.setText("NewHome Success\n");
                    }
                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        // do something
                        text_view.setText("NewHome Failed"+"\n");
                    }
                });


            }

        });



        btn_DeleteHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mHomeBeans == null)
                {
                    text_view.setText("No Home\n");
                    return;
                }
                for(int i=0;i<mHomeBeans.size();i++) {
                    TuyaHomeSdk.newHomeInstance(mHomeBeans.get(i).getHomeId()).dismissHome(new IResultCallback() {
                        @Override
                        public void onSuccess() {
                            // do something
                            text_view.setText("DeleteHome Success\n");
                        }

                        @Override
                        public void onError(String code, String error) {
                            // do something
                            text_view.setText("DeleteHome Failed" + error + "\n");
                        }
                    });
                }


            }

        });



        btn_HomeNetEz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mHomeBeans == null || mHomeBeans.size() == 0)
                {
                    text_view.setText("No Home\n");
                    return;
                }

                TuyaHomeSdk.newHomeInstance(mHomeBeans.get(0).getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
                    @Override
                    public void onSuccess(HomeBean bean) {
                        // do something

                        Intent intent = new Intent(MenuActivity.this,EZActivity.class);
                        intent.putExtra("homeid",bean.getHomeId());  // 传递参数，根据需要填写
                        //intent.putExtra("roomid",bean.getRooms().get(0).getRoomId());  // 传递参数，根据需要填写
                        startActivity(intent);

                    }


                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        // do something
                    }
                });


            }

        });


        btn_HomeDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mHomeBeans == null || mHomeBeans.size() == 0)
                {
                    text_view.setText("No Home\n");
                    return;
                }
                TuyaHomeSdk.newHomeInstance(mHomeBeans.get(0).getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
                    @Override
                    public void onSuccess(HomeBean bean) {
                        // do something
                        List<DeviceBean> dev = bean.getDeviceList();
                        if(dev.size()>0)
                        {
                            Intent intent = new Intent(MenuActivity.this,DeviceActivity.class);
                            intent.putExtra("devid",dev.get(0).getDevId());  // 传递参数，根据需要填写
                            startActivity(intent);
                        }
                        else
                        {
                            text_view.setText("No Fevice\n");
                        }


                    }


                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        // do something
                        text_view.setText("Home Error\n");
                    }
                });





            }

        });










    }
}
