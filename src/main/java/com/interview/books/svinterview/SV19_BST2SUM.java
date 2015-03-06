package com.interview.books.svinterview;

import com.interview.leetcode.utils.TreeNode;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午8:21
 */
public class SV19_BST2SUM {

    public void find(TreeNode root, int K){
        if(root == null) return;
        Stack<TreeNode> leftStack = new Stack<>();
        Stack<TreeNode> rightStack = new Stack<>();
        populateLeft(root, leftStack);
        populateRight(root, rightStack);
        do{
            int sum = leftStack.peek().val + rightStack.peek().val;
            if(sum == K){
                System.out.println(leftStack.peek().val + " + " + rightStack.peek().val);
                TreeNode largest = rightStack.pop();
                populateRight(largest.left, rightStack);
                TreeNode smallest = leftStack.pop();
                populateLeft(smallest.right, leftStack);
            } else if(sum > K){
                TreeNode largest = rightStack.pop();
                populateRight(largest.left, rightStack);
            } else {
                TreeNode smallest = leftStack.pop();
                populateLeft(smallest.right, leftStack);
            }
        } while(!leftStack.isEmpty() && !rightStack.isEmpty() && leftStack.peek().val < rightStack.peek().val);

    }

    public void populateRight(TreeNode node, Stack<TreeNode> nodes){
        while(node != null){
            nodes.push(node);
            node = node.right;
        }
    }

    public void populateLeft(TreeNode node, Stack<TreeNode> nodes){
        while(node != null){
            nodes.push(node);
            node = node.left;
        }
    }

    public static void main(String[] args){
        SV19_BST2SUM finder = new SV19_BST2SUM();
        TreeNode root = TreeNode.sampleBST();
        finder.find(root, 8);
    }
}
