package com.interview.algorithms.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-6
 * Time: 上午8:40
 */
public class C1_73D_UniquePermutation {
    private int[] num;
    private boolean[] marked;
    private Integer[] current;
    List<List<Integer>> result;
    public List<List<Integer>> permuteUnique(int[] num) {
        this.num = num;
        marked = new boolean[num.length];
        current = new Integer[num.length];
        result = new ArrayList<>();
        this.num = num;
        Arrays.sort(num);
        permute(0);
        return result;
    }

    private void permute(int offset){
        if(offset == num.length) {
            result.add(new ArrayList<>(Arrays.asList(current)));
            return;
        }
        for(int i = 0; i < num.length; i++){
            if(marked[i]) continue;
            // If in left there is an element with the same value but not used in permutation
            // it means that we have case when we 1(at0) 1(at1) try to put 1(at1) before 1(0), and
            // it will generate duplicates.
            // so there is simple check for that case by iterating
            boolean unusedDuplication = false;
            int j = i - 1;
            while(j >= 0 && num[j] == num[i]){
                if(!marked[j])  unusedDuplication = true;
                j--;
            }
            if(!unusedDuplication){
                marked[i] = true;
                current[offset] = num[i];
                permute(offset + 1);
                marked[i] = false;
            }
        }
    }
}
