package com.in_sync.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AssetDtos {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class AddAssetDto{
        private String projectId;
        private String assetName;
        private String type;
        private String filePath;
    }
}
