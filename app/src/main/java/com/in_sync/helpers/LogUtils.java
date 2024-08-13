package com.in_sync.helpers;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.in_sync.daos.FirebaseLogService;
import com.in_sync.models.Log;
import com.in_sync.models.LogSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LogUtils {
    private static LogUtils instance;
    private FirebaseLogService firebaseLogService;
    private LogUtils() {
        firebaseLogService = new FirebaseLogService();
    }


    public static synchronized LogUtils getInstance() {
        if (instance == null) {
            instance = new LogUtils();
        }
        return instance;
    }

    public void saveSessionLogOnFireBase(String scenarioId, LogSession logSession, List<com.in_sync.models.Log> logs, boolean isLog) {
        if (!isLog) return;
        firebaseLogService.addLogSessionWithLogs(scenarioId, logSession, logs, new FirebaseLogService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {

            }
        });
    }



}
