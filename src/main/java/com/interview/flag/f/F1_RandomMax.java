package com.interview.flag.f;

import java.util.Random;

/**
 * Created_By: stefanie
 * Date: 14-11-26
 * Time: 下午6:22
 *
 * Interview Game from Facebook
 *  Return the index of the max element in a vector, if there are several results, return them in the same probability.
 *
 *  Basic Solution:     Time 0(N) and Space: O(N)
 *  Improved Solution:  Time O(N) and Space O(1)
 */
public class F1_RandomMax {
    Random RANDOM = new Random();
    public int max(int[] array){
        int maxIdx = 0;
        int count = 1;
        for(int i = 1; i < array.length; i++){
            if(array[i] > array[maxIdx]){
                maxIdx = i;
                count = 1;
            } else if(array[i] == array[maxIdx]){     //random pick one, like shuffle
                count++;
                int r = RANDOM.nextInt(count);
                if(r == 0)  maxIdx = i;
            }
        }
        return maxIdx;
    }

    public static void main(String[] args){
        int[] num = new int[]{1,4,7,4,7,7,9,9,4,9,7,9};  //6,7,9,11
        F1_RandomMax picker = new F1_RandomMax();
        int[] count = new int[12];
        for(int i = 0; i < 1000; i++){
            int max = picker.max(num);
            count[max]++;
        }

        for(int i = 0; i < 12; i++){
            System.out.println(i + ": " + count[i]);
        }

    }
}
