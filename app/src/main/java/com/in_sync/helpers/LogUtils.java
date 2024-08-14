package com.in_sync.helpers;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.in_sync.daos.FirebaseLogService;
import com.in_sync.models.Log;

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

    public void LogOnFireBase(String scenarioId, String description, String note, boolean isLog) {
        if (!isLog) return;
        firebaseLogService.addLog(scenarioId, description, note);
    }

    public void LogManyLogsOnFireBase(List<Log> logs,boolean isLog) {
        if (!isLog) return;
        firebaseLogService.addLogs(logs);
    }

}
