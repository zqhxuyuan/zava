package com.interview.books.svinterview;

import com.interview.leetcode.utils.TreeNode;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午8:03
 */
public class SV18_PathPrinterIteractive {
    public void print(TreeNode root){
        Stack<TreeNode> stack = new Stack<TreeNode>();
        TreeNode prev = null;
        if(root != null) stack.push(root);
        while(stack.size() > 0){
            TreeNode node = stack.peek();
            if(prev == null || prev.left == node || prev.right == node){
                if(node.left != null) stack.push(node.left);
                else if(node.right != null) stack.push(node.right);
            } else if(node.left == prev){
                if(node.right != null)  stack.push(node.right);
            } else {
                if(node.left == null && node.right == null){
                    printPath(stack, node);
                }
                stack.pop();
            }
            prev = node;
        }
    }

    public void printPath(Stack<TreeNode> nodes, TreeNode node){
        System.out.printf("Path of %d is: ", node.val);
        for(int i = nodes.size() - 1; i >= 0; i--){
            System.out.print(nodes.get(i).val + " ");
        }
        System.out.println();
    }

    public static void main(String[] args){
        TreeNode root = TreeNode.sampleBST();
        SV18_PathPrinterIteractive printer = new SV18_PathPrinterIteractive();
        printer.print(root);
    }
}
