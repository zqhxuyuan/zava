package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 上午11:11
 */
public class LOJ94_BinaryTreeInorderTraversal {
    //use Stack to push node.left;
    //the while loop condition: (root != null || !stack.isEmpty())
    //root = stack.pop(), nodes.add(root.val), root = root.right;
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> nodes = new ArrayList();
        Stack<TreeNode> stack = new Stack();
        while(root != null || !stack.isEmpty()){
            while(root != null){
                stack.push(root);
                root = root.left;
            }
            if(!stack.isEmpty()){
                root = stack.pop();
                nodes.add(root.val);
                root = root.right;
            }
        }
        return nodes;
    }
}
