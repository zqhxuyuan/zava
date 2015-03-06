package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinaryTree;
import com.interview.basics.model.tree.BinaryTreeNode;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/17/14
 * Time: 3:27 PM
 */
public class C5_16_TreeSumPath {

    public static void findSum(BinaryTree<Integer> tree, int sum){
        int height = getHeight(tree.getRoot());
        int[] buffer = new int[height];
        printSum(tree.getRoot(), sum, buffer, 0);
    }

    private static void printSum(BinaryTreeNode<Integer> node, int sum, int[] buffer, int level){
        if(node == null) return;
        buffer[level] = node.value;
        int tmp = 0;
        for(int i = level; i >= 0; i--){
            tmp += buffer[i];
            if(tmp == sum) printPath(buffer, i, level);
        }
        printSum(node.left, sum, buffer, level + 1);
        printSum(node.right, sum, buffer, level + 1);
        buffer[level] = 0;
    }

    private static void printPath(int[] buffer, int start, int end){
        for(int i = start; i <= end; i++) System.out.print(buffer[i] + " ");
        System.out.println();
    }

    private static int getHeight(BinaryTreeNode<Integer> node){
        if(node == null) return 0;
        int left = getHeight(node.left);
        int right = getHeight(node.right);
        return Math.max(left, right) + 1;
    }
}
