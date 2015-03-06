package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinaryTree;
import com.interview.basics.model.tree.BinaryTreeNode;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/22/14
 * Time: 3:20 PM
 *
 * Solution:
 *   The max distance have 2 cases:
 *    1. two leaf node
 *    2. one leaf node to its parent
 *   So for a parent node
 *      max_dis(parent) = max {max_dis(left), max_dis(right), height(left) + height(right)}
 *   The init: node == null || (node.left == 0 && node.right == 0), return 0;
 *
 *   P.S. the height of the node is the max {height(left), height(right)} + 1
 *        when node == null, the height = 0;
 */
public class C5_19_MaxDistance {

    public static int distance(BinaryTree tree){
        tree.reheight();
        return distance(tree.getRoot());
    }

    public static int distance(BinaryTreeNode node){
        if(node == null || node.left == null && node.right == null) return 0;
        int leftDistance = distance(node.left);
        int rightDistance = distance(node.right);
        int count = 0;
        if(node.left != null) count += node.left.height;
        if(node.right != null) count += node.right.height;
        return Math.max(Math.max(leftDistance, rightDistance), count);
    }
}
