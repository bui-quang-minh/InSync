package com.in_sync.fragments;

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
import android.widget.AdapterView;
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

import com.in_sync.LogsOfSessionActivity;
import com.in_sync.R;
import com.in_sync.adapters.LogSessionAdapter;
import com.in_sync.adapters.ScenarioSpinnerAdapter;
import com.in_sync.daos.FirebaseLogService;
import com.in_sync.models.LogSession;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LogFragment extends Fragment implements LogSessionAdapter.OnItemClickLogSessionListener {
    Toolbar toolbar;
    RecyclerView logSessionRecyclerView;
    Spinner scenarioSpinner;
    SearchView searchViewInToolBar;
    TextView notifyTextView;

    ScenarioSpinnerAdapter scenarioSpinnerAdapter;
    LogSessionAdapter sessionAdapter;
    FirebaseLogService service;
    private static final String TAG = "LogFragment";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        handlerEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        return view;
    }


    private void initView(View view) {
        service = new FirebaseLogService();
        toolbar = view.findViewById(R.id.toolbar_log);
        logSessionRecyclerView = view.findViewById(R.id.log_session_recycle);
        searchViewInToolBar = view.findViewById(R.id.search_view_key);
        scenarioSpinner = view.findViewById(R.id.scenario_sp);
        notifyTextView = view.findViewById(R.id.notify_no_session);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setTitle("Log Sesstion");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initDataForScenarioSpinner();
    }


    private void handlerEvent() {

        scenarioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchSessionLog();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("alsdfjalds", "Nothing");
            }
        });
        searchViewInToolBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchSessionLog();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchSessionLog();
                return true;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Inflate menu vào ActionBar
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Xử lý sự kiện khi người dùng chọn mục trong ActionBar
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initDataForScenarioSpinner() {
        service.getAllScenario(new FirebaseLogService.LogCallback<List<String>>() {
            @Override
            public void onCallback(List<String> data) {
                List<String> result = new ArrayList<>();
                if (data != null) {
                    result = data;
                    scenarioSpinnerAdapter = new ScenarioSpinnerAdapter(getContext(), result);
                    scenarioSpinner.setAdapter(scenarioSpinnerAdapter);
                }
            }
        });
    }

    public void searchSessionLog() {
        String scenarioId = (String) scenarioSpinner.getSelectedItem();
        String search = searchViewInToolBar.getQuery().toString();

        Log.e(TAG, scenarioId);
        service.getLogSessionsByScenarioIdAndDate(scenarioId, null, null, search, new FirebaseLogService.LogCallback<List<LogSession>>() {
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
                }else{
                    notifyTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    public void onViewClick(View view, int position) {
        String scenarioId = (String) scenarioSpinner.getSelectedItem();
        LogSession logSession = sessionAdapter.getItem(position);
        if (logSession == null) {
            Toast.makeText(getContext(), "Can't find the right session please try again", Toast.LENGTH_SHORT).show();
        }
        String logSessionId = logSession.getSession_id();
        Intent intent = new Intent(getContext(), LogsOfSessionActivity.class);
        intent.putExtra("scenarioId", scenarioId);
        intent.putExtra("logSessionId", logSessionId);
        startActivity(intent);

        Log.e(TAG, "CLick Detail");
    }

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
                        searchSessionLog();
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