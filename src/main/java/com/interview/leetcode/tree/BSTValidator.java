package com.interview.leetcode.tree;

import com.interview.leetcode.utils.TreeNode;

/**
 * Created_By: stefanie
 * Date: 14-11-22
 * Time: 下午8:37
 */
public class BSTValidator {
    /**
     * Given a binary tree, determine if it is a valid binary search tree (BST).
     * Using in-order traverse
     */
    //Time:O(N), Space: O(1), StackSpace: O(N)
    static class Validator{
        TreeNode preVisited = null;
        public boolean isValidBST(TreeNode root) {
            if(root == null) return true;
            if(!isValidBST(root.left)) return false;
            if(preVisited != null && root.val <= preVisited.val) return false;
            preVisited = root;
            if(!isValidBST(root.right)) return false;
            return true;
        }
    }

    /**
     * Given a sequence of int, write code to check if this the post-order traverse of a binary search tree.
     *  post order: the last number is the root of the tree, and it should split the sequence into two set:
     *  smaller than it and larger then it as the two sub-tree.
     *  recursive the check if the seq follow the above rule.
     */
    static class BSTTraverseValidator{

        public static boolean checkPostOrder(int[] nums){
            return checkPostOrder(nums, 0, nums.length - 1);
        }

        public static boolean checkPostOrder(int[] nums, int low, int high){
            if (low > high) return true;
            int offset = high - 1;
            while (offset >= low && nums[high] <= nums[offset]) offset--;
            int mid = offset;
            while (offset >= low && nums[high] >= nums[offset]) offset--;
            if(low != offset + 1) return false;  //can be partitioned into 2 part
            return checkPostOrder(nums, mid + 1, high -1) && checkPostOrder(nums, low, mid);  //recursive check it's subtrees
        }
    }

}
