package com.in_sync.api;

import com.in_sync.dtos.ProjectDtos;
import com.in_sync.models.Project;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIProject {
    @GET("/api/projects/project-user-clerk-is-publish/{userIdClerk}")
    Call<ResponsePaging<ArrayList<Project>>> getAllProjectsOfUser(@Path("userIdClerk")String userIdClerk, @Query("keySearch") String keySearch, @Header("api-key") String token);
    @GET("/api/projects/{id}")
    Call<Project> GetProject(@Path("id") UUID id, @Header("api-key") String token);
    @DELETE("/api/projects/{id}")
    Call<ResponseSuccess> DeleteProject(@Path("id") UUID id, @Header("api-key") String token);
    @POST("/api/projects/byuserclerk")
    Call<ResponseSuccess> AddProject(@Body ProjectDtos.AddProjectDto projectDto, @Header("api-key") String token);
    @PUT("/api/projects/{id}")
    Call<ResponseSuccess> UpdateProject(@Path("id") UUID id ,@Body ProjectDtos.UpdateProjectDto projectDto, @Header("api-key") String token);
}
