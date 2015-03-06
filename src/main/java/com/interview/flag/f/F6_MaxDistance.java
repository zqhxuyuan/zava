package com.interview.flag.f;

import com.interview.utils.ConsoleWriter;
import com.interview.utils.DataGenerator;

/**
 * Created_By: stefanie
 * Date: 14-12-10
 * Time: 下午1:00
 */
public class F6_MaxDistance {

    //distance[i] = A[i] + A[k] + k - i, and distance[i+1] = A[i+1] + A[k] + k - (i + 1)
    //distance[i] == max(distance[i+1] - A[i+1] + A[i] + 1, A[i] * 2);
    public int maxDistance(int[] nums){
        if(nums == null || nums.length < 1) return 0;
        int pre = nums[nums.length  - 1] * 2;
        int max = pre;
        for(int i = nums.length - 2; i >= 0; i--){
            int distance = Math.max(nums[i] * 2, pre - nums[i + 1] + nums[i] + 1);
            max = Math.max(max, distance);
            pre = distance;
        }
        return max;
    }

    public int maxDistanceO2(int[] nums){
        int max = Integer.MIN_VALUE;
        for(int i = nums.length - 1; i >= 0; i--){
            for(int j = i; j < nums.length; j++){
                int distance = nums[i] + nums[j] + j - i;
                max = Math.max(max, distance);
            }
        }
        return max;
    }

    public static void main(String[] args){
        F6_MaxDistance finder = new F6_MaxDistance();
        for(int i = 0; i < 10; i++){
            int[] nums = DataGenerator.generateIntArray(10, false);
            ConsoleWriter.printIntArray(nums);
            System.out.println(finder.maxDistanceO2(nums));
            System.out.println(finder.maxDistance(nums));
        }

    }
}
