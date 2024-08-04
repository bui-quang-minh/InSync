package com.in_sync;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.in_sync.fragments.ExploreFragment;
import com.in_sync.fragments.HomeFragment;
import com.in_sync.fragments.LogFragment;
import com.in_sync.fragments.ProfileFragment;
import com.in_sync.fragments.ScreenCaptureFragment;
import com.in_sync.fragments.TestFragment;
import com.in_sync.validates.PermissionValid;

public class MainActivity extends AppCompatActivity {
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private LogFragment logFragment;
    private ExploreFragment exploreFragment;
    private TestFragment testFragment;
    private ProfileFragment profileFragment;
    private ScreenCaptureFragment screenCaptureFragment;

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
                }
                else if (item.getItemId() == R.id.screen_capture) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, screenCaptureFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }

    private void onAppStart() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        homeFragment = new HomeFragment();
        logFragment = new LogFragment();
        exploreFragment = new ExploreFragment();
        testFragment = new TestFragment();
        profileFragment = new ProfileFragment();
        screenCaptureFragment = new ScreenCaptureFragment(this);
        //Set the default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, homeFragment).commit();
    }

    // New dialog will display when first time opens the app
    // Request to enable accessibility service
    private void buildDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.permission_request);
        dialog.setCancelable(false);
        Button requestSetting = dialog.findViewById(R.id.request_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        requestSetting.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Requesting", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

}