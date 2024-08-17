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
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.in_sync.R;
import com.in_sync.activities.ScreenshotPermissionActivity;
import com.in_sync.adapter.ImageGalleryAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ScreenCaptureFragment extends Fragment {
    private View captureButton;
    private Context context;
    private String TAG = "SCREENCAPTURE";
    private String FOLDER_PATH;
    private RecyclerView imagesRV;
    private ImageGalleryAdapter imageRVAdapter;

    public ScreenCaptureFragment() {}
    public ScreenCaptureFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_screen_capture, null);
        return view;
    }

    private void initView(View view) {
        FOLDER_PATH = Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/screenshots/";
        captureButton = view.findViewById(R.id.screen_capture_button);
        imagesRV = view.findViewById(R.id.image_gallery);
    }


    private void catchEvent() {
        captureButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ScreenshotPermissionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        catchEvent();
        prepareRecyclerView();
    }


    private void prepareRecyclerView() {
        imageRVAdapter = new ImageGalleryAdapter(context, getFileName());
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
                        filesName.add(FOLDER_PATH + file.getName());
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "ERROR: CAN NOT READ IMAGE FOLDER", Toast.LENGTH_SHORT).show();
        }

        return filesName;
    }


}