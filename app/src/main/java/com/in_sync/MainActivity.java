package com.in_sync;

import static org.opencv.android.CameraRenderer.LOGTAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.in_sync.fragments.ExploreFragment;
import com.in_sync.fragments.HomeFragment;
import com.in_sync.fragments.LogFragment;
import com.in_sync.fragments.ProfileFragment;
import com.in_sync.fragments.TestFragment;

public class MainActivity extends AppCompatActivity {
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private LogFragment logFragment;
    private ExploreFragment exploreFragment;
    private TestFragment testFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Send request to enable accessibility service
        if(!isAccessibilityEnabled()) {
            Intent intentt = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intentt);
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
    private void onAppStart() {
        bottomNavigationView    = findViewById(R.id.bottom_navigation_view);
        homeFragment            = new HomeFragment();
        logFragment             = new LogFragment();
        exploreFragment         = new ExploreFragment();
        testFragment            = new TestFragment();
        profileFragment         = new ProfileFragment();
        //Set the default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, homeFragment).commit();
    }

    public boolean isAccessibilityEnabled() {
        int accessibilityEnabled = 0;
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);

        } catch (Settings.SettingNotFoundException e) {
            Log.e(LOGTAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled==1) {
            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.d(LOGTAG, "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(Settings.ACTION_ACCESSIBILITY_SETTINGS)){
                        return true;
                    }
                }
            }

            Log.d(LOGTAG, "***END***");
        }
        else {
            Log.d(LOGTAG, "***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }

}