package com.in_sync.daos;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;


import com.in_sync.models.LogSession;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LogService {
    private static final String TAG = "LogService";
    private static LogService instance;
    private LogsFirebaseService firebaseLogService;


    private LogService() {
        firebaseLogService = new LogsFirebaseService();
    }

    public static LogService getInstance() {
        if (instance == null) {
            instance = new LogService();
        }
        return instance;
    }

    private void SaveLogSession(LogSession logSession, List<com.in_sync.models.Log> listLogs) {
        firebaseLogService.addLogSessionWithLogs(logSession, listLogs, new LogsFirebaseService.LogCallback<Boolean>() {
            @Override
            public void onCallback(Boolean data) {
                if (data) {
                    Log.e(TAG, "SaveLogSession: Success");
                } else {
                    Log.d(TAG, "SaveLogSession: Fail");
                }
            }
        });
    }


}
