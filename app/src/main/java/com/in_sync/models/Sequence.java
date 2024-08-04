package com.in_sync.models;

import android.util.Log;

import java.util.List;

public class Sequence {
    private List<Action> actions;
    private List<Action> flatenedActions;
    private TreeNode root;
    public Sequence(List<Action>actions, List<Action> flatenedActions, TreeNode root){
        this.actions = actions;
        this.flatenedActions = flatenedActions;
        this.root = root;
        Log.d("Sequence", "Sequence created");
        for (Action action : flatenedActions){
            Log.e("Sequence", "flatenedActions: " + action.getActionType() + " on: " + action.getIndex());
        }
    }
    public Action traverseAction(boolean conditionResult, Action currentAction){
        int index = currentAction.getIndex();
        if(index == 0){
            //push action to 1
            Log.e("Sequence", "Action index is 0, pushing to 1 " + flatenedActions.get(index+1).getActionType());
            return flatenedActions.get(++index);
        }
        if (index < flatenedActions.size()){
            boolean logResult = currentAction.isLogResult();
            String actionType = currentAction.getActionType();
            if (actionType.equals("IF")){
                if(conditionResult){
                    if (logResult){
                        Log.e("Sequence", "Action: " + currentAction.getActionType() + " on: " + currentAction.getCondition() + " is true"+" index: "+ index);
                    }
                    return flatenedActions.get(++index);
                } else {
                    if (logResult){
                        Log.e("Sequence", "Action: " + currentAction.getActionType() + " on: " + currentAction.getCondition() + " is false"+" index: "+ index);
                    }
                    return flatenedActions.get(index);
                }
            }
            if (actionType.equals("CLICK")){
                if (logResult){
                    if (!conditionResult){
                        Log.e("Sequence", "Action: " + currentAction.getActionType() + " on: " + currentAction.getOn() + " index: "+ index);
                        return flatenedActions.get(index);
                    }
                    Log.e("Sequence", "Action: " + currentAction.getActionType() + " on: " + currentAction.getOn() + " index: "+ index);
                    return flatenedActions.get(++index);
                }else{
                    if (!conditionResult){
                        return flatenedActions.get(index);
                    }
                    return flatenedActions.get(++index);
                }
            }
        }else{
            Log.e("Sequence", "Action index is greater than flatened actions size");
            Action endAction = new Action();
            endAction.setIndex(-1);
            return endAction;
        }
        return currentAction;
    }
}
