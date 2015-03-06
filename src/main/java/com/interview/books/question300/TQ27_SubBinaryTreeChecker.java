package com.interview.books.question300;

import com.interview.leetcode.utils.TreeNode;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-12-16
 * Time: 上午11:58
 */
public class TQ27_SubBinaryTreeChecker {

    public String inOrderTraverse(TreeNode root){
        if(root == null) return "";
        StringBuffer buffer = new StringBuffer();
        inOrderTraverse(root, buffer);
        return buffer.toString();
    }

    private void inOrderTraverse(TreeNode node, StringBuffer buffer){
        if(node.left == null && node.right == null){
            buffer.append(node.val);
            return;
        }
        if(node.left == null) buffer.append("#");
        else inOrderTraverse(node.left, buffer);

        buffer.append(node.val);

        if(node.right == null) buffer.append("#");
        else inOrderTraverse(node.right, buffer);
    }

    public String preOrderTraverse(TreeNode root){
        if(root == null) return "";
        StringBuffer buffer = new StringBuffer();
        preOrderTraverse(root, buffer);
        return buffer.toString();
    }

    private void preOrderTraverse(TreeNode node, StringBuffer buffer){
        buffer.append(node.val);
        if(node.left == null && node.right == null) return;
        if(node.left == null) buffer.append("#");
        else preOrderTraverse(node.left, buffer);
        if(node.right == null) buffer.append("#");
        else preOrderTraverse(node.right, buffer);
    }

    public boolean isSubTree(TreeNode t1, TreeNode t2){
        if(inOrderTraverse(t1).contains(inOrderTraverse(t2)) && preOrderTraverse(t1).contains(preOrderTraverse(t2))) return true;
        else return false;
    }

    public static void main(String[] args){
        TQ27_SubBinaryTreeChecker checker = new TQ27_SubBinaryTreeChecker();
        TreeNode t1 = TreeNode.sampleBST();

        HashMap<Integer, TreeNode> nodes = new HashMap<>();
        for (int i = 1; i < 5; i++) {
            nodes.put(i, new TreeNode(i));
        }
        nodes.get(2).left = nodes.get(1);
        nodes.get(2).right = nodes.get(3);

        TreeNode t2 = nodes.get(2);
        System.out.println(checker.isSubTree(t1, t2)); //false

        nodes.get(3).right = nodes.get(4);
        System.out.println(checker.isSubTree(t1, t2)); //true

        t2 = new TreeNode(3);
        t2.left = new TreeNode(4);

        System.out.println(checker.isSubTree(t1, t2)); //false

    }
}
