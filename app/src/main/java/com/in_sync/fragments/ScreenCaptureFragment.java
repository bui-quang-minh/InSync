package com.in_sync.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.in_sync.R;
import com.in_sync.services.ScreenCaptureService;

public class ScreenCaptureFragment extends Fragment {
    private Button captureButton;
    private Context context;

    public ScreenCaptureFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_capture, container, false);
        initView(view);
        handleEvent();
        return  view;
    }

    private void initView(View view) {
        captureButton = view.findViewById(R.id.capture_button);
    }

    private void handleEvent() {
        captureButton.setOnClickListener(v -> {
            context.startService(new Intent(context, ScreenCaptureService.class));
        });
    }
}