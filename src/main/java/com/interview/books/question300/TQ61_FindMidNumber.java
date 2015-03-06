package com.interview.books.question300;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 8/8/14
 * Time: 12:07 PM
 */
public class TQ61_FindMidNumber {

    public static boolean[] find(Integer[] numbers){
        boolean[] mark = new boolean[numbers.length];

        Integer[] max = new Integer[numbers.length];
        max[0] = numbers[0];
        for(int i = 1; i < numbers.length; i++){
            max[i] = Math.max(max[i-1], numbers[i]);
        }

        int min = Integer.MAX_VALUE;
        for(int i = numbers.length - 1; i >= 0; i--){
            if(numbers[i] <= min){
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
}
