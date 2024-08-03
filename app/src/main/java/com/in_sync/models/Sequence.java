package com.in_sync.models;

import android.util.Log;

import java.util.List;

public class Sequence {
    private List<Action> actions;
    private TreeNode root;
    public Sequence(List<Action> actions, TreeNode root){
        this.actions = actions;
        this.root = root;
        Log.d("Sequence", "Sequence created");
        for (Action action : actions){
            Log.e("Sequence", "Action: " + action.getActionType() + " on: " + action.getIndex());
        }
    }
    private Action traverseAction(boolean conditionResult, Action currentAction){
        int index = currentAction.getIndex();
        boolean logResult = currentAction.isLogResult();
        if(conditionResult){
            if (logResult){
                Log.e("Sequence", "Action: " + currentAction.getActionType() + " on: " + currentAction.getCondition() + " is true");
            }
            return actions.get(index++);
        } else {
            if (logResult){
                Log.e("Sequence", "Action: " + currentAction.getActionType() + " on: " + currentAction.getCondition() + " is false");
            }
            return actions.get(index);
        }
    };
}
