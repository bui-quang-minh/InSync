package com.in_sync.models;

import android.util.Log;

import com.in_sync.actions.definition.ActionDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Sequence {
    private Queue<Action> actions;
    private List<Integer> allExecutedActions = new ArrayList<Integer>();
    public Sequence(Queue<Action>actions){
        this.actions = actions;
        Log.d("Sequence", "Sequence created");
        for (Action action : actions){
            Log.e("Sequence", "Actions: " + action.getActionType() + " on: " + action.getIndex());
        }
    }
    public Action traverseAction(boolean conditionResult, Action currentAction){
        switch(currentAction.getActionType()){
            case ActionDef.CLICK:
                if (conditionResult){
                    Log.e("Seq", "traverseAction: Click Poll" );
                    return actions.poll();
                }
                break;

            case ActionDef.DELAY:
                if (conditionResult){
                    Log.e("Seq", "traverseAction: Delay Poll" );
                    return actions.poll();
                }
                break;
            case ActionDef.SWIPE:
                if (conditionResult){
                    Log.e("Seq", "traverseAction: Swipe Poll" );
                    return actions.poll();
                }
                break;
            case ActionDef.ZOOM:
                if (conditionResult){
                    Log.e("Seq", "traverseAction: Zoom Poll" );
                    return actions.poll();
                }
            case ActionDef.OPEN_APP:
                if (conditionResult){
                    Log.e("Seq", "traverseAction: Open App Poll" );
                    return actions.poll();
                }
                break;
        }
        return currentAction;
    }

    public com.in_sync.models.Action peekQueue(){
        return actions.peek();
    }
    public Action getFirstAction() {
        return actions.poll();
    }
}
