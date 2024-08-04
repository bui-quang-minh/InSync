package com.in_sync.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MEDIA_PROJECTION_SERVICE;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
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
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 1;
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;

    public ScreenCaptureFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_capture, container, false);
        initView(view);
        handleEvent();
        return view;
    }

    private void initView(View view) {
        captureButton = view.findViewById(R.id.capture_button);
        mediaProjectionManager = (MediaProjectionManager) context.getSystemService(MEDIA_PROJECTION_SERVICE);
    }

    private void handleEvent() {
        captureButton.setOnClickListener(v -> {
            startScreenCaptureRequest();
        });
    }

    private void startScreenCaptureRequest() {
        Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_CODE_SCREEN_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE && resultCode == RESULT_OK) {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
            // Start the screen capture service
            Intent serviceIntent = new Intent(context, ScreenCaptureService.class);
            serviceIntent.putExtra("media_projection", mediaProjection.toString());
            context.startService(serviceIntent);
        }
    }
}