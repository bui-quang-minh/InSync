package com.in_sync.dialogs;

import static com.in_sync.BuildConfig.API_KEY;
import static com.in_sync.BuildConfig.API_SECRET;
import static com.in_sync.BuildConfig.CLOUD_NAME;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.in_sync.R;
import com.in_sync.activities.LoginActivity;
import com.in_sync.adapters.ProjectAdapter;
import com.in_sync.adapters.ProjectSpinnerAdapter;
import com.in_sync.api.APIAssets;
import com.in_sync.api.APIProject;
import com.in_sync.api.ResponsePaging;
import com.in_sync.api.ResponseSuccess;
import com.in_sync.common.ApiClient;
import com.in_sync.daos.LogsFirebaseService;
import com.in_sync.dtos.AssetDtos;
import com.in_sync.dtos.ProjectDtos;
import com.in_sync.fragments.ProjectFragment;
import com.in_sync.models.Project;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadAssetsDialog  extends DialogFragment {

    private UploadAssetsDialog.UploadAssetsDialogListener mListener;
    private Context context;
    private List<String> selectedImages;
    TextInputEditText projectName, description;
    SwitchMaterial isPublish;
    Button btn_add, btn_cancel;
    APIProject apiProject;
    APIAssets apiAssets;
    String userIdClerk = "";
    Spinner spinner;
    ArrayList<Project> projects = new ArrayList<>();
    String projectId = "";
    ProgressBar progressBar;


    public UploadAssetsDialog(Context context, UploadAssetsDialogListener listener, List<String> selectedImages) {
        mListener = listener;
        this.context = context;
        this.selectedImages = selectedImages;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            apiProject = ApiClient.getRetrofitInstance().create(APIProject.class);
            apiAssets = ApiClient.getRetrofitInstance().create(APIAssets.class);
        }
        getInformationUserLogin();
        checkUserLogin();
    }
    private void getInformationUserLogin() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String userInfo = sharedPreferences.getString("UserInfo", "User");
        Log.d("UserInfo", userInfo);

        try {
            JSONArray userArray = new JSONArray(userInfo);
            if (userArray.length() > 0) {
                JSONObject user = userArray.getJSONObject(0);
                userIdClerk = user.getString("id");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkUserLogin() {
        if (userIdClerk.equals("")) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = requireActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.assets_upload_layout, null);
        //View layout = inflater.inflate(R.layout.custom_toast, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_add = view.findViewById(R.id.btn_add);
        progressBar = view.findViewById(R.id.progressBar);
        btn_add.setEnabled(false);
        spinner = view.findViewById(R.id.projectSpinner);
        spinner.setVisibility(View.GONE);
        Call<ResponsePaging<ArrayList<Project>>> callProject = apiProject.getAllProjectsOfUser(userIdClerk, "");
        callProject.enqueue(new Callback<ResponsePaging<ArrayList<Project>>>() {
            @Override
            public void onResponse(Call<ResponsePaging<ArrayList<Project>>> call, Response<ResponsePaging<ArrayList<Project>>> response) {
                if (response.isSuccessful()) {
                    ResponsePaging<ArrayList<Project>> responsePaging = response.body();
                    projects = responsePaging.getData();
                    for (Project project : projects) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Log.e("Project", project.getProjectName());
                        }
                    }
                    ProjectSpinnerAdapter adapter = new ProjectSpinnerAdapter(getContext(), projects);
                    spinner.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    spinner.setVisibility(View.VISIBLE);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Project selectedProject = adapter.getItem(position);
                            if (selectedProject != null) {
                                projectId = null; // Assuming you have a getId() method in Project class
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    projectId = String.valueOf(selectedProject.getId());
                                }
                                Log.d("SelectedProject", "Selected Project ID: " + projectId);
                                btn_add.setEnabled(true);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Do nothing
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Error occurred during data retrievalL", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsePaging<ArrayList<Project>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error occurred during data retrieval", Toast.LENGTH_SHORT).show();
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                Log.e("Upload", "Image uploaded: " + uploadResult.get("url"));
                                if (context != null) {
                                    ((Activity) context).runOnUiThread(() ->
                                            Toast.makeText(context, "Upload completed!", Toast.LENGTH_SHORT).show()
                                    );
                                }
                                AssetDtos.AddAssetDto addAssetDto = new AssetDtos.AddAssetDto();
                                addAssetDto.setProjectId(projectId);
                                addAssetDto.setAssetName(file.getName());
                                addAssetDto.setType("image");
                                addAssetDto.setFilePath(uploadResult.get("url").toString());
                                Call<ResponseSuccess> callAddAssets= apiAssets.AddAsset(addAssetDto);
                                callAddAssets.enqueue(new Callback<ResponseSuccess>() {
                                    @Override
                                    public void onResponse(Call<ResponseSuccess> call, Response<ResponseSuccess> response) {
                                        if (response.isSuccessful()) {
                                            ResponseSuccess responseSuccess = response.body();
                                            if (response.isSuccessful()) {
                                                showDialogNotification("Success", "Upload completed", 1000);
                                            } else {
                                                showDialogNotification("Error", "Upload failed. Please try again", 1000);
                                            }
                                        } else {
                                            showDialogNotification("Error", "Upload failed. Please try again", 1000);
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<ResponseSuccess> call, Throwable t) {
                                        showDialogNotification("Error", "Upload failed. Please try again", 1000);
                                    }
                                });
                            } catch (Exception e) {
                                Log.e("Upload", "Upload failed: ", e);
                            }
                        }).start(); // Start a new thread to avoid blocking the UI
                    }
                }
                Log.e("Upload", "sendData: upload completed");
                dialog.dismiss();
            }
        });
        return dialog;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mListener.onAddDialogClosed();
            }
        }
    }

    public void showDialogNotification(String title, String message, int timeShow) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle(title);
//        if(title.equalsIgnoreCase("Success")){
//            builder.setIcon(R.drawable.status_success_of_log);
//        }else{
//            builder.setIcon(R.drawable.status_fail_of_log);
//        }
        builder.setIcon(R.drawable.baseline_info_24);
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        }, timeShow);
    }

    public interface UploadAssetsDialogListener{
        void onAddDialogClosed();
    }
}