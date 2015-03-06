package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.TreeNode;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-12-28
 * Time: 下午1:37
 */
public class LOJ145_BinaryTreePostorderTraversal {
    //use Stack and HashSet(tracking if child of one node is already put in stack).
    //stack.peek() one node, if(!childrenVisited.contains(node))
    //      put node.right and node.left in stack,
    //      and put node in childrenVisited
    //  else visit this node.
    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> nodes = new ArrayList();
        Stack<TreeNode> stack = new Stack();
        Set<TreeNode> childrenVisited = new HashSet();
        if(root != null) stack.push(root);
        while(!stack.isEmpty()){
            TreeNode node = stack.peek();
            if(!childrenVisited.contains(node)){
                if(node.right != null) stack.add(node.right);
                if(node.left != null) stack.add(node.left);
                childrenVisited.add(node);
            } else {
                nodes.add(node.val);
                stack.pop();
            }
        }
        return nodes;
    }
}
