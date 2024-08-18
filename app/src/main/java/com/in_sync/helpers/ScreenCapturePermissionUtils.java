package com.in_sync.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;

import com.in_sync.services.ScreenCaptureService;

public class ScreenCapturePermissionUtils {
    private MediaProjectionManager mediaProjectionManager;
    private Context context;
    private ActivityResultLauncher<Intent> launcher;

    public ScreenCapturePermissionUtils(Context context, ActivityResultLauncher<Intent> launcher) {
        this.context = context;
        this.mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        this.launcher = launcher;
    }

    public void startProjection() {
        Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
        launcher.launch(captureIntent);
    }

    public void handleResult(int resultCode, Intent data) {
        if (data != null && data.getExtras() != null) {
            Bundle extras = data.getExtras();
            String json = extras.getString("json");

            if (json != null) {
                Log.e("ScreenCapture", "Received JSON: " + json);
                // Continue with your logic using the json data
            } else {
                Log.e("ScreenCapture", "JSON data is null");
            }
        } else {
            Log.e("ScreenCapture", "Intent data is null or does not contain extras");
        }
        Log.e("ScreenCapture", "Intent data: " + data);
        if (resultCode == Activity.RESULT_OK) {
            Intent intent = ScreenCaptureService.getStartIntent(context, resultCode, data);
            Bundle extras = data.getExtras();
            String json = extras.getString("json");
            intent.putExtra("json", json);
            context.startService(intent);
        }
    }
}
