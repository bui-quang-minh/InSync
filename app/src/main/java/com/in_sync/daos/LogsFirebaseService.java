package com.in_sync.daos;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.in_sync.models.LogSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LogsFirebaseService {
    private static final String TAG = "FirebaseRealTimeService";
    private static final String LOGS_PATH = "logs";
    private static final String SCENARIOS_PATH = "scenarios";
    private static final String LOG_SESSIONS_PATH = "log_sessions";
    private static final String PATHBASE = "https://projectinsync-f627a-default-rtdb.firebaseio.com/";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_2 = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final String SORT_A_Z = "Sort A-Z", SORT_Z_A = "Sort Z-A", SORT_BY_NEWEST = "Sort by Newest", SORT_BY_OLDEST = "Sort by Oldest";

    private DatabaseReference databaseReference;

    //Phan Quang Huy
    //Constructor initialization of Firebase Realtime Database
    public LogsFirebaseService() {
        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(PATHBASE);
    }

    //Phan Quang Huy
    //Add a logging session and list the logs of that session
    public void addLogSessionWithLogs(LogSession logSession, List<com.in_sync.models.Log> logs, LogsFirebaseService.LogCallback<Boolean> callback) {

        // Tạo bản đồ để cập nhật các giá trị của log
        Map<String, Object> updates_logsession = new HashMap<>();
        Map<String, Object> updates_logs = new HashMap<>();
        // path of new session in firebase
        String logSessionPath = LOG_SESSIONS_PATH + "/" + logSession.getSession_id();

        List<com.in_sync.models.Log> logFails = logs.stream().filter(l -> !l.isStatus())
                .collect(Collectors.toList());
        if (logFails.size() > 0) {
            logSession.setNeedResolve(true);
        }

        // add logSession in to Map to update on firebase
        updates_logsession.put(logSessionPath, logSession);

        // add logs in to Map to update on firebase
        for (com.in_sync.models.Log log : logs) {
            String logPath = LOGS_PATH + "/" + log.getLog_scenarios_id();
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

    //Phan Quang Huy
    //Get all log sessions of a specific scenario
    public void getLogSessionsByScenarioId(String scenarioId, String sortBy, LogsFirebaseService.LogCallback<List<LogSession>> callback) {
        DatabaseReference logSessionsRef = databaseReference
                .child(LOG_SESSIONS_PATH);
        databaseReference.orderByChild("scenario_id").equalTo(scenarioId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<LogSession> logSessions = new ArrayList<>();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            LogSession logSession = snapshot1.getValue(LogSession.class);
                            logSessions.add(logSession);
                        }
                        // Sắp xếp danh sách logSessions theo date_created
                        logSessions.sort((session1, session2) -> LogSessionSortWithOption(session1, session2, sortBy));

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

    //Phan Quang Huy
//Get a specific log session by its ID
    public void getLogSessionsById(String scenarioId, String sessionId, LogsFirebaseService.LogCallback<LogSession> callback) {
        DatabaseReference logSessionsRef = databaseReference
                .child(LOG_SESSIONS_PATH)
                .child(sessionId);
        logSessionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                LogSession logSession = snapshot.getValue(LogSession.class);
                if (logSession == null) {
                    callback.onCallback(null);
                    return;
                }
                Log.d(TAG, "Log sessions retrieved successfully: " + logSession.getSession_name());
                callback.onCallback(logSession);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(null);
                Log.e(TAG, "Failed to retrieve log sessions" + error.getMessage());
            }
        });
    }


    //Phan Quang Huy
//Get all scenario
    public void getAllScenario(LogsFirebaseService.LogCallback<List<String>> callback) {
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

    //Phan Quang Huy
//Get all log sessions of a specific scenario
    public void getLogSessionsByScenarioIdAndDate(String scenarioId, Date dateFrom, Date dateTo, String keySearch, String sortBy, LogsFirebaseService.LogCallback<List<LogSession>> callback) {
        DatabaseReference logSessionsRef = (DatabaseReference) databaseReference
                .child(LOG_SESSIONS_PATH);


        // Sử dụng Calendar để thiết lập thời gian bắt đầu và kết thúc cho khoảng thời gian
        Calendar calFrom = Calendar.getInstance(), calTo = Calendar.getInstance();
        if (dateFrom != null) {
            calFrom = GetCalender(0, 0, 0, 0, dateFrom);
        } else {
            calFrom = null;
        }
        if (dateTo != null) {
            calTo = GetCalender(23, 59, 59, 999, dateTo);
        } else {
            calTo = null;
        }


        Calendar finalCalFrom = calFrom;
        Calendar finalCalTo = calTo;

        logSessionsRef.orderByChild("scenario_id").equalTo(scenarioId)
                .addValueEventListener(new ValueEventListener() {
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
                                boolean inRange = sessionDate != null && (finalCalFrom == null || !sessionDate.before(finalCalFrom.getTime())) && (finalCalTo == null || !sessionDate.after(finalCalTo.getTime()));
                                boolean isContain = logSession.getSession_name().toLowerCase().contains(keySearch.toLowerCase()) || logSession.getDevice_name().toLowerCase().contains(keySearch.toLowerCase());
                                // Kiểm tra xem ngày của phiên log có nằm trong khoảng thời gian không
                                if (inRange && isContain) {
                                    logSessions.add(logSession);
                                }
                            }
                        }
                        logSessions.sort((session1, session2) -> LogSessionSortWithOption(session1, session2, sortBy));
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


    //Phan Quang Huy
//Get all logs of a specific log session
    public void getLogsByScenarioIdAndSessionId(String sessionId, String keySearch, LogsFirebaseService.LogCallback<List<com.in_sync.models.Log>> callback) {

        DatabaseReference logsRef = databaseReference
                .child(LOGS_PATH);

        logsRef.orderByChild("session_id").equalTo(sessionId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<com.in_sync.models.Log> logs = new ArrayList<>();

                        for (DataSnapshot logSnapshot : snapshot.getChildren()) {
                            com.in_sync.models.Log log = logSnapshot.getValue(com.in_sync.models.Log.class);
                            if (log != null && (keySearch == null || log.getDescription().toLowerCase().contains(keySearch.toLowerCase()) || log.getNote().toLowerCase().contains(keySearch.toLowerCase()))) {
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

    //Phan Quang Huy
//Update log session
    public void updateLogSession(String sessionId, LogSession updatedLogSession, LogsFirebaseService.LogCallback<Boolean> callback) {
        DatabaseReference sessionRef = databaseReference
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

    //Phan Quang Huy
//Delete log session
    public void deleteLogSession(String sessionId, LogsFirebaseService.LogCallback<Boolean> callback) {
        DatabaseReference sessionRef = databaseReference
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
//Phan Quang Huy
//Add log to session
    public void addLogToSession( String sessionId, com.in_sync.models.Log log, LogsFirebaseService.LogCallback<Boolean> callback) {

        DatabaseReference logsRef = databaseReference
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

    //Phan Quang Huy
//Add logs to session
    public void addLogsToSession( String sessionId, List<com.in_sync.models.Log> logs, LogsFirebaseService.LogCallback<Boolean> callback) {

        Map<String, Object> updates_log = new HashMap<>();
        String logPath = LOGS_PATH;


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

    //Phan Quang Huy
//Update log in session
    public void updateLogInSession(String logId, com.in_sync.models.Log updatedLog, LogsFirebaseService.LogCallback<Boolean> callback) {

        DatabaseReference logRef = databaseReference.child(SCENARIOS_PATH)
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

    //Phan Quang Huy
//Delete log from session
    public void deleteLogFromLogSession(String logId, LogsFirebaseService.LogCallback<Boolean> callback) {

        DatabaseReference logRef = databaseReference.child(SCENARIOS_PATH)
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



    private Calendar GetCalender(int hour, int minute, int second, int millisecond, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal;
    }

    public int LogSessionSortWithOption(LogSession object1, LogSession object2, String sortBy) {

        if (sortBy.equalsIgnoreCase(SORT_A_Z)) {
            return object1.getSession_name().compareTo(object2.getSession_name());
        } else if (sortBy.equalsIgnoreCase(SORT_Z_A)) {
            return object2.getSession_name().compareTo(object1.getSession_name());
        } else if (sortBy.equalsIgnoreCase(SORT_BY_NEWEST)) {
            try {
                LocalDateTime date1 = LocalDateTime.parse(object1.getDate_created(), DATE_TIME_FORMATTER_2);
                LocalDateTime date2 = LocalDateTime.parse(object2.getDate_created(), DATE_TIME_FORMATTER_2);
                return date2.compareTo(date1);
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse date_created", e);
                return 0;
            }
        } else if (sortBy.equalsIgnoreCase(SORT_BY_OLDEST)) {
            try {
                LocalDateTime date1 = LocalDateTime.parse(object1.getDate_created(), DATE_TIME_FORMATTER_2);
                LocalDateTime date2 = LocalDateTime.parse(object2.getDate_created(), DATE_TIME_FORMATTER_2);
                return date1.compareTo(date2);
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse date_created", e);
                return 0;
            }
        }
        return 0;
    }

    public interface LogCallback<T> {
        void onCallback(T data);
    }
}
