package com.in_sync.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.in_sync.activities.LoginActivity;
import com.in_sync.activities.LogsOfSessionActivity;
import com.in_sync.R;
import com.in_sync.adapters.LogSessionAdapter;
import com.in_sync.adapters.ScenarioSpinnerAdapter;

import com.in_sync.api.APIScenario;
import com.in_sync.api.ResponsePaging;
import com.in_sync.common.ApiClient;
import com.in_sync.daos.LogsFirebaseService;
import com.in_sync.models.LogSession;
import com.in_sync.models.Scenario;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LogFragment extends Fragment implements LogSessionAdapter.OnItemClickLogSessionListener {
    Toolbar toolbar;
    ProgressBar progressBar;

    RecyclerView logSessionRecyclerView;
    Spinner scenarioSpinner;
    SearchView searchViewInToolBar;
    TextView notifyTextView;
    ImageView empty_box;
    View overlay;
    ScenarioSpinnerAdapter scenarioSpinnerAdapter;
    LogSessionAdapter sessionAdapter;
    LogsFirebaseService service;
    APIScenario apiScenario;
    String userIdClerk = "";
    UUID uuidDefault = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String TAG = "LogFragment";
    private static final String All_SCENARIO = "All Scenario";
    private static final String[] sortOptions = {LogsFirebaseService.SORT_A_Z, LogsFirebaseService.SORT_Z_A, LogsFirebaseService.SORT_BY_NEWEST, LogsFirebaseService.SORT_BY_OLDEST};

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
        getInformationUserLogin();
        checkUserLogin();
        initDataForScenarioSpinner();
        return view;
    }

    // Phan Quang Huy
    // Initialize controls
    private void initView(View view) {
        overlay = view.findViewById(R.id.overlay);
        service = new LogsFirebaseService();
        toolbar = view.findViewById(R.id.toolbar_log);
        progressBar = view.findViewById(R.id.progress_bar);
        logSessionRecyclerView = view.findViewById(R.id.log_session_recycle);
        scenarioSpinner = view.findViewById(R.id.scenario_sp);
        scenarioSpinner.setVisibility(View.GONE);
        notifyTextView = view.findViewById(R.id.notify_no_session);
        empty_box = view.findViewById(R.id.empty_box);
        apiScenario = ApiClient.getRetrofitInstance().create(APIScenario.class);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

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

    // Phan Quang Huy
    // Get data for scenario spinner
    public void initDataForScenarioSpinner() {
        progressBar.setVisibility(View.VISIBLE);

        Call<ResponsePaging<ArrayList<Scenario>>> callAllScenario = apiScenario.getAllScenaroOfUserClerk(userIdClerk, "", 0, Integer.MAX_VALUE);
        callAllScenario.enqueue(new Callback<ResponsePaging<ArrayList<Scenario>>>() {
            @Override
            public void onResponse(Call<ResponsePaging<ArrayList<Scenario>>> call, Response<ResponsePaging<ArrayList<Scenario>>> response) {
                try {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        ResponsePaging<ArrayList<Scenario>> responsePaging = response.body();
                        ArrayList<Scenario> listScenario = responsePaging.getData();

                        Scenario allScenario = new Scenario();
                        allScenario.setId(uuidDefault);
                        allScenario.setTitle(All_SCENARIO);

                        listScenario.add(0, allScenario);
                        scenarioSpinnerAdapter = new ScenarioSpinnerAdapter(getContext(), listScenario);
                        scenarioSpinner.setAdapter(scenarioSpinnerAdapter);
                        scenarioSpinner.setVisibility(View.VISIBLE);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "An error occurred while retrieving data.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onResponse: " + e);
                }
            }

            @Override
            public void onFailure(Call<ResponsePaging<ArrayList<Scenario>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "An error occurred while retrieving data.", Toast.LENGTH_SHORT).show();

            }
        });

    }

    // Phan Quang Huy
    // Search session log
    public void searchSessionLog(String sortBy) {
        Scenario scenario = null;
        if (scenarioSpinner != null) {
            scenario = (Scenario) scenarioSpinner.getSelectedItem();
        }
        String search = "";
        if (searchViewInToolBar != null) {
            search = searchViewInToolBar.getQuery().toString();
        }

        if (scenario.getTitle().equals(All_SCENARIO)) {
            GetAllLogSessionOfAllScenario(search, sortBy);
        } else {
            GetAllLogSessionOfScenario(scenario.getId().toString().toUpperCase(), search, sortBy);
        }
    }


    // Phan Quang Huy
    // Get all log session of a scenario
    public void GetAllLogSessionOfScenario(String scenarioId, String search, String sortBy) {
        progressBar.setVisibility(View.VISIBLE);
        service.getLogSessionsByScenarioIdAndDate(scenarioId, null, null, search, sortBy, new LogsFirebaseService.LogCallback<List<LogSession>>() {
            @Override
            public void onCallback(List<LogSession> data) {
                List<LogSession> result = new ArrayList<>();
                if (data != null && data.size() > 0) {
                    result = data;
                    hideNotifyEmpty();
                    sessionAdapter = new LogSessionAdapter(getContext(), result, LogFragment.this);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    logSessionRecyclerView.setLayoutManager(layoutManager);
                    logSessionRecyclerView.setAdapter(sessionAdapter);
                } else if (data.size() == 0) {
                    showNotifyEmpty();
                } else {
                    showNotifyEmpty();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Phan Quang Huy
    // Get all log session of all scenario
    public void GetAllLogSessionOfAllScenario(String search, String sortBy) {

        progressBar.setVisibility(View.VISIBLE);

        Call<ResponsePaging<ArrayList<Scenario>>> callAllScenario = apiScenario.getAllScenaroOfUserClerk(userIdClerk, "", 0, Integer.MAX_VALUE);
        callAllScenario.enqueue(new Callback<ResponsePaging<ArrayList<Scenario>>>() {
            @Override
            public void onResponse(Call<ResponsePaging<ArrayList<Scenario>>> call, Response<ResponsePaging<ArrayList<Scenario>>> response) {
                if (response.isSuccessful()) {
                    ResponsePaging<ArrayList<Scenario>> responsePaging = response.body();
                    ArrayList<String> listScenarioid = responsePaging.getData().stream()
                            .map(scenario -> scenario.getId().toString().toUpperCase()).collect(Collectors.toCollection(ArrayList::new));
                    if (listScenarioid.size() == 0) {
                        showNotifyEmpty();
                        return;
                    }
                    service.getLogSessionsByListScenarioIdAndDate(listScenarioid, null, null, search, sortBy, new LogsFirebaseService.LogCallback<List<LogSession>>() {
                        @Override
                        public void onCallback(List<LogSession> data) {
                            List<LogSession> result = new ArrayList<>();
                            if (data != null && data.size() > 0) {
                                result = data;
                                hideNotifyEmpty();
                                sessionAdapter = new LogSessionAdapter(getContext(), result, LogFragment.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                logSessionRecyclerView.setLayoutManager(layoutManager);
                                logSessionRecyclerView.setAdapter(sessionAdapter);
                            } else if (data.size() == 0) {
                                showNotifyEmpty();
                            } else {
                                hideNotifyEmpty();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "An error occurred while retrieving data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsePaging<ArrayList<Scenario>>> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred while retrieving data.", Toast.LENGTH_SHORT).show();

            }
        });
        progressBar.setVisibility(View.GONE);
    }

    private void hideNotifyEmpty() {
        notifyTextView.setVisibility(View.GONE);
        empty_box.setVisibility(View.GONE);
    }

    private void showNotifyEmpty() {
        notifyTextView.setVisibility(View.VISIBLE);
        empty_box.setVisibility(View.VISIBLE);
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
        Intent intent = new Intent(getContext(), LogsOfSessionActivity.class);
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
            LogSession logSession = sessionAdapter.getItem(position);
            if (logSession == null) {
                Toast.makeText(getContext(), "Can't find the right", Toast.LENGTH_SHORT).show();
            }
            String logSessionId = logSession.getSession_id();
            service.deleteLogSession(logSessionId, new LogsFirebaseService.LogCallback<Boolean>() {

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