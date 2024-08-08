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
        if(index == 5){
            Log.e("Sequence", "Index is 5");
        }
        String actionType = currentAction.getActionType();
        boolean logResult = currentAction.isLogResult();
        if (currentAction == flatenedActions.get(flatenedActions.size()-1) && conditionResult){
            Log.e("Sequence", "All actions have been executed");
            currentAction.setIndex(-1);
            return currentAction;
        }
        if(index == 0){
            //push action to 1
            Log.e("Sequence", "Action index is 0, pushing to 1 " + flatenedActions.get(index+1).getActionType());
            List<TreeNode> treeChildren = root.findNodeWithIndex(root, 0).getChildren();
            for (TreeNode<Action> child : treeChildren){
                currentList.add((Action)child.getAction());
            }
            return flatenedActions.get(++index);
        }
        if (currentList.size() == 1){
            switch(actionType){
                case "CLICK":
                    allExecutedActions.add(currentAction.getIndex());
                    if (conditionResult){
                        Log.e("Sequence", "Last action in list, popping to parent");
                        currentList = new ArrayList<Action>();
                        Action parrentAction = findParentNotInExecutedActions(currentAction.getParent(), flatenedActions, excecutedActions);
                        TreeNode parrentTreeNode = root.findNodeWithIndex(root, parrentAction.getIndex());
                        List<TreeNode> treeChildren = parrentTreeNode.getChildren();
                        for (TreeNode<Action> child : treeChildren){
                            currentList.add((Action)child.getAction());
                        }
                        for (Action action : currentList){
                            if (allExecutedActions.contains(action.getIndex())){
                                continue;
                            } else {
                                currentList = new ArrayList<Action>();
                                currentList.add(action);
                                return action;
                            }
                        }
                    } else {
                        Log.e("Sequence", "Condition not met, skipping: " + currentAction.getIndex());
                    }
                    break;
                case "IF":
                    excecutedActions.add(currentAction.getIndex());
                    allExecutedActions.add(currentAction.getIndex());
                    if (conditionResult){
                        Log.e("Sequence", "Condition met, pushing to: " + currentAction.getIsTrue());
                        currentList = currentAction.getIsTrue();
                        return currentAction.getIsTrue().get(0);
                    } else {
                        Log.e("Sequence", "Condition not met, pushing to: " + currentAction.getIsFalse());
                        Log.e("Sequence", "Condition met, pushing to: " + currentAction.getIsFalse());
                        currentList = currentAction.getIsFalse();
                        return currentAction.getIsFalse().get(0);
                    }
            }
        }
        if (currentList.size() > 1){
            switch(actionType){
                case "CLICK":
                    allExecutedActions.add(currentAction.getIndex());
                    if (conditionResult){
                        Log.e("Sequence", "Clicking on: " + currentAction.getIndex());

                        if(currentList.get(currentList.size()-1).getIndex() == currentAction.getIndex()){
                            Log.e("Sequence", "Last action in list, popping to parent");
                            currentList = new ArrayList<Action>();
                            Action parrentAction = findParentNotInExecutedActions(currentAction.getParent(), flatenedActions, excecutedActions);
                            TreeNode parrentTreeNode = root.findNodeWithIndex(root, parrentAction.getIndex());
                            List<TreeNode> treeChildren = parrentTreeNode.getChildren();
                            for (TreeNode<Action> child : treeChildren){
                                currentList.add((Action)child.getAction());
                            }
                            for (Action action : currentList){
                                if (allExecutedActions.contains(action.getIndex())){
                                    continue;
                                } else {
                                    currentList = new ArrayList<Action>();
                                    currentList.add(action);
                                    return action;
                                }
                            }

                        }
                        for (Action action : currentList){
                            if(action.getIndex() == currentAction.getIndex()){
                                return flatenedActions.get(index+1);
                            }
                        }
                    } else {
                        Log.e("Sequence", "Condition not met, skipping: " + currentAction.getIndex());
                    }
                    break;
                case "IF":
                    excecutedActions.add(currentAction.getIndex());
                    allExecutedActions.add(currentAction.getIndex());
                    if (conditionResult){
                        Log.e("Sequence", "Condition met, pushing to: " + currentAction.getIsTrue());

                        if (currentList.isEmpty()){
                            Action parrentAction = findParentNotInExecutedActions(currentAction.getParent(), flatenedActions, allExecutedActions);
                            TreeNode parrentTreeNode = root.findNodeWithIndex(root, parrentAction.getIndex());
                            List<TreeNode> treeChildren = parrentTreeNode.getChildren();
                            for (TreeNode<Action> child : treeChildren){
                                currentList.add((Action)child.getAction());
                            }
                            for (Action action : currentList){
                                if (allExecutedActions.contains(action.getIndex())){
                                    continue;
                                } else {
                                    currentList = new ArrayList<Action>();
                                    currentList.add(action);
                                    return action;
                                }
                            }
                        }
                        currentList = currentAction.getIsTrue();
                        return currentAction.getIsTrue().get(0);
                    } else {
                        Log.e("Sequence", "Condition met, pushing to: " + currentAction.getIsTrue());

                        if (currentList.isEmpty()){
                            Action parrentAction = findParentNotInExecutedActions(currentAction.getParent(), flatenedActions, allExecutedActions);
                            TreeNode parrentTreeNode = root.findNodeWithIndex(root, parrentAction.getIndex());
                            List<TreeNode> treeChildren = parrentTreeNode.getChildren();
                            for (TreeNode<Action> child : treeChildren){
                                currentList.add((Action)child.getAction());
                            }
                            for (Action action : currentList){
                                if (allExecutedActions.contains(action.getIndex())){
                                    continue;
                                } else {
                                    currentList = new ArrayList<Action>();
                                    currentList.add(action);
                                    return action;
                                }
                            }
                        }
                        currentList = currentAction.getIsFalse();
                        return currentAction.getIsFalse().get(0);
                    }
            }
        }
        if (currentList.isEmpty()){
            Log.e("Sequence", "Current list is empty");
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
