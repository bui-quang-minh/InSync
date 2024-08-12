package com.in_sync.daos;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.in_sync.models.LogSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import lombok.var;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FirebaseLogService {
    private static final String TAG = "FirebaseRealTimeService";
    private static final String LOGS_PATH = "logs";

    private static final String SCENARIOS_PATH = "scenarios";
    private static final String LOG_SESSIONS_PATH = "log_sessions";
    private static final String PATHBASE = "https://projectinsync-f627a-default-rtdb.firebaseio.com/";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private DatabaseReference databaseReference;

    public FirebaseLogService() {
        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(PATHBASE);
    }


    public void addLogSessionWithLogs(String scenarioId, LogSession logSession, List<com.in_sync.models.Log> logs, LogCallback<Boolean> callback) {

        // Tạo bản đồ để cập nhật các giá trị của log
        Map<String, Object> updates_logsession = new HashMap<>();
        Map<String, Object> updates_logs = new HashMap<>();
        // path of new session in firebase
        String logSessionPath = SCENARIOS_PATH + "/" + scenarioId + "/" + LOG_SESSIONS_PATH + "/" + logSession.getSession_id();

        // add logSession in to Map to update on firebase
        updates_logsession.put(logSessionPath, logSession);

        // add logs in to Map to update on firebase
        for (com.in_sync.models.Log log : logs) {
            String logPath = logSessionPath + "/" + LOGS_PATH + "/" + log.getLog_scenarios_id();
            updates_logs.put(logPath, log);
        }

        // Cập nhật tất cả các thay đổi vào Firebase trong một lần
        databaseReference.updateChildren(updates_logsession)
                .addOnSuccessListener(aVoid -> {
                    // Nếu cập nhật logSession thành công, cập nhật logs
                    databaseReference.updateChildren(updates_logs).addOnSuccessListener(aVoid1 -> {
                        callback.onCallback(true);
                        Log.d(TAG, "Log session and logs added successfully to Firebase.");
                    }).addOnFailureListener(e -> {
                        callback.onCallback(false);
                        Log.e(TAG, "Failed to add log  logs to Firebase", e);
                    });

                    Log.d(TAG, "Log session and logs added successfully to Firebase.");
                })
                .addOnFailureListener(e -> {
                    callback.onCallback(false);
                    Log.e(TAG, "Failed to add log session and logs to Firebase", e);
                });
    }

    //    public void getLogSessionsByScenarioIdOneTime(String scenarioId, LogCallback<List<LogSession>> callback) {
//        DatabaseReference logSessionsRef = databaseReference.child(SCENARIOS_PATH)
//                .child(scenarioId)
//                .child(LOG_SESSIONS_PATH);
//
//        logSessionsRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DataSnapshot dataSnapshot = task.getResult();
//                List<LogSession> logSessions = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    LogSession logSession = snapshot.getValue(LogSession.class);
//                    logSessions.add(logSession);
//                }
//                callback.onCallback(logSessions);
//                // Do something with the logSessions list
//                Log.d(TAG, "Log sessions retrieved successfully: " + logSessions.size());
//            } else {
//                callback.onCallback(null);
//                Log.e(TAG, "Failed to retrieve log sessions", task.getException());
//            }
//        });
//    }
    public void getLogSessionsByScenarioId(String scenarioId, LogCallback<List<LogSession>> callback) {
        DatabaseReference logSessionsRef = databaseReference.child(SCENARIOS_PATH)
                .child(scenarioId)
                .child(LOG_SESSIONS_PATH);
        logSessionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<LogSession> logSessions = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    LogSession logSession = snapshot1.getValue(LogSession.class);
                    logSessions.add(logSession);
                }
                Log.d(TAG, "Log sessions retrieved successfully: " + logSessions.size());
                callback.onCallback(logSessions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(null);
                Log.e(TAG, "Failed to retrieve log sessions" + error.getMessage());
            }
        });
    }

    public void getAllScenario(LogCallback<List<String>> callback) {
        DatabaseReference logSessionsRef = databaseReference.child(SCENARIOS_PATH);
        logSessionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> scenarioList = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String scenarioId = snapshot1.getKey();
                    scenarioList.add(scenarioId);
                }
                Log.d(TAG, "Log sessions retrieved successfully: " + scenarioList.size());
                callback.onCallback(scenarioList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(null);
                Log.e(TAG, "Failed to retrieve log sessions" + error.getMessage());
            }
        });
    }

    public void getLogSessionsByScenarioIdAndDate(String scenarioId, Date dateFrom, Date dateTo, LogCallback<List<LogSession>> callback) {
        DatabaseReference logSessionsRef = databaseReference.child(SCENARIOS_PATH)
                .child(scenarioId)
                .child(LOG_SESSIONS_PATH);


        // Sử dụng Calendar để thiết lập thời gian bắt đầu và kết thúc cho khoảng thời gian
        Calendar calFrom = GetCalender(0, 0, 0, 0, dateFrom);
        Calendar calTo = GetCalender(23, 59, 59, 999, dateTo);

        logSessionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<LogSession> logSessions = new ArrayList<>();

                for (DataSnapshot sessionSnapshot : snapshot.getChildren()) {
                    LogSession logSession = sessionSnapshot.getValue(LogSession.class);

                    if (logSession != null) {
                        // Chuyển đổi date_created từ String thành Date
                        Date sessionDate;
                        try {
                            sessionDate = DATE_TIME_FORMATTER.parse(logSession.getDate_created());
                        } catch (ParseException e) {
                            Log.e(TAG, "Failed to parse date", e);
                            continue;
                        }

                        // Kiểm tra xem ngày của phiên log có nằm trong khoảng thời gian không
                        boolean inRange = sessionDate != null && !sessionDate.before(calFrom.getTime()) && !sessionDate.after(calTo.getTime());

                        // Kiểm tra xem ngày của phiên log có nằm trong khoảng thời gian không
                        if (inRange) {
                            logSessions.add(logSession);
                        }
                    }
                }

                Log.d(TAG, "Log sessions retrieved successfully: " + logSessions.size());
                callback.onCallback(logSessions);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(null);
                Log.e(TAG, "Failed to retrieve log sessions: " + error.getMessage());
            }
        });
    }
    private Calendar GetCalender(int hour, int minute, int second, int millisecond, Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal;
    }



    public void getLogsByScenarioIdAndSessionId(String scenarioId, String sessionId, LogCallback<List<com.in_sync.models.Log>> callback) {

        DatabaseReference logsRef = databaseReference.child(SCENARIOS_PATH)
                .child(scenarioId)
                .child(LOG_SESSIONS_PATH)
                .child(sessionId)
                .child(LOGS_PATH);

        logsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<com.in_sync.models.Log> logs = new ArrayList<>();

                for (DataSnapshot logSnapshot : snapshot.getChildren()) {
                    com.in_sync.models.Log log = logSnapshot.getValue(com.in_sync.models.Log.class);
                    if (log != null) {
                        logs.add(log);
                    }
                }
                Log.d(TAG, "Logs retrieved successfully: " + logs.size());
                callback.onCallback(logs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(null);
                Log.e(TAG, "Failed to retrieve logs: " + error.getMessage());
            }
        });
    }

    public void updateLogSession(String scenarioId, String sessionId, LogSession updatedLogSession, LogCallback<Boolean> callback) {
        DatabaseReference sessionRef = databaseReference.child(SCENARIOS_PATH)
                .child(scenarioId)
                .child(LOG_SESSIONS_PATH)
                .child(sessionId);


        Map<String, Object> sessionUpdates = new HashMap<>();
        if (updatedLogSession.getSession_name() != null) {
            sessionUpdates.put("session_name", updatedLogSession.getSession_name());
        }
        if (updatedLogSession.getDevice_name() != null) {
            sessionUpdates.put("device_name", updatedLogSession.getDevice_name());
        }
        // Add other fields if necessary

        sessionRef.updateChildren(sessionUpdates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "LogSession updated successfully.");
                    callback.onCallback(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update LogSession", e);
                    callback.onCallback(false);
                });
    }

    public void deleteLogSession(String scenarioId, String sessionId, LogCallback<Boolean> callback) {
        DatabaseReference sessionRef = databaseReference
                .child(SCENARIOS_PATH)
                .child(scenarioId)
                .child(LOG_SESSIONS_PATH)
                .child(sessionId);


        sessionRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "LogSession deleted successfully.");
                    callback.onCallback(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete LogSession", e);
                    callback.onCallback(false);
                });
    }

    // action relative to log
    public void addLogToSession(String scenarioId, String sessionId, com.in_sync.models.Log log, LogCallback<Boolean> callback) {

        DatabaseReference logsRef = databaseReference
                .child(SCENARIOS_PATH)
                .child(scenarioId)
                .child(LOG_SESSIONS_PATH)
                .child(sessionId)
                .child(LOGS_PATH);

        String logId = log.getLog_scenarios_id();  // Assuming this is the unique ID of the log
        logsRef.child(logId).setValue(log)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Log added successfully to session.");
                    callback.onCallback(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add log to session", e);
                    callback.onCallback(false);
                });
    }

    public void addLogsToSession(String scenarioId, String sessionId, List<com.in_sync.models.Log> logs, LogCallback<Boolean> callback) {

        Map<String, Object> updates_log = new HashMap<>();
        String logPath = SCENARIOS_PATH + "/" + scenarioId + "/" + LOG_SESSIONS_PATH + "/" + sessionId + "/" + LOGS_PATH;


        // add logs in to Map to update on firebase
        for (com.in_sync.models.Log log : logs) {
            String logPathDetail = logPath + "/" + log.getLog_scenarios_id();
            updates_log.put(logPathDetail, log);
        }
        databaseReference.updateChildren(updates_log)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Log session and logs added successfully to Firebase.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add log session and logs to Firebase", e);
                });
    }

    public void updateLogInSession(String scenarioId, String sessionId, String logId, com.in_sync.models.Log updatedLog, LogCallback<Boolean> callback) {

        DatabaseReference logRef = databaseReference.child(SCENARIOS_PATH)
                .child(scenarioId)
                .child(LOG_SESSIONS_PATH)
                .child(sessionId)
                .child(LOGS_PATH)
                .child(logId);

        Map<String, Object> logUpdates = new HashMap<>();
        if (updatedLog.getDescription() != null) {
            logUpdates.put("description", updatedLog.getDescription());
        }
        if (updatedLog.getNote() != null) {
            logUpdates.put("note", updatedLog.getNote());
        }


        logRef.updateChildren(logUpdates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Log updated successfully in session.");
                    callback.onCallback(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update log in session", e);
                    callback.onCallback(false);
                });
    }

    public void deleteLogFromLogSession(String scenarioId, String sessionId, String logId, LogCallback<Boolean> callback) {

        DatabaseReference logRef = databaseReference.child(SCENARIOS_PATH)
                .child(scenarioId)
                .child(LOG_SESSIONS_PATH)
                .child(sessionId)
                .child(LOGS_PATH)
                .child(logId);

        logRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Log deleted successfully from session.");
                    callback.onCallback(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete log from session", e);
                    callback.onCallback(false);
                });
    }

    public interface LogCallback<T> {
        void onCallback(T data);
    }

}
