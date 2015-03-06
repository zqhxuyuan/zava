package com.interview.books.svinterview;

import com.interview.leetcode.utils.TreeNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午1:02
 */
public class SV9_BSTKNN {
    public Iterable<Integer> findKNN(TreeNode root, int k, int target){
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Math.abs(o2) - Math.abs(o1);
            }
        };
        PriorityQueue<Integer> closestHeap = new PriorityQueue<>(k, comparator);
        inorderTraverse(root, closestHeap, k, target);
        List<Integer> numbers = new ArrayList<>();
        while(!closestHeap.isEmpty()){
            numbers.add(0, closestHeap.poll() + target);
        }
        return numbers;
    }

    public void inorderTraverse(TreeNode node, PriorityQueue<Integer> heap, int k, int target){
        if(node == null) return;
        inorderTraverse(node.left, heap, k, target);
        if(heap.size() < k) heap.add(node.val - target);
        else {
            int diff = Math.abs(node.val - target);
            if(diff < Math.abs(heap.peek())){
                heap.poll();
                heap.add(node.val - target);
            }
        }
        inorderTraverse(node.right, heap, k, target);
    }

    public static void main(String[] args){
        TreeNode root = TreeNode.sampleBST();
        SV9_BSTKNN finder = new SV9_BSTKNN();
        for(Integer num : finder.findKNN(root, 3, 6)){
            System.out.print(num + ", ");
        }
        System.out.println();
    }

}
