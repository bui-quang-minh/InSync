package com.in_sync;

import static org.opencv.android.CameraRenderer.LOGTAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.in_sync.daos.FirebaseLogService;
import com.in_sync.fragments.ExploreFragment;
import com.in_sync.fragments.HomeFragment;
import com.in_sync.fragments.LogFragment;
import com.in_sync.fragments.ProfileFragment;
import com.in_sync.fragments.TestFragment;
import com.in_sync.helpers.FileLogUtils;
import com.in_sync.helpers.LogUtils;
import com.in_sync.models.LogSession;
import com.in_sync.services.ScreenCaptureService;
import com.in_sync.validates.PermissionValid;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private LogFragment logFragment;
    private ExploreFragment exploreFragment;
    private TestFragment testFragment;
    private ProfileFragment profileFragment;
    private Dialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //Test Firebase
        FirebaseLogService service = new FirebaseLogService();
//
//        String scenarioId ="8bd1d256-0a2d-4768-bde5-590855473285";
//        String sessionName = "New Session";
//        String deviceName = "Device Name";
//        LogSession logSession = new LogSession(sessionName, deviceName, scenarioId);
//        List<com.in_sync.models.Log> logs = new ArrayList<>();
//
//// Tạo Log 1
//        com.in_sync.models.Log log1 = new com.in_sync.models.Log(logSession.getSession_id(), "Description for log 1", "Note for log 1");
//        logs.add(log1);
//
//// Tạo Log 2
//        com.in_sync.models.Log log2 = new com.in_sync.models.Log(logSession.getSession_id(), "Description for log 2", "Note for log 2");
//        logs.add(log2);
//      service.addLogSessionWithLogs(scenarioId, logSession, logs, new FirebaseLogService.LogCallback<Boolean>(){
//
//          @Override
//          public void onCallback(Boolean data) {
//              Log.e("TAG", "onCallback:" + data);
//          }
//      });
        Calendar from = Calendar.getInstance();
        from.set(2024, 7, 7);
        Calendar to = Calendar.getInstance();
        to.set(2024, 7, 10);
        service.getLogSessionsByScenarioIdAndDate("8bd1d256-0a2d-4768-bde5-590855473285"
                ,from.getTime()
                , to.getTime(), new FirebaseLogService.LogCallback<List<LogSession>>() {
            @Override
            public void onCallback(List<LogSession> data) {
                Log.e("TAG", "onCallback:" + data.size());
            }
        });


        // Send request to enable accessibility service
        if (!PermissionValid.isAccessibilitySettingsOn(this, getPackageName())) {
            buildDialog();
        }


        onAppStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
        //Setup for the bottom navigation view
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, homeFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.log) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, logFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.explore) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, exploreFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.test) {
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.container, testFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.profile) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, profileFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionValid.REQUEST_CODE_PERMISSIONS_READ_WRITE_EXTERNAL_STORAGE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // Quyền đã được cấp, thực hiện hành động cần thiết
                Log.e(TAG, "Quyền đọc và ghi vào bộ nhớ ngoài bị đã chấp nhận.");
            } else {
                // Quyền bị từ chối
                Log.e(TAG, "Quyền đọc và ghi vào bộ nhớ ngoài bị từ chối.");
            }
        }
    }

    private void onAppStart() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        homeFragment = new HomeFragment();
        logFragment = new LogFragment();
        exploreFragment = new ExploreFragment();
        testFragment = new TestFragment();
        profileFragment = new ProfileFragment();
        //Set the default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, homeFragment).commit();
    }


    // New dialog will display when first time opens the app
    // Request to enable accessibility service
    private void buildDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.permission_request);
        dialog.setCancelable(false);
        Button requestSetting = dialog.findViewById(R.id.request_button);
        requestSetting.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Requesting", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            dialog.dismiss();
        });
        //cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Created By: Bui Quang Minh
    // Created Date: 07-08-2024
    // On Resume, check for accessibility settings
    // Request to enable accessibility service
    protected void onResume() {
        super.onResume();
        // Check if accessibility settings are enabled
        if (!PermissionValid.isAccessibilitySettingsOn(this, getPackageName())) {
            if (dialog == null || !dialog.isShowing()) {
                buildDialog();
            }
        }
    }
}