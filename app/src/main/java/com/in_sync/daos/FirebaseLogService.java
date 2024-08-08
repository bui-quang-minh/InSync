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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.var;

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

    public void  monitorValueChange(){
        databaseReference.child(PATH).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded: " + snapshot.getValue());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildChanged: " + snapshot.getValue());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved: " + snapshot.getValue());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildMoved: " + snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }
    public void addLog(String scenarioId, String description, String note) {
        com.in_sync.models.Log data = new com.in_sync.models.Log();
        data.setScenario_id(scenarioId);
        if (description != null) {
            data.setDescription(description);
        }
        if (note != null) {
            data.setNote(note);
        }
        databaseReference.child(PATH).child(data.getLog_scenarios_id()).setValue(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Log added successfully into");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add log into ", e);
                });
    }


    public List<com.in_sync.models.Log> getAllLogs(LogCallback<List<com.in_sync.models.Log>> callback) {
        List<com.in_sync.models.Log> logList = new ArrayList<>();
        databaseReference.child(PATH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    com.in_sync.models.Log log = snapshot.getValue(com.in_sync.models.Log.class);
                    logList.add(log);
                }
                callback.onCallback(logList);
                Log.e(TAG, "Success to fetch logs");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch logs", databaseError.toException());
                callback.onCallback(null);
            }
        });
        return logList;
    }

    public List<com.in_sync.models.Log> getAllLogOfScenarioByDate(String scenarioId, Date date, LogCallback<List<com.in_sync.models.Log>> callback) {
        String temp = "";
        if (date != null) {
            temp = DATE_FORMAT.format(date);
        }
        String dateFormat = temp;
        List<com.in_sync.models.Log> logList = new ArrayList<>();
        databaseReference.child(PATH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    com.in_sync.models.Log log = snapshot.getValue(com.in_sync.models.Log.class);
                    String dateLog = log.getDate_created().substring(0, 10);
                    if (log.getScenario_id().equals(scenarioId)
                            && (dateFormat.equals("") || dateLog.equals(dateFormat))) {
                        logList.add(log);
                    }
                }
                // sắp xếp các log theo thứ tự thêm vào mới nhất
                logList.sort((o1, o2) -> {
                    LocalDateTime localDateTime1 = LocalDateTime.parse(o1.getDate_created(), DateTimeFormatter.ISO_DATE_TIME);
                    LocalDateTime localDateTime2 = LocalDateTime.parse(o2.getDate_created(), DateTimeFormatter.ISO_DATE_TIME);
                    return localDateTime2.compareTo(localDateTime1);
                });
                callback.onCallback(logList);
                Log.e(TAG, "Success to fetch logs");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCallback(null);
                Log.e(TAG, "Failed to fetch logs", databaseError.toException());
            }
        });
        return logList;
    }

    public com.in_sync.models.Log getLogById(String logId, LogCallback<com.in_sync.models.Log> callback) {

        databaseReference.child(PATH).child(logId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                com.in_sync.models.Log logFind = snapshot.getValue(com.in_sync.models.Log.class);
                if(logFind != null){
                    callback.onCallback(logFind);
                }else{
                    Log.e(TAG, "Not found log with id " + logId);
                    callback.onCallback(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(null);
                Log.e(TAG, "Failed to fetch logs with id " + logId, error.toException());
            }
        });

        return null;
    }

    public void deleteLogEntry(String logId) {
        DatabaseReference logRef = databaseReference.child(PATH).child(logId);

        logRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Log entry deleted successfully");
                })
                .addOnFailureListener(e -> {
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
    public interface LogCallback<T> {
        void onCallback(T data);
    }

}
