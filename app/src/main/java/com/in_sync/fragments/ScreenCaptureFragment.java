package com.in_sync.fragments;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MEDIA_PROJECTION_SERVICE;
import androidx.recyclerview.selection.SelectionTracker;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.in_sync.R;
import com.in_sync.activities.ScreenshotPermissionActivity;
import com.in_sync.adapters.ImageGalleryAdapter;
import com.in_sync.services.ScreenshotService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ScreenCaptureFragment extends Fragment {
    private View captureButton;
    private Context context;
    private final String TAG = "SCREENCAPTURE";
    private static final int REQUEST_CODE = 100;
    private String FOLDER_PATH;
    private RecyclerView imagesRV;
    private List<String> imageList;
    public ScreenCaptureFragment() {}
    public ScreenCaptureFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen_capture, null);
    }

    private void initView(View view) {
        FOLDER_PATH = Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/screenshots/";
        captureButton = view.findViewById(R.id.screen_capture_button);
        imagesRV = view.findViewById(R.id.image_gallery);
        imageList = getFileName();
    }

    private void catchEvent() {
        captureButton.setOnClickListener(v -> {
            startProjection();
        });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        catchEvent();
        prepareRecyclerView();
        displayUploadButton();
    }
    private void displayUploadButton() {
        if (Ima.size() > 0) {
            captureButton.setVisibility(View.VISIBLE);
        } else {
            captureButton.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void prepareRecyclerView() {
        ImageGalleryAdapter imageRVAdapter = new ImageGalleryAdapter(context, imageList);
        // Set the layout manager and adapter for the RecyclerView
        GridLayoutManager manager = new GridLayoutManager(context, 2);
        imagesRV.setLayoutManager(manager);
        imagesRV.setAdapter(imageRVAdapter);
    }

    private List<String> getFileName() {
        List<String> filesName = new ArrayList<>();
        try {
            File folder = new File(FOLDER_PATH);
            if(folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles((file) -> file.getName().endsWith(".png"));
                if(files != null) {
                    for (File file: files) {
                        if(file.length() > 0)
                            filesName.add(FOLDER_PATH + file.getName());
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "ERROR: CAN NOT READ IMAGE FOLDER", Toast.LENGTH_SHORT).show();
        }

        return filesName;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent intent = ScreenshotService.getStartIntent(context, resultCode, data);
                context.startService(intent);
            }
        }
    }

    private void startProjection() {
        MediaProjectionManager mProjectionManager =
                (MediaProjectionManager) context.getSystemService(MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

}