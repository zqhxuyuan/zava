package com.interview.algorithms.sort;

import java.util.BitSet;

/**
 * Created_By: stefanie
 * Date: 14-7-8
 * Time: 下午9:02
 *
 * Int sort in O(N), suitable for tensive and non-duplicate int array, such as telphone number, license number, etc.
 */
public class C8_5_BitSort {

    public static int[] sort(int[] array){

        int max = Integer.MIN_VALUE;
        for(int i = 0; i < array.length; i++) {
            if(array[i] > max) max = array[i];
        }

        BitSet bitset = new BitSet(max);
        for(int i = 0; i < array.length; i++) bitset.set(array[i]);

        int[] sorted = new int[array.length];
        int j = 0;
        for(int i = 0; i <= max; i++){
            if(bitset.get(i)) sorted[j++] = i;
        }

        return sorted;
    }
}
