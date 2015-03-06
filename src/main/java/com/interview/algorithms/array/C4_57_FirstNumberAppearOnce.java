package com.interview.algorithms.array;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/17/14
 * Time: 1:45 PM
 */
public class C4_57_FirstNumberAppearOnce {

    public static int find(int[] array){
        int max = array[0];
        int min = array[0];
        for(int i = 0; i < array.length; i++){
            if(array[i] < min) min = array[i];
            else if(array[i] > max) max = array[i];
        }

        int[] mark = new int[max - min + 1];
        for(int i = 0; i < array.length; i++){
            mark[array[i] - min]++;
        }

        for(int i = 0; i < mark.length; i++){
            if(mark[i] == 1) return i + min;
        }
        return -1;
    }
}
