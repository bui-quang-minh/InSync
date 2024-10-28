//package com.in_sync.activities;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.media.projection.MediaProjectionManager;
//import android.os.Bundle;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.in_sync.services.ScreenshotService;
//
//public class ScreenshotPermissionActivity extends Activity {
//
//    public static final String EXTRA_RESULT_CODE = "RESULT_CODE";
//    public static final String EXTRA_DATA = "DATA";
//    private static final int REQUEST_CODE_PROJECTION = 10001;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        MediaProjectionManager projectionManager =
//                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        Intent permissionIntent = projectionManager.createScreenCaptureIntent();
//        startActivityForResult(permissionIntent, REQUEST_CODE_PROJECTION);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == REQUEST_CODE_PROJECTION) {
//            Intent resultIntent = new Intent();
//            resultIntent.putExtra(EXTRA_RESULT_CODE, resultCode);
//            resultIntent.putExtra(EXTRA_DATA, data);
//            setResult(RESULT_OK, resultIntent);
//            // Broadcast or start the service with the result
//            Intent serviceIntent = new Intent(this, ScreenshotService.class);
//            serviceIntent.putExtra(EXTRA_RESULT_CODE, resultCode);
//            serviceIntent.putExtra(EXTRA_DATA, data);
//            startService(serviceIntent);
//        }
//        finish();
//    }
//}