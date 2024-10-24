package com.in_sync.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.in_sync.R;
import com.in_sync.helpers.ScreenCapturePermissionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestFragment extends Fragment {
    private static final String TAG = "TestFragment";
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private Button initiateOverlayButton;
    private Context context;
    private com.google.android.material.textfield.TextInputEditText inputEditText;
    private ScreenCapturePermissionUtils screenCaptureHelper;
    private ActivityResultLauncher<Intent> screenCaptureLauncher;
    private TextView validationResult;
    private TextInputLayout textView;
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
        textView = view.findViewById(R.id.textView);
        validationResult = view.findViewById(R.id.validationResult);
        inputEditText = view.findViewById(R.id.inputEditText);
        inputEditText.setText("[\n" +
                "    {\n" +
                "        \"index\": 0,\n" +
                "        \"actionType\": \"DELAY\",\n" +
                "        \"isLog\": true,\n" +
                "        \"logContent\": \"Delay for 1 second\",\n" +
                "        \"duration\": 1000\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 1,\n" +
                "        \"actionType\": \"OPEN_APP\",\n" +
                "        \"open\": \"com.netease.onmyoji.gb\",\n" +
                "        \"isLog\": true,\n" +
                "        \"logDescription\": \"Open the app\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 2,\n" +
                "        \"actionType\": \"DELAY\",\n" +
                "        \"isLog\": true,\n" +
                "        \"logContent\": \"Delay for 2 seconds\",\n" +
                "        \"duration\": 2000\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 3,\n" +
                "        \"actionType\": \"CLICK\",\n" +
                "        \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1726843049/oljthjfswvxxgtpyzxhv.jpg\",\n" +
                "        \"isLog\": true,\n" +
                "        \"logContent\": \"Click on the image\",\n" +
                "        \"duration\": 100\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 4,\n" +
                "        \"actionType\": \"DELAY\",\n" +
                "        \"isLog\": true,\n" +
                "        \"logContent\": \"Delay for 2 seconds\",\n" +
                "        \"duration\": 2000\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 5,\n" +
                "        \"actionType\": \"FOR\",\n" +
                "        \"executeActions\": [\n" +
                "            {\n" +
                "                \"index\": 6,\n" +
                "                \"actionType\": \"ZOOM\",\n" +
                "                \"direction\": \"IN\",\n" +
                "                \"isLog\": true,\n" +
                "                \"logContent\": \"Zoom in\",\n" +
                "                \"duration\": 100\n" +
                "            },\n" +
                "            {\n" +
                "                \"index\": 7,\n" +
                "                \"actionType\": \"DELAY\",\n" +
                "                \"isLog\": true,\n" +
                "                \"logContent\": \"Delay for 1 second\",\n" +
                "                \"duration\": 1000\n" +
                "            },\n" +
                "            {\n" +
                "                \"index\": 8,\n" +
                "                \"actionType\": \"ZOOM\",\n" +
                "                \"direction\": \"OUT\",\n" +
                "                \"isLog\": true,\n" +
                "                \"logContent\": \"Zoom out\",\n" +
                "                \"duration\": 100\n" +
                "            },\n" +
                "            {\n" +
                "                \"index\": 9,\n" +
                "                \"actionType\": \"DELAY\",\n" +
                "                \"isLog\": true,\n" +
                "                \"logContent\": \"Delay for 1 second\",\n" +
                "                \"duration\": 1000\n" +
                "            }\n" +
                "        ],\n" +
                "        \"isLog\": true,\n" +
                "        \"logContent\": \"Zoom in and out\",\n" +
                "        \"times\": 3\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 10,\n" +
                "        \"actionType\": \"DELAY\",\n" +
                "        \"isLog\": true,\n" +
                "        \"logContent\": \"Delay for 2 seconds\",\n" +
                "        \"duration\": 2000\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 11,\n" +
                "        \"actionType\": \"SWIPE\",\n" +
                "        \"direction\": \"RIGHT\",\n" +
                "        \"isLog\": true,\n" +
                "        \"logContent\": \"Swipe right\",\n" +
                "        \"duration\": 500\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 12,\n" +
                "        \"actionType\": \"DELAY\",\n" +
                "        \"isLog\": true,\n" +
                "        \"logContent\": \"Delay for 2 seconds\",\n" +
                "        \"duration\": 2000\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 13,\n" +
                "        \"actionType\": \"CLICK\",\n" +
                "        \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1726844127/i3jjo1kgdkphcaxqfmz3.jpg\",\n" +
                "        \"isLog\": true,\n" +
                "        \"logContent\": \"Click on the image\",\n" +
                "        \"duration\": 100\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 14,\n" +
                "        \"actionType\": \"FOR\",\n" +
                "        \"executeActions\": [\n" +
                "            {\n" +
                "                \"index\": 15,\n" +
                "                \"actionType\": \"DELAY\",\n" +
                "                \"isLog\": true,\n" +
                "                \"logContent\": \"Delay for 1 second\",\n" +
                "                \"duration\": 1000\n" +
                "            },\n" +
                "            {\n" +
                "                \"index\": 16,\n" +
                "                \"actionType\": \"CLICK\",\n" +
                "                \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1727182935/dzaoq0zfrfnuvmjqqgky.jpg\",\n" +
                "                \"isLog\": true,\n" +
                "                \"logContent\": \"Click on the image\",\n" +
                "                \"duration\": 100\n" +
                "            },\n" +
                "            {\n" +
                "                \"index\": 17,\n" +
                "                \"actionType\": \"DELAY\",\n" +
                "                \"isLog\": true,\n" +
                "                \"logContent\": \"Delay for 1 second\",\n" +
                "                \"duration\": 1000\n" +
                "            },\n" +
                "            {\n" +
                "                \"index\": 18,\n" +
                "                \"actionType\": \"CLICK\",\n" +
                "                \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1727182947/ajzgz5fjgfxbdunsivbj.jpg\",\n" +
                "                \"isLog\": true,\n" +
                "                \"logContent\": \"Click on the image\",\n" +
                "                \"duration\": 100\n" +
                "            }\n" +
                "        ],\n" +
                "        \"isLog\": true,\n" +
                "        \"duration\": 500,\n" +
                "        \"times\": 3\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 19,\n" +
                "        \"actionType\": \"DELAY\",\n" +
                "        \"isLog\": true,\n" +
                "        \"logContent\": \"Delay for 1 second\",\n" +
                "        \"duration\": 1000\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 20,\n" +
                "        \"actionType\": \"CLICK\",\n" +
                "        \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1727183150/calprkydbyd3a8qehpxm.jpg\",\n" +
                "        \"isLog\": true,\n" +
                "        \"logContent\": \"Click on the image\",\n" +
                "        \"duration\": 100\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 21,\n" +
                "        \"actionType\": \"DELAY\",\n" +
                "        \"isLog\": true,\n" +
                "        \"logContent\": \"Delay for 1 second\",\n" +
                "        \"duration\": 1000\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 22,\n" +
                "        \"actionType\": \"CLICK\",\n" +
                "        \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1727183150/calprkydbyd3a8qehpxm.jpg\",\n" +
                "        \"isLog\": true,\n" +
                "        \"logContent\": \"Click on the image\",\n" +
                "        \"duration\": 100\n" +
                "    },\n" +
                "    {\n" +
                "        \"index\": 23,\n" +
                "        \"actionType\": \"END_RUN\"\n" +
                "    }\n" +
                "]");
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text change
                validateJson();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validate JSON on text change
                validateJson();
                Log.e(TAG, "Valiadtor call" );
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text change
                validateJson();
            }
        });

        onViewStart(view);
        eventHandling();
        validateJson();
        // Inflate the layout for this fragment
        return view;
    }

    private void validateJson() {
        String jsonText = inputEditText.getText().toString();

        ///validationResult.setText(isValidJson(jsonText) ? "Valid JSON" : "Invalid JSON");
        if (isValidJson(jsonText)) {
            //set green color
            validationResult.setText("");
        } else {
        }
    }

    private boolean isValidJson(String json) {
        try {
            // Attempt to parse as JSON array
            new JSONArray(json);
            textView.setError(null);
            initiateOverlayButton.setEnabled(true);
            return true;
        } catch (JSONException e) {
            textView.setError("Invalid JSON");
            initiateOverlayButton.setEnabled(false);
            validationResult.setText(truncate(e+ "" , 65 ));
            return false; // If an exception is thrown, it's not valid JSON
        }
    }
    private String truncate(String input, int maxLength) {
        if (input.length() <= maxLength) {
            return input;
        }
        return input.substring(0, maxLength) + "..."; // Append ellipsis if truncated
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

        screenCaptureHelper.startProjection();
    }
}