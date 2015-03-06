package com.interview.books.leetcode;

import com.interview.utils.ArrayUtil;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 上午10:36
 */
public class LCS5_FairPainterBinarySearch {
    /**
     * the range of minTime is (max(walls), sum(walls)), we can do binary search on the range
     *  for every mid, get how many painter needed to achieve the minTime.
     *  the Binary Search code is to find the low_bound which painter is K
     *
     */
    //Time: O(NlgC) C is the sum(walls)
    public int minTime(int[] walls, int K){
        int low = ArrayUtil.max(walls);
        int high = ArrayUtil.sum(walls, 0, walls.length - 1);

        while(low < high) {
            int mid = low + (high - low) / 2;
            int requiredPainter = getRequiredPainters(walls, mid);
            if (requiredPainter <= K)
                high = mid;
            else
                low = mid + 1;
        }
        return low;
    }

    public int getRequiredPainters(int A[], int maxLengthPerPainter) {
        int sum = 0, numPainters = 1;
        for (int i = 0; i < A.length; i++) {
            sum += A[i];
            if (sum > maxLengthPerPainter) {
                sum = A[i];
                numPainters++;
            }
        }
        return numPainters;
    }

    public static void main(String[] args){
        LCS5_FairPainterBinarySearch painter = new LCS5_FairPainterBinarySearch();
        int[] walls = new int[]{1,2,4,5,6,3,1,2,7,8,2,5};
        System.out.println(ArrayUtil.sum(walls, 0, walls.length - 1));
        System.out.println(painter.minTime(walls, 4));
    }
}
