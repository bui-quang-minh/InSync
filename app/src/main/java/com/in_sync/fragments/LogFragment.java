package com.in_sync.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.in_sync.LogsOfSessionActivity;
import com.in_sync.R;
import com.in_sync.adapters.LogSessionAdapter;
import com.in_sync.adapters.ScenarioSpinnerAdapter;
import com.in_sync.daos.FirebaseLogService;
import com.in_sync.models.LogSession;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LogFragment extends Fragment implements LogSessionAdapter.OnItemClickLogSessionListener {
    Toolbar toolbar;
    ProgressBar progressBar;

    RecyclerView logSessionRecyclerView;
    Spinner scenarioSpinner;
    SearchView searchViewInToolBar;
    TextView notifyTextView;
    ImageView sort_icon;
    View overlay;
    ScenarioSpinnerAdapter scenarioSpinnerAdapter;
    LogSessionAdapter sessionAdapter;
    FirebaseLogService service;
    private static final String TAG = "LogFragment";
    private static final String All_SCENARIO = "All Scenario";
    private static final  String[] sortOptions = {FirebaseLogService.SORT_A_Z, FirebaseLogService.SORT_Z_A, FirebaseLogService.SORT_BY_NEWEST, FirebaseLogService.SORT_BY_OLDEST};

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handlerEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        setHasOptionsMenu(true);
        initView(view);

        return view;
    }

    // Phan Quang Huy
    // Initialize controls
    private void initView(View view) {
        overlay = view.findViewById(R.id.overlay);
        service = new FirebaseLogService();
        toolbar = view.findViewById(R.id.toolbar_log);
        progressBar = view.findViewById(R.id.progress_bar);
        logSessionRecyclerView = view.findViewById(R.id.log_session_recycle);
        scenarioSpinner = view.findViewById(R.id.scenario_sp);
        notifyTextView = view.findViewById(R.id.notify_no_session);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        initDataForScenarioSpinner();
    }


    // Phan Quang Huy
    // Handle events in the screen
    private void handlerEvent() {

        scenarioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchSessionLog(sortOptions[2]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("alsdfjalds", "Nothing");
            }
        });
    }

    // Phan Quang Huy
    // Show sort options dialog
    private void showSortOptionsDialog() {
        // Các tùy chọn sắp xếp
        String[] sortOptions = {FirebaseLogService.SORT_A_Z, FirebaseLogService.SORT_Z_A, FirebaseLogService.SORT_BY_NEWEST, FirebaseLogService.SORT_BY_OLDEST};

        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Sort Option")
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý khi người dùng chọn một tùy chọn
                        switch (which) {
                            case 0:
                               searchSessionLog(sortOptions[0]);
                                break;
                            case 1:
                                searchSessionLog(sortOptions[1]);
                                break;
                            case 2:
                                searchSessionLog(sortOptions[2]);
                                break;
                            case 3:
                                searchSessionLog(sortOptions[3]);
                                break;
                        }
                    }
                });
        // Hiển thị dialog
        builder.show();
    }

    // Phan Quang Huy
    // Inflate menu vào ActionBar
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
        searchViewInToolBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchSessionLog(sortOptions[2]);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchSessionLog(sortOptions[2]);
                return true;
            }
        });
        searchViewInToolBar.setOnCloseListener(() -> {
            // Hide keyboard when SearchView is closed
            imm.hideSoftInputFromWindow(searchViewInToolBar.getWindowToken(), 0);
            return false; // Return false to allow default behavior (collapse the SearchView)
        });


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

    // Phan Quang Huy
    // Get data for scenario spinner
    public void initDataForScenarioSpinner() {
        overlay.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        service.getAllScenario(new FirebaseLogService.LogCallback<List<String>>() {
            @Override
            public void onCallback(List<String> data) {
                List<String> result = new ArrayList<>();
                if (data != null) {
                    result = data;
                    result.add(0, All_SCENARIO);
                    scenarioSpinnerAdapter = new ScenarioSpinnerAdapter(getContext(), result);
                    scenarioSpinner.setAdapter(scenarioSpinnerAdapter);
                }
                overlay.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Phan Quang Huy
    // Search session log
    public void searchSessionLog(String sortBy) {
        String scenarioId = (String) scenarioSpinner.getSelectedItem();
        String search = searchViewInToolBar.getQuery().toString();

        if (scenarioId == null) {
            Toast.makeText(getContext(), "Please select scenario", Toast.LENGTH_SHORT).show();
            return;
        }
        if(scenarioId.equals(All_SCENARIO)){
            GetAllLogSessionOfAllScenario(search, sortBy);
        }else{
            GetAllLogSessionOfScenario(scenarioId, search, sortBy);
        }
    }


    // Phan Quang Huy
    // Get all log session of a scenario
    public void GetAllLogSessionOfScenario(String scenarioId, String search, String sortBy) {
        progressBar.setVisibility(View.VISIBLE);
        service.getLogSessionsByScenarioIdAndDate(scenarioId, null, null, search, sortBy,new FirebaseLogService.LogCallback<List<LogSession>>() {
            @Override
            public void onCallback(List<LogSession> data) {
                List<LogSession> result = new ArrayList<>();
                if (data != null) {
                    result = data;
                    notifyTextView.setVisibility(View.GONE);
                    sessionAdapter = new LogSessionAdapter(getContext(), result, LogFragment.this);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    logSessionRecyclerView.setLayoutManager(layoutManager);
                    logSessionRecyclerView.setAdapter(sessionAdapter);
                } else {
                    notifyTextView.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Phan Quang Huy
    // Get all log session of all scenario
    public void GetAllLogSessionOfAllScenario(String search, String sortBy) {
        progressBar.setVisibility(View.VISIBLE);
        service.getAllScenario(new FirebaseLogService.LogCallback<List<String>>() {
            @Override
            public void onCallback(List<String> data) {
                if(data == null){
                    return;
                }else{
                    service.getLogSessionsByListScenarioIdAndDate(data, null, null, search, sortBy,new FirebaseLogService.LogCallback<List<LogSession>>() {
                        @Override
                        public void onCallback(List<LogSession> data) {
                            List<LogSession> result = new ArrayList<>();
                            if (data != null) {
                                result = data;
                                notifyTextView.setVisibility(View.GONE);
                                sessionAdapter = new LogSessionAdapter(getContext(), result, LogFragment.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                logSessionRecyclerView.setLayoutManager(layoutManager);
                                logSessionRecyclerView.setAdapter(sessionAdapter);
                            } else {
                                notifyTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    // Phan Quang Huy
    // Handle click event to switch to a new activity
    @Override
    public void onViewClick(View view, int position) {
        LogSession logSession = sessionAdapter.getItem(position);
        if (logSession == null) {
            Toast.makeText(getContext(), "Can't find the right session please try again", Toast.LENGTH_SHORT).show();
        }
        String logSessionId = logSession.getSession_id();
        String scenarioId = logSession.getScenario_id();
        Intent intent = new Intent(getContext(), LogsOfSessionActivity.class);
        intent.putExtra("scenarioId", scenarioId);
        intent.putExtra("logSessionId", logSessionId);
        startActivity(intent);

        Log.e(TAG, "CLick Detail");
    }

    // Phan Quang Huy
    // Handle click event to delete a session
    @Override
    public void onDeleteClick(View view, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Notification delele session");
        builder.setMessage("Are you sure you want to delete this session?");
        builder.setIcon(R.drawable.alert);
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            String scenarioId = (String) scenarioSpinner.getSelectedItem();
            LogSession logSession = sessionAdapter.getItem(position);
            if (logSession == null) {
                Toast.makeText(getContext(), "Can't find the right", Toast.LENGTH_SHORT).show();
            }
            String logSessionId = logSession.getSession_id();
            service.deleteLogSession(scenarioId, logSessionId, new FirebaseLogService.LogCallback<Boolean>() {

                @Override
                public void onCallback(Boolean data) {
                    if (data) {
                        Toast.makeText(getContext(), "Delete success", Toast.LENGTH_SHORT).show();
                        searchSessionLog(sortOptions[2]);
                    } else {
                        Toast.makeText(getContext(), "Delete fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {

        });
        builder.create().show();

        Log.e(TAG, "CLick Delete");
    }


}