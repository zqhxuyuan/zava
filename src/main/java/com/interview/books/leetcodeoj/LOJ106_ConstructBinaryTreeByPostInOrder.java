package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.TreeNode;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午2:55
 */
public class LOJ106_ConstructBinaryTreeByPostInOrder {
    //offset initialize as inorder.length - 1, and offset--
    //during buildTree, build right(position + 1, high) before build left(low, position - 1)
    int offset;
    public TreeNode buildTree(int[] inorder, int[] postorder) {
        offset = inorder.length - 1;
        return buildTree(postorder, inorder, 0, inorder.length - 1);
    }

    public TreeNode buildTree(int[] postorder, int[] inorder, int low, int high){
        if(low > high) return null;
        Integer value = postorder[offset--];
        int position = high;
        while(position >= low && inorder[position] != value) position--;
        TreeNode node = new TreeNode(value);
        node.right = buildTree(postorder, inorder, position + 1, high);
        node.left = buildTree(postorder, inorder, low, position - 1);
        return node;
    }
}
