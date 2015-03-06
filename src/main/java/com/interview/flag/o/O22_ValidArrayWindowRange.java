package com.interview.flag.o;

import java.util.HashMap;
import java.util.Map;

/**
 * Created_By: stefanie
 * Date: 15-2-3
 * Time: 上午10:14
 */
public class O22_ValidArrayWindowRange {


    public boolean valid(int[] array, int K, int L){
        Map<Integer, Integer> map = new HashMap();
        for(int i = 0; i < array.length; i++){
            if(i >= K) map.remove(array[i-K]/(L + 1));
            int key = array[i]/(L+1);
            if(map.containsKey(key)
               || map.containsKey(key + 1) && map.get(key + 1) - array[i] <= L
               || map.containsKey(key - 1) && array[i] - map.get(key - 1) <= L){
                return true;
            }
            map.put(key, array[i]);
        }
        return false;
    }

    public static void main(String[] args){
        O22_ValidArrayWindowRange checker = new O22_ValidArrayWindowRange();
        int[] array = new int[]{1,12,4,20,1,5,8,6};
        System.out.println(checker.valid(array, 2, 1));//false
        System.out.println(checker.valid(array, 2, 2));//true
        System.out.println(checker.valid(array, 3, 1));//true

    }
}
