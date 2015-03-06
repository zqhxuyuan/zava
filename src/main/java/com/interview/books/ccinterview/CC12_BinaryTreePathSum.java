package com.interview.books.ccinterview;

import com.interview.leetcode.utils.TreeNode;
import com.interview.leetcode.utils.TreeNodePrinter;
import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午2:33
 */
public class CC12_BinaryTreePathSum {
    public List<List<Integer>> find(TreeNode root, int target){
        List<List<Integer>> paths = new ArrayList<>();
        traverse(root, target, new ArrayList<Integer>(), paths);
        return paths;
    }

    //Time: O(NlgN)
    public void traverse(TreeNode node, int target, List<Integer> path, List<List<Integer>> paths){
        if(node == null) return;
        path.add(node.val);
        int sum = 0;
        for(int i = path.size() - 1; i >= 0; i--){
            sum += path.get(i);
            if(sum == target){
                paths.add(getPath(path, i, path.size() - 1));
            }
        }
        traverse(node.left, target, path, paths);
        traverse(node.right, target, path, paths);
        path.remove(path.size() - 1);
    }

    public List<Integer> getPath(List<Integer> path, int start, int end){
        List<Integer> copy = new ArrayList<>();
        for(int i = start; i <= end; i++) copy.add(path.get(i));
        return copy;
    }

    public static void main(String[] args){
        TreeNode root = TreeNode.sampleBST();
        TreeNodePrinter.print(root);
        CC12_BinaryTreePathSum finder = new CC12_BinaryTreePathSum();
        List<List<Integer>> paths = finder.find(root, 7);
        ConsoleWriter.print(paths);
    }
}
