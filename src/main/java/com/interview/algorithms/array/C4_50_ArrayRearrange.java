package com.interview.algorithms.array;

import com.interview.utils.ArrayUtil;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 8/29/14
 * Time: 1:05 PM
 *
 * With Rotate solution is space O(1) but not proved of time O(N).
 * The basic idea is when swap, do some rotation to make sure the seq is in reverse order.
 * After all the scan, reverse the positive and negitive seq.
 *
 *
 */
public class C4_50_ArrayRearrange {

    public static int[] arrange(int[] numbers){
        //return arrangeWithExtraN(numbers);
        return arrangeWithRotate(numbers);
    }

    public static int[] arrangeWithExtraN(int[] numbers){
        int[] aux = new int[numbers.length];
        int k = 0;
        for(int i = 0; i < numbers.length; i++){
            if(numbers[i] < 0)  aux[k++] = numbers[i];
        }
        for(int i = 0; i < numbers.length; i++){
            if(numbers[i] >= 0) aux[k++] = numbers[i];
        }
        return aux;
    }

    public static int[] arrangeWithRotate(int[] numbers){
        int s = -1;
        int i = numbers.length - 1;
        while(true){
            if(s == -1) {
                if(numbers[i] < 0)  s = i;
                else                s = firstOpposite(numbers, i);
            }
            int mid = firstOpposite(numbers, s);
            int e = firstOpposite(numbers, mid) + 1;
            if(e < 0) break;
            else {
                ArrayUtil.reverse(numbers, e, s);
                mid = s - (mid - e);
                if(s > mid) ArrayUtil.reverse(numbers, mid, s);
                if(mid - 1 > e) ArrayUtil.reverse(numbers, e, mid - 1);
                s = mid - 1;
                i = s;
            }
        }
        return numbers;
    }

    public static int firstOpposite(int[] numbers, int i){
        while(i > 0 && numbers[i]*numbers[i-1] > 0) i--;
        return i - 1;
    }

//    public static int[] arrangeWithRotate(int[] numbers){
//        int i = 0;
//        int j = numbers.length - 1;
//        int k = numbers.length - 1;
//        int s = -1;
//        while(true){
//            int si = i; int sj = j;
//            while(i < numbers.length && numbers[i] < 0)  i++;
//            if(s == -1) s = i;
//            else if(i > si + 1) ArrayUtil.reverse(numbers, si, i-1);
//            while(i < j && numbers[j] >= 0) j--;
//            if(i >= j) {
//                if(j < sj) ArrayUtil.reverse(numbers, i, k);
//                break;
//            }
//            else {
//                ArrayUtil.swap(numbers, i++, j--);
//                ArrayUtil.reverse(numbers, j+1, k);
//                k--;
//            }
//        }
//
//        ArrayUtil.reverse(numbers, s, i-1);
//        ArrayUtil.reverse(numbers, i, numbers.length - 1);
//        return numbers;
//    }
}
