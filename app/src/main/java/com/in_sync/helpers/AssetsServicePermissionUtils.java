package com.in_sync.helpers;

import android.media.projection.MediaProjectionManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;

import com.in_sync.services.AssetsService;
public class AssetsServicePermissionUtils {
    private MediaProjectionManager mediaProjectionManager;
    private Context context;
    private ActivityResultLauncher<Intent> launcher;

    public AssetsServicePermissionUtils(Context context, ActivityResultLauncher<Intent> launcher) {
        this.context = context;
        this.mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        this.launcher = launcher;
    }

    public void startProjection() {
        Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
        launcher.launch(captureIntent);
    }

    public void handleResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Intent stopintent = AssetsService.getStopIntent(context);
            context.stopService(stopintent);
            Intent intent = AssetsService.getStartIntent(context, resultCode, data);
            context.startService(intent);
        }
    }
}
