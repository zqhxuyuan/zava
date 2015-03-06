package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinaryTreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-10
 * Time: 下午12:51
 */
public class C5_27_UniqueBST {
    public List<BinaryTreeNode> generateTrees(int n) {
        return generateTrees(1, n);
    }

    private List<BinaryTreeNode> generateTrees(int low, int high){
        List<BinaryTreeNode> trees = new ArrayList<BinaryTreeNode>();
        if(low > high) {
            trees.add(null);
            return trees;
        }
        for(int i = low; i <= high; i++){
            List<BinaryTreeNode> lefts = generateTrees(low, i - 1);
            List<BinaryTreeNode> rights = generateTrees(i + 1, high);
            for(BinaryTreeNode left : lefts){
                for(BinaryTreeNode right : rights){
                    BinaryTreeNode root = new BinaryTreeNode(i);
                    root.left = left;
                    root.right = right;
                    trees.add(root);
                }
            }
        }
        return trees;
    }
}
