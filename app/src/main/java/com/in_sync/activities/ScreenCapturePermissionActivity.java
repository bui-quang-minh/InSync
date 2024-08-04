package com.in_sync.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import com.in_sync.services.ScreenStreamingService;


public class ScreenCapturePermissionActivity extends Activity {

    private static final int REQUEST_CODE = 100;
    private MediaProjectionManager mediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startProjection();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent intent = ScreenStreamingService.getStartIntent(this, resultCode, data);
                String json = getIntent().getExtras().get("json").toString();
                intent.putExtra("json", json);
                startService(intent);
                finish();
            }

        }
    }
    private void startProjection() {
        MediaProjectionManager mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void stopProjection() {
        startService(ScreenStreamingService.getStopIntent(this));
    }
}
