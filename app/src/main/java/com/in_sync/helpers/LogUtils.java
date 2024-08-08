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
    public static void LogOnFireBase(String scenarioId, String description, String note, boolean isLog) {
        if(!isLog) return;
        FirebaseLogService firebaseLogService = new FirebaseLogService();
        firebaseLogService.addLog(scenarioId, description, note);
    }

}
