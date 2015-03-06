package com.interview.algorithms.array;

import com.interview.utils.ArrayUtil;

/**
 * Created_By: stefanie
 * Date: 14-9-22
 * Time: 下午5:28
 */
public class C4_8_KthElement_QuickSelect {

    public static int select(int[] array, int K){
        int[] shuffled = array.clone();
        C4_11_RandomShuffle.shuffle(shuffled);
        return select(shuffled, 0, shuffled.length - 1, K - 1);
    }

    private static int select(int[] array, int low, int high, int k){
        int offset = partition(array, low, high);
        int c_offset = offset - low;
        if(c_offset == k)     return array[offset];
        else if(c_offset > k) return select(array, low, offset - 1, k);
        else                  return select(array, offset + 1, high, k - c_offset - 1);

    }

    private static int partition(int[] array, int low, int high){
        int key = array[low];
        int i = low, j = high + 1;
        while(true){
            while( ++i < high && array[i] < key);
            while( --j > low  && array[j] > key);
            if(i >= j) break;
            ArrayUtil.swap(array, i, j);
        }
        ArrayUtil.swap(array, low, j);
        return j;
    }
}
