package com.nbclient.androideatitv2client.Remote;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroFitCloudClient {
    private static Retrofit instance;
    public static Retrofit getInstance(){
        if (instance == null)
            instance = new Retrofit.Builder()
                    .baseUrl("https://www.google.com") //Skipped-1
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

        return instance;
    };

}
