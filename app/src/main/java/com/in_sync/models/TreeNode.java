package com.in_sync.models;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T extends Action> {
    public T action;  // The action associated with this node
    private List<TreeNode<T>> children;  // List of child nodes

    // Constructor
    public TreeNode(T action) {
        this.action = action;
        this.children = new ArrayList<>();
    }

    // Getter for action
    public T getAction() {
        return action;
    }

    // Setter for action
    public void setAction(T action) {
        this.action = action;
    }

    // Getter for children
    public List<TreeNode<T>> getChildren() {
        return children;
    }

    // Add a child node
    public void addChild(TreeNode<T> child) {
        children.add(child);
    }

    // Recursive method to find a node with the specified index
    public TreeNode<T> findNodeWithIndex(TreeNode<T> root, int targetIndex) {
        if (root == null) {
            return null;
        }

        // Check if the current node is the target node
        if (root.getAction().getIndex() == targetIndex) {
            return root;
        }

        // Recursively search the children
        for (TreeNode<T> child : root.getChildren()) {
            TreeNode<T> result = findNodeWithIndex(child, targetIndex);
            if (result != null) {
                return result;
            }
        }

        // Return null if the target node is not found
        return null;
    }
    public T findLargestIndex(TreeNode<T> node) {
        if (node == null) {
            return null;
        }

        T maxAction = node.getAction();

        for (TreeNode<T> child : node.getChildren()) {
            T childMaxAction = findLargestIndex(child);
            if (childMaxAction.getIndex() > maxAction.getIndex()) {
                maxAction = childMaxAction;
            }
        }

        return maxAction;
    }

}
