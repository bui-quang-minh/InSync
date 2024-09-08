package com.in_sync.fragments;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.in_sync.R;
import com.in_sync.helpers.DeviceInfoUtils;

import java.util.Locale;

public class DeviceInfoFragment extends Fragment {
    private Context context;
    private TextView tvDeviceName;
    private ProgressBar prStorage;
    private ProgressBar prRam;
    private ProgressBar prBattery;
    private TextView tvStorageProgress;
    private TextView tvRamProgress;
    private TextView tvBatteryProgress;
    private TextView tvSize;
    private TextView tvDensity;
    private TextView tvOrientation;
    private TextView tvAndroidVersion;
    private TextView tvSDK;
    private TextView tvConnectToCharge;
    private TextView tvConnectToInternet;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_device_information, container, false);

        // Initialize views
        tvDeviceName = view.findViewById(R.id.tv_device_name);
        prStorage = view.findViewById(R.id.progress_storage);
        prRam = view.findViewById(R.id.progress_ram);
        prBattery = view.findViewById(R.id.progress_battery);
        tvSize = view.findViewById(R.id.tv_device_size);
        tvStorageProgress = view.findViewById(R.id.tv_storage);
        tvRamProgress = view.findViewById(R.id.tv_ram);
        tvBatteryProgress = view.findViewById(R.id.tv_battery);
        tvDensity = view.findViewById(R.id.tv_device_density);
        tvOrientation = view.findViewById(R.id.tv_device_orientation);
        tvAndroidVersion = view.findViewById(R.id.tv_android_version);
        tvSDK = view.findViewById(R.id.tv_SDK);
        tvConnectToCharge = view.findViewById(R.id.tv_connect_to_charge);
        tvConnectToInternet = view.findViewById(R.id.tv_connect_to_internet);

        // Load and set device information
        setDeviceInformation();

        // Handle connectivity status
        handleConnectivityStatus(view);

        return view;
    }

    @SuppressLint("DefaultLocale")
    private void setDeviceInformation() {
        // Display Metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (getActivity() != null) {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }

        // Width and Height
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        // Screen Density
        float densityDpi = getResources().getDisplayMetrics().densityDpi;

        // Screen Orientation
        int orientation = getResources().getConfiguration().orientation;
        String orientationType = "Undefined";
        if (orientation == 1) {
            orientationType = "Portrait";
        } else if (orientation == 2) {
            orientationType = "Landscape";
        }

        // Device Model and Manufacturer
        String manufacturer = Build.MANUFACTURER.toUpperCase(Locale.ROOT);
        String model = Build.MODEL.toUpperCase(Locale.ROOT);

        // Available Memory (RAM)
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);
        }
        long totalRam = memoryInfo.totalMem / (1000 * 1000); // in MB
        long availableRam = memoryInfo.availMem / (1000 * 1000); // in MB

        // Android Version
        String versionRelease = Build.VERSION.RELEASE;
        String versionSdk = String.valueOf(Build.VERSION.SDK_INT);

        // Battery Level
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = -1;
        int scale = -1;
        if (batteryStatus != null) {
            level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }
        float batteryPct = (level != -1 && scale != -1) ? (level / (float) scale) * 100 : 0;

        // Set device information to TextViews and ProgressBars
        tvDeviceName.setText(manufacturer + " " + model);

        // Set progress and max for storage, RAM, and battery
        DeviceInfoUtils.StorageInfo storageInfo = DeviceInfoUtils.getStorageDetails(context);
        prStorage.setMax((int) storageInfo.getTotalStorage());
        prStorage.setProgress((int) storageInfo.getUsedStorage());

        prRam.setMax((int) totalRam);
        prRam.setProgress((int) availableRam);

        prBattery.setMax(100); // Battery percentage max is always 100
        prBattery.setProgress((int) batteryPct);


        tvStorageProgress.setText(storageInfo.getUsedStorage() + " / " + storageInfo.getTotalStorage() + " GB");
        tvRamProgress.setText(availableRam + " / " + totalRam + " MB");
        tvBatteryProgress.setText("Battery Level: " + (int) batteryPct + "%");

        tvSize.setText(String.format("Size: %d px x %d px", width, height));
        tvDensity.setText("Density: " + densityDpi + " dpi");
        tvOrientation.setText("Orientation: " + orientationType);
        tvAndroidVersion.setText("Version Release: " + versionRelease);
        tvSDK.setText("SDK: " + versionSdk);
    }

    private void handleConnectivityStatus(View view) {
        // Battery Connection Status
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getActivity().registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // Network Connection Status
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnectedToInternet = activeNetwork != null && activeNetwork.isConnected();

        // Set visibility based on status
        if (isCharging) {
            tvConnectToCharge.setVisibility(View.VISIBLE);
        } else {
            tvConnectToCharge.setVisibility(View.GONE);
        }

        if (isConnectedToInternet) {
            tvConnectToInternet.setVisibility(View.VISIBLE);
        } else {
            tvConnectToInternet.setVisibility(View.GONE);
        }
    }
}
