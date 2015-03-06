package com.interview.flag.f;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 15-1-28
 * Time: 下午10:46
 */
public class F13_SubsetSumK {
    public int[] subarray(int[] array, int K){
        int[] sums = new int[array.length];
        HashMap<Integer, Integer> sumMap = new HashMap();
        for(int i = 0; i < array.length; i++){
            sums[i] = i == 0? array[i] : sums[i - 1] + array[i];
            if(!sumMap.containsKey(sums[i])) sumMap.put(sums[i], i);
        }

        for(int i = 0; i < sums.length; i++){
            if(sums[i] == K) return new int[]{0, i};
            int target = sums[i] - K;
            if(sumMap.containsKey(target) && sumMap.get(target) < i) return new int[]{sumMap.get(target) + 1, i};
        }
        return new int[]{-1, -1};
    }

    public static void main(String[] args){
        F13_SubsetSumK finder = new F13_SubsetSumK();
        int[] array = new int[]{1,5,3,5,2,1,4};
        int[] range = finder.subarray(array, 8);
        System.out.println(range[0] + ", " + range[1]); //1,2
        range = finder.subarray(array, 10);
        System.out.println(range[0] + ", " + range[1]); //2,4
        range = finder.subarray(array, 17);
        System.out.println(range[0] + ", " + range[1]); //0,5
        range = finder.subarray(array, 18);
        System.out.println(range[0] + ", " + range[1]); //-1,-1
    }
}
