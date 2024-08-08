package com.in_sync.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.in_sync.R;
import com.in_sync.activities.ScreenCapturePermissionActivity;
import com.in_sync.validates.PermissionValid;

public class TestFragment extends Fragment {
    private static final String TAG = "TestFragment";
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private Button initiateOverlayButton;
    private Context context;
    private com.google.android.material.textfield.TextInputEditText inputEditText;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context= context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        inputEditText = view.findViewById(R.id.inputEditText);
        inputEditText.setText("[\n" +
                "  {\n" +
                "      \"index\": 1,\n" +
                "      \"parent\": 0,\n" +
                "      \"actionType\": \"CLICK\",\n" +
                "      \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1722710948/svakqn8easwstoxfffig.jpg\",\n" +
                "      \"logResult\" : true,\n" +
                "      \"duration\": 100,\n" +
                "      \"tries\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"index\": 2,\n" +
                "      \"parent\": 0,\n" +
                "      \"actionType\": \"IF\",\n" +
                "      \"conditionType\": \"FIND_SOURCE\",\n" +
                "      \"condition\": \"In-Sync\",\n" +
                "      \"logResult\" : true,\n" +
                "      \"isTrue\": [\n" +
                "\n" +
                "                  {\n" +
                "                    \"index\": 3,\n" +
                "                    \"parent\": 2,\n" +
                "                    \"actionType\": \"CLICK\",\n" +
                "                    \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1722776490/ebmdwccbpwem5fw9nvqs.jpg\",\n" +
                "                    \"logResult\" : true,\n" +
                "                    \"duration\": 100,\n" +
                "                    \"tries\": 1\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"index\": 4,\n" +
                "                    \"parent\": 2,\n" +
                "                    \"actionType\": \"IF\",\n" +
                "                    \"conditionType\": \"FIND_SOURCE\",\n" +
                "                    \"condition\": \"Home\",\n" +
                "                    \"logResult\" : true,\n" +
                "                    \"isTrue\": [\n" +
                "                                \n" +
                "                                {\n" +
                "                                  \"index\": 5,\n" +
                "                                  \"parent\": 4,\n" +
                "                                  \"actionType\": \"CLICK\",\n" +
                "                                  \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1722776541/hl1dcpp0vhwjlppfjmn1.jpg\",\n" +
                "                                  \"logResult\" : true,\n" +
                "                                  \"duration\": 100,\n" +
                "                                  \"tries\": 1\n" +
                "                                }\n" +
                "                              ]\n" +
                "                  }\n" +
                "                ],\n" +
                "      \"isFalse\":[\n" +
                "                  {\n" +
                "                    \"index\": 6,\n" +
                "                    \"parent\": 2,\n" +
                "                    \"actionType\": \"CLICK\",\n" +
                "                    \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1722776576/sm0fnvilzzdnnfgnbx4n.jpg\",\n" +
                "                    \"logResult\" : true,\n" +
                "                    \"duration\": 100,\n" +
                "                    \"tries\": 1\n" +
                "                  }\n" +
                "                ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"index\": 7,\n" +
                "      \"parent\": 0,\n" +
                "      \"actionType\": \"CLICK\",\n" +
                "      \"on\": \"https://res.cloudinary.com/dt3zxmzwx/image/upload/v1722776611/wrervde2ep8fr9oxllb6.jpg\",\n" +
                "      \"logResult\" : true,\n" +
                "      \"duration\": 100,\n" +
                "      \"tries\": 1\n" +
                "    }\n" +
                "]\n");
        // Inflate the layout for this fragment
        return view;
    }

    private void onViewStart() {
        initiateOverlayButton = getActivity().findViewById(R.id.initiateOverlayButton);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewStart();
        eventHandling();

    }

    private void eventHandling() {
        initiateOverlayButton.setOnClickListener(this::initiateOverlay);
    }

    private void initiateOverlay(View view) {
        Intent intent = new Intent(context, ScreenCapturePermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("json", inputEditText.getText()) ;
        startActivity(intent);
    }
}