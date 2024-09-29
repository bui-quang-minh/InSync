package com.in_sync.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.in_sync.R;
import com.in_sync.adapters.ProjectAdapter;
import com.in_sync.adapters.ScenarioAdapter;
import com.in_sync.api.APIProject;
import com.in_sync.api.APIScenario;
import com.in_sync.api.ResponsePaging;
import com.in_sync.common.ApiClient;
import com.in_sync.daos.LogsFirebaseService;
import com.in_sync.fragments.ProjectFragment;
import com.in_sync.models.Project;
import com.in_sync.models.Scenario;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ScenariosActivity extends AppCompatActivity implements ScenarioAdapter.OnItemClickScenarioListener{

    private static final String[] sortOptions = {LogsFirebaseService.SORT_A_Z, LogsFirebaseService.SORT_Z_A, LogsFirebaseService.SORT_BY_NEWEST, LogsFirebaseService.SORT_BY_OLDEST};
    Toolbar toolbar;
    ProgressBar progressBar;
    RecyclerView scenarioRecyclerView;
    SearchView searchViewInToolBar;
    ScenarioAdapter scenarioAdapter;
    TextView notifyTextView;
    TextView projectNameTextView;
    APIScenario apiScenario;
    APIProject apiProject;
    UUID projectUUID;
    String projectName;

    View overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scenarios);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        String projectId = intent.getStringExtra("projectId");
        projectName = intent.getStringExtra("projectName");
        if(projectId == null){
            finish();
        }
        projectUUID = UUID.fromString(projectId);
        initView();
        initData();
        handlerEvent();
    }

    private void initData() {
        projectNameTextView.setText(projectName);
        searchScenario(sortOptions[2]);
    }

    private void handlerEvent() {


    }


    private void initView() {
        overlay = findViewById(R.id.overlay_scenario);
        toolbar = findViewById(R.id.toolbar_scenario);
        progressBar = findViewById(R.id.progress_bar_scenario);
        scenarioRecyclerView = findViewById(R.id.scenario_recycle);
        notifyTextView = findViewById(R.id.notify_no_scenario);
        projectNameTextView = findViewById(R.id.project_name_of_scenario);
        // api
        apiScenario = ApiClient.getRetrofitInstance().create(APIScenario.class);
        apiProject = ApiClient.getRetrofitInstance().create(APIProject.class);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate menu vào ActionBar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scenario_menu, menu);

        // Lấy SearchView từ menu và thiết lập sự kiện tìm kiếm
        MenuItem searchItem = menu.findItem(R.id.action_search_scenario);
        searchViewInToolBar = (SearchView) searchItem.getActionView();
        searchViewInToolBar.setQueryHint("Enter key word to search scenarios...");
        searchViewInToolBar.setIconified(false);
        searchViewInToolBar.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(searchViewInToolBar.findFocus(), InputMethodManager.SHOW_IMPLICIT);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Expand the search view to take full width
                searchViewInToolBar.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchViewInToolBar, InputMethodManager.SHOW_IMPLICIT);
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
                searchScenario(sortOptions[2]);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchScenario(sortOptions[2]);
                return true;
            }
        });


//        searchViewInToolBar.setOnCloseListener(() -> {
//            // Hide keyboard when SearchView is closed
//            imm.hideSoftInputFromWindow(searchViewInToolBar.getWindowToken(), 0);
//            return false; // Return false to allow default behavior (collapse the SearchView)
//        });

        return true;
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
        if (item.getItemId() == R.id.action_sort_scenario) {
            showSortOptionsDialog();
            return true;
        } else if (item.getItemId() == R.id.action_add_scenario) {
            Toast.makeText(ScenariosActivity.this, "Add project", Toast.LENGTH_SHORT).show();
            return true;
        }else if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showSortOptionsDialog() {
        // Các tùy chọn sắp xếp
        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ScenariosActivity.this);
        builder.setTitle("Choose Sort Option")
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý khi người dùng chọn một tùy chọn
                        switch (which) {
                            case 0:
                                searchScenario(sortOptions[0]);
                                break;
                            case 1:
                                searchScenario(sortOptions[1]);
                                break;
                            case 2:
                                searchScenario(sortOptions[2]);
                                break;
                            case 3:
                                searchScenario(sortOptions[3]);
                                break;
                        }
                    }

                });
        // Hiển thị dialog
        builder.show();
    }
    private void searchScenario(String sortOption) {
        String keySearch = "";
        if (searchViewInToolBar != null) {
            keySearch = searchViewInToolBar.getQuery().toString();
        }
        UUID projectId = UUID.fromString("F521DE50-6E6F-4EF8-99A6-20BB15F7AB8B");
        String userIdClerk = "flasdflx.jlfasdfal_sdlfajs";
        progressBar.setVisibility(View.VISIBLE);
        Call<ResponsePaging<ArrayList<Scenario>>> call = apiScenario.getAllScenaroOfProject(projectId, userIdClerk, keySearch, 0, Integer.MAX_VALUE);
        call.enqueue(new Callback<ResponsePaging<ArrayList<Scenario>>>() {
            @Override
            public void onResponse(Call<ResponsePaging<ArrayList<Scenario>>> call, Response<ResponsePaging<ArrayList<Scenario>>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    ResponsePaging<ArrayList<Scenario>> responsePaging = response.body();
                    ArrayList<Scenario> scenarios = responsePaging.getData();

                    switch (sortOption) {
                        case LogsFirebaseService.SORT_A_Z:
                            scenarios.sort((o1, o2) -> o1.getTitle().compareTo(o2.getTitle()));
                            break;
                        case LogsFirebaseService.SORT_Z_A:
                            scenarios.sort((o1, o2) -> o2.getTitle().compareTo(o1.getTitle()));
                            break;
                        case LogsFirebaseService.SORT_BY_NEWEST:
                            scenarios.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
                            break;
                        case LogsFirebaseService.SORT_BY_OLDEST:
                            scenarios.sort((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()));
                            break;
                    }
                    scenarioAdapter = new ScenarioAdapter(ScenariosActivity.this, scenarios, ScenariosActivity.this);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ScenariosActivity.this, LinearLayoutManager.VERTICAL, false);
                    scenarioRecyclerView.setAdapter(scenarioAdapter);
                    scenarioRecyclerView.setLayoutManager(layoutManager);

                    if (scenarios == null || scenarios.isEmpty()) {
                        notifyTextView.setVisibility(View.VISIBLE);
                        return;
                    } else {
                        notifyTextView.setVisibility(View.GONE);
                        return;
                    }

                } else {
                    Toast.makeText(ScenariosActivity.this, "Error occurred during data retrieval", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponsePaging<ArrayList<Scenario>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(ScenariosActivity.this, "Error occurred during data retrieval", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onRunClick(View view, int position) {

    }

    @Override
    public void onDeleteClick(View view, int position) {

    }
}