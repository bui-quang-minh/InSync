package com.in_sync.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.in_sync.R;

public class ExploreFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);

        // Display Metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        // Screen Density
        float density = getResources().getDisplayMetrics().density;

        // Screen Orientation
        int orientation = getResources().getConfiguration().orientation;

        // Device Model and Manufacturer
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        // Android Version
        String versionRelease = Build.VERSION.RELEASE;
        int versionSdk = Build.VERSION.SDK_INT;

        // Available Memory
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long availableMemory = memoryInfo.availMem;

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
        Log.d("DeviceProperties", "Width: " + width + ", Height: " + height + ", Density: " + density);
        Log.d("DeviceProperties", "Orientation: " + orientation);
        Log.d("DeviceProperties", "Manufacturer: " + manufacturer + ", Model: " + model);
        Log.d("DeviceProperties", "Android Version: " + versionRelease + ", SDK: " + versionSdk);
        Log.d("DeviceProperties", "Available Memory: " + availableMemory);
        Log.d("DeviceProperties", "Battery Level: " + batteryPct);
        Log.d("DeviceProperties", "Network Connected: " + isConnected);
        return rootView;
    }
}
