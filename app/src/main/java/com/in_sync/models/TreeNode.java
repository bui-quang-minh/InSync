package com.in_sync.models;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
    public Action action;  // The action associated with this node
    private List<TreeNode> children;  // List of child nodes

    // Constructor
    public TreeNode(Action action) {
        this.action = action;
        this.children = new ArrayList<>();
    }

    // Getter for action
    public Action getAction() {
        return action;
    }

    // Setter for action
    public void setAction(Action action) {
        this.action = action;
    }

    // Getter for children
    public List<TreeNode> getChildren() {
        return children;
    }

    // Add a child node
    public void addChild(TreeNode child) {
        children.add(child);
    }

}