package com.in_sync.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
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
public class Scenario {
    private UUID id;
    private UUID projectId;
    private String projectName;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String stepsWeb;
    private String stepsAndroid;
    private Boolean isFavorites;
    private String imageUrl;
    private UUID AuthorId;
    private UUID AuthorName;
}
