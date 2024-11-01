package com.in_sync.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.in_sync.actions.definition.ActionDef;
import com.in_sync.daos.LogsFirebaseService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

public class Sequence {
    private Queue<Action> actions;
    private List<Integer> allExecutedActions = new ArrayList<Integer>();
    SharedPreferences sharedPreferences;
    LogsFirebaseService logsFirebaseService;

    // Check if "scenarioId" exists
    String scenarioId;
    String sessionId;

    public Sequence(Queue<Action> actions, Context context) {
        this.actions = actions;
        Log.d("Sequence", "Sequence created");
        for (Action action : actions) {
            Log.e("Sequence", "Actions: " + action.getActionType() + " on: " + action.getIndex());
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            logsFirebaseService = new LogsFirebaseService();
        }
        sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        scenarioId = sharedPreferences.getString("scenarioId", null);
        String date = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss ", Locale.getDefault()).format(new Date());
        String deviceName = Build.MANUFACTURER + " " + Build.MODEL;
        if (scenarioId != null) {
            //Log to firebase
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LogSession logSession = new LogSession("Run "+ date, deviceName, scenarioId);
                logsFirebaseService.initializeLogSession(logSession,new LogsFirebaseService.LogCallback<LogSession>() {
                    @Override
                    public void onCallback(LogSession session) {
                        if (session != null) {
                            // LogSession initialized successfully
                            Log.d("TAG", "LogSession initialized with ID: " + session.getSession_id());
                            // Do further processing with the initialized session
                            sessionId = session.getSession_id();
                        } else {
                            // Initialization failed
                            Log.e("TAG", "Failed to initialize LogSession.");
                        }
                    }
                });
                Log.e("Sequence", "LogSession: " + logSession.getSession_id() + " " + logSession.getSession_name() + " " + logSession.getDevice_name() + " " + logSession.getScenario_id() + " " + logSession.getDate_created());
            }
        }else{
            //Log to local
        }
    }

    public Action traverseAction(boolean conditionResult, Action currentAction) {
        switch (currentAction.getActionType()) {
            case ActionDef.CLICK:
                if (conditionResult) {
                    Logging(currentAction);
                    Log.e("Seq", "traverseAction: Click Poll");
                    return actions.poll();
                }
                break;

            case ActionDef.DELAY:
                if (conditionResult) {
                    Logging(currentAction);
                    Log.e("Seq", "traverseAction: Delay Poll");
                    return actions.poll();
                }
                break;
            case ActionDef.SWIPE:
                if (conditionResult) {
                    Logging(currentAction);
                    Log.e("Seq", "traverseAction: Swipe Poll");
                    return actions.poll();
                }
                break;
            case ActionDef.ZOOM:
                if (conditionResult) {
                    Logging(currentAction);
                    Log.e("Seq", "traverseAction: Zoom Poll");
                    return actions.poll();
                }
            case ActionDef.OPEN_APP:
                if (conditionResult) {
                    Logging(currentAction);
                    Log.e("Seq", "traverseAction: Open App Poll");
                    return actions.poll();
                }
                break;
            case ActionDef.ROTATE:
                if (conditionResult) {
                    Logging(currentAction);
                    Log.e("Seq", "traverseAction: Rotate App Poll");
                    return actions.poll();
                }
                break;
            case ActionDef.PASTE:
                if (conditionResult) {
                    Logging(currentAction);
                    Log.e("Seq", "traverseAction: Paste Poll");
                    return actions.poll();
                }
                break;
        }
        return currentAction;
    }

    public com.in_sync.models.Action peekQueue() {
        return actions.peek();
    }

    public Action getFirstAction() {
        return actions.poll();
    }

    public void clearActions() {
        actions.clear();
    }

    public void Logging(Action currentAction) {
        if(currentAction.isLog()){
            if (scenarioId != null) {
                //Log to firebase
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    com.in_sync.models.Log log = new com.in_sync.models.Log(sessionId, "Action "+ currentAction.getActionType() ,currentAction.getLogContent());
                    logsFirebaseService.uploadLogEntry(sessionId, log, new LogsFirebaseService.LogCallback<com.in_sync.models.Log>() {
                        @Override
                        public void onCallback(com.in_sync.models.Log log) {
                            if (log != null) {
                                // Log entry uploaded successfully
                                Log.d("TAG", "Log entry uploaded with ID: " + log.getLog_scenarios_id());
                                // Do further processing with the uploaded log entry
                            } else {
                                // Upload failed
                                Log.e("TAG", "Failed to upload log entry.");
                            }
                        }
                    });
                }

            }else{
                //Log to local
            }
        }
    }
}
