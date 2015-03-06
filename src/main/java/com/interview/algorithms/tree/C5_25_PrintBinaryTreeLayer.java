package com.interview.algorithms.tree;

import com.interview.basics.model.collection.queue.LinkedQueue;
import com.interview.basics.model.collection.queue.Queue;
import com.interview.basics.model.tree.BinaryTreeNode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created_By: stefanie
 * Date: 14-9-26
 * Time: 下午3:37
 */
public class C5_25_PrintBinaryTreeLayer {
    public static void print(BinaryTreeNode tree){
        Queue<BinaryTreeNode> queue = new LinkedQueue<>();
        queue.push(tree);
        queue.push(null);
        while(!queue.isEmpty()){
            BinaryTreeNode obj = queue.pop();
            if(obj == null){
                if(queue.size() > 0) {
                    queue.push(null);
                    System.out.println();
                    continue;
                } else return;
            }
            System.out.printf("%s  ", obj.value.toString());
            if(obj.left != null)  queue.push(obj.left);
            if(obj.right != null) queue.push(obj.right);
        }
    }

    public static List<Object> printRecursive(BinaryTreeNode node, int n){
        List<Object> objs = new ArrayList<>();
        printRecursive(node, n, objs);
        return objs;
    }

    private static void printRecursive(BinaryTreeNode node, int n, List<Object> objs){
        if(node == null || n < 0)   return;
        if(n == 0){
            objs.add(node.value);
        } else {
            if(node.left != null)  printRecursive(node.left, n - 1, objs);
            if(node.right != null) printRecursive(node.right, n - 1, objs);
        }
    }

    public static List<Object> print(BinaryTreeNode tree, int n){
        List<Object> objs = new ArrayList<>();
        Queue<BinaryTreeNode> queue = new LinkedQueue<>();
        queue.push(tree);
        queue.push(null);
        while(n > 0 && !queue.isEmpty()){
            BinaryTreeNode obj = queue.pop();
            if(obj == null){
                if(queue.size() > 0) {
                    queue.push(null);
                    n--;
                    continue;
                } else return objs;
            }
            //System.out.printf("%s  ", obj.value.toString());
            if(obj.left != null)  queue.push(obj.left);
            if(obj.right != null) queue.push(obj.right);
        }

        while(!queue.isEmpty()) {
            if(queue.peek() == null) break;
            objs.add(queue.pop().value);
        }
        return objs;
    }
}
