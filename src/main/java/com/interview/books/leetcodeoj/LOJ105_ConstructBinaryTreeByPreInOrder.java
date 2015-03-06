package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.TreeNode;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午2:49
 */
public class LOJ105_ConstructBinaryTreeByPreInOrder {
    //use preorder[offset] to divide inorder into left and right part as left/right subtree.
    //offset should be class attribute to enable offset++.
    //build left subtree before right subtree
    int offset;
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        offset = 0;
        return buildTree(preorder, inorder, 0, inorder.length - 1);
    }

    public TreeNode buildTree(int[] preorder, int[] inorder, int low, int high){
        if(low > high) return null;
        int value = preorder[offset++];
        int position = low;
        while(position <= high && inorder[position] != value) position++;
        TreeNode node = new TreeNode(value);
        node.left = buildTree(preorder, inorder, low, position - 1);
        node.right = buildTree(preorder, inorder, position + 1, high);
        return node;
    }
}
