package com.in_sync.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.in_sync.R;

public class DeviceInfoFragment extends Fragment {
    private Context context;
    private TextView tvWidth, tvHeight, tvManufacturer, tvDensity, tvOrientation, tvDeviceName, tvStorage, tvDeviceId, tvAvailableMemory;
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
        tvDeviceId = view.findViewById(R.id.device_id);
        tvAvailableMemory = view.findViewById(R.id.available_memory);
        arrowWidth = view.findViewById(R.id.arrow_width);
        arrowHeight = view.findViewById(R.id.arrow_height);

        // Load and set device information
        setDeviceInformation();

        return view;
    }

    private void setDeviceInformation() {
        // This is where you'd load your device information
        // For example purposes, we're setting these statically

        tvWidth.setText("1080 px");
        tvHeight.setText("2214 px");
        tvManufacturer.setText("Samsung Galaxy S20");
        tvDensity.setText("Density: 420 dpi");
        tvOrientation.setText("Orientation: Portrait");
        tvDeviceName.setText("Samsung SPH-L710");
        tvStorage.setText("14 / 41 GB");
        tvDeviceId.setText("Device ID: f1733c58e2306eb2");
        tvAvailableMemory.setText("Available Memory: 1971138560");

        // For screen shape, arrows, etc., you can load relevant drawables if needed
        // Example: screenShape.setImageResource(R.drawable.mobile_screen);
    }
}