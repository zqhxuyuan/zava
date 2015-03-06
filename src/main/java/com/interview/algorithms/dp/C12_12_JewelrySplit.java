package com.interview.algorithms.dp;

import com.interview.utils.ArrayUtil;

import java.util.*;

/**
 * Created by chenting on 2014/6/26.
 * You have been given a list of jewelry items that must be split amongst two people: Frank and Bob. Frank likes very expensive jewelry.
 * Bob doesn't care how expensive the jewelry is, as long as he gets a lot of jewelry.
 * Based on these criteria you have devised the following policy:
 *
 * 1) Each piece of jewelry given to Frank must be valued greater than or equal to each piece of jewelry given to Bob.
 *    In other words, Frank's least expensive piece of jewelry must be valued greater than or equal to Bob's most expensive piece of jewelry.
 * 2) The total value of the jewelry given to Frank must exactly equal the total value of the jewelry given to Bob.
 * 3) There can be pieces of jewelry given to neither Bob nor Frank.
 * 4) Frank and Bob must each get at least 1 piece of jewelry.
 * Given the value of each piece, you will determine the number of different ways you can allocate the jewelry to
 * Bob and Frank following the above policy.
 *
 * For example:
 * values = {1,2,5,3,4,5}
 * Valid allocations are:
 * Bob       		Frank
 * 1,2		         3
 * 1,3        		 4
 * 1,4		         5  (first 5)
 * 1,4         		 5  (second 5)
 * 2,3 		         5  (first 5)
 * 2,3         		 5  (second 5)
 * 5  (first 5)		 5  (second 5)
 * 5  (second 5)	 5  (first 5)
 * 1,2,3,4       		5,5
 * Note that each '5' is a different piece of jewelry and needs to be accounted for separately.
 * There are 9 legal ways of allocating the jewelry to Bob and Frank given the policy, so your method would return 9.
 */
public class C12_12_JewelrySplit {

    static class CountedList<Key>{
        Map<Key, Integer> map = new HashMap<Key, Integer>();

        public void add(Key key, int count){
            if(map.containsKey(key))
                map.put(key, map.get(key) + count);
            else
                map.put(key, count);
        }

        public Set<Key> keySet(){
            return map.keySet();
        }

        public Integer get(Key key){
            return map.get(key);
        }

        public void clear(){
            map.clear();
        }

        public void addAll(CountedList<Key> list){
            for(Key key : list.keySet()){
                this.add(key, list.get(key));
            }
        }
    }

    public static long find(Integer[] jevelries){
        int count = 0;
        jevelries = ArrayUtil.sort(jevelries);
        int N = jevelries.length;

        CountedList<Integer> solutions = new CountedList<Integer>();
        CountedList<Integer> waitingList = new CountedList<Integer>();
        solutions.add(0, 1);

        for(int i = 0; i < N - 1; i ++){
            for(Integer s : solutions.keySet()){
                Integer key = s + jevelries[i];
                waitingList.add(key, solutions.get(key));
            }
            for(Integer value : waitingList.keySet()){
                solutions.add(value, waitingList.get(value));
                count += findSum(value, i+1, jevelries) * waitingList.get(value);
            }
            waitingList.clear();
        }

        return count;
    }

    private static int findSum(Integer value, int n, Integer[] jevelries) {
        int num = n < jevelries.length - n? n : jevelries.length - n;
        int count = 0;
        CountedList<Integer> solutions = new CountedList<Integer>();
        CountedList<Integer> waitingList = new CountedList<Integer>();
        solutions.add(0,1);
        for(int i = 1; i <= num; i++){   //number of jevelries
            for(int j = n; j <= n + 1; j++)  //start and end of the
            for(Integer s : solutions.keySet()){
                int sum = s + jevelries[i];
                if(sum == value) count++;
                else if(sum > value) return count;
                waitingList.add(sum, solutions.get(sum));
            }
        }
        return count;
    }

}
