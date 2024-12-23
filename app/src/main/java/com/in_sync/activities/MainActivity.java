package com.in_sync.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.in_sync.R;
import com.in_sync.daos.AddData;
import com.in_sync.fragments.DeviceInfoFragment;
import com.in_sync.fragments.ProjectFragment;
import com.in_sync.fragments.HomeFragment;
import com.in_sync.fragments.LogFragment;
import com.in_sync.fragments.ProfileFragment;
import com.in_sync.fragments.ScreenCaptureFragment;
import com.in_sync.fragments.TestFragment;
import com.in_sync.validates.PermissionValid;

public class MainActivity extends AppCompatActivity {
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private static final String SELECTED_FRAGMENT_KEY = "selected_fragment";
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private DeviceInfoFragment deviceInfoFragment;
    private LogFragment logFragment;
    private ProjectFragment projectFragment;
    private TestFragment testFragment;
    private ProfileFragment profileFragment;
    private ScreenCaptureFragment screenCaptureFragment;
    private Dialog dialog;
    private Fragment currentFragment;
    private Fragment selectedFragment = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Send request to enable accessibility service
        if (!PermissionValid.isAccessibilitySettingsOn(this, getPackageName())) {
            buildDialog();
        }
        onAppStart();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.canDrawOverlays(this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                        Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
//            }
//        }
        eventHandler();
        //Setup for the bottom navigation view
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    selectedFragment = profileFragment;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, profileFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.log) {
                    selectedFragment = logFragment;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, logFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.project) {
                    selectedFragment = projectFragment;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, projectFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.test) {
                    selectedFragment = testFragment;
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.container, testFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.screen_capture) {
                    selectedFragment = screenCaptureFragment;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, screenCaptureFragment).commit();
                    return true;
                }
                return false;



            }
        });
    }

    private void eventHandler() {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onAppStart() {

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        homeFragment = new HomeFragment();
        logFragment = new LogFragment();
        projectFragment = new ProjectFragment();
        testFragment = new TestFragment();
        profileFragment = new ProfileFragment();
        screenCaptureFragment = new ScreenCaptureFragment(this);
        //Set the default fragment
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container, profileFragment).commit();
        if (currentFragment == null) {
            selectedFragment = profileFragment;
            currentFragment = profileFragment;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, currentFragment)
                    .commit();
        }
    }


    // New dialog will display when first time opens the app
    // Request to enable accessibility service
    private void buildDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.permission_request);
        dialog.setCancelable(false);
        Button guideButton = dialog.findViewById(R.id.btn_guide);
        guideButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WalkthoughNavigationActivity.class);
            startActivity(intent);
        });
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