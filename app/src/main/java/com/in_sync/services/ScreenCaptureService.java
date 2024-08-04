package com.in_sync.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * @Author Dương Thành Luân
 * @Date 4/8/2024
 * @Desc Capture screen of user when start this service
 */
public class ScreenCaptureService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
