package com.in_sync.validates;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.util.Log;

import com.in_sync.services.ScreenCaptureService;

public class PermissionValid {
    public static final int REQUEST_CODE_PERMISSIONS_READ_WRITE_EXTERNAL_STORAGE = 100;

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String TAG = "PermissionValid";
    // Check in the permission settings if accessibility service is enabled
    public static boolean isAccessibilitySettingsOn(Context mContext, String packageName) {
        int accessibilityEnabled = 0;
        final String service = packageName + "/" + ScreenCaptureService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {

            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    public static void requestAccessReadWriteExternalStorage(Activity activity) {
        // Kiểm tra và yêu cầu quyền trong Activity của bạn
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.READ_EXTERNAL_STORAGE)
            || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Hiển thị lời giải thích nếu người dùng đã từ chối quyền trước đó
                new AlertDialog.Builder(activity)
                        .setTitle("External memory access")
                        .setMessage("The application needs external access to data storage. Ensure your phone's information security")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity,
                                        REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS_READ_WRITE_EXTERNAL_STORAGE);
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e(TAG, "Quyền đọc và ghi vào bộ nhớ ngoài bị từ chối.");
                            }
                        })
                        .create()
                        .show();
            } else {
                // Yêu cầu quyền nếu chưa từng yêu cầu hoặc người dùng đã từ chối nhưng không chọn "Không bao giờ hỏi lại"
                ActivityCompat.requestPermissions(activity,
                        REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS_READ_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

}
