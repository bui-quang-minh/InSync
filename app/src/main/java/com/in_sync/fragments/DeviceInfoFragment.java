package com.in_sync.fragments;

import static android.content.Context.ACTIVITY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

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
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.StatFs;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.in_sync.R;

import java.io.File;
import java.util.List;

public class DeviceInfoFragment extends Fragment {
    private Context context;
    private TextView tvWidth;
    private TextView tvHeight;
    private TextView tvManufacturer;
    private TextView tvDensity;
    private TextView tvOrientation;
    private TextView tvDeviceName;
    private TextView tvStorage;
    private TextView tvDeviceId;
    private TextView tvOtherInfo;
    private TextView tvRam;
    private ImageView arrowWidth, arrowHeight;

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
        tvWidth = view.findViewById(R.id.width_value);
        tvHeight = view.findViewById(R.id.height_value);
        tvManufacturer = view.findViewById(R.id.manufacturer_info);
        tvDensity = view.findViewById(R.id.density);
        tvOrientation = view.findViewById(R.id.orientation);
        tvDeviceName = view.findViewById(R.id.device_name);
        tvStorage = view.findViewById(R.id.storage_value);
        tvRam = view.findViewById(R.id.ram_value);
        tvDeviceId = view.findViewById(R.id.device_id);
        tvOtherInfo = view.findViewById(R.id.other_info);
        arrowWidth = view.findViewById(R.id.arrow_width);
        arrowHeight = view.findViewById(R.id.arrow_height);

        // Load and set device information
        setDeviceInformation();

        return view;
    }

    private void setDeviceInformation() {

        // Display Metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //Width and Height
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        // Screen Density
        float densityDpi = getResources().getDisplayMetrics().densityDpi;

        // Screen Orientation
//        public static final int ORIENTATION_UNDEFINED = 0;
//        public static final int ORIENTATION_PORTRAIT = 1;
//        public static final int ORIENTATION_LANDSCAPE = 2;
        int orientation = getResources().getConfiguration().orientation;
        String orientationType = "Undefined";
        if (orientation == 1) {
            orientationType = "Portrait";
        } else if (orientation == 2) {
            orientationType = "Landscape";
        }

        // Device Model and Manufacturer
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        // Storage Information
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statFs.getBlockSizeLong();
        long totalBlocks = statFs.getBlockCountLong();
        long availableBlocks = statFs.getAvailableBlocksLong();
        long totalStorage = (totalBlocks * blockSize) / (1024 * 1024 * 1024);
        long availableStorage = (availableBlocks * blockSize) / (1024 * 1024 * 1024);

        // Available Memory (RAM)
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long totalRam = memoryInfo.totalMem / (1024 * 1024); // in MB
        long availableRam = memoryInfo.availMem / (1024 * 1024); // in MB

        // Android Version
        String versionRelease = Build.VERSION.RELEASE;
        String versionSdk = String.valueOf(Build.VERSION.SDK_INT);

        // Other Information
        // Battery Level
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getActivity().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale * 100;

        // Network Connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        tvWidth.setText(width + " px");
        tvHeight.setText(height + " px");
        tvManufacturer.setText(manufacturer +"\n"+ model);
        tvDensity.setText("Density: " + densityDpi + "dpi");
        tvOrientation.setText("Orientation: " + orientationType);
        tvDeviceName.setText(manufacturer +" "+ model);
        tvStorage.setText(availableStorage + "/" + totalStorage + " GB");
        tvRam.setText(availableRam + "/" + totalRam + " MB");
        tvDeviceId.setText("Version Release: " + versionRelease + "\nSDK: " + versionSdk);
        tvOtherInfo.setText("Battery Level: " + batteryPct + "%\nNetwork Connected: " + isConnected);
    }
}