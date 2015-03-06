package com.interview.books.question300;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/22/14
 * Time: 1:34 PM
 *
 * Given a sequence of int, write code to check if this the post-order traverse of a binary search tree.
 *
 * post order: the last number is the root of the tree, and it should split the sequence into two set:
 *  smaller than it and larger then it as the two sub-tree.
 *  recursive the check if the seq follow the above rule.
 */
public class TQ28_BSTPostOrderChecker {

    public static boolean check(Integer[] nums){
        return check(nums, 0, nums.length - 1);
    }

    public static boolean check(Integer[] nums, int low, int high){
        if (low > high) return true;
        int offset = high - 1;
        while (offset >= low && nums[high] <= nums[offset]) offset--;
        int mid = offset;
        while (offset >= low && nums[high] >= nums[offset]) offset--;
        if(low != offset + 1) return false;  //can be partitioned into 2 part
        return check(nums, mid + 1, high -1) && check(nums, low, mid);
    }
}
