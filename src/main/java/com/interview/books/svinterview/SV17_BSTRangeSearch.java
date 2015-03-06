package com.interview.books.svinterview;

import com.interview.leetcode.utils.TreeNode;
import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午6:15
 */
public class SV17_BSTRangeSearch {
    public List<Integer> search(TreeNode root, int m, int n){
        List<Integer> numbers = new ArrayList<>();
        if(root == null) return numbers;
        if(m > n) return search(root, n, m);
        search(root, m, n, numbers);
        return numbers;
    }

    public void search(TreeNode node, int m, int n, List<Integer> numbers){
        if(node == null) return;
        if(m <= node.val)   search(node.left, m, n, numbers);
        if(node.val >= m && node.val <= n)  numbers.add(node.val);
        if(n >= node.val)  search(node.right, m, n, numbers);
    }

    public static void main(String[] args){
        TreeNode root = TreeNode.sampleBST();
        SV17_BSTRangeSearch searcher = new SV17_BSTRangeSearch();
        List<Integer> numbers = searcher.search(root, 3, 6);
        ConsoleWriter.printCollection(numbers);
    }
}
