package com.in_sync.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@RequiresApi(api = Build.VERSION_CODES.O)
public class Asset {
    private UUID id;
    private UUID projectId;
    private String assetName;
    private String type;
    private String filePath;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
}
