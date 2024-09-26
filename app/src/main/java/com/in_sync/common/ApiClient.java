package com.in_sync.common;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.in_sync.adapters.LocalDateAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Retrofit getRetrofitInstance(String Base_URL) {

        OkHttpClient ok = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Gson gson;

             gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(Base_URL)
                    .client(ok)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            return retrofit;

    }
}
