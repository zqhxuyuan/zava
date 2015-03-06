package com.interview.flag.f;

import com.interview.utils.ArrayUtil;
import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-11-30
 * Time: 下午9:37
 */
public class F3_MoveNonZeroLeft {
    public static int move(int[] nums){
        int low = 0;
        int high = nums.length - 1;
        while(low < high){
            if(nums[high] == 0) high--;
            else if(nums[low] != 0) low++;
            else ArrayUtil.swap(nums, high--, low++);
        }
        return high + 1;
    }

    public static void main(String[] args){
        int[] nums = new int[]{1,2,0,4,1,0,5,1,0,0,2};
        System.out.println(F3_MoveNonZeroLeft.move(nums));
        ConsoleWriter.printIntArray(nums);
    }
}
