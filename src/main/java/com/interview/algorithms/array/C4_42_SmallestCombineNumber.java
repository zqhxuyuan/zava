package com.interview.algorithms.array;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 8/4/14
 * Time: 3:20 PM
 *
 * Given an int array, combine all the integer to a int, such as {23, 125} -> 12523.
 * Write code to get the smallest combined number.
 *
 * using string to determine: if ab < ba then a < b;
 */
public class C4_42_SmallestCombineNumber {
    public static String find(int[] numbers){
        Integer aux[] = new Integer[numbers.length];
        for (int i=0; i<numbers.length; i++) aux[i] = numbers[i];
        Arrays.sort(aux, new Comparator<Integer>(){
            public int compare(Integer i1, Integer i2) {
                return ("" + i1 + i2).compareTo("" + i2 + i1);
            }
        });
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<aux.length; i++) {
            sb.append(aux[i]);
        }
        return sb.toString();
    }
}
