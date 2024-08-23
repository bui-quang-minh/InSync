package com.in_sync.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MEDIA_PROJECTION_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.in_sync.R;
import com.in_sync.activities.ImageDetailActivity;
import com.in_sync.adapters.ImageGalleryAdapter;
import com.in_sync.file.FileSystem;
import com.in_sync.listener.RecyclerItemClickListener;
import com.in_sync.services.ScreenshotService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ScreenCaptureFragment extends Fragment {
    private View captureButton;
    private FloatingActionButton uploadButton;
    private Context context;
    private final String TAG = "SCREENCAPTURE";
    private static final int REQUEST_CODE = 100;
    private String FOLDER_PATH;
    private RecyclerView imagesRV;
    private List<String> imageList;
    private List<String> selectedImages;

    public ScreenCaptureFragment() {
    }

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
        imageList = FileSystem.getFileName(context);
        uploadButton = view.findViewById(R.id.upload_button);
        selectedImages = new ArrayList<>();
        uploadButton.hide();
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
    }


    @SuppressLint("NotifyDataSetChanged")
    private void prepareRecyclerView() {
        ImageGalleryAdapter imageRVAdapter = new ImageGalleryAdapter(context, imageList);
        // Set the layout manager and adapter for the RecyclerView
        GridLayoutManager manager = new GridLayoutManager(context, 2);
        imagesRV.setLayoutManager(manager);
        imagesRV.setAdapter(imageRVAdapter);
        imagesRV.addOnItemTouchListener(
                new RecyclerItemClickListener(context, imagesRV, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(context, ImageDetailActivity.class);
                        i.putExtra("imgPath", imageList.get(position));
                        context.startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        try {
                            //Catch hold action
                            if (view.getBackground() != null
                                    && view.getBackground().getConstantState() == context.getDrawable(R.drawable.border).getConstantState()) {
                                view.setBackground(null);
                                selectedImages.remove(imageList.get(position));
                                Log.e(TAG, "Removed: " + imageList.get(position));
                            } else {
                                view.setBackground(context.getDrawable(R.drawable.border));
                                selectedImages.add(imageList.get(position));
                                Log.e(TAG, "Added: " + imageList.get(position));
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        // Set hide/unhidden upload button
                        if (!selectedImages.isEmpty()) {
                            uploadButton.show();
                        } else {
                            uploadButton.hide();
                        }
                    }
                }));
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