package com.interview.algorithms.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-6
 * Time: 下午5:37
 */
public class C4_32B_UniqueTripleSumK {

    public static List<List<Integer>> threeSum(int[] num) {
        List<List<Integer>> sols = new ArrayList<>();
        Arrays.sort(num);
        Integer prev = null;
        for(int i = 0; i < num.length - 2; i++){
            if(prev == null || num[i] != prev){
                int key = 0 - num[i];
                search(num, key, i, i + 1, num.length - 1, sols);
            }
            prev = num[i];
        }
        return sols;
    }

    private static void search(int[] num, int key, int offset, int begin, int end, List<List<Integer>> sols){
        while(begin < end){
            int sum = num[begin] + num[end];
            if(sum == key){
                List<Integer> sol = new ArrayList<>();
                sol.add(num[offset]);
                sol.add(num[begin++]);
                sol.add(num[end--]);
                sols.add(sol);
                while(begin < end && num[begin] == num[begin - 1]) begin++;
                while(begin < end && num[end] == num[end + 1]) end--;
            } else if(sum > key) end--;
            else begin++;
        }
    }

}
