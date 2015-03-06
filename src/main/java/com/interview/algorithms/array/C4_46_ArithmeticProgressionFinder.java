package com.interview.algorithms.array;

import com.interview.basics.sort.QuickSorter;

/**
 * Created_By: stefanie
 * Date: 14-8-19
 * Time: 下午6:20
 */
public class C4_46_ArithmeticProgressionFinder {
    static QuickSorter<Integer> SORTER = new QuickSorter<>();
    static int MIN_LENGTH = 3;

    public static Integer[] find(Integer[] array){

        SORTER.sort(array);

        int MAX = (int) Math.ceil((array[array.length - 1] - array[0])/(MIN_LENGTH+0.0));
        int[][] mark = new int[array.length][MAX + 1];

        int last = 0;
        int maxLen = 0;
        int step = 0;
        for(int i = 0; i < array.length; i++){
            for(int j = i - 1; j >= 0; j--){
                int dis = array[i] - array[j];
                if(dis > MAX) continue;
                mark[i][dis] = mark[j][dis] == 0? 2: mark[j][dis] + 1;
                if(mark[i][dis] > maxLen){
                    last = array[i];
                    maxLen = mark[i][dis];
                    step = dis;
                }
            }
        }

        if(maxLen > MIN_LENGTH){
            Integer[] progression = new Integer[maxLen];
            for(int i = maxLen - 1; i >= 0; i--) {
                progression[i] = last - step * (maxLen - 1 - i);
            }
            return progression;
        } else {
            return null;
        }
    }
}
