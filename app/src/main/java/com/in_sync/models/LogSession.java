package com.in_sync.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
public class LogSession {
    private String session_id = UUID.randomUUID().toString();
    private String session_name;
    private String device_name;
    private String scenario_id;
    private String date_created = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    public LogSession(String session_name, String device_name, String scenario_id) {
        this.session_name = session_name;
        this.device_name = device_name;
        this.scenario_id = scenario_id;
    }
}
