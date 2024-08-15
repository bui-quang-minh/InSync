package com.in_sync.fragments;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.Context;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.in_sync.R;
import com.in_sync.adapter.ImageGalleryAdapter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.Inflater;


public class ScreenCaptureFragment extends Fragment {
    private Button captureButton;
    private Context context;
    private String TAG = "SCREENCAPTURE";
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 1;
    private MediaProjectionManager mediaProjectionManager;
    private String FOLDER_PATH;

    public ScreenCaptureFragment(Context context) {
        this.context = context;
    }

    // on below line we are creating variables for
    // our array list, recycler view and adapter class.
    private static final int PERMISSION_REQUEST_CODE = 200;
    private ArrayList<String> imagePaths;
    private RecyclerView imagesRV;
    private ImageGalleryAdapter imageRVAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initView();
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_screen_capture, null);
        return view;
    }

    private void initView() {
        FOLDER_PATH = Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/screenshots/";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imagePaths = new ArrayList<>();
        imagesRV = view.findViewById(R.id.image_gallery);
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