package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-11-9
 * Time: 下午9:36
 *
 * Solution: found the max, left part scan to right, and right part scan to left.
 *    for each A[i], find a higher one (always has, at least have max),
 *    when found higher, amount += total area - blocked area
 *      total area = width between cur and higher * cur;
 *      block area = the sum height of  A[i] from cur next to higher
 *    cur move to higher.
 */
public class C4_77_TrappingWater {
    public static int trap(int[] A) {
        if(A.length < 2) return 0;
        int amount = 0;
        int max = max(A);   //find the max
        int cur = 0;    // scan 0 - max from left to right
        while(cur < max){
            int higher = cur + 1;
            while(higher <= max && A[higher] < A[cur]) higher++; //found the next equals or higher one;
            amount += (higher - 1 - cur) * A[cur]; //add the total area.
            for(int i = cur + 1; i < higher; i++) amount -= A[i]; //minus the blocked area.
            cur = higher;
        }
        cur = A.length - 1;  // scan end - max from right to left
        while(cur > max){
            int higher = cur - 1;
            while(higher >= max && A[higher] < A[cur]) higher--; //found the next equals or higher one;
            amount += (cur - 1 - higher) * A[cur]; //add the total area.
            for(int i = cur - 1; i > higher; i--) amount -= A[i]; //minus the blocked area.
            cur = higher;
        }
        return amount;
    }

    private static int max(int[] A){
        int max = 0;
        for(int i = 1; i < A.length; i++){
            if(A[i] > A[max]) max = i;
        }
        return max;
    }
}
