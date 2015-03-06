package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-9-23
 * Time: 下午10:26
 *
 * Solution:
 * 1. hold left[] and right[] as the product of left side of i-th element and right side.
 *    scan the array, to find the max product left[i] * right[i]
 *    O(N), but product of lots number, maybe cause overflow.
 * 2. analysis the product: calculate positive, negitive, and 0 numbers in the array.
 *    a. if contains two more 0, it always 0.
 *    b. if contains one 0,
 *      b.1 if the other N-1 number product is positive, remove 0
 *      b.2 if the other N-1 number product is negitive, remove some element except 0.
 *    c. if no 0:
 *      c.1 if the product of N number is positive, remove the smallest positive number.
 *      c.2 if the product of N number is negitive, remove the largest negitive number.
 *
 */
public class C4_63_MaxProduct {
    public static int exceptNumberBest(int[] array){
        int[] numbers = new int[5]; //0:0-number, 1:negitive number, 2:smallest positive number, 3:largest negitive number.4:0 number offset
        numbers[3] = -1;
        numbers[2] = -1;
        for(int i = 0; i < array.length; i++){
            if(array[i] == 0) {
                numbers[0]++;
                numbers[4] = i;
            }
            else if(array[i] < 0) {
                numbers[1]++;
                if(numbers[3] == -1) numbers[3] = i;
                else if(array[i] > array[numbers[3]]) numbers[3] = i;
            } else {
                if(numbers[2] == -1) numbers[2] = i;
                else if(array[i] < array[numbers[2]]) numbers[2] = i;
            }
        }

        if(numbers[0] > 1) return 0;
        else if(numbers[0] == 1){
            if((numbers[1] & 1) == 0) return numbers[4];
            else return array[numbers[4]] == 0? 0: 1;
        } else {
            if((numbers[1] & 1) == 0) return numbers[2];
            else return numbers[3];
        }

    }

    public static int exceptNumber(int[] array){
        long[] left  = new long[array.length];
        long[] right = new long[array.length];
        left[0] = 1;
        for(int i = 1; i < array.length; i++){
            left[i] = array[i - 1] * left[i - 1];
        }
        right[array.length - 1] = 1;
        for(int i = array.length - 2; i >= 0; i--){
            right[i] = array[i + 1] * right[i + 1];
        }

        long maxProduct = Integer.MIN_VALUE;
        int offset = 0;
        for(int i = 0; i < array.length; i++){
            long ten = left[i] * right[i];
            if(ten > maxProduct){
                maxProduct = ten;
                offset = i;
            }
        }
        return offset;
    }

    public static int correct(int[] array){
        long maxProduct = Integer.MIN_VALUE;
        int offset = 0;
        for(int i = 0; i < array.length; i++){
            long ten = 1;
            for(int j = 0; j < array.length; j++){
                if(i == j) continue;
                ten *= array[j];
            }
            if(ten > maxProduct){
                maxProduct = ten;
                offset = i;
            }
        }
        return offset;
    }
}
