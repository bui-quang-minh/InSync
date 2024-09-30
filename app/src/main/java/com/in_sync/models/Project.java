package com.in_sync.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
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
public class Project {
    private UUID id;
    private String projectName;
    private String description;
    private UUID userId;
    private String displayName;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private Boolean isPublish;
}