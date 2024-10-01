package com.in_sync.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.in_sync.R;
import com.in_sync.helpers.ScreenCapturePermissionUtils;

public class TestFragment extends Fragment {
    private static final String TAG = "TestFragment";
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private Button initiateOverlayButton;
    private Context context;
    private com.google.android.material.textfield.TextInputEditText inputEditText;
    private ScreenCapturePermissionUtils screenCaptureHelper;
    private ActivityResultLauncher<Intent> screenCaptureLauncher;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context= context;
        screenCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultData = result.getData();
                        if (resultData != null) {
                            // Add the json data to the intent before passing it to the handler
                            String json = inputEditText.getText().toString();
                            resultData.putExtra("json", json);
                            screenCaptureHelper.handleResult(result.getResultCode(), resultData);
                        }
                    }
                }
        );
        // Initialize ScreenCapturePermissionUtils
        screenCaptureHelper = new ScreenCapturePermissionUtils(context, screenCaptureLauncher);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        inputEditText = view.findViewById(R.id.inputEditText);
        inputEditText.setText("[\n" +
                "    {\n" +
                "        \"index\": 0,\n" +
                "        \"actionType\": \"CLICK\",\n" +
                "        \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1726843049/oljthjfswvxxgtpyzxhv.jpg\",\n" +
                "        \"logResult\": true,\n" +
                "        \"duration\": 100,\n" +
                "        \"tries\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 1,\n" +
                "        \"actionType\": \"DELAY\",\n" +
                "        \"on\": \"\",\n" +
                "        \"logResult\": true,\n" +
                "        \"duration\": 500,\n" +
                "        \"tries\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 2,\n" +
                "        \"actionType\": \"CLICK\",\n" +
                "        \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1726844127/i3jjo1kgdkphcaxqfmz3.jpg\",\n" +
                "        \"logResult\": true,\n" +
                "        \"duration\": 100,\n" +
                "        \"tries\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 3,\n" +
                "        \"actionType\": \"FOR\",\n" +
                "        \"executeActions\": [\n" +
                "            {\n" +
                "                \"index\": 4,\n" +
                "                \"actionType\": \"DELAY\",\n" +
                "                \"on\": \"\",\n" +
                "                \"logResult\": true,\n" +
                "                \"duration\": 500,\n" +
                "                \"tries\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"index\": 5,\n" +
                "                \"actionType\": \"CLICK\",\n" +
                "                \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1727182935/dzaoq0zfrfnuvmjqqgky.jpg\",\n" +
                "                \"logResult\": true,\n" +
                "                \"duration\": 100,\n" +
                "                \"tries\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"index\": 6,\n" +
                "                \"actionType\": \"DELAY\",\n" +
                "                \"on\": \"\",\n" +
                "                \"logResult\": true,\n" +
                "                \"duration\": 500,\n" +
                "                \"tries\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"index\": 7,\n" +
                "                \"actionType\": \"CLICK\",\n" +
                "                \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1727182947/ajzgz5fjgfxbdunsivbj.jpg\",\n" +
                "                \"logResult\": true,\n" +
                "                \"duration\": 100,\n" +
                "                \"tries\": 1\n" +
                "            }\n" +
                "        ],\n" +
                "        \"logResult\": true,\n" +
                "        \"duration\": 500,\n" +
                "        \"tries\": 3\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 8,\n" +
                "        \"actionType\": \"DELAY\",\n" +
                "        \"on\": \"\",\n" +
                "        \"logResult\": true,\n" +
                "        \"duration\": 500,\n" +
                "        \"tries\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 9,\n" +
                "        \"actionType\": \"CLICK\",\n" +
                "        \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1727183150/calprkydbyd3a8qehpxm.jpg\",\n" +
                "        \"logResult\": true,\n" +
                "        \"duration\": 100,\n" +
                "        \"tries\": 1\n" +
                "    }\n" +
                "]");
        onViewStart(view);
        eventHandling();
        // Inflate the layout for this fragment
        return view;
    }

    private void onViewStart(View v) {
        initiateOverlayButton = (v).findViewById(R.id.initiateOverlayButton);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void eventHandling() {
        initiateOverlayButton.setOnClickListener(this::initiateOverlay);
    }

    private void initiateOverlay(View view) {
//        Intent intent = new Intent(context, ScreenCapturePermissionActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("json", inputEditText.getText()) ;
//        startActivity(intent);
        screenCaptureHelper.startProjection();
    }
}