package com.interview.books.ninechapter;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-12-12
 * Time: 下午4:41
 */
public class NC10_EqualSubarray {
    public int longest(int[] array){
        HashMap<Integer, Integer> sumMap = new HashMap<>();
        int max = 0;
        int sum = 0;
        for(int i = 0; i < array.length; i++){
            sum += array[i] == 0? -1 : 1;
            if(sum == 0) max = Math.max(max, i + 1);
            else if(sumMap.containsKey(sum)){
                int len = i - sumMap.get(sum);
                max = Math.max(max, len);
            } else {
                sumMap.put(sum, i);
            }
        }
        return max;
    }

    public static void main(String[] args){
        NC10_EqualSubarray finder = new NC10_EqualSubarray();
        int[] array = new int[]{0,1,1,0,1,0,1,0,0};
        System.out.println(finder.longest(array));  //8

        array = new int[]{0,1,1,0,1,0,1,0,0,1};//10
        System.out.println(finder.longest(array));

        array = new int[]{0,1,1,0,1,1,1,1,0};
        System.out.println(finder.longest(array));//4
    }
}
