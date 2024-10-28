package com.in_sync.fragments;

//import com.cloudinary.*;
//import com.cloudinary.utils.ObjectUtils;
import static com.in_sync.BuildConfig.API_KEY;
import static com.in_sync.BuildConfig.API_SECRET;
import static com.in_sync.BuildConfig.CLOUD_NAME;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
        import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.in_sync.R;
import com.in_sync.activities.ImageDetailActivity;
import com.in_sync.adapters.ImageGalleryAdapter;
import com.in_sync.file.FileSystem;
import com.in_sync.helpers.AssetsServicePermissionUtils;
        import com.in_sync.listener.RecyclerItemClickListener;

        import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private ActivityResultLauncher<Intent> screenshotLauncher;

    public ScreenCaptureFragment() {
    }

    public ScreenCaptureFragment(Context context) {
        this.context = context;
    }
    private AssetsServicePermissionUtils assetsHelper;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context= context;
        screenshotLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultData = result.getData();
                        if (resultData != null) {
                            assetsHelper.handleResult(result.getResultCode(), resultData);
                        }
                    }
                }
        );
        // Initialize ScreenCapturePermissionUtils
        assetsHelper = new AssetsServicePermissionUtils(context, screenshotLauncher);
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
        //imageList = FileSystem.getFileName(context);
        uploadButton = view.findViewById(R.id.upload_button);
        selectedImages = new ArrayList<>();
        uploadButton.hide();
    }

    private void catchEvent() {
        captureButton.setOnClickListener(v -> {
            startProjection();
        });
        uploadButton.setOnClickListener(this::sendData);
    }

    private void sendData(View view) {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET
        ));

        for (String path : selectedImages) {
            File file = new File(path);
            if (file.exists()) {
                // Upload to Cloudinary
                new Thread(() -> {
                    try {
                        // Upload the file
                        Map<String, Object> uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                        Log.e(TAG, "Image uploaded: " + uploadResult.get("url"));
                    } catch (Exception e) {
                        Log.e(TAG, "Upload failed: ", e);
                    }
                }).start(); // Start a new thread to avoid blocking the UI
            }
        }
        Log.e(TAG, "sendData: upload completed");
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        catchEvent();
    }
    /*
        * onResume() method is called when the activity will start interacting with the user.
        * So when start app again the image list will be updated.
        * The sequence of lifecycle methods is: onCreate() -> onStart() -> onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: Calling");
        refreshImageList();
    }
    public void refreshImageList() {
        // Retrieve the file names
        imageList = FileSystem.getFileName(context);
        // Reverse the imageList
        Collections.reverse(imageList);
        // Prepare the RecyclerView with the reversed list
        prepareRecyclerView();
    }
    @SuppressLint("NotifyDataSetChanged")
    private void prepareRecyclerView() {
        ImageGalleryAdapter imageRVAdapter = new ImageGalleryAdapter(context, imageList);
        for (String a: imageList) {
            Log.e(TAG, "prepareRecyclerView: " + a);
        }
        // Set the layout manager and adapter for the RecyclerView
        GridLayoutManager manager = new GridLayoutManager(context, 5);
        imagesRV.hasFixedSize();
        imagesRV.setLayoutManager(manager);
        imagesRV.setAdapter(imageRVAdapter);
        // Catch event when item is clicked or long clicked
        imagesRV.addOnItemTouchListener(
                new RecyclerItemClickListener(context, imagesRV, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(context, ImageDetailActivity.class);
                        i.putExtra("imgPath", imageList.get(position));
                        context.startActivity(i);
                    }
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onLongItemClick(View view, int position) {
                        try {
                            //Catch hold action
                            if (view.getBackground() != null
                                    && view.getBackground().getConstantState() == Objects.requireNonNull(context.getDrawable(R.drawable.border)).getConstantState()) {
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
    private void startProjection() {
        assetsHelper.startProjection();
    }
}