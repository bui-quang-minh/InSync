package com.in_sync.api;

import com.in_sync.models.Project;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIProject {
    @GET("api/projects/pagination")
    Call<ResponsePaging<ArrayList<Project>>> getAllProjects(@Query("keySearch") String keySearch);
}
