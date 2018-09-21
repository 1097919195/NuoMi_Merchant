package com.example.zjl.nuomimerchant.common.http;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by castl on 2016/5/13.
 */
public class NetWork {
    private static WeatherApi weatherApi;
    private static MyApi myApi;

    public static WeatherApi getWeatherApi(){
        if (weatherApi==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .client(OkHttpClient.okhttp())
                    .baseUrl(ApiUrl.WEATHER_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            weatherApi = retrofit.create(WeatherApi.class);
        }
        return weatherApi;
    }

    public static MyApi getMyApi() {
        if (myApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(OkHttpClient.okhttp())
                    .baseUrl(ApiUrl.MY_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            myApi = retrofit.create(MyApi.class);
        }
        return myApi;
    }
}
