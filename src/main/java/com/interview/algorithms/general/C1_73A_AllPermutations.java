package com.interview.algorithms.general;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-6
 * Time: 上午7:50
 */
public class C1_73A_AllPermutations {
    public static List<List<Integer>> permute(int[] num) {
        boolean[] mark = new boolean[num.length];
        int[] current = new int[num.length];
        List<List<Integer>> permutations = new ArrayList<List<Integer>>();
        permute(num, mark, current, 0, permutations);
        return permutations;
    }

    private static void permute(int[] num, boolean[] mark, int[] current, int offset, List<List<Integer>> permutations){
        for(int i = 0; i < num.length; i++){
            if(mark[i]) continue;
            current[offset] = num[i];
            mark[i] = true;
            if(offset == current.length - 1){
                List<Integer> permutation = new ArrayList<Integer>();
                for(int item : current) permutation.add(item);
                permutations.add(permutation);
            } else {
                permute(num, mark, current, offset + 1, permutations);
            }
            mark[i] = false;
        }
    }
}
