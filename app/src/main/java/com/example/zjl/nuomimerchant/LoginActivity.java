package com.example.zjl.nuomimerchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.example.zjl.nuomimerchant.common.http.NetWork;
import com.example.zjl.nuomimerchant.bean.MyResult;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {
    private EditText username,password;
    private RelativeLayout login;
    private CloudPushService cloudPushService;
    private static final String TAG="LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cloudPushService = PushServiceFactory.getCloudPushService();
        initView();
        init();
        initDeviceId();
    }

    //测试阿里云消息推送（根据设备发送通知时需要获取deviceid）
    private void initDeviceId() {
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        String deviceId = pushService.getDeviceId();
        Log.e(TAG, deviceId);
    }

    private void initView() {
        username= (EditText) findViewById(R.id.edt_login_user);
        password= (EditText) findViewById(R.id.edt_login_password);
        login= (RelativeLayout) findViewById(R.id.txt_login_btnlogin);
    }

    private void init() {
        Hawk.init(this)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM)
                .setStorage(HawkBuilder.newSharedPrefStorage(this))
                .setLogLevel(LogLevel.FULL)
                .build();
        if (Hawk.contains("isLogin")){
        Log.e("isLogin", String.valueOf(Hawk.get("isLogin")));
        if (Hawk.get("isLogin")){
            Intent intent=new Intent(LoginActivity.this,Main2Activity.class);
            startActivity(intent);
            finish();
        }
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().length()==0){
                    Toast.makeText(LoginActivity.this,"请输入用户名",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().length()==0){
                    Toast.makeText(LoginActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
                    return;
                }
                checkAppLogin();
            }
        });
    }

    public  void checkAppLogin() {
        Call<MyResult> call;
        call = (Call<MyResult>) NetWork.getMyApi().getUserinfo(username.getText().toString(),password.getText().toString());
        call.enqueue(new Callback<MyResult>() {
            @Override
            public void onResponse(Call<MyResult> call, retrofit2.Response<MyResult> response) {
                Log.e("LoginBean",response.message());
                Hawk.put("isLogin",true);
                Hawk.put("username",username.getText().toString());
                cloudPushService.bindAccount(username.getText().toString(), new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        Log.d(TAG,"bind account " + username.getText().toString() + " success");
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        Log.e(TAG,"bind account " + username.getText().toString() + " failed." +
                                "errorCode: " + s + ", errorMsg:" + s1);
                    }
                });
                Intent intent=new Intent(LoginActivity.this,Main2Activity.class);
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(Call<MyResult> call, Throwable t) {
                Log.e(TAG,"请求失败");
                Toast.makeText(LoginActivity.this,"用户名密码错误",Toast.LENGTH_LONG).show();
                System.err.print(t.getMessage());
            }
        });}

}
