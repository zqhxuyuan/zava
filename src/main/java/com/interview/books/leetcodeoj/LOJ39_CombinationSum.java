package com.interview.books.leetcodeoj;

import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-21
 * Time: 下午4:38
 */
public class LOJ39_CombinationSum {
    //find all combination, and tracking the sum of combination.
    //1. sort num to de-dup
    //2. when sum + num[offset] == K, also need remove new-added element in cur.
    //3. when could reuse, do(offset, cur, sum + num[offset]) and remove new-added element then (offset + 1, cur, sum).
    //   when not reuse, do (offset + 1, cur, sum + num[offset]) and remove new-added element then (offset + 1, cur, sum)
    //4. de dup by while(offset + 1 < num.length && num[offset + 1] == num[offset]) offset++;
    List<List<Integer>> sols;
    public List<List<Integer>> combinationSum(int[] num, int K, boolean reuse){
        sols = new ArrayList();
        Arrays.sort(num);
        if(num.length == 0) return sols;
        List<Integer> cur = new ArrayList();
        combination(num, 0, cur, 0, K, reuse);
        return sols;
    }

    public void combination(int[] num, int offset, List<Integer> cur, int sum, int K, boolean reuse){
        if(offset >= num.length || sum > K) return;
        cur.add(num[offset]);
        if(sum + num[offset] == K){
            sols.add(new ArrayList(cur));
        } else {
            if(reuse) combination(num, offset, cur, sum + num[offset], K, reuse);
            else combination(num, offset + 1, cur, sum + num[offset], K, reuse);
        }
        cur.remove(cur.size() - 1);
        while(offset + 1 < num.length && num[offset + 1] == num[offset]) offset++;
        if(offset + 1 < num.length) combination(num, offset + 1, cur, sum, K, reuse);
    }

    public static void main(String[] args){
        int[] num = new int[]{1,2};
        LOJ39_CombinationSum finder = new LOJ39_CombinationSum();
        List<List<Integer>> sols = finder.combinationSum(num, 2, true);
        ConsoleWriter.print(sols);
        sols = finder.combinationSum(num, 2, false);
        ConsoleWriter.print(sols);
    }
}
