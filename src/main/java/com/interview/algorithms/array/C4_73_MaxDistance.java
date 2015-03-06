package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-10-31
 * Time: 下午10:11
 */
public class C4_73_MaxDistance {

    static class Pair{
        int start;
        int end;
        int dist;
    }

    public static Pair find(int[] array){
        boolean[] mark = new boolean[array.length];
        Pair result = new Pair();
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < array.length; i++){
            if(array[i] < min){
                mark[i] = true;
                min = array[i];
                result.start = i;
            }
        }

        for(result.end = array.length - 1; result.end >= 0 && array[result.end] <= array[result.start]; result.end--);
        for(int i = result.end - 1; i >= 0; i--){
            if(mark[i] == false) continue;
            if(array[i] < array[result.end]) result.start = i;
        }
        result.dist = result.end - result.start;
        return result;
    }

    public static Pair findO2(int[] array){
        Pair max = new Pair();
        max.dist = 0;
        for(int j = array.length - 1; j >0; j--){
            for(int i = j - max.dist; i >= 0; i--){
                if(array[i] < array[j] && j - i > max.dist){
                    max.start = i;
                    max.end = j;
                    max.dist = j - i;
                }
            }
        }
        return max;
    }
}
