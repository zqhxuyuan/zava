package com.interview.algorithms.general;

import com.interview.basics.sort.QuickSorter;

/**
 * Created_By: stefanie
 * Date: 14-9-4
 * Time: 下午4:31
 */
public class C1_53_HorseRacing {
    public static QuickSorter<Integer> SORTER = new QuickSorter<>();

    public static void sort(Integer[] horses){
        SORTER.sort(horses);
    }

    public static Integer[] top5(Integer[] horses){
        Integer[][] groups = new Integer[5][];
        for(int i = 0; i < 5; i++){
            Integer[] group = new Integer[5];
            for(int j = 0; j < 5; j++) group[j] = horses[5*i+j];
            sort(group);
            groups[i] = group;
        }
        Integer[] canidates = new Integer[5];
        Integer[] groupIndex = new Integer[5];
        Integer[] top5 = new Integer[5];
        for(int i = 0; i < 5; i++){
            canidates[i] = groups[i][4];
            groupIndex[i] = 3;
        }
        for(int i = 0; i < 5; i++){
            int max = canidates[0];
            int maxIndex = 0;
            for(int j = 1; j < 5; j++){
                if(canidates[j] > max){
                    max = canidates[j];
                    maxIndex = j;
                }
            }
            top5[i] = max;
            int index = groupIndex[maxIndex]--;
            canidates[maxIndex] = index >= 0? groups[maxIndex][index] : 0;
        }
        return top5;
    }
}
