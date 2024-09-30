package com.in_sync.api;

import com.in_sync.models.Project;
import com.in_sync.models.Scenario;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIScenario {
    @GET("/api/scenarios/scenarios-project-useridclerk/{projectId}")
    Call<ResponsePaging<ArrayList<Scenario>>> getAllScenaroOfProject(@Path("projectId") UUID projectId, @Query("userIdClerk")String userIdClerk, @Query("keySearch") String keySearch, @Query("index") int index, @Query("size") int size);

    @GET("/api/scenarios/scenarios-user-clerk/{userIdClerk}")
    Call<ResponsePaging<ArrayList<Scenario>>> getAllScenaroOfUserClerk(@Path("userIdClerk") String userIdClerk, @Query("keySearch") String keySearch, @Query("index") int index, @Query("size") int size);

}
