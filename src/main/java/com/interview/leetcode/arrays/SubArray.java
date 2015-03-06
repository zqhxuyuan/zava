package com.interview.leetcode.arrays;

import com.interview.leetcode.utils.IndexedValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-15
 * Time: 下午5:38
 */

public class SubArray {
    /**
     * Given an array with positive and negative numbers, find the subarray which sum is max
     */
    //Time: O(N), Space: O(1)
    public static int maxSumOptz(int[] A){
        int max = 0;
        int largest = A[0];
        int sum = 0;
        for(int i = 0; i < A.length; i++){
            if(A[i] > largest) largest = A[i];
            sum = Math.max(sum + A[i], 0);
            max = Math.max(max, sum);
        }
        return max == 0? largest : max;
    }

    /**
     * Given an array with positive and negative numbers, find the subarray which sum is max, return the range
     */
    public static int[] maxSumRange(int[] A){
        int[] range = new int[3];
        int largest = 0;
        int sum = 0;
        for(int i = 0; i < A.length; i++){
            if(A[i] > A[largest]) largest = i;
            sum += A[i];
            if(sum < 0){
                sum = 0;
                range[1] = i + 1;
            }
            if(sum > range[0]){
                range[0] = sum;
                range[2] = i;
            }
        }
        if(range[0] == 0){
            range[0] = A[largest];
            range[1] = largest;
            range[2] = largest;
        }
        return range;
    }

    //Time: O(N), Space O(N)
    public static int maxSum(int[] A) {
        int[] sum = new int[A.length];
        sum[0] = A[0];
        for(int i = 1; i < A.length; i++) sum[i] = sum[i - 1] + A[i];

        int max = A[0];
        int min = Math.min(0, A[0]);
        for(int i = 1; i < sum.length; i++){
            int subarray = Math.max(A[i], sum[i] - min);
            max = Math.max(subarray, max);
            if(sum[i] < min) min = sum[i];
        }
        return max;
    }

    /**
     * Given an array of integers, find the subarray with smallest sum.
     *   Can also change every integer in A to 0-A[i], change the problem to maxSum, remember to change back when return
     */
    //Time: O(N), Space: O(1)
    public static int minSum(int[] A){
        int min = 0;
        int smallest = A[0];
        int sum = 0;
        for(int i = 0; i < A.length; i++){
            smallest = Math.min(smallest, A[i]);
            sum = Math.min(sum + A[i], 0);
            min = Math.min(sum, min);
        }
        return min == 0? smallest : min;
    }

    /**
     * Given an array of integers, find two non-overlapping subarrays which have the largest sum.
     */
    //Time: O(N), Space: O(N)
    public int maxSumTwoSubArrays(int[] nums) {
        // write your code
        int[] left = new int[nums.length];  //the max subarray from 0 - i
        int[] right = new int[nums.length]; //the max subarray from i ~ size - 1

        //scan forward cal left[i]
        int max = 0;
        int largest = nums[0];
        int sum = 0;
        for(int i = 0; i < nums.length; i++){
            largest = Math.max(largest, nums[i]);
            sum = Math.max(sum + nums[i], 0);
            max = Math.max(max, sum);
            left[i] = max == 0? largest : max;
        }

        //scan backward cal right[i]
        max = 0;
        largest = nums[nums.length - 1];
        sum = 0;
        for(int i = nums.length - 1; i >= 0; i--){
            largest = Math.max(largest, nums[i]);
            sum = Math.max(sum + nums[i], 0);
            max = Math.max(max, sum);
            right[i] = max == 0? largest : max;
        }

        //for every breaking point, max = left[i - 1] + right[i]
        max = Integer.MIN_VALUE;
        for(int i = 1; i < nums.length; i++){
            max = Math.max(left[i-1] + right[i], max);
        }
        return max;
    }

    /**
     * Given an array with integers.
     * Find two non-overlapping subarrays A and B, which |SUM(A) - SUM(B)| is the largest.
     */
    //Time: O(N), Space O(N)
    public static int maxDiffSubArrays(int[] nums) {
        int[] leftMax = getMaxSum(nums, 0, nums.length - 1, 1);  //max subarray from 0 - i
        int[] leftMin = getMinSum(nums, 0, nums.length - 1, 1);
        int[] rightMax = getMaxSum(nums, nums.length - 1, 0, -1);
        int[] rightMin = getMinSum(nums, nums.length - 1, 0, -1);

        int max = Integer.MIN_VALUE;
        for(int i = 1; i < nums.length; i++){
            max = Math.max(leftMax[i-1] - rightMin[i], max);
            max = Math.max(rightMax[i] - leftMin[i-1], max);
        }
        return max;
    }

