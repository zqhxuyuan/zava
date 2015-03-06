package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午3:22
 */
public class LOJ18_FourSum {
    List<List<Integer>> sols;
    public List<List<Integer>> fourSum(int[] num, int target) {
        Arrays.sort(num);
        sols = new ArrayList<>();
        Integer[] candidate = new Integer[4];
        for(int i = 0; i < num.length - 3; i++){
            if(i > 0 && num[i] == num[i - 1]) continue;
            for(int j = i + 1; j < num.length - 2; j++){
                if(j > i + 1 && num[j] == num[j - 1]) continue;
                int k = target - num[i] - num[j];
                candidate[0] = num[i];
                candidate[1] = num[j];
                find(num, k, j + 1, candidate);
            }
        }
        return sols;
    }

    public void find(int[] num, int target, int start, Integer[] candidate){
        int end = num.length - 1;
        while(start < end){
            int sum = num[start] + num[end];
            if(sum == target){
                candidate[2] = num[start];
                candidate[3] = num[end];
                sols.add(new ArrayList<>(Arrays.asList(candidate)));
                start++;
                end--;
                while(start < end && num[start] == num[start - 1]) start++;
                while(start < end && num[end] == num[end + 1]) end--;
            } else if(sum > target){
                end--;
            } else start++;
        }
    }
}
