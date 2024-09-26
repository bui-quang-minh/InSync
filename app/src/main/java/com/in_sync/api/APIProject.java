package com.in_sync.api;

import com.in_sync.models.Project;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;

public interface APIProject {
    @GET("api/projects")
    Call<ArrayList<Project>> getAllProjects();
}