    private static int[] getMaxSum(int[] nums, int begin, int end, int step){
        int[] maxMatrix = new int[nums.length];
        int max = 0;
        int sum = 0;
        int largest = nums[begin];
        for(int i = begin; i != end; i += step){
            largest = Math.max(largest, nums[i]);
            sum = Math.max(sum + nums[i], 0);
            max = Math.max(max, sum);
            maxMatrix[i] = max == 0? largest : max;
        }
        return maxMatrix;
    }

    private static int[] getMinSum(int[] nums, int begin, int end, int step){
        int[] minMatrix = new int[nums.length];
        int min = 0;
        int sum = 0;
        int smallest = nums[begin];
        for(int i = begin; i != end; i += step){
            smallest = Math.min(smallest, nums[i]);
            sum = Math.min(sum + nums[i], 0);
            min = Math.min(min, sum);
            minMatrix[i] = min == 0? smallest : min;
        }
        return minMatrix;
    }

    /**
     * Given a matrix, find a sub matrix which sum is max
     */
    //Time: O(row^2*col) Space: O(col)
    public static int maxSum(int[][] matrix){
        int max = Integer.MIN_VALUE;
        for(int i = 0; i < matrix.length - 1; i++){
            int[] rowSum = new int[matrix.length];
            for(int j = i; j < matrix.length; j++){
                for(int k = 0; k < matrix[0].length; k++){
                    rowSum[k] += matrix[j][k];
                }
                max = Math.max(max, maxSumOptz(rowSum));
            }
        }
        return max;
    }

    /**
     * Given a array, find a subarray which sum is zero, if no such subarray return {-1, -1};
     */
    //Time: O(N), Space: O(N)
    public static int[] sumZero(int[] nums){
        HashMap<Integer, Integer> sumMap = new HashMap<>();
        int sum = 0;
        for(int i = 0; i < nums.length; i++){
            sum = sum + nums[i];
            if(sumMap.containsKey(sum)){
                int[] range = new int[2];
                range[0] = sumMap.get(sum) + 1;
                range[1] = i;
                return range;
            }
            sumMap.put(sum, i);
        }
        return new int[]{-1, -1};
    }

    /**
     * Given a array, find a subarray which sum is closet to zero
     *   subarray(i, j) = sum[j] - sum[i-1]
     *   sum[j]- sum[i-1] ~~ 0
     */
    //Time: O(nlgn), Space:O(N)
    public static int[] sumClosetZero(int[] nums){
        List<IndexedValue> sums = new ArrayList<IndexedValue>();
        sums.add(new IndexedValue(nums[0], 0));
        for(int i = 1; i < nums.length; i++){
            sums.add(new IndexedValue(sums.get(i - 1).value + nums[i], i));
        }
        Collections.sort(sums);
        int closest = Integer.MAX_VALUE;
        int begin = 0;
        int end = 0;
        for(int i = 1; i < sums.size(); i++){
            int diff = sums.get(i).value - sums.get(i - 1).value;
            if(diff < closest){
                closest = diff;
                begin = Math.min(sums.get(i).offset, sums.get(i - 1).offset) + 1;
                end = Math.max(sums.get(i).offset, sums.get(i - 1).offset);
                if(closest == 0) return new int[]{closest, begin, end};
            }
        }
        return new int[]{closest, begin, end};
    }

    /**
     * Find the contiguous subarray within an array (containing at least one number) which has the largest product.
     * For example, given the array [2,3,-2,4], the contiguous subarray [2,3] has the largest product = 6.
     * If the array doesn't contain 0, so every time we multiple a number, it went to max(pos) or min(neg).
     * if neg it will become positive when have a neg number after i-th.
     * So cal forward and backward product,
     *      1  -1   1,     if 2nd element -1 is not used in forward and backward, so backward and forward will all be negative
     *     -1, -1,  1      if 2nd element -1 is used in forward, forward will be the max.
     *      1, -1, -1      if 2nd element -1 is used in backward, backward will be the max.
     * so max = Math.max(max, Math.max(backward, forward))
     * if have zero, product will be 0, so set forward and backward as 1.
     * Tricks:
     * 1. if one variable get effect from previous and bring effect to post. we could scan array forward and backward.
     * 2. define a general case (no zero), and try to handle special case (zeros)
     */
    //Time: O(N), Space: O(N)
    public static int maxProduct(int[] A) {
        int backward = 1;
        int forward = 1;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < A.length; i++) {
            forward *= A[i];
            backward *= A[A.length - 1 - i];
            int bigger = Math.max(backward, forward);
            max = Math.max(max, bigger);
            if (backward == 0) backward = 1;
            if (forward == 0) forward = 1;
        }
        return max;
    }


}
