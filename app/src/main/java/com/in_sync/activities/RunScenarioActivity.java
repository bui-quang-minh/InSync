package com.in_sync.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.in_sync.R;
import com.in_sync.helpers.ScreenCapturePermissionUtils;

public class RunScenarioActivity extends AppCompatActivity {

    private static final String TAG = "RunScenarioActivity";
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private Button initiateOverlayButton;
    private Context context;
    private com.google.android.material.textfield.TextInputEditText inputEditText;
    private ScreenCapturePermissionUtils screenCaptureHelper;
    private ActivityResultLauncher<Intent> screenCaptureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_run_scenario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initService();
        eventHandling();
    }

    private void initService() {
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
        screenCaptureHelper = new ScreenCapturePermissionUtils(context, screenCaptureLauncher);
    }

    private void initViews() {
        inputEditText = findViewById(R.id.content_edit_text);
        context = RunScenarioActivity.this;
        Intent intent = getIntent();
        if (intent != null) {
            String json = intent.getStringExtra("android_json_string");
            if (json != null) {
                inputEditText.setText(json);
                inputEditText.setEnabled(false);
                inputEditText.setMovementMethod(new ScrollingMovementMethod());

            }
        }
        initiateOverlayButton = findViewById(R.id.run_scenario_btn);


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