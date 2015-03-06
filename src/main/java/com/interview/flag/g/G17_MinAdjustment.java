package com.interview.flag.g;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 15-1-1
 * Time: 下午5:07
 */
public class G17_MinAdjustment {

    // state: cost[i][v] - the total cost of changing A[i] to v, where v belongs to [0, max]
    //        preValues[i][v] - the i-1 selected value to make cost[i][v] to be the min cost, using to backtrace B[]
    // init: cost[0][v] = |A[0] - v|;
    // function: cost[i][v] = min(cost[i-1][v - target ... v + target]) + |A[i] - v|
    //           where v, v - target and v + target all belong to [0, max]
    //           preValues[i][v] = v1 where finally cost[i][v] = cost[i-1][v1] + |A[i] - v|.
    // result: find the min cost in cost[A.length - 1][v], B[A.length - 1] = v
    //         backtrace B, B[i] = preValues[i+1][B[i+1]] for i from A.length - 2 to 0.
    public int[] minAdjustment(int[] A, int target){
        int max = getMax(A);
        int[][] cost = new int[A.length][max + 1];
        int[][] preValues = new int[A.length][max + 1];
        for(int v = 0; v <= max; v++) cost[0][v] = Math.abs(v - A[0]);
        for(int i = 1; i < A.length; i++){
            for(int v = 0; v <= max; v++){
                int preCost = Integer.MAX_VALUE;
                int preValue = 0;
                for(int diff = -1 * target; diff <= target; diff++){
                    if(v + diff < 0) continue;
                    if(v + diff > max) break;
                    if(cost[i-1][v+diff] < preCost){
                        preCost = cost[i-1][v+diff];
                        preValue = v+diff;
                    }
                }
                cost[i][v] = preCost + Math.abs(v - A[i]);
                preValues[i][v] = preValue;
            }
        }
        int minCost = Integer.MAX_VALUE;
        int[] B = new int[A.length];
        for(int v = 0; v <= max; v++){
            if(cost[A.length - 1][v] < minCost){
                minCost = cost[A.length - 1][v];
                B[A.length - 1] = v;
            }
        }
        for(int i = A.length - 2; i >= 0; i--){
            B[i] = preValues[i+1][B[i+1]];
        }
        return B;
    }

    private int getMax(int[] num) {
        int max = 0;
        for(int i = 0; i < num.length; i ++) if(num[i] > max) max = num[i];
        return max;
    }

    public static void main(String[] args){
        int[] nums = new int[]{1,4,4,3};
        G17_MinAdjustment adjustment = new G17_MinAdjustment();
        int[] B = adjustment.minAdjustment(nums, 1); //{2,3,4,3}    minCost = 2
        ConsoleWriter.printIntArray(B);
    }
}
