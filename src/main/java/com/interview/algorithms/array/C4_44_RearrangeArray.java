package com.interview.algorithms.array;

import com.interview.utils.ArrayUtil;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 8/8/14
 * Time: 12:30 PM
 */
public class C4_44_RearrangeArray {
    public static void rearrange(Integer[] array){
        int i = 0;
        int j = array.length - 1;
        while(true){
            while(i < j && array[i] % 2 != 0)   i++;
            while(j > i && array[j] % 2 == 0)   j--;
            if(i < j) ArrayUtil.swap(array, i++, j--);
             else break;
        }
    }
}
