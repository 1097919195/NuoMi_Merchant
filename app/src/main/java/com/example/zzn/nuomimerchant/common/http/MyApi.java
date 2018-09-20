package com.example.zzn.nuomimerchant.common.http;


import com.example.zzn.nuomimerchant.model.MyResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by ZZN on 2017/8/25.
 */

public interface MyApi {
//    http://jisutqybmf.market.alicloudapi.com/weather/query?location=29.982631,120.612979
//    http://106.15.198.49/nuomi/nuomi.php
    @GET("nuomi.php")
    Observable<MyResult> getMy();

    @POST("getorder.php")
    Observable<MyResult> getOrder();

    @FormUrlEncoded
    @POST("MerchantUserinfo.php")
    Call<MyResult> getUserinfo(@Field("userid") String userid,
                               @Field("password") String password);

    @POST("update.php")
    Call<MyResult> getUpdate();

    @FormUrlEncoded
    @POST("changeState.php")
    Call<MyResult> getState(@Field("state") String state
                            ,@Field("id") String id);
}
