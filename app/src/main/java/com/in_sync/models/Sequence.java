package com.in_sync.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Sequence {
    private List<Action> actions;
    private List<Action> flatenedActions;
    private TreeNode root;
    private List<Action> currentList = new ArrayList<Action>();
    private List<Integer> excecutedActions = new ArrayList<Integer>();
    private List<Integer> allExecutedActions = new ArrayList<Integer>();
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
                excecutedActions.add(currentAction.getIndex());
                allExecutedActions.add(currentAction.getIndex());
                if(conditionResult){
                    if (logResult){
                        Log.e("Sequence", "Action: " + currentAction.getActionType() + "| on: " + currentAction.getCondition() + " |is true, the current index: "+ index);
                    }
                    if (!currentList.isEmpty()){
                        for (Action action : currentList){
                            if (action == currentAction&&currentList.indexOf(action)!=currentList.size()-1){
                                return currentList.get(currentList.indexOf(action)+1);
                            }
                            //parent search
                            if (action == currentAction&&currentList.indexOf(action)==currentList.size()-1){
                                Action actionRoot = findParentNotInExecutedActions(action.getParent(), flatenedActions, excecutedActions);
                                TreeNode parentNode = root.findNodeWithIndex(root, currentAction.getIndex());
                                //return the children of parent node that not in executed actions
                                List<TreeNode> children = parentNode.getChildren();
                                currentList = new ArrayList<Action>();
                                if(!children.isEmpty()){
                                    return currentAction.getIsTrue().get(0);
                                }
                                for (TreeNode<Action> child : children){
                                    if (!excecutedActions.contains((child.getAction()).getIndex())&&!allExecutedActions.contains((child.getAction()).getIndex())&&!parentNode.getAction().getActionType().equals("IF")){
                                        return (Action)child.getAction();
                                    }
                                }
                            }
                        }
                    }else {
                        currentList = currentAction.getIsTrue();
                        return currentList.get(0);
                    }
                } else {
                    if (logResult){
                        Log.e("Sequence", "Action: " + currentAction.getActionType() + "| on: " + currentAction.getCondition() + " | is false, current index: "+ index);
                    }
                    if (!currentList.isEmpty()){
                        for (Action action : currentList){
                            if (action == currentAction&&currentList.indexOf(action)!=currentList.size()-1){
                                return currentList.get(currentList.indexOf(action)+1);
                            }
                            //parent search
                            if (action == currentAction&&currentList.indexOf(action)==currentList.size()-1){
                                Action actionRoot = findParentNotInExecutedActions(action.getParent(), flatenedActions, excecutedActions);
                                TreeNode parentNode = root.findNodeWithIndex(root, actionRoot.getIndex());
                                //return the children of parent node that not in executed actions
                                List<TreeNode> children = parentNode.getChildren();
                                currentList = new ArrayList<Action>();
                                if(!children.isEmpty()){
                                    return currentAction.getIsFalse().get(0);
                                }
                                for (TreeNode<Action> child : children){
                                    if (!excecutedActions.contains((child.getAction()).getIndex())&&!allExecutedActions.contains((child.getAction()).getIndex())&&!parentNode.getAction().getActionType().equals("IF")){
                                        return (Action)child.getAction();
                                    }
                                }
                            }
                        }
                    }else {
                        currentList = currentAction.getIsFalse();
                        return currentList.get(0);
                    }
                    return flatenedActions.get(index);
                }
            }
            if (actionType.equals("CLICK")){
                if (logResult){
                    if (!conditionResult){
                        Log.e("Sequence", "Action: " + currentAction.getActionType() + "| on: " + currentAction.getOn() + " index: "+ index);
                        return flatenedActions.get(index);
                    }
                    allExecutedActions.add(currentAction.getIndex());
                    //conditionResult is true
                    if (!currentList.isEmpty()){
                        for (Action action : currentList){
                            if (action == currentAction&&currentList.indexOf(action)!=currentList.size()-1){
                                return currentList.get(currentList.indexOf(action)+1);
                            }
                            //parent search
                            if (action == currentAction&&currentList.indexOf(action)==currentList.size()-1){
                                Action actionRoot = findParentNotInExecutedActions(action.getParent(), flatenedActions, excecutedActions);
                                TreeNode parentNode = root.findNodeWithIndex(root, actionRoot.getIndex());
                                //return the children of parent node that not in executed actions
                                List<TreeNode> children = parentNode.getChildren();
                                if(!children.isEmpty()){
                                    //may need to add smth
                                    currentList = new ArrayList<Action>();
                                }
                                for (TreeNode<Action> child : children){
                                    if (!excecutedActions.contains((child.getAction()).getIndex())&&!allExecutedActions.contains((child.getAction()).getIndex())&&!parentNode.getAction().getActionType().equals("IF")){
                                        currentList = new ArrayList<Action>();
                                        return (Action)child.getAction();
                                    }
                                }
                            }
                        }
                    }
                    Log.e("Sequence", "Action: " + currentAction.getActionType() + " on: " + currentAction.getOn() + " index: "+ index);
                    return flatenedActions.get(++index);
                }else{
                    if (!conditionResult){
                        return flatenedActions.get(index);
                    }
                    allExecutedActions.add(currentAction.getIndex());
                    Log.e("Sequence", "Action: " + currentAction.getActionType() + " on: " + currentAction.getOn() + " index: "+ index);
                    return flatenedActions.get(index);
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

    private Action findParentNotInExecutedActions(int parentIndex, List<Action> flattenedActions, List<Integer> executedActions) {
        Action parent = flattenedActions.get(parentIndex);
        if (!executedActions.contains(parent.getIndex())) {
            return parent;
        } else {
            return findParentNotInExecutedActions(parent.getParent(), flattenedActions, executedActions);
        }
    }
}
