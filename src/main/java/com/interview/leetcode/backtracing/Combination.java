package com.interview.leetcode.backtracing;

import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-23
 * Time: 下午8:05
 */
public class Combination {

    static class KCombination{
        List<List<Integer>> sols;
        public List<List<Integer>> combine(int[] num, int k) {
            sols = new ArrayList<>();
            Arrays.sort(num);
            List<Integer> current = new ArrayList<Integer>();
            combine(0, num, current, k);
            return sols;
        }

        public void combine(int offset, int[] num, List<Integer> current, int k){
            if(offset >= num.length) return;
            current.add(num[offset]);
            if(current.size() == k){
                sols.add(new ArrayList<Integer>(current));
            } else {
                combine(offset + 1, num, current, k);
            }
            current.remove(current.size() - 1);
            while(offset < num.length - 1 && num[offset + 1] == num[offset]) offset++; //de-dup
            if(offset < num.length - 1) combine(offset + 1, num, current, k);
        }
    }

    static class CombinationSum{

        List<List<Integer>> sols;
        public List<List<Integer>> combinationSum(int[] num, int K, boolean reuse){
            sols = new ArrayList<>();
            Arrays.sort(num);
            List<Integer> current = new ArrayList<Integer>();
            find(num, 0, current, 0, K, reuse);
            return sols;
        }

        public void find(int[] num, int offset, List<Integer> current, int sum, int K, boolean reuse){
            if(offset >= num.length || sum >= K) return;
            current.add(num[offset]);
            if(sum + num[offset] == K){
                sols.add(new ArrayList<Integer>(current));
            } else {
                if(reuse)   find(num, offset, current, sum + num[offset], K, reuse);
                else        find(num, offset + 1, current, sum + num[offset], K, reuse);
            }
            current.remove(current.size() - 1);
            while(offset < num.length - 1 && num[offset + 1] == num[offset]) offset++; //de-dup
            if(offset < num.length - 1) find(num, offset + 1, current, sum, K, reuse);
        }
    }

    public static void main(String[] args){
        KCombination combinator = new KCombination();
        List<List<Integer>> combinations = combinator.combine(new int[]{1,2,3,4,4,5,5,6}, 5);
        ConsoleWriter.print(combinations);
    }
}
