package com.in_sync.fragments;

import static com.in_sync.BuildConfig.INSYNC_API;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.in_sync.R;
import com.in_sync.activities.LoginActivity;
import com.in_sync.activities.ScenariosActivity;
import com.in_sync.adapters.LogSessionAdapter;
import com.in_sync.adapters.ProjectAdapter;
import com.in_sync.adapters.ScenarioAdapter;
import com.in_sync.adapters.ScenarioSpinnerAdapter;
import com.in_sync.api.APIProject;
import com.in_sync.api.ResponsePaging;
import com.in_sync.api.ResponseSuccess;
import com.in_sync.common.ApiClient;
import com.in_sync.daos.LogsFirebaseService;
import com.in_sync.dialogs.AddProjectDialog;
import com.in_sync.dialogs.UpdateProjectDialog;
import com.in_sync.models.LogSession;
import com.in_sync.models.Project;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ProjectFragment extends Fragment implements
        ProjectAdapter.OnItemClickProjectListener, AddProjectDialog.AddProjectDialogListener
        , UpdateProjectDialog.UpdateProjectDialogListener {
    private static final String[] sortOptions = {LogsFirebaseService.SORT_A_Z, LogsFirebaseService.SORT_Z_A, LogsFirebaseService.SORT_BY_NEWEST, LogsFirebaseService.SORT_BY_OLDEST};
    Toolbar toolbar;
    ProgressBar progressBar;
    RecyclerView projectRecyclerView;
    SearchView searchViewInToolBar;
    ProjectAdapter projectAdapter;
    TextView notifyTextView;
    ImageView empty_box;

    APIProject apiProject;
    String userIdClerk = "";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handlerEvent();
        //Init data

        searchProject(sortOptions[2]);
    }

    private void handlerEvent() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_project, container, false);
        String model = Build.MODEL;
        setHasOptionsMenu(true);
        getInformationUserLogin();
        checkUserLogin();
        initView(rootView);
        return rootView;
    }

    private void getInformationUserLogin() {
        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
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


    private void initView(View rootView) {

        toolbar = rootView.findViewById(R.id.toolbar_project);
        progressBar = rootView.findViewById(R.id.progress_bar);
        projectRecyclerView = rootView.findViewById(R.id.project_recycle);
        notifyTextView = rootView.findViewById(R.id.notify_no_project);
        empty_box = rootView.findViewById(R.id.empty_box);
        // api
        apiProject = ApiClient.getRetrofitInstance().create(APIProject.class);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        //initDataForScenarioSpinner();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Inflate menu vào ActionBar
        inflater.inflate(R.menu.project_menu, menu);

        // Lấy SearchView từ menu và thiết lập sự kiện tìm kiếm
        MenuItem searchItem = menu.findItem(R.id.action_search_project);
        searchViewInToolBar = (SearchView) searchItem.getActionView();
        searchViewInToolBar.setQueryHint("Enter key word to search project...");
        searchViewInToolBar.setIconified(false);
        searchViewInToolBar.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchViewInToolBar.findFocus(), InputMethodManager.SHOW_IMPLICIT);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Expand the search view to take full width
                expandSearchView();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Reset to normal size when collapsed
                resetSearchView();
                return true;
            }
        });
        searchViewInToolBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProject(sortOptions[2]);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProject(sortOptions[2]);
                return true;
            }
        });
        searchViewInToolBar.setOnCloseListener(() -> {
            // Hide keyboard when SearchView is closed
            imm.hideSoftInputFromWindow(searchViewInToolBar.getWindowToken(), 0);
            return false; // Return false to allow default behavior (collapse the SearchView)
        });
    }

    // Method to expand the search view to full width
    private void expandSearchView() {
        searchViewInToolBar.setMaxWidth(Integer.MAX_VALUE);  // Set to max width
    }

    // Method to reset search view width when collapsed
    private void resetSearchView() {
        searchViewInToolBar.setMaxWidth(-1);  // Reset to default width
    }

    // Phan Quang Huy
    // Handle event when user selects item in ActionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Xử lý sự kiện khi người dùng chọn mục trong ActionBar
        if (item.getItemId() == R.id.action_sort_project) {
            showSortOptionsDialog();
            return true;
        } else if (item.getItemId() == R.id.action_add_project) {
            FragmentManager fm = getParentFragmentManager();
            AddProjectDialog addProjectDialog = new AddProjectDialog(getContext(), ProjectFragment.this);
            addProjectDialog.show(fm, "AddProjectDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortOptionsDialog() {
        // Các tùy chọn sắp xếp
        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Sort Option")
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý khi người dùng chọn một tùy chọn
                        switch (which) {
                            case 0:
                                searchProject(sortOptions[0]);
                                break;
                            case 1:
                                searchProject(sortOptions[1]);
                                break;
                            case 2:
                                searchProject(sortOptions[2]);
                                break;
                            case 3:
                                searchProject(sortOptions[3]);
                                break;
                        }
                    }
                });
        // Hiển thị dialog
        builder.show();
    }

    private void searchProject(String sort) {
        String keySearch = "";
        if (searchViewInToolBar != null) {
            keySearch = searchViewInToolBar.getQuery().toString();
        }

        progressBar.setVisibility(View.VISIBLE);
        Call<ResponsePaging<ArrayList<Project>>> callProject = apiProject.getAllProjectsOfUser(userIdClerk, keySearch, INSYNC_API);
        callProject.enqueue(new Callback<ResponsePaging<ArrayList<Project>>>() {
            @Override
            public void onResponse(Call<ResponsePaging<ArrayList<Project>>> call, Response<ResponsePaging<ArrayList<Project>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    ResponsePaging<ArrayList<Project>> responsePaging = response.body();
                    ArrayList<Project> projects = responsePaging.getData();

                    switch (sort) {
                        case LogsFirebaseService.SORT_A_Z:
                            projects.sort((o1, o2) -> o1.getProjectName().compareTo(o2.getProjectName()));
                            break;
                        case LogsFirebaseService.SORT_Z_A:
                            projects.sort((o1, o2) -> o2.getProjectName().compareTo(o1.getProjectName()));
                            break;
                        case LogsFirebaseService.SORT_BY_NEWEST:
                            projects.sort((o1, o2) -> o2.getDateCreated().compareTo(o1.getDateCreated()));
                            break;
                        case LogsFirebaseService.SORT_BY_OLDEST:
                            projects.sort((o1, o2) -> o1.getDateCreated().compareTo(o2.getDateCreated()));
                            break;
                    }
                    projectAdapter = new ProjectAdapter(getContext(), projects, ProjectFragment.this);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    projectRecyclerView.setAdapter(projectAdapter);
                    projectRecyclerView.setLayoutManager(layoutManager);

                    if (projects == null || projects.isEmpty()) {
                        notifyTextView.setVisibility(View.VISIBLE);
                        empty_box.setVisibility(View.VISIBLE);
                        return;
                    } else {
                        notifyTextView.setVisibility(View.GONE);
                        empty_box.setVisibility(View.GONE);

                        return;
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error occurred during data retrievalL", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponsePaging<ArrayList<Project>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error occurred during data retrieval", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewClick(View view, int position) {
        Project project = projectAdapter.getItem(position);
        if (project == null) {
            Toast.makeText(getContext(), "Project information is not correct.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getActivity(), ScenariosActivity.class);
            intent.putExtra("projectId", project.getId().toString());
            intent.putExtra("projectName", project.getProjectName());
            startActivity(intent);
        }

    }

    @Override
    public void onUpdateClick(View view, int position) {
        Project project = projectAdapter.getItem(position);
        if (project == null) {

            return;
        }
        FragmentManager fm = getParentFragmentManager();
        UpdateProjectDialog dialogFragment = new UpdateProjectDialog(getContext(), ProjectFragment.this);
        Bundle args = new Bundle();
        args.putSerializable("projectUpdate", project);
        dialogFragment.setArguments(args);

        dialogFragment.show(fm, "popup_update_topic");
    }

    @Override
    public void onDeleteClick(View view, int position) {
        Project project = projectAdapter.getItem(position);
        if (project == null) {
            showDialogNotification("Error", "Project information is not correct.", 1000);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Notification delele project.");
            builder.setMessage("Are you sure you want to delete this project?");
            builder.setIcon(R.drawable.alert);
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {

                Call<ResponseSuccess> callDeleteProject = apiProject.DeleteProject(project.getId(), INSYNC_API);
                progressBar.setVisibility(View.VISIBLE);
                callDeleteProject.enqueue(new Callback<ResponseSuccess>() {
                    @Override
                    public void onResponse(Call<ResponseSuccess> call, Response<ResponseSuccess> response) {
                        ResponseSuccess responseSuccess = response.body();
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            showDialogNotification("Delete success", responseSuccess.getMessage(), 1000);
                            searchProject(sortOptions[2]);
                        } else {
                            showDialogNotification("Delete failed", "Project information is not correct.", 1000);
                        }

                    }
                    @Override
                    public void onFailure(Call<ResponseSuccess> call, Throwable t) {
                        showDialogNotification("Error", "Project information is not correct.", 1000);
                        progressBar.setVisibility(View.GONE);
                    }
                });

            });
            builder.setNegativeButton("No", (dialogInterface, i) -> {

            });
            builder.create().show();
        }


    }

    public void showDialogNotification(String title, String message, int timeShow) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.alert);
        builder.setTitle(title);
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

    @Override
    public void onAddDialogClosed() {
        searchProject(sortOptions[2]);
    }
}
