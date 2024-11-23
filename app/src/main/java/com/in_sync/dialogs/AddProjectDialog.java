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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddProjectDialog extends DialogFragment {

    private AddProjectDialogListener mListener;
    private Context context;
    TextInputEditText projectName, description;
    SwitchMaterial isPublish;
    Button btn_add, btn_cancel;
    APIProject apiProject;
    String userIdClerk = "";


    public AddProjectDialog(Context context,AddProjectDialogListener listener) {
      mListener = listener;
      this.context = context;
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
        View view = inflater.inflate(R.layout.add_project_layout, null);


        projectName = view.findViewById(R.id.add_project_name);
        description = view.findViewById(R.id.add_project_desciption);
        isPublish = view.findViewById(R.id.add_project_is_publish);
        btn_add = view.findViewById(R.id.btn_add_project);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        apiProject = ApiClient.getRetrofitInstance().create(APIProject.class);


        builder.setView(view);
        AlertDialog dialog = builder.create();


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị view

                ProjectDtos.AddProjectDto addProjectDto = new ProjectDtos.AddProjectDto();
                addProjectDto.setProjectName(projectName.getText().toString());
                addProjectDto.setDescription(description.getText().toString());
                addProjectDto.setIsPublish(isPublish.isChecked());
                addProjectDto.setUserIdClerk(userIdClerk);
                Call<ResponseSuccess> callAddProject = apiProject.AddProject(addProjectDto , INSYNC_API);

                btn_add.setEnabled(false);
                callAddProject.enqueue(new Callback<ResponseSuccess>() {
                    @Override
                    public void onResponse(Call<ResponseSuccess> call, Response<ResponseSuccess> response) {
                        if(response.isSuccessful()){
                            showDialogNotification("Success", "Add project successfully", 1000);
                            dialog.dismiss();
                        }else{
                            showDialogNotification("Error", "Add project failed. Please try again", 1000);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseSuccess> call, Throwable t) {
                        showDialogNotification("Error", "Add project failed. Please try again", 1000);
                    }
                });
                btn_add.setEnabled(false);

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

    public interface AddProjectDialogListener {
        void onAddDialogClosed();
    }
}
