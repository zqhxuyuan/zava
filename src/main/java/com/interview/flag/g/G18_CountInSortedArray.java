package com.interview.flag.g;

/**
 * Created_By: stefanie
 * Date: 15-1-3
 * Time: 下午9:33
 */
public class G18_CountInSortedArray {
    public int count(int[] nums, int target){
        if(nums == null || nums.length == 0) return 0;
        int low = searchLow(nums, target);
        if(low == -1) return 0;
        int high = searchHigh(nums, target);
        return high - low + 1;
    }

    private int searchLow(int[] nums, int target){
        int low = 0;
        int high = nums.length - 1;
        while(low < high){
            int mid = low + (high - low)/2;
            if(nums[mid] <= mid) high = mid;
            else low = mid - 1;
        }
        return nums[low] == target? low : -1;
    }

    private int searchHigh(int[] nums, int target){
        int low = 0;
        int high = nums.length - 1;
        while(low < high){
            int mid = low + (high - low)/2;
            if(nums[mid] >= mid) low = mid - 1;
            else high = mid;
        }
        return nums[high] == target? high : high - 1;
    }

}
