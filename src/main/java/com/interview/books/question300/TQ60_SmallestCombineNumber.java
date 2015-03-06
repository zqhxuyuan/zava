package com.interview.books.question300;

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
public class TQ60_SmallestCombineNumber {
    public static String find(Integer[] numbers){
        Arrays.sort(numbers, new Comparator<Integer>(){
            public int compare(Integer i1, Integer i2) {
                return ("" + i1 + i2).compareTo("" + i2 + i1);
            }
        });
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < numbers.length; i++) {
            buffer.append(numbers[i]);
        }
        return buffer.toString();
    }
}
