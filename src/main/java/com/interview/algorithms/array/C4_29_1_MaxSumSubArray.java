package com.interview.algorithms.array;

/**
 * Created_By: zouzhile
 * Date: 11/1/14
 * Time: 4:05 PM
 */
public class C4_29_1_MaxSumSubArray {

    class Range {
        int lo;
        int hi;
        int sum;
    }

    /**
     * get the max sum of all possible sub arrays
     * @param array
     * @return
     */
    public int maxSum(int[] array) {
        int maxSum = 0;
        int maxElement = array[0];
        int sum = 0;

        for(int i = 0; i < array.length; i++) {
            sum = Math.max(sum + array[i], 0);
            if(sum > maxSum) maxSum = sum;
            if(array[i] > maxElement) maxElement = array[i];
        }

        maxSum = maxSum == 0 ? maxElement : maxSum;
        return maxSum;
    }

    /**
     * get the continuous sub array with max sum
     * @param array
     * @return
     */
    public Range maxRange(int[] array) {
        Range max = new Range();
        Range current = new Range();

        int sum = 0;
        int maxElementOffset = 0;

        for(int i = 0; i < array.length; i ++) {
            sum = Math.max(sum + array[i], 0);

            if(sum == 0) {
                current.lo = i + 1;
                current.hi = i + 1;
            }

            if(sum > max.sum) {
                current.hi = i;
                max.lo = current.lo;
                max.hi = current.hi;
                max.sum = sum;
            }

            if(array[i] > array[maxElementOffset]) {
                maxElementOffset = i;
            }
        }

        if(max.sum == 0) {
            max.lo = max.hi = maxElementOffset;
        }

        return max;
    }

    public static void main(String[] args) {
        int[] array = new int[]{2,-8,3,2,4,-10};
        C4_29_1_MaxSumSubArray maxSumSubArray = new C4_29_1_MaxSumSubArray();
        Range range = maxSumSubArray.maxRange(array);
        System.out.println(String.format("lo=%s, hi=%s, max_sum=%s", range.lo, range.hi, range.sum));
        System.out.println("maxSum = " + maxSumSubArray.maxSum(array));
    }

}
