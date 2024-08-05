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

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FirebaseLogService {
    private static final String TAG = "FirebaseRealTimeService";
    private static final String PATH = "logs";
    private static final String PATHBASE = "https://projectinsync-f627a-default-rtdb.firebaseio.com/";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private DatabaseReference databaseReference;

    public FirebaseLogService() {
        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(PATHBASE);
    }

    public void addLog(String scenarioId,String description, String note) {
        com.in_sync.models.Log data = new com.in_sync.models.Log();
        data.setScenarioId(scenarioId);
        if (description != null) {
            data.setDescription(description);
        }
        if (note != null) {
            data.setNote(note);
        }
        databaseReference.child(PATH).child(data.getLogScenariosId().toString()).setValue(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Log added successfully into"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add log into ", e));
    }

    public void getAllLogs(DataCallback<List<com.in_sync.models.Log>> callback) {
        databaseReference.child(PATH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<com.in_sync.models.Log> logList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    com.in_sync.models.Log log = snapshot.getValue(com.in_sync.models.Log.class);
                    logList.add(log);
                }
                callback.onSuccess(logList);
                Log.e(TAG, "Success to fetch logs");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
                Log.e(TAG, "Failed to fetch logs", databaseError.toException());
            }
        });
    }
    public void getAllLogOfScenarioByDate(String scenarioId, Date date, DataCallback<List<com.in_sync.models.Log>> callback) {
        String dateFormat = DATE_FORMAT.format(date);
        databaseReference.child(PATH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<com.in_sync.models.Log> logList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    com.in_sync.models.Log log = snapshot.getValue(com.in_sync.models.Log.class);
                    String dateLog = log.getDateCreated().substring(0, 10);
                    if(log.getScenarioId().equals(scenarioId)
                            && dateLog.equals(dateFormat)){
                        logList.add(log);
                    }
                }
                callback.onSuccess(logList);
                Log.e(TAG, "Success to fetch logs");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
                Log.e(TAG, "Failed to fetch logs", databaseError.toException());
            }
        });
    }
    public void getLogById(String logId, DataCallback<com.in_sync.models.Log> callback) {
        databaseReference.child(PATH).child(logId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                com.in_sync.models.Log log = snapshot.getValue(com.in_sync.models.Log.class);
                if (log != null) {
                    callback.onSuccess(log);
                    Log.e(TAG, "Success to get logs by id" + logId);
                } else {
                    callback.onFailure(new Exception("Log not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
                Log.e(TAG, "Failed to fetch logs with id " + logId, error.toException());
            }
        });


    }

    public void deleteLogEntry(String logId, DataCallback<com.in_sync.models.Log> callback) {
        DatabaseReference logRef = databaseReference.child(PATH).child(logId);

        logRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(null);
                    Log.d(TAG, "Log entry deleted successfully");
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                    Log.e(TAG, "Failed to delete log entry", e);
                });
    }

    public void updateLog(String logId, String description, String note) {
        DatabaseReference logRef = databaseReference.child("logs").child(logId);
        com.in_sync.models.Log log = null;
        logRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                com.in_sync.models.Log logFind = dataSnapshot.getValue(com.in_sync.models.Log.class);
                if (logFind != null) {
                    Map<String, Object> updates = new HashMap<>();
                    if (description != null) {
                        updates.put("description", description);
                    }
                    if (note != null) {
                        updates.put("note", note);

                    }
                    logRef.updateChildren(updates)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Log entry updated successfully");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to update log entry: " + e.getMessage());
                            });
                } else {
                    Log.e(TAG, "Not found log with id " + logId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to get log entry: " + databaseError.getMessage());
            }
        });
    }

    public interface DataCallback<T> {
        void onSuccess(T data);

        void onFailure(Exception e);
    }
}
