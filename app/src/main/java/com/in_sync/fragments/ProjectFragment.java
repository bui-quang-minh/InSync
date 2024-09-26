package com.in_sync.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.in_sync.R;
import com.in_sync.adapters.LogSessionAdapter;
import com.in_sync.adapters.ScenarioSpinnerAdapter;
import com.in_sync.api.APIProject;
import com.in_sync.common.ApiClient;
import com.in_sync.daos.LogsFirebaseService;
import com.in_sync.models.Project;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ProjectFragment extends Fragment {

    Toolbar toolbar;
    ProgressBar progressBar;

    RecyclerView logSessionRecyclerView;
    Spinner scenarioSpinner;
    SearchView searchViewInToolBar;
    TextView notifyTextView;
    ImageView sort_icon;

    APIProject apiProject;

    View overlay;
    ScenarioSpinnerAdapter scenarioSpinnerAdapter;
    LogSessionAdapter sessionAdapter;
    LogsFirebaseService service;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handlerEvent();
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
        initView(rootView);
        apiProject = ApiClient.getRetrofitInstance(com.in_sync.common.Settings.BASE_SYSTEM_API_URL).create(APIProject.class);
        Call<ArrayList<Project>> callProject = apiProject.getAllProjects();

        callProject.enqueue(new Callback<ArrayList<Project>>() {
            @Override
            public void onResponse(Call<ArrayList<Project>> call, Response<ArrayList<Project>> response) {
                if(response.isSuccessful()){
                    ArrayList<Project> projects = response.body();
                    for(Project project: projects){
                        Log.e("Project", project.getProjectName());
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Project>> call, Throwable t) {
                int i = 0;
            }
        });

        return rootView;
    }

    private void initView(View rootView) {

        overlay = rootView.findViewById(R.id.overlay);
        //service = new LogsFirebaseService();
        toolbar = rootView.findViewById(R.id.toolbar_project);
        progressBar = rootView.findViewById(R.id.progress_bar);
        logSessionRecyclerView = rootView.findViewById(R.id.log_session_recycle);
        scenarioSpinner = rootView.findViewById(R.id.scenario_sp);
        notifyTextView = rootView.findViewById(R.id.notify_no_session);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        //initDataForScenarioSpinner();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Inflate menu vào ActionBar
        inflater.inflate(R.menu.log_menu, menu);

        // Lấy SearchView từ menu và thiết lập sự kiện tìm kiếm
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchViewInToolBar = (SearchView) searchItem.getActionView();
        searchViewInToolBar.setQueryHint("Search...");
        searchViewInToolBar.setIconified(false);
        searchViewInToolBar.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchViewInToolBar.findFocus(), InputMethodManager.SHOW_IMPLICIT);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Expand the search view to take full width
                expandSearchView(searchViewInToolBar);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Reset to normal size when collapsed
                resetSearchView(searchViewInToolBar);
                return true;
            }
        });
        searchViewInToolBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //searchSessionLog(sortOptions[2]);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //searchSessionLog(sortOptions[2]);
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
    private void expandSearchView(SearchView searchView) {
        searchView.setMaxWidth(Integer.MAX_VALUE);  // Set to max width
    }

    // Method to reset search view width when collapsed
    private void resetSearchView(SearchView searchView) {
        searchView.setMaxWidth(-1);  // Reset to default width
    }

    // Phan Quang Huy
    // Handle event when user selects item in ActionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Xử lý sự kiện khi người dùng chọn mục trong ActionBar
        if (item.getItemId() == R.id.action_sort) {
            showSortOptionsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showSortOptionsDialog() {
        // Các tùy chọn sắp xếp
        String[] sortOptions = {LogsFirebaseService.SORT_A_Z, LogsFirebaseService.SORT_Z_A, LogsFirebaseService.SORT_BY_NEWEST, LogsFirebaseService.SORT_BY_OLDEST};

        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Sort Option")
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý khi người dùng chọn một tùy chọn
                        switch (which) {
                            case 0:
                                //searchSessionLog(sortOptions[0]);
                                break;
                            case 1:
                                //searchSessionLog(sortOptions[1]);
                                break;
                            case 2:
                                //searchSessionLog(sortOptions[2]);
                                break;
                            case 3:
                                //searchSessionLog(sortOptions[3]);
                                break;
                        }
                    }
                });
        // Hiển thị dialog
        builder.show();
    }
}
