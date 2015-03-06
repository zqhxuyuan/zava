package com.interview.books.fgdsb;

import com.interview.leetcode.utils.TreeNode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 15-2-1
 * Time: 下午10:16
 */
public class NLC3_PrettyBSTPrinter {

    public void print(TreeNode root){
        int height = maxHeight(root);
        int lastLayer = (int) Math.pow(2, height - 1);
        int total = 6 * lastLayer + 1;

        Queue<TreeNode> queue = new LinkedList();
        if(root != null) queue.offer(root);
        int layer = 0;
        while(layer < height){
            int layerSize = queue.size();
            int space = total / (layerSize + 1);
            for(int i = 0; i < layerSize; i++){
                printSpace(space);
                TreeNode node = queue.poll();
                System.out.print(node == null? " " : node.val);
                queue.offer(node == null? null : node.left);
                queue.offer(node == null? null : node.right);
            }
            System.out.println();
            layer++;
        }
    }

    private int maxHeight(TreeNode root){
        if(root == null) return 0;
        else return Math.max(maxHeight(root.left), maxHeight(root.right)) + 1;
    }

    private void printSpace(int count){
        for(int i = 0; i < count; i++) System.out.print(" ");
    }

    public static void main(String[] args){
        NLC3_PrettyBSTPrinter printer = new NLC3_PrettyBSTPrinter();
        TreeNode root = TreeNode.sampleBST();
        printer.print(root);
    }
}
