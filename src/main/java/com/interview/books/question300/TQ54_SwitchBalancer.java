package com.interview.books.question300;

import com.interview.utils.ArrayUtil;
import com.interview.utils.ConsoleWriter;

import java.util.Arrays;

/**
 * Created by stefanie on 1/21/15.
 */
public class TQ54_SwitchBalancer {
    public void switchPair(int[] A, int[] B){
        int sumA = ArrayUtil.sum(A, 0, A.length - 1);
        int sumB = ArrayUtil.sum(B, 0, B.length - 1);

        int minGap = Integer.MAX_VALUE;
        int idxA = -1;
        int idxB = -1;
        
        Arrays.sort(A);
        for(int i = 0; i < B.length; i++){
            int target = (sumA - sumB + 2 * B[i])/2;
            int closest = closest(A, target);
            int cur = Math.abs(sumA - sumB - 2 * A[closest] + 2 * B[i]);
            if(cur < minGap){
                minGap = cur;
                idxA = closest;
                idxB = i;
            }
        }
        int temp = A[idxA];
        A[idxA] = B[idxB];
        B[idxB] = temp;
    }
    
    public int closest(int[] A, int target){
        int low = 0;
        int high = A.length;
        while(low < high){
            int mid = low + (high - low)/2;
            if(A[mid] == target) return mid;
            else if(target < A[mid]) high = mid;
            else low = mid + 1;
        }
        if(low > 0 && Math.abs(A[low - 1] - target) < Math.abs(A[low] - target)) return low - 1;
        else return low;
    }
    
    public static void main(String[] args){
        int[] A = new int[]{1,7,8,4};
        int[] B = new int[]{9,8,7,6};
        
        TQ54_SwitchBalancer balancer = new TQ54_SwitchBalancer();
        balancer.switchPair(A, B);
        ConsoleWriter.printIntArray(A);
        System.out.println(ArrayUtil.sum(A, 0, A.length - 1));
        ConsoleWriter.printIntArray(B);
        System.out.println(ArrayUtil.sum(B, 0, B.length - 1));
    }
}
