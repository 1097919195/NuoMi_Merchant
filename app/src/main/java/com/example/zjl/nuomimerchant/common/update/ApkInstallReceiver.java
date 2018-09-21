package com.example.zjl.nuomimerchant.common.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-27
 * Time: 16:04
 * 类描述：
 *
 * @version :
 */
public class ApkInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("test","test");
        if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
              long downloadApkId =intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
              installApk(context, downloadApkId);
        }
    }

    /**
     * 安装apk
     */
    private void installApk(Context context, long downloadApkId) {
        // 获取存储ID
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long downId =sp.getLong(DownloadManager.EXTRA_DOWNLOAD_ID,-1L);
        if(downloadApkId == downId){
            DownloadManager downManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadFileUri = downManager.getUriForDownloadedFile(downloadApkId);
            if (downloadFileUri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(install);
                }else{
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(install);
                }
            }else{
                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
