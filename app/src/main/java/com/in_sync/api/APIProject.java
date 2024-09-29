package com.in_sync.api;

import com.in_sync.models.Project;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIProject {
    @GET("/api/projects/project-user-clerk-is-publish/{userIdClerk}")
    Call<ResponsePaging<ArrayList<Project>>> getAllProjectsOfUser(@Path("userIdClerk")String userIdClerk,@Query("keySearch") String keySearch);
    @GET("/api/projects/{id}")
    Call<Project> GetProject(@Path("id") UUID id);
}
