package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午1:46
 */
public class LOJ95_UniqueBinarySearchTreeII {
    //BST: the left subtree is smaller than root, and the right subtree is larger than root
    //use low and high to do permuation
    public List<TreeNode> generateTrees(int n) {
        return generateTrees(1, n);
    }

    private List<TreeNode> generateTrees(int low, int high){
        List<TreeNode> trees = new ArrayList();
        if(low > high) trees.add(null);
        else {
            for(int i = low; i <= high; i++){
                List<TreeNode> leftTrees = generateTrees(low, i - 1);
                List<TreeNode> rightTrees = generateTrees(i + 1, high);
                for(TreeNode left : leftTrees){
                    for(TreeNode right : rightTrees){
                        TreeNode root = new TreeNode(i);
                        root.left = left;
                        root.right = right;
                        trees.add(root);
                    }
                }
            }
        }
        return trees;
    }
}
