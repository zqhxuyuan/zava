package com.interview.algorithms.array;

import com.interview.basics.sort.QuickSorter;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 8/8/14
 * Time: 12:07 PM
 */
public class C4_43_FindMidNumber {

    public static boolean[] find(Integer[] numbers){
        boolean[] mark = new boolean[numbers.length];
        QuickSorter<Integer> sorter = new QuickSorter<>();
        Integer[] sorted = sorter.sort(numbers.clone());
        for(int i = 0; i < numbers.length; i++)
            mark[i] = numbers[i] == sorted[i];
        return mark;
    }

    public static boolean[] findON(Integer[] numbers){
        boolean[] mark = new boolean[numbers.length];

        Integer[] max = new Integer[numbers.length];
        max[0] = numbers[0];
        for(int i = 1; i < numbers.length; i++){
            max[i] = numbers[i] > max[i-1]? numbers[i] : max[i-1];
        }

        int min = numbers[numbers.length - 1];
        for(int i = numbers.length - 2; i >= 0; i--){
            if(numbers[i] < min){
                min = numbers[i];
                if(numbers[i] == max[i]) {
                    mark[i] = true;
                    continue;
                }
            }
            mark[i] = false;
        }
        return mark;
    }

    public static boolean[] findAnswer(Integer[] numbers){
        boolean[] mark = new boolean[numbers.length];
        for(int i = 0; i < numbers.length; i++){
            mark[i] = markOne(numbers, i);
        }
        return mark;
    }

    private static boolean markOne(Integer[] numbers, int i){
        for(int j = 0; j < i; j++)
            if(numbers[j] > numbers[i]) return false;
        for(int j = i+1; j < numbers.length; j++)
            if(numbers[j] < numbers[i]) return false;
        return true;
    }
}
