package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午2:57
 */
public class LOJ15_ThreeSum {
    //sort array, and enumerate i, and find j and k using two pointer.
    //1. i, j, k is the offset not element;
    //2. inner while
    //3. de dup using while
    public List<List<Integer>> threeSum(int[] num) {
        List<List<Integer>> triples = new ArrayList();
        if(num.length < 3) return triples;
        Arrays.sort(num);
        for(int i = 0; i < num.length - 2; i++){
            if(i != 0 && num[i] == num[i - 1]) continue; //de-dup
            int j = i + 1;
            int k = num.length - 1;
            while(j < k){
                int sum = num[i] + num[j] + num[k];
                if(sum == 0){
                    List<Integer> triple = new ArrayList();
                    triple.add(num[i]);
                    triple.add(num[j]);
                    triple.add(num[k]);
                    triples.add(triple);
                    j++;
                    k--;
                    while(j < k && num[j] == num[j-1]) j++; //de-dup
                    while(j < k && num[k] == num[k+1]) k--; //de-dup
                } else if(sum > 0){
                    k--;
                } else {
                    j++;
                }
            }
        }
        return triples;
    }
}
