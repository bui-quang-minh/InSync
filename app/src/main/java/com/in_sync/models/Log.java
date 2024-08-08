package com.in_sync.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
public class Log {
    private String log_scenarios_id = UUID.randomUUID().toString();
    private String scenario_id;
    private String date_created = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    private String description;
    private String note;
    private String device_name = "";


}

