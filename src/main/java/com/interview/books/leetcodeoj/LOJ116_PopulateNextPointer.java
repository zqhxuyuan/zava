package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午5:23
 */
public class LOJ116_PopulateNextPointer {
    class TreeLinkNode {
        int val;
        TreeLinkNode left, right, next;
    }
    //connect left and right child of it's self, than connect(node.left) and connect(node.right), then fill the middle gap
    //fill the middle gap: while(root.right != null && root.next != null) root.right.next = root.next.left; root = root.right
    public void connect(TreeLinkNode root) {
        if(root == null) return;
        if(root.left != null && root.right != null) root.left.next = root.right;
        connect(root.left);
        connect(root.right);
        while(root.right != null && root.next != null){
            root.right.next = root.next.left;
            root = root.right;
        }
    }
}
