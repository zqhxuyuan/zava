package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-7-18
 * Time: 下午11:17
 *
 * A int array contains positive and negative number, find the max of sum of continous sub array.
 */
public class C4_29_MaxSubArraySum {
    static class Range{
        int begin;
        int end;
        int sum;
    }

    public static int max(int[] array){
        int sum = 0, max = 0;
        int largest = array[array.length - 1];
        for(int i = array.length - 1; i >= 0; i--){
            sum = Math.max(sum + array[i], 0);
            if(sum > max) max = sum;
            if(largest < array[i]) largest = array[i];
        }
        return max == 0? largest : max;
    }

    public static Range maxRange(int[] array){
        C4_29_MaxSubArraySum.Range r = new C4_29_MaxSubArraySum.Range();
        r.end = array.length - 1;
        int largest = array.length - 1;
        int sum = 0;
        for(int i = array.length - 1; i >= 0; i--){
            if(sum + array[i] <= 0) r.end = i - 1;
            sum = Math.max(sum + array[i], 0);
            if(sum > r.sum) {
                r.sum = sum;
                r.begin = i;
            }
            if(array[largest] < array[i]) largest = i;
        }
        if(r.sum == 0){
            r.sum = array[largest];
            r.begin = r.end = largest;
        }
        return r;
    }

    public static int maxForword(int[] array){
        int sum = 0, max = 0;
        int largest = array[0];
        for(int i = 0; i < array.length; i++){
            if(largest < array[i]) largest = array[i];
            sum = Math.max(sum + array[i], 0);
            if(sum > max) max = sum;
        }
        return max == 0? largest : max;
    }


}
