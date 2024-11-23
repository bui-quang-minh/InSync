package com.in_sync.api;

import com.in_sync.dtos.AssetDtos;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIAssets {
    @POST("/api/assets/")
    Call<ResponseSuccess> AddAsset(@Body AssetDtos.AddAssetDto assetDto, @Header("api-key") String token);
}
