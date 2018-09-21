package com.example.zjl.nuomimerchant.common.update;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.zjl.nuomimerchant.common.http.NetWork;
import com.example.zjl.nuomimerchant.bean.MyResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by ZZN on 2017/9/13.
 */

public class UpDate extends Activity {
    private float serverVersion,versionname;
    private AlertDialog.Builder mDialog;
    private String ChannelName;

    public void upDate(final Context context){
        ChannelName=getAppMetaData(context,"ChannelName");
        try {
            versionname= Float.parseFloat(context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Call<MyResult> call;
        call = (Call<MyResult>) NetWork.getMyApi().getUpdate();
        call.enqueue(new Callback<MyResult>() {
            @Override
            public void onResponse(Call<MyResult> call, retrofit2.Response<MyResult> response) {
                List<MyResult.Results> datas = response.body().getResults();
                MyResult.Results data=datas.get(0);
                Log.e("success",data.getServerVersion());
                if (Float.parseFloat(data.getServerVersion())>versionname){
                    normalUpdate(context,data.getAppname(),data.getUpdateurl());
                }else {
                    Toast.makeText(context,"已经是最新版本啦！",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<MyResult> call, Throwable t) {
                System.out.println("请求失败");
                Toast.makeText(getApplicationContext(),"请检查你的网络",Toast.LENGTH_LONG).show();
                System.out.print(t.getMessage());
            }
        });
    }

    private void normalUpdate(final Context context, final String appname, final String url) {
        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle("更新提示");
        mDialog.setMessage("有新的版本可以更新，是否更新？");
        mDialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DownLoadApk.download(context,url,appname,appname);
        }
        }).setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false).create().show();
    }

    /**
     * 获取application中指定的meta-data。本例中，调用方法时key就是UMENG_CHANNEL
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

}
