package com.in_sync.models;

import android.util.Log;

import com.in_sync.actions.definition.ActionDef;

import java.util.ArrayList;
import java.util.List;

public class Sequence {
    private List<Action> actions;
    private List<Integer> allExecutedActions = new ArrayList<Integer>();
    public Sequence(List<Action>actions){
        this.actions = actions;
        Log.d("Sequence", "Sequence created");
        for (Action action : actions){
            Log.e("Sequence", "Actions: " + action.getActionType() + " on: " + action.getIndex());
        }
    }
    public Action traverseAction(boolean conditionResult, Action currentAction){
        int index = currentAction.getIndex();
        String actionType = currentAction.getActionType();
        if (currentAction.getIndex() == actions.get(actions.size()-1).getIndex() && conditionResult){
            Log.e("Sequence", "All actions have been executed");
            currentAction.setIndex(-1);
            return currentAction;
        }
        switch(actionType){
            case ActionDef.CLICK:
                if (conditionResult){
                    allExecutedActions.add(currentAction.getIndex());
                    return actions.get(index + 1);
                }
                break;

            case ActionDef.DELAY:
                if (conditionResult) {
                    allExecutedActions.add(currentAction.getIndex());
                    return actions.get(index + 1);

                }
                break;
        }
        return currentAction;
    }

    public com.in_sync.models.Action getActionByIndex(int index){
        return actions.get(index);
    }
    public Action getFirstAction() {
        return actions.get(0);
    }
}
