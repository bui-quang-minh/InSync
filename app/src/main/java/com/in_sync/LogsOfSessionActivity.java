package com.in_sync;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.in_sync.adapters.LogAdapter;
import com.in_sync.daos.FirebaseLogService;
import com.in_sync.models.LogSession;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LogsOfSessionActivity extends AppCompatActivity implements LogAdapter.OnItemClickLogListener {

    private Toolbar toolbar;
    private static final String TAG = "LogsOfSessionActivity";
    private SearchView searchView;
    private RecyclerView logsRecycleView;
    private LogAdapter logAdapter;
    private String scenarioId ,logSessionId;
    private FirebaseLogService service;


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
        service = new FirebaseLogService();
        scenarioId = getIntent().getStringExtra("scenarioId");
        logSessionId = getIntent().getStringExtra("logSessionId");
        SetTitleForToolbar();
        searchLog("");

        Log.d(TAG, "initData: "+scenarioId);
        Log.d(TAG, "initData: "+logSessionId);
    }

    public void searchLog(String keySearch){
        service.getLogsByScenarioIdAndSessionId(scenarioId, logSessionId,keySearch, new FirebaseLogService.LogCallback<List<com.in_sync.models.Log>>() {
            @Override
            public void onCallback(List<com.in_sync.models.Log> data) {
                List<com.in_sync.models.Log> result = new ArrayList<>();
                if(data != null){
                    result= data;
                    logAdapter = new LogAdapter(LogsOfSessionActivity.this,result, LogsOfSessionActivity.this);
                    GridLayoutManager layoutManager = new GridLayoutManager(LogsOfSessionActivity.this,1, RecyclerView.VERTICAL,false);
                    logsRecycleView.setLayoutManager(layoutManager);
                    logsRecycleView.setAdapter(logAdapter);
                }
            }
        });
    }
    public void SetTitleForToolbar(){
        service.getLogSessionsById(scenarioId, logSessionId, new FirebaseLogService.LogCallback<LogSession>() {
            @Override
            public void onCallback(LogSession data) {
                if(data == null){
                    finish();
                }else{
                    LogsOfSessionActivity.this.getSupportActionBar().setTitle("Log of Seession : " + data.getSession_name());
                    Log.d(TAG, "onCallback: "+data.getSession_name());
                }
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