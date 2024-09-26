package com.in_sync.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.in_sync.R;
import com.in_sync.adapters.LogAdapter;
import com.in_sync.daos.LogsFirebaseService;
import com.in_sync.models.LogSession;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LogsOfSessionActivity extends AppCompatActivity implements LogAdapter.OnItemClickLogListener {

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView notify_no_log;
    private static final String TAG = "LogsOfSessionActivity";
    private SearchView searchView;
    private RecyclerView logsRecycleView;
    private LogAdapter logAdapter;
    private String logSessionId;
    private LogsFirebaseService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logs_of_session);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        initData();
        handlerEvent();
    }


    private void initData() {
        service = new LogsFirebaseService();
        logSessionId = getIntent().getStringExtra("logSessionId");
        SetTitleForToolbar();
        searchLog("");
        Log.d(TAG, "initData: "+logSessionId);
    }

    public void searchLog(String keySearch){
        notify_no_log.setVisibility(View.VISIBLE);
        service.getLogsByScenarioIdAndSessionId( logSessionId,keySearch, new LogsFirebaseService.LogCallback<List<com.in_sync.models.Log>>() {
            @Override
            public void onCallback(List<com.in_sync.models.Log> data) {
                List<com.in_sync.models.Log> result = new ArrayList<>();
                if(data != null){
                    result= data;
                    logAdapter = new LogAdapter(LogsOfSessionActivity.this,result, LogsOfSessionActivity.this);
                    GridLayoutManager layoutManager = new GridLayoutManager(LogsOfSessionActivity.this,1, RecyclerView.VERTICAL,false);
                    logsRecycleView.setLayoutManager(layoutManager);
                    logsRecycleView.setAdapter(logAdapter);
                }else{
                    notify_no_log.setVisibility(View.VISIBLE);
                    logsRecycleView.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    public void SetTitleForToolbar(){
        progressBar.setVisibility(View.VISIBLE);
        service.getLogSessionsById(logSessionId, new LogsFirebaseService.LogCallback<LogSession>() {
            @Override
            public void onCallback(LogSession data) {
                if(data == null){
                    finish();
                }else{
                    LogsOfSessionActivity.this.getSupportActionBar().setTitle("Session: " + data.getSession_name());
                    Log.d(TAG, "onCallback: "+data.getSession_name());
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void handlerEvent() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLog(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchLog(newText);
                return false;
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_log_session);
        searchView = findViewById(R.id.search_view);
        logsRecycleView = findViewById(R.id.log_session_recycle);
        notify_no_log = findViewById(R.id.notify_no_log);
        progressBar = findViewById(R.id.progress_bar_log);

        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Xử lý sự kiện khi người dùng chọn mục trong ActionBar
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}