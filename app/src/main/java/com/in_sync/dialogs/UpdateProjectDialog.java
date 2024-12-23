package com.in_sync.dialogs;

import static com.in_sync.BuildConfig.INSYNC_API;

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
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.in_sync.R;
import com.in_sync.activities.LoginActivity;
import com.in_sync.api.APIProject;
import com.in_sync.api.ResponseSuccess;
import com.in_sync.common.ApiClient;
import com.in_sync.dtos.ProjectDtos;
import com.in_sync.models.Project;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@RequiresApi(api = Build.VERSION_CODES.O)

public class UpdateProjectDialog extends DialogFragment {
    private UpdateProjectDialogListener mListener;
    private Context context;
    TextInputEditText projectName, description;
    TextView projectId;
    SwitchMaterial isPublish;
    Button btn_update, btn_cancel;
    APIProject apiProject;
    String userIdClerk = "";


    public UpdateProjectDialog(Context context, UpdateProjectDialogListener listener) {
        mListener = listener;
        this.context = context;
        getInformationUserLogin();
        checkUserLogin();

    }
    private void getInformationProject(){
        if (getArguments() != null) {
            Project project = (Project) getArguments().getSerializable("projectUpdate");
            projectName.setText(project.getProjectName());
            description.setText(project.getDescription());
            isPublish.setChecked(project.getIsPublish());
            projectId.setText(project.getId().toString());
        }
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
        View view = inflater.inflate(R.layout.update_project_layout, null);

        projectName = view.findViewById(R.id.update_project_name);
        description = view.findViewById(R.id.update_project_desciption);
        isPublish = view.findViewById(R.id.update_project_is_publish);
        btn_update = view.findViewById(R.id.btn_update_project);
        projectId = view.findViewById(R.id.update_project_id);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        apiProject = ApiClient.getRetrofitInstance().create(APIProject.class);
        getInformationProject();
        builder.setView(view);
        AlertDialog dialog = builder.create();


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị view
                btn_update.setEnabled(false);
                ProjectDtos.UpdateProjectDto updateProjectDto = new ProjectDtos.UpdateProjectDto();
                UUID project_id = UUID.fromString(projectId.getText().toString());
                updateProjectDto.setProjectName(projectName.getText().toString());
                updateProjectDto.setDescription(description.getText().toString());
                updateProjectDto.setIsPublish(isPublish.isChecked());
                updateProjectDto.setId(project_id);

                Call<ResponseSuccess> updateProject = apiProject.UpdateProject(project_id, updateProjectDto, INSYNC_API);

                updateProject.enqueue(new Callback<ResponseSuccess>() {
                    @Override
                    public void onResponse(Call<ResponseSuccess> call, Response<ResponseSuccess> response) {
                        if(response.isSuccessful()){
                            showDialogNotification("Success", "Update project successfully", 1000);
                            dialog.dismiss();
                        }else{
                            showDialogNotification("Error", "Update project failed. Please try again", 1000);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseSuccess> call, Throwable t) {
                        showDialogNotification("Error", "Update project failed. Please try again", 1000);
                    }
                });
                btn_update.setEnabled(true);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            mListener.onAddDialogClosed();
        }
    }

    public void showDialogNotification(String title, String message, int timeShow) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle(title);
        if(title.equalsIgnoreCase("Success")){
            //builder.setIcon(R.drawable.status_success_of_log);
        }else{
            //builder.setIcon(R.drawable.status_fail_of_log);
        }

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

    public interface UpdateProjectDialogListener {
        void onAddDialogClosed();
    }
}
