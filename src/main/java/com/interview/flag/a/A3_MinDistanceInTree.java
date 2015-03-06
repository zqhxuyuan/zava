package com.interview.flag.a;

import com.interview.basics.model.collection.hash.HashMap;
import com.interview.leetcode.utils.TreeNode;

/**
 * Created_By: stefanie
 * Date: 14-12-4
 * Time: 下午5:53
 */
public class A3_MinDistanceInTree {

    public static int minDistance(TreeNode root, TreeNode n1, TreeNode n2){
        HashMap<Integer, Integer> depth = new HashMap<>();
        depth(root, depth, 1);
        TreeNode commonAncestor = lowestCommonAncestor(root, n1, n2);
        if(commonAncestor == n1 || commonAncestor == n2){
            return Math.abs(depth.get(n1.val) - depth.get(n2.val));
        } else {
            return depth.get(n1.val) + depth.get(n2.val) - 2 * depth.get(commonAncestor.val);
        }
    }

    public static void depth(TreeNode node, HashMap<Integer, Integer> depthMap, int depth){
        if(node == null) return;
        depthMap.put(node.val, depth);
        depth(node.left, depthMap, depth + 1);
        depth(node.right, depthMap, depth + 1);
    }

    public static TreeNode lowestCommonAncestor(TreeNode node, TreeNode n1, TreeNode n2){
        if(node == null) return null;
        if(node == n1 || node == n2) return node;
        TreeNode left = lowestCommonAncestor(node.left, n1, n2);
        TreeNode right = lowestCommonAncestor(node.right, n1, n2);
        if(left == null) return right;
        else if(right == null) return left;
        else return node;
    }



    public static void main(String[] args){
        HashMap<Integer, TreeNode> nodes = new HashMap<>();
        for(int i = 0; i < 12; i++){
            nodes.put(i, new TreeNode(i));
        }
        nodes.get(1).left = nodes.get(5);
        nodes.get(1).right = nodes.get(7);
        nodes.get(5).left = nodes.get(2);
        nodes.get(5).right = nodes.get(6);
        nodes.get(6).right = nodes.get(11);
        nodes.get(7).right = nodes.get(9);
        nodes.get(9).right = nodes.get(4);

        System.out.println(A3_MinDistanceInTree.minDistance(nodes.get(1), nodes.get(2), nodes.get(11)));
        System.out.println(A3_MinDistanceInTree.minDistance(nodes.get(1), nodes.get(1), nodes.get(11)));
        System.out.println(A3_MinDistanceInTree.minDistance(nodes.get(1), nodes.get(4), nodes.get(11)));
    }
}
